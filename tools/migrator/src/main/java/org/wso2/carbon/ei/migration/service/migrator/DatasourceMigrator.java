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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.MigrationClientException;
import org.wso2.carbon.ei.migration.internal.MigrationServiceDataHolder;
import org.wso2.carbon.ei.migration.service.Migrator;
import org.wso2.carbon.ei.migration.service.dao.DataSourceDAO;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;
import org.wso2.carbon.ndatasource.common.DataSourceException;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.Tenant;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Password transformation class for DataSource.
 */
public class DatasourceMigrator extends Migrator {
    private static final Log log = LogFactory.getLog(DatasourceMigrator.class);

    @Override
    public void migrate() {
        transformPasswordInRegistryDatasources();
    }

    /**
     * This method will transform the data source password encrypted with old encryption algorithm to new encryption
     * algorithm.
     */
    private void transformPasswordInRegistryDatasources() {
        log.info(Constant.MIGRATION_LOG + "Password transformation starting on DataSource.");
        //In tenants
        Tenant[] tenants;
        try {
            tenants = MigrationServiceDataHolder.getRealmService().getTenantManager().getAllTenants();
            for (Tenant tenant : tenants) {
                int tenantId = tenant.getId();
                List<Resource> dataSources = DataSourceDAO.getInstance().getAllDataSources(tenantId);
                this.updatePasswordInRegistryDataSources(tenantId, dataSources);
            }
        } catch (Exception e) {
            log.error("Error while updating secondary data source password for tenant", e);
        }
        //In super tenant
        try {
            List<Resource> dataSources = DataSourceDAO.getInstance().getAllDataSources(Constant.SUPER_TENANT_ID);
            this.updatePasswordInRegistryDataSources(Constant.SUPER_TENANT_ID, dataSources);
        } catch (Exception e) {
            log.error("Error while updating secondary data source password for super tenant", e);
        }
    }

    /**
     * Migrate the password in datasource configuration
     *
     * @param tenantId
     * @param dataSources
     * @throws MigrationClientException
     */
    private void updatePasswordInRegistryDataSources(int tenantId, List<Resource> dataSources)
            throws MigrationClientException {

        for (Resource dataSource : dataSources) {
            try {
                InputStream contentStream = dataSource.getContentStream();
                OMElement omElement = Utility.toOM(contentStream);
                Iterator pit = ((OMElement) ((OMElement) omElement.getChildrenWithName(Constant.DEFINITION_Q).next())
                        .getChildrenWithName(Constant.CONFIGURATION_Q).next()).getChildrenWithName(Constant.PASSWORD_Q);
                while (pit.hasNext()) {
                    OMElement passwordElement = (OMElement) pit.next();
                    if ("true".equals(passwordElement.getAttributeValue(Constant.ENCRYPTED_Q))) {
                        String password = passwordElement.getText();
                        String newEncryptedPassword = Utility.getNewEncryptedValue(password);
                        if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                            passwordElement.setText(newEncryptedPassword);
                            dataSource.setContent(omElement.toString().getBytes());
                            DataSourceDAO.saveDataSource(tenantId, dataSource);
                        }
                    }
                }
            } catch (XMLStreamException | CryptoException | RegistryException | DataSourceException e) {
                throw new MigrationClientException(e.getMessage());
            }
        }
    }
}
