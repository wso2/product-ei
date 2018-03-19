/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.ei.migration.service.migrator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.internal.MigrationServiceDataHolder;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;
import org.wso2.carbon.user.api.Tenant;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Password transformation class for User Store.
 */
public class UserStorePasswordMigrator extends Migrator {

    private static final Log log = LogFactory.getLog(UserStorePasswordMigrator.class);

    @Override
    public void migrate() {
        log.info(Constant.MIGRATION_LOG + "Migration starting on Secondary User Stores");
        updateSuperTenantConfigs();
        updateTenantConfigs();
    }

    /**
     * Update the user store configurations in tenants
     */
    private void updateTenantConfigs() {
        Tenant[] tenants;
        try {
            tenants = MigrationServiceDataHolder.getRealmService().getTenantManager().getAllTenants();
            boolean isIgnoreForInactiveTenants =
                    Boolean.parseBoolean(System.getProperty(Constant.IGNORE_INACTIVE_TENANTS));
            for (Tenant tenant : tenants) {
                if (isIgnoreForInactiveTenants && !tenant.isActive()) {
                    log.info("Tenant " + tenant.getDomain() + " is inactive. Skipping secondary userstore migration!");
                    continue;
                }
                File[] userstoreConfigs = getUserStoreConfigFiles(tenant.getId());
                for (File file : userstoreConfigs) {
                    if (file.isFile()) {
                        updatePassword(file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while updating secondary user store password for tenant", e);
        }
    }

    /**
     * Update the user store configurations in super tenant
     */
    private void updateSuperTenantConfigs() {
        try {
            File[] userstoreConfigs = getUserStoreConfigFiles(Constant.SUPER_TENANT_ID);
            for (File file : userstoreConfigs) {
                if (file.isFile()) {
                    updatePassword(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("Error while updating secondary user store password for super tenant", e);
        }
    }

    /**
     * Get all user store config files
     *
     * @param tenantId
     * @return
     * @throws FileNotFoundException
     */
    private File[] getUserStoreConfigFiles(int tenantId) throws FileNotFoundException {
        String carbonHome = System.getProperty(Constant.CARBON_HOME);
        String userStorePath;
        if (tenantId == Constant.SUPER_TENANT_ID) {
            userStorePath = Paths.get(carbonHome,
                    new String[]{"repository", "deployment", "server", "userstores"}).toString();
        } else {
            userStorePath = Paths.get(carbonHome,
                    new String[]{"repository", "tenants", String.valueOf(tenantId), "userstores"}).toString();
        }
        File[] files = new File(userStorePath).listFiles();
        return files != null ? files : new File[0];
    }

    /**
     * Encrypt by new algorithm and update the password in the provided file
     *
     * @param filePath
     * @throws FileNotFoundException
     * @throws CryptoException
     */
    private void updatePassword(String filePath) throws FileNotFoundException, CryptoException {
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
                if (Constant.PASSWORD.equals(element.getAttributeValue(Constant.NAME_Q)) ||
                        Constant.CONNECTION_PASSWORD.equals(element.getAttributeValue(Constant.NAME_Q))) {
                    String encryptedPassword = element.getText();
                    newEncryptedPassword = Utility.getNewEncryptedValue(encryptedPassword);
                    if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                        element.setText(newEncryptedPassword);
                    }
                }
            }

            if (newEncryptedPassword != null) {
                OutputStream outputStream = new FileOutputStream(filePath);
                documentElement.serialize(outputStream);
            }
        } catch (XMLStreamException ex) {
            log.error("Error while updating password for: " + filePath);
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
                if (stream != null) {
                    try {
                        stream.close();
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
