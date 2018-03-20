/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.ei.migration.service.migrator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Password transformation class for Event Publisher and Receiver.
 */
public class InputOutputDataMigration extends Migrator {
    private static final Log log = LogFactory.getLog(InputOutputDataMigration.class);

    private static InputOutputDataMigration instance = new InputOutputDataMigration();

    public static InputOutputDataMigration getInstance() {
        return instance;
    }

    @Override
    public void migrate() {
        log.info(Constant.MIGRATION_LOG + "Password transformation starting on Event Publisher and Receiver.");

        String carbonPath = System.getProperty(Constant.CARBON_HOME);
        migratePublishers(carbonPath);
        migrateReceivers(carbonPath);
    }

    private static File readFiles(String path) {
        return new File(path);
    }

    private static void migratePublishers(String carbonHome) {
        File publisherPath = readFiles(carbonHome + Constant.EVENT_PUBLISHER_PATH);
        try {
            migrateData(publisherPath);
            log.info("Migrating publishers was successful");
        } catch (MigrationClientException e) {
            log.error("Error while migrating publishers: " + e.getMessage());
        }
    }

    private static void migrateReceivers(String carbonHome) {
        File receiverPath = readFiles(carbonHome + Constant.EVENT_RECIEVER_PATH);
        try {
            migrateData(receiverPath);
            log.info("Migrating receivers was successful");
        } catch (MigrationClientException e) {
            log.error("Error while migrating receivers : " + e);
        }
    }

    /**
     * Migrate password in event publisher and receiver which is found in the provided path
     *
     * @param folder
     * @throws MigrationClientException
     */
    private static void migrateData(File folder) throws MigrationClientException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        XMLStreamReader parser;
        FileInputStream stream;
        try {
            File[] configs = folder.listFiles();
            if (configs != null) {
                for (File fileEntry : configs) {
                    if (fileEntry.isFile() && fileEntry.getName().toLowerCase().endsWith(".xml")) {
                        stream = new FileInputStream(fileEntry);
                        parser = XMLInputFactory.newInstance().createXMLStreamReader(stream);
                        StAXOMBuilder builder = new StAXOMBuilder(parser);
                        OMElement documentElement = builder.getDocumentElement();
                        Iterator it = ((OMElement) documentElement
                                .getChildrenWithName(Constant.TO_Q).next()).getChildElements();
                        String newEncryptedPassword = null;
                        while (it.hasNext()) {
                            OMElement element = (OMElement) it.next();
                            if ("true".equals(element.getAttributeValue(Constant.ENCRYPTED_Q))) {
                                String password = element.getText();
                                newEncryptedPassword = Utility.getNewEncryptedValue(password);
                                if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                                    element.setText(newEncryptedPassword);
                                }
                            }
                        }

                        if (newEncryptedPassword != null) {
                            OutputStream outputStream =
                                    new FileOutputStream(new File(fileEntry.getAbsolutePath()).getPath());
                            documentElement.serialize(outputStream);
                        }
                    }
                }
            }
        } catch (IOException | CryptoException | XMLStreamException e) {
            throw new MigrationClientException(e.getMessage());
        }
    }
}
