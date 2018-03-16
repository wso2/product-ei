package org.wso2.carbon.ei.migration.service.dao;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.service.migrator.ServerProfileMigrator;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Iterator;

public class ServerProfileDAO {
    public boolean isModified = false;
    private static final Log log = LogFactory.getLog(ServerProfileDAO.class);
    private static ServerProfileDAO instance = new ServerProfileDAO();

    private ServerProfileDAO() {

    }

    public static ServerProfileDAO getInstance() {

        return instance;
    }
    public void modifyInsideExtractedFolder(String path) throws MigrationClientException {
        isModified = false;
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    transformSPPassword(file.getAbsolutePath());
                }
            }
        }
    }

    public void transformSPPassword(String filePath) throws MigrationClientException {
        XMLStreamReader parser = null;
        FileInputStream stream = null;
        try {
            log.info("Migrating password in: " + filePath);
            stream = new FileInputStream(filePath);
            parser = XMLInputFactory.newInstance().createXMLStreamReader(stream);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement documentElement = builder.getDocumentElement();

            Iterator it = documentElement.getChildElements();
            String newEncryptedPassword = null;
            while (it.hasNext()) {
                OMElement element = (OMElement) it.next();
                if ("true".equals(element.getAttributeValue(Constant.SECURE_PASSWORD_Q))) {
                    String password = element.getAttributeValue(Constant.PASSWORD_Q);
                    newEncryptedPassword = Utility.getNewEncryptedValue(password);
                    if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                        element.getAttribute(Constant.PASSWORD_Q).setAttributeValue(newEncryptedPassword);
                    }
                }
            }

            if (newEncryptedPassword != null) {
                OutputStream outputStream = new FileOutputStream(new File(filePath));
                documentElement.serialize(outputStream);
                isModified = true;
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            new MigrationClientException("Error while writing the file: " + e);
        } catch (CryptoException e) {
            e.printStackTrace();
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
                if (stream != null) {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        log.error("Error occurred while closing Input stream", e);
                    }
                }
            } catch (XMLStreamException ex) {
                log.error("Error while closing XML stream", ex);
            }
        }
    }
}
