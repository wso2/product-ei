/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.application.deployer;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.osgi.framework.Bundle;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.application.deployer.config.Artifact;
import org.wso2.carbon.application.deployer.config.CappFile;
import org.wso2.carbon.application.deployer.config.RegistryConfig;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.feature.mgt.core.util.ProvisioningUtils;
import org.wso2.carbon.micro.integrator.core.internal.ApplicationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.xml.namespace.QName;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class AppDeployerUtils {
	
	private static final Log log = LogFactory.getLog(AppDeployerUtils.class);
	
	private static final AppDeployerUtils INSTANCE = new AppDeployerUtils();
	
	private static String APP_UNZIP_DIR;
	private static final String INTERNAL_ARTIFACTS_DIR = "internal-artifacts";
	private static volatile boolean isAppDirCreated = false;
	
	private AppDeployerUtils() {
		// hide utility class
		
		
	}

    static {
        String javaTmpDir = System.getProperty("java.io.tmpdir");
        APP_UNZIP_DIR = javaTmpDir.endsWith(File.separator) ? javaTmpDir + AppDeployerConstants.CARBON_APPS :
                        javaTmpDir + File.separator + AppDeployerConstants.CARBON_APPS;
    }

    public static String getAppUnzipDir() {
        return APP_UNZIP_DIR;
    }

    private static void createAppDirectory(){
		//cApps should be temporarily uploaded to worker directory,
    	//then house keeping task will delete after timeout
		if(isAppDirCreated){
			return;
		}

        createDir(getAppUnzipDir());

        File doNotDeleteNote = new File(getAppUnzipDir(), "DO-NOT-DELETE.txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(doNotDeleteNote);
            writer.println("Do not delete this folder if the Carbon server is running! Otherwise, " +
                           "it might cause issues for artifacts that come from CApps.");
        } catch (FileNotFoundException e) {
            log.error("Error while writing a file to the CApp extraction folder: " + doNotDeleteNote, e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        isAppDirCreated = true;
		
	}


    /**
     * Check whether the given bundle is a project artifact. If yes, return the app name. If no,
     * return null
     *
     * @param bundle - bundle to check
     * @return - app name
     */
    public static String getProjectArtifactName(Bundle bundle) {
        Dictionary dictionary = bundle.getHeaders();
        if (dictionary != null) {

            // Iterate through the headers and find the WSO2-Project-Artifact custom header
            for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {

                String headerKey = (String) e.nextElement();
                if (AppDeployerConstants.WSO2_APP_NAME_HEADER.equals(headerKey)) {

                    // retireve the header value
                    String headerValue = (String) dictionary.get(headerKey);
                    if (headerValue != null) {
                        return headerValue;
                    }
                }
            }
        }
        return null;
    }

    public static String getParentAppName(Bundle bundle) {
        Dictionary dictionary = bundle.getHeaders();
        if (dictionary != null) {

            // Iterate through the headers and find the ParentApplication custom header
            for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {

                String headerKey = (String) e.nextElement();
                if (AppDeployerConstants.PARENT_APP_HEADER.equals(headerKey)) {
                    // retireve the header value
                    String headerValue = (String) dictionary.get(headerKey);
                    if (headerValue != null) {
                        return headerValue;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checking whether a bundle contains the WSO2-Application-Deployer header
     *
     * @param bundle - input bundle
     * @return - if found header - true, else - false
     */
    public static boolean isAppDeployer(Bundle bundle) {
        Dictionary dictionary = bundle.getHeaders();
        if (dictionary != null) {

            // Iterate through the headers and find the WSO2-Project-Artifact custom header
            for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {

                String headerKey = (String) e.nextElement();
                if (AppDeployerConstants.WSO2_APP_DEPLOYER_HEADER.equals(headerKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the carbon repository location
     *
     * @return - repo path
     */
    public static String getAxis2Repo() {
        String axis2Repo = CarbonUtils.getAxis2Repo();
        if (axis2Repo == null) {
            axis2Repo = CarbonUtils.getCarbonRepository();
        }
        return axis2Repo;
    }

    /**
     * Copy the artifact file at the fromPath to toPath
     *
     * @param fromPath - path at which the read the file to copy
     * @param toPath   - path to which the file should be copied
     */
    public static void copyFile(String fromPath, String toPath) throws DeploymentException{
        File in = new File(fromPath);
        if (!in.exists()) {
            log.error("Artifact file not found at : " + fromPath);
            throw new DeploymentException("Artifact file not found at : " + fromPath);
        }

        File out = new File(toPath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(in);
            fos = new FileOutputStream(out);
            byte[] buf = new byte[10240];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            log.error("Error occured while copying artifact : " + fromPath, e);
        } finally {
            try {
                if (fis != null) {
                	fis.close();
                }                
            } catch (IOException e) {
                log.error("Error occured while closing the streams", e);
            }
            
            try {                
                if (fos != null) {
                	fos.close();
                }
            } catch (IOException e) {
                log.error("Error occured while closing the streams", e);
            }
        }
    }

    /**
     * Builds the Artifact object when an artifact element is given
     *
     * @param artifactEle - artifact OMElement
     * @return created Artifact object
     */
    public static Artifact populateArtifact(OMElement artifactEle) {
        if (artifactEle == null) {
            return null;
        }

        Artifact artifact = new Artifact();
        // read top level attributes
        artifact.setName(readAttribute(artifactEle, Artifact.NAME));
        artifact.setVersion(readAttribute(artifactEle, Artifact.VERSION));
        artifact.setType(readAttribute(artifactEle, Artifact.TYPE));
        artifact.setServerRole(readAttribute(artifactEle, Artifact.SERVER_ROLE));

        // read the dependencies
        Iterator itr = artifactEle.getChildrenWithLocalName(Artifact.DEPENDENCY);
        while (itr.hasNext()) {
            OMElement depElement = (OMElement) itr.next();
            // create an artifact for each dependency and add to the root artifact
            Artifact.Dependency dep = new Artifact.Dependency();
            dep.setServerRole(readAttribute(depElement, Artifact.SERVER_ROLE));
            dep.setName(readAttribute(depElement, Artifact.ARTIFACT));
            dep.setVersion(readAttribute(depElement, Artifact.VERSION));
            artifact.addDependency(dep);
        }

        // read the subArtifacts
        OMElement subArtifactsElement = artifactEle
                .getFirstChildWithName(new QName(Artifact.SUB_ARTIFACTS));
        if (subArtifactsElement != null) {
            Iterator subArtItr = subArtifactsElement.getChildrenWithLocalName(Artifact.ARTIFACT);
            while (subArtItr.hasNext()) {
                // as this is also an artifact, use recursion
                Artifact subArtifact = populateArtifact((OMElement) subArtItr.next());
                artifact.addSubArtifact(subArtifact);
            }
        }

        // read the files
        Iterator fileItr = artifactEle.getChildrenWithLocalName(Artifact.FILE);
        while (fileItr.hasNext()) {
            OMElement fileElement = (OMElement) fileItr.next();
            CappFile tempFile = new CappFile();
            tempFile.setName(fileElement.getText());
            tempFile.setVersion(readAttribute(fileElement, Artifact.VERSION));
            artifact.addFile(tempFile);            
        }

        return artifact;
    }

    /**
     * Builds a RegistryConfig instance using an OMElement which is built using the registry
     * config file inside and cApp Registry/Resource artifact
     *
     * @param resourcesElement - regConfig element
     * @return - RegistryConfig instance built
     */
    public static RegistryConfig populateRegistryConfig(OMElement resourcesElement) {
        if (resourcesElement == null) {
            return null;
        }

        RegistryConfig regConfig = new RegistryConfig();

        // read Item elements under Resources
        Iterator itemItr = resourcesElement.getChildrenWithLocalName(RegistryConfig.ITEM);
        while (itemItr.hasNext()) {
            OMElement itemElement = (OMElement) itemItr.next();
            regConfig.addResource(readChildText(itemElement, RegistryConfig.PATH),
                    readChildText(itemElement, RegistryConfig.FILE),
                    readChildText(itemElement, RegistryConfig.REGISTRY_TYPE),
                    readChildText(itemElement, RegistryConfig.MEDIA_TYPE));
        }

        // read Item elements under Resources
        Iterator dumpItr = resourcesElement.getChildrenWithLocalName(RegistryConfig.DUMP);
        while (dumpItr.hasNext()) {
            OMElement itemElement = (OMElement) dumpItr.next();
            regConfig.addDump(readChildText(itemElement, RegistryConfig.PATH),
                    readChildText(itemElement, RegistryConfig.FILE),
                    readChildText(itemElement, RegistryConfig.REGISTRY_TYPE));
        }

        // read Collection elements under Resources
        Iterator collectionItr = resourcesElement.getChildrenWithLocalName(RegistryConfig.COLLECTION);
        while (collectionItr.hasNext()) {
            OMElement collElement = (OMElement) collectionItr.next();
            regConfig.addCollection(readChildText(collElement, RegistryConfig.PATH),
                    readChildText(collElement, RegistryConfig.DIRECTORY),
                    readChildText(collElement, RegistryConfig.REGISTRY_TYPE));
        }

        // read Association elements under Resources
        Iterator associationItr = resourcesElement.getChildrenWithLocalName(RegistryConfig.ASSOCIATION);
        while (associationItr.hasNext()) {
            OMElement assoElement = (OMElement) associationItr.next();
            regConfig.addAssociation(readChildText(assoElement, RegistryConfig.SOURCE_PATH),
                    readChildText(assoElement, RegistryConfig.TARGET_PATH),
                    readChildText(assoElement, RegistryConfig.TYPE),
                    readChildText(assoElement, RegistryConfig.REGISTRY_TYPE));
        }
        return regConfig;
    }

    /**
     * Reads an attribute in the given element and returns the value of that attribute
     *
     * @param element - Element to search
     * @param attName - attribute name
     * @return if the attribute found, return value. else null.
     */
    public static String readAttribute(OMElement element, String attName) {
        if (element == null) {
            return null;
        }
        OMAttribute temp = element.getAttribute(new QName(attName));
        if (temp != null) {
            return temp.getAttributeValue();
        }
        return null;
    }

    public static String readChildText(OMElement element, String ln) {
        return readChildText(element, ln, null);
    }

    /**
     * Reads a text node which is in a child element of the given element and returns the text
     * value.
     *
     * @param element - Element to search
     * @param ln - Child element name
     * @param ns - Child element namespace
     * @return if the child text element found, return text value. else null.
     */
    public static String readChildText(OMElement element, String ln, String ns) {
        if (element == null) {
            return null;
        }
        OMElement temp = element.getFirstChildWithName(new QName(ns, ln));
        if (temp != null) {
            return temp.getText();
        }
        return null;
    }

    /**
     * First checks for the "serverRoles" system property. If null, reads the ServerRoles property
     * from carbon.xml.
     *
     * @return server roles array
     */
    public static String[] readServerRoles() {
        String[] serverRoles;
        // read the system property
        String temp = System.getProperty(AppDeployerConstants.SERVER_ROLES_CMD_OPTION);
        if (temp != null) {
            serverRoles = temp.split(",");
        } else {
            // now try to read from carbon.xml
            ServerConfiguration serverConfig = ServerConfiguration.getInstance();
            serverRoles = serverConfig.getProperties(AppDeployerConstants.CARBON_SERVER_ROLE);
        }
        return serverRoles;
    }

    /**
     * Computes the application artifact file path when the bundle is given
     *
     * @param b - App artifact as an OSGi bundle
     * @return - App file path
     */
    public static String getArchivePathFromBundle(Bundle b) {
        //compute app file path
        String bundlePath = b.getLocation();
        bundlePath = formatPath(bundlePath);
        return CarbonUtils.getComponentsRepo() + File.separator +
                bundlePath.substring(bundlePath.lastIndexOf('/') + 1);
    }

    /**
     * Finds repo/carbonapps path
     *
     * @return - path
     */
    public static String getApplicationLocation() {
        return getAxis2Repo() + File.separator + AppDeployerConstants.CARBON_APPS;
    }

    /**
     * Finds teh extension of a given file
     *
     * @param fileName - name of the file
     * @return - extension
     */
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return fileName.substring(index + 1);
    }

    /**
     * Extract the Carbon application at the provided path to the java temp dir. Return the
     * extracted location
     *
     * @param appCarPath - Absolute path of the Carbon application .car file
     * @return - extracted location
     * @throws org.wso2.carbon.CarbonException - error on extraction
     */
    public static String extractCarbonApp(String appCarPath) throws CarbonException {
        createAppDirectory();

        //append tenant id to the capp extraction path
        String tenantId = AppDeployerUtils.getTenantIdString();
        String appCarPathFormatted = formatPath(appCarPath);
        String fileName = appCarPathFormatted.substring(appCarPathFormatted.lastIndexOf('/') + 1);
        String dest = getAppUnzipDir() + File.separator + tenantId + File.separator +
                      System.currentTimeMillis() + fileName + File.separator;
        createDir(dest);

        try {
            extract(appCarPath, dest);
        } catch (IOException e) {
            throw new CarbonException("Error while extracting Carbon Application : " + fileName, e);
        }
        return dest;
    }

    public static String createAppExtractionPath(String parentAppName) {
    	createAppDirectory();
        String tenantId = AppDeployerUtils.getTenantIdString();
        String parentPath = getAppUnzipDir() + File.separator + tenantId + File.separator +
                            System.currentTimeMillis() + parentAppName + File.separator;
        createDir(parentPath);
        return parentPath;
    }

    /**
     * Extract an individual cApp artifact at the provided path to the java temp dir. Return the
     * extracted location
     *
     * @param artifactPath - Absolute path of the cApp artifact file
     * @param parentPath - Parent's extracted path
     * @return - extracted location
     * @throws org.wso2.carbon.CarbonException - on error
     */
    public static String extractAppArtifact(String artifactPath,
                                            String parentPath) throws CarbonException {

        createDir(parentPath + INTERNAL_ARTIFACTS_DIR);

        String fileName = artifactPath.substring(artifactPath.lastIndexOf('/') + 1);
        String dest = parentPath + INTERNAL_ARTIFACTS_DIR + File.separator +
                fileName.substring(0, fileName.lastIndexOf('.')) + File.separator;
        createDir(dest);

        try {
            extract(artifactPath, dest);
        } catch (IOException e) {
            throw new CarbonException("Error while extracting cApp artifact : " + fileName, e);
        }
        return dest;
    }

    /**
     * Finds the owner application of the provided artifact file name and sets the runtime
     * object name in the corresponding artifact.
     *
     * @param fileName     - file name of the artifact
     * @param artifactType - this can be a module or a service
     * @param runtimeObjectName - name of the runtime object corresponding to this file
     * @param tenantId - id of the tenant in which the artifact is deployed
     */
    public static void attachArtifactToOwnerApp(String fileName,
                                                String artifactType,
                                                String runtimeObjectName,
                                                int tenantId) {
        if (fileName == null || artifactType == null || 
        		tenantId == MultitenantConstants.INVALID_TENANT_ID) {
            return;
        }
        ApplicationManager appManager = ApplicationManager.getInstance();
        Artifact appArtifact;
        for (CarbonApplication carbonApp : appManager.getCarbonApps(String.valueOf(tenantId))) {
            appArtifact = carbonApp.getAppConfig().getApplicationArtifact();
            for (Artifact.Dependency dep : appArtifact.getDependencies()) {
                if (dep.getArtifact() != null) {
                    Artifact depArtifact = dep.getArtifact();
                    for (CappFile file : depArtifact.getFiles()) {
                        if (file.getName().equals(fileName) &&
                                depArtifact.getType().equals(artifactType)) {
                            depArtifact.setRuntimeObjectName(runtimeObjectName);
                        }
                    }
                }
            }
        }
    }

    /**
     * Format the string paths to match any platform.. windows, linux etc..
     *
     * @param path - input file path
     * @return formatted file path
     */
    public static String formatPath(String path) {
        // removing white spaces
        String pathformatted = path.replaceAll("\\b\\s+\\b", "%20");
        try {
                pathformatted = java.net.URLDecoder.decode(pathformatted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding in the path :"+ pathformatted);
        }
        // replacing all "\" with "/"
        return pathformatted.replace('\\', '/');
    }

    /**
     * Checks whether the given dependencies has library type artifacts
     * 
     * @param deps - list of dependencies
     * @return - true if found..
     */
    public static boolean hasLibs(List<Artifact.Dependency> deps) {
        for (Artifact.Dependency dep : deps) {
            Artifact artifact = dep.getArtifact();
            if (artifact != null) {
                if (artifact.getType().startsWith("lib/")) {
                    return true;
                } else {
                    if(hasLibs(artifact.getDependencies())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the given list of features are already installed in the systme
     *
     * @param features - list of features
     * @return - true if all are installed, else false
     */
    public static boolean areAllFeaturesInstalled(List<Feature> features) {
        for (Feature feature : features) {

        IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(feature.getId(), feature.getVersionRange());
        IInstallableUnit[] installableUnits = ProvisioningUtils.getProfile().query(query, null).toArray(IInstallableUnit.class);
            if (installableUnits == null || installableUnits.length == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * For each artifact type, there's a set of features which are needed to be installed
     * in the system to properly deploy the artifacts. If all those features don't exist
     * in the system, artifacts of that type can't be accepted for deployment.
     * This method builds the acceptance list by checking whether the needed features are already
     * installed or not.
     * 
     * @param features - contains the list of features needed for each artifact type
     * @return whether each artifact type can be deployed or not
     */
    public static Map<String, Boolean> buildAcceptanceList(Map<String, 
            List<Feature>> features) {
        HashMap<String, Boolean> acceptanceList = new HashMap<String, Boolean>();

        for (Map.Entry<String, List<Feature>> entry : features.entrySet()) {
            if (entry.getValue() != null) {
                acceptanceList.put(entry.getKey(),
                        AppDeployerUtils.areAllFeaturesInstalled(entry.getValue()));
            }
        }
        return acceptanceList;
    }

    /**
     * Reads the root element of the required-features.xml file and returns a list of required
     * features for each and every artifact type.
     *
     * @param featureSets - root element of the required-features.xml
     * @return - map which includes the list of features for each artifact type
     */
    public static Map<String, List<Feature>> readRequiredFeaturs(OMElement featureSets) {
        if (featureSets == null) {
            return null;
        }
        HashMap<String, List<Feature>> reqFeatureMap = new HashMap<String, List<Feature>>();

        // read featureSet elements
        Iterator itr = featureSets.getChildrenWithLocalName(AppDeployerConstants.FEATURE_SET);
        while (itr.hasNext()) {
            OMElement fsElement = (OMElement) itr.next();
            String artifactType = readAttribute(fsElement, AppDeployerConstants.ARTIFACT_TYPE);

            // read feature elements
            Iterator featureItr = fsElement.getChildrenWithLocalName(AppDeployerConstants.FEATURE);
            List<Feature> featureList = new ArrayList<Feature>();
            while (featureItr.hasNext()) {
                OMElement featureElement = (OMElement) featureItr.next();
                Feature requiredFeature = new Feature();
                requiredFeature.setId(readAttribute(featureElement, Feature.ID));
                requiredFeature.setVersionRange(readAttribute(featureElement, Feature.VERSION));
                featureList.add(requiredFeature);
            }
            reqFeatureMap.put(artifactType, featureList);
        }
        return reqFeatureMap;
    }

    @Deprecated
    public static String getTenantIdString(AxisConfiguration axisConfig) {
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return String.valueOf(carbonContext.getTenantId());
    }

    public static String getTenantIdString() {
        // multi-tenancy is not supported in micro integrator. hence return super tenant ID
        return AppDeployerConstants.SUPER_TENANT_ID_STR;
    }

    @Deprecated
    public static int getTenantId(AxisConfiguration axisConfig) {
        // multi-tenancy is not supported in micro integrator. hence return super tenant ID
        return AppDeployerConstants.SUPER_TENANT_ID_INT;
    }

    public static int getTenantId() {
        // multi-tenancy is not supported in micro integrator. hence return super tenant ID
        return AppDeployerConstants.SUPER_TENANT_ID_INT;
    }

    public static String getTenantIdLogString(int tenantId) {
        // multi-tenancy is not supported in micro integrator. hence return super tenant log string
        return "{super-tenant}";
    }

    public static String computeResourcePath(String basePath, String resourceName) {
        String fullResourcePath;
        if (basePath.endsWith("/")) {
            fullResourcePath = basePath + resourceName;
        } else {
            fullResourcePath = basePath + "/" + resourceName;
        }
        return fullResourcePath;
    }

    private static void extract(String sourcePath, String destPath) throws IOException {
        Enumeration entries;
        ZipFile zipFile;

        zipFile = new ZipFile(sourcePath);
        entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // we don't need to copy the META-INF dir
            if (entry.getName().startsWith("META-INF/")) {
                continue;
            }
            // if the entry is a directory, create a new dir
            if (entry.isDirectory()) {
                createDir(destPath + entry.getName());
                continue;
            }
            // if the entry is a file, write the file
            copyInputStream(zipFile.getInputStream(entry),
                            new BufferedOutputStream(new FileOutputStream(destPath + entry.getName())));
        }
        zipFile.close();
    }

    /**
     * Read the mediaType from the meta file of a resource. Currently we read only the mediaType
     * from this file. If we need more info from this file in the future, create a Config instance
     * for the meta file as well.
     *
     * @param artifactExtractedPath - extracted path of the artifact
     * @param fileName - original resource file name. this is used to figure out meta file name
     * @return - mediaType if the file found. else null..
     */
    public static String readMediaType(String artifactExtractedPath, String fileName) {
        if (artifactExtractedPath == null || fileName == null) {
            return null;
        }
        String mediaType = null;
        String metaFilePath = artifactExtractedPath + File.separator +
                AppDeployerConstants.RESOURCES_DIR + File.separator +
                AppDeployerConstants.META_DIR + File.separator +
                AppDeployerConstants.META_FILE_PREFIX + fileName +
                AppDeployerConstants.META_FILE_POSTFIX;
        File metaFile = new File(metaFilePath);
        if (metaFile.exists()) {
            try (FileInputStream fis = new FileInputStream(metaFile)) {
                OMElement docElement = new StAXOMBuilder(fis).getDocumentElement();
                OMElement mediaTypeElement = docElement
                        .getFirstChildWithName(new QName(AppDeployerConstants.META_MEDIA_TYPE));
                if (mediaTypeElement != null) {
                    mediaType = mediaTypeElement.getText();
                }
            } catch (Exception e) {
                log.error("Error while reading meta file : " + metaFilePath, e);
            }
        }
        return mediaType;
    }

    public static void createDir(String path) {
        File temp = new File(path);
        if (!temp.exists() && !temp.mkdirs()) {
            log.error("Error while creating directory : " + path);
            return;
        }

    }

    private static void copyInputStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[40960];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * Finds the correct deployer for the given directory and extension
     *
     * @param axisConfig - AxisConfiguration instance
     * @param directory - Directory to retrieve the deployer
     * @param extension - Extension of the deployable artifact
     * @return Deployer instance
     *
     */
    public static Deployer getArtifactDeployer(AxisConfiguration axisConfig, String directory,
                                               String extension) {
        // access the deployment engine through axis config
        DeploymentEngine deploymentEngine = (DeploymentEngine) axisConfig.getConfigurator();
        return deploymentEngine.getDeployer(directory, extension);
    }

}
