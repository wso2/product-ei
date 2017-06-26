/*
 * Copyright (c) 2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ei.businessprocess.utils.migration.deployment;

import org.apache.axis2.util.XMLUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.wso2.carbon.humantask.HumanInteractionsDocument;
import org.wso2.carbon.humantask.core.HumanTaskConstants;
import org.wso2.carbon.humantask.core.deployment.HumanTaskDeploymentException;
import org.wso2.carbon.humantask.core.deployment.HumanTaskDeploymentUnitBuilder;
import org.wso2.carbon.humantask.core.deployment.HumanTaskWSDLLocator;
import org.wso2.carbon.humantask.core.deployment.config.HTDeploymentConfigDocument;
import org.wso2.carbon.humantask.core.utils.FileUtils;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Process HumanTask zip archive to get humanTask related files
 * Are we doing this? This class is no longer used since a Registry Handler is used to get the job done
 */
public class ArchiveBasedHumanTaskDeploymentUnitBuilder extends HumanTaskDeploymentUnitBuilder {


    private static final FileFilter wsdlFilter = new FileFilter() {
        public boolean accept(File path) {
            return path.getName().endsWith(".wsdl") && path.isFile();
        }
    };
    private static final FileFilter xsdFilter = new FileFilter() {
        public boolean accept(File path) {
            return path.getName().endsWith(".xsd") && path.isFile();
        }
    };
    private static final FileFilter humantaskFilter = new FileFilter() {
        public boolean accept(File path) {
            return path.getName().endsWith(HumanTaskConstants.HUMANTASK_FILE_EXT) && path.isFile();
        }
    };
    private File humantaskDir;
    private String fileName;
    private long version;
    private String md5sum;
    private List<Definition> wsdlDefinitions = new ArrayList<Definition>();
    private InputStream hiDefinition;
    private InputStream hiConfiguration;
    private File humanTaskDefinitionFile;
    private Map<String, InputStream> schemasMap = new HashMap<String, InputStream>();

    // Build human task deployment unit with unextracted archive
    public ArchiveBasedHumanTaskDeploymentUnitBuilder(String BPS_HOME, File hiArchiveZip, int tenantId, long version,
                                                      String md5sum)
            throws HumanTaskDeploymentException {
        String hiArchiveZipName = hiArchiveZip.getName();
        this.fileName = FilenameUtils.removeExtension(hiArchiveZipName);
        this.version = version;
        this.md5sum = md5sum;
        humantaskDir = extractHumanTaskArchive(hiArchiveZip, tenantId, version, BPS_HOME);
        buildHumanInteractionDocuments();
        buildDeploymentConfiguration();
        buildWSDLs();
        buildSchemas();
    }

    // Build human task deployment unit with the
    public ArchiveBasedHumanTaskDeploymentUnitBuilder(File extractedTaskArchive, int tenantId, long version,
                                                      String packageName,
                                                      String md5sum) throws HumanTaskDeploymentException {
        this.fileName = packageName;
        this.version = version;
        this.humantaskDir = extractedTaskArchive;
        this.md5sum = md5sum;
        buildHumanInteractionDocuments();
        buildDeploymentConfiguration();
        buildWSDLs();
        buildSchemas();
    }

    /**
     * Read the WSDL file given the input stream for the WSDL source
     *
     * @param in           WSDL input stream
     * @param entryName    ZIP file entry name
     * @param fromRegistry whether the wsdl is read from registry
     * @return WSDL Definition
     * @throws javax.wsdl.WSDLException at parser error
     */
    public static Definition readInTheWSDLFile(InputStream in, String entryName,
                                               boolean fromRegistry) throws WSDLException {

        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();

        // switch off the verbose mode for all usecases
        reader.setFeature(HumanTaskConstants.JAVAX_WSDL_VERBOSE_MODE_KEY, false);
        reader.setFeature("javax.wsdl.importDocuments", true);

        Definition def;
        Document doc;
        try {
            doc = XMLUtils.newDocument(in);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Error", e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser SAX Error", e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error",
                    e);
        }

        if (fromRegistry) {
            throw new UnsupportedOperationException("This operation is not currently " +
                    "supported in this version of WSO2 BPS.");
        } else {
            def = reader.readWSDL(entryName, doc.getDocumentElement());
        }
        def.setDocumentBaseURI(entryName);
        return def;

    }

    public static File extractHumanTaskArchive(final File archiveFile, int tenantId, long version, String BPS_HOME)
            throws HumanTaskDeploymentException {
        ZipInputStream zipStream = null;

        try {
            String humanTaskExtractionLocation = BPS_HOME + File.separator +
                    "repository" + File.separator +
                    HumanTaskConstants.HUMANTASK_REPO_DIRECTORY + File.separator +
                    tenantId + File.separator + FilenameUtils.removeExtension(archiveFile.getName()) + "-" + version;
            zipStream = new ZipInputStream(new FileInputStream(archiveFile));
            ZipEntry entry;

            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    if (!new File(humanTaskExtractionLocation, entry.getName()).mkdirs() &&
                            !new File(humanTaskExtractionLocation, entry.getName()).exists()) {
                        throw new HumanTaskDeploymentException("Archive extraction failed. " +
                                "Cannot create directory: "
                                + new File(humanTaskExtractionLocation,
                                entry.getName()).getAbsolutePath() + ".");
                    }
                    continue;
                }

                File destFile = new File(humanTaskExtractionLocation, entry.getName());

                if (!destFile.getParentFile().exists() && !destFile.getParentFile().mkdirs()) {
                    throw new HumanTaskDeploymentException("Archive extraction failed. " +
                            "Cannot create directory: "
                            + destFile.getParentFile().getAbsolutePath());
                }
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile));
                copyInputStream(zipStream, outputStream);
            }


            return new File(humanTaskExtractionLocation);
        } catch (IOException e) {
            String errMsg = "Error occurred during extracting the archive: " + archiveFile;
            System.out.println(errMsg);
            throw new HumanTaskDeploymentException(errMsg, e);
        } finally {
            if (zipStream != null) {
                try {
                    zipStream.close();

                } catch (IOException e) {
                    String errMsg = "Error occurred during extracting the archive: " + archiveFile;
                    System.out.println(errMsg + e);
                    throw new HumanTaskDeploymentException(errMsg, e);
                }
            }
        }
    }

    private static void copyInputStream(final InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.close();
    }

    @Override
    public void buildHumanInteractionDocuments() throws HumanTaskDeploymentException {
        if (hiDefinition == null) {
            List<File> hiDefinitionFiles = FileUtils.directoryEntriesInPath(humantaskDir,
                    humantaskFilter);
            if (hiDefinitionFiles.size() != 1) {
                String errMsg;
                if (hiDefinitionFiles.size() == 0) {
                    errMsg = "No human task definition files were found in " + fileName;
                } else {
                    errMsg = hiDefinitionFiles.size() +
                            " human task definition files were found in " + fileName;
                }
                System.out.println(errMsg);
                throw new HumanTaskDeploymentException(errMsg);
            }

            try {
                hiDefinition = new FileInputStream(hiDefinitionFiles.get(0));
                humanTaskDefinitionFile = hiDefinitionFiles.get(0);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                throw new HumanTaskDeploymentException("Error building humantask archive; " +
                        fileName, e);
            }
        }
    }

    @Override
    public void buildDeploymentConfiguration() throws HumanTaskDeploymentException {
        if (hiConfiguration == null) {
            File humantaskConfFile = new File(humantaskDir, "htconfig.xml");
            if (!humantaskConfFile.exists()) {
                String errMsg = "htconfig.xml file not found for the " + fileName;
                System.out.println(errMsg);
                throw new HumanTaskDeploymentException(errMsg);
            }
            try {
                hiConfiguration = new FileInputStream(humantaskConfFile);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                throw new HumanTaskDeploymentException("Error building humantask archive: " +
                        fileName, e);
            }
        }
    }

    @Override
    public void buildWSDLs() throws HumanTaskDeploymentException {
        URI baseUri = humantaskDir.toURI();
        for (File file : FileUtils.directoryEntriesInPath(humantaskDir, wsdlFilter)) {

            try {
                URI uri = baseUri.relativize(file.toURI());
                if (!uri.isAbsolute()) {
                    File f = new File(baseUri.getPath() + File.separator + uri.getPath());
                    URI abUri = f.toURI();
                    if (abUri.isAbsolute()) {
                        uri = abUri;
                    }
                }

                WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
                reader.setFeature(HumanTaskConstants.JAVAX_WSDL_VERBOSE_MODE_KEY, false);
                reader.setFeature("javax.wsdl.importDocuments", true);
                Definition definition = reader.readWSDL(new HumanTaskWSDLLocator(uri));
                wsdlDefinitions.add(definition);

            } catch (WSDLException e) {
                System.out.println("Error processing wsdl " + file.getName());
                throw new HumanTaskDeploymentException(" Error processing wsdl ", e);
            } catch (URISyntaxException e) {
                System.out.println("Invalid uri in reading wsdl ");
                throw new HumanTaskDeploymentException(" Invalid uri in reading wsdl ", e);
            }
//            wsdlsMap.put(file.getName(), is);
        }
    }

    @Override
    public void buildSchemas() throws HumanTaskDeploymentException {
        for (File file : FileUtils.directoryEntriesInPath(humantaskDir, xsdFilter)) {
            InputStream is;
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                throw new HumanTaskDeploymentException("Error building humantask archive: " +
                        fileName, e);
            }
            schemasMap.put(file.getName(), is);
        }
    }

    @Override
    public HumanInteractionsDocument getHumanInteractionsDocument()
            throws HumanTaskDeploymentException {
        HumanInteractionsDocument hiDoc;
        try {
            hiDoc = HumanInteractionsDocument.Factory.parse(hiDefinition);
        } catch (Exception e) {
            String errMsg = "Error occurred while parsing the human interaction definition";
            System.out.println(errMsg);
            throw new HumanTaskDeploymentException(errMsg, e);
        }
        return hiDoc;
    }

    @Override
    public HTDeploymentConfigDocument getHTDeploymentConfigDocument()
            throws HumanTaskDeploymentException {
        HTDeploymentConfigDocument hiConf;
        try {
            hiConf = HTDeploymentConfigDocument.Factory.parse(hiConfiguration);
        } catch (Exception e) {
            String errMsg = "Error occurred while parsing the human interaction configuration " +
                    "file: htconfig.xml";
            System.out.println(errMsg);
            throw new HumanTaskDeploymentException(errMsg, e);
        }

        return hiConf;
    }

    @Override
    public String getArchiveName() {
        return fileName;
    }

    public List<Definition> getWsdlDefinitions() throws HumanTaskDeploymentException {
        return wsdlDefinitions;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    public String getMd5sum() {
        return this.md5sum;
    }

    public File getHumanTaskDefinitionFile() {
        return humanTaskDefinitionFile;
    }
}
