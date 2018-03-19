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
package org.wso2.carbon.ei.migration.service;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.ei.migration.internal.MigrationServiceDataHolder;
import org.wso2.carbon.ei.migration.util.Constant;
import org.wso2.carbon.ei.migration.util.Utility;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
import static org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_ID;

public class RegistryDataManager {

    private static final Log log = LogFactory.getLog(RegistryDataManager.class);

    private static RegistryDataManager instance = new RegistryDataManager();

    private RegistryDataManager() {
    }

    public static RegistryDataManager getInstance() {
        return instance;
    }

    private void startTenantFlow(Tenant tenant) {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantId(tenant.getId());
        carbonContext.setTenantDomain(tenant.getDomain());
    }

    /**
     * Method to migrate encrypted password of key stores
     *
     * @param migrateActiveTenantsOnly
     */
    public void migrateKeyStorePassword(boolean migrateActiveTenantsOnly) {
        try {
            //migrating super tenant configurations
            migrateKeyStorePasswordForTenant(SUPER_TENANT_ID);
            log.info("Keystore passwords migrated for tenant : " + SUPER_TENANT_DOMAIN_NAME);

            //migrating tenant configurations
            Tenant[] tenants = MigrationServiceDataHolder.getRealmService().getTenantManager().getAllTenants();
            for (Tenant tenant : tenants) {
                if (migrateActiveTenantsOnly && !tenant.isActive()) {
                    log.info("Tenant " + tenant.getDomain() + " is inactive. Skipping Subscriber migration!");
                    continue;
                }
                startTenantFlow(tenant);
                migrateKeyStorePasswordForTenant(tenant.getId());
                log.info("Keystore passwords migrated for tenant : " + tenant.getDomain());
            }
        } catch (RegistryException | CryptoException e) {
            log.error("Error while migrating keystore passwords for tenant");
        } catch (UserStoreException e) {
            log.error("Error while getting tenants");
        }
    }

    /**
     * Method to migrate encrypted password of SYSLOG_PROPERTIES registry resource
     *
     * @param migrateActiveTenantsOnly
     * @throws UserStoreException user store exception
     */
    public void migrateSysLogPropertyPassword(boolean migrateActiveTenantsOnly)
            throws UserStoreException, RegistryException, CryptoException {
        try {
            //migrating super tenant configurations
            migrateSysLogPropertyPasswordForTenant(SUPER_TENANT_ID);
            log.info("Sys log property password migrated for tenant : " + SUPER_TENANT_DOMAIN_NAME);
        } catch (Exception e) {
            log.error("Error while migrating Sys log property password for tenant : " + SUPER_TENANT_DOMAIN_NAME, e);
        }
        Tenant[] tenants = MigrationServiceDataHolder.getRealmService().getTenantManager().getAllTenants();
        for (Tenant tenant : tenants) {
            if (migrateActiveTenantsOnly && !tenant.isActive()) {
                log.info("Tenant " + tenant.getDomain() + " is inactive. Skipping SYSLOG_PROPERTIES file migration. ");
                continue;
            }
            try {
                startTenantFlow(tenant);
                migrateSysLogPropertyPasswordForTenant(tenant.getId());
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }

    /**
     * Migrate keystore password in super tenant and other tenants
     *
     * @param tenantId
     * @throws RegistryException
     * @throws CryptoException
     */
    private void migrateKeyStorePasswordForTenant(int tenantId) throws RegistryException, CryptoException {
        Registry registry = MigrationServiceDataHolder.getRegistryService().getGovernanceSystemRegistry(tenantId);
        if (registry.resourceExists(Constant.KEYSTORE_RESOURCE_PATH)) {
            Collection keyStoreCollection = (Collection) registry.get(Constant.KEYSTORE_RESOURCE_PATH);
            for (String keyStorePath : keyStoreCollection.getChildren()) {
                updateRegistryProperties(registry, keyStorePath,
                        new ArrayList<>(Arrays.asList(Constant.PASSWORD, Constant.PRIVATE_KEY_PASS)));
            }
        }
    }

    private void migrateSysLogPropertyPasswordForTenant(int tenantId) throws RegistryException, CryptoException {
        Registry registry = MigrationServiceDataHolder.getRegistryService().getConfigSystemRegistry(tenantId);
        updateRegistryProperties(registry, Constant.SYSLOG, new ArrayList<>(Arrays.asList(Constant.PASSWORD)));
    }

    /**
     * Method to migrate encrypted password of service principle registry resource
     *
     * @param migrateActiveTenantsOnly
     * @throws CryptoException
     * @throws RegistryException
     * @throws UserStoreException
     */
    public void migrateServicePrinciplePassword(boolean migrateActiveTenantsOnly) throws
            CryptoException, RegistryException, UserStoreException {

        //migrating super tenant configurations
        try {
            updateSecurityPolicyPassword(SUPER_TENANT_ID);
            log.info("Policy Subscribers migrated for tenant : " + SUPER_TENANT_DOMAIN_NAME);
        } catch (XMLStreamException e) {
            log.error("Error while migrating Policy Subscribers for tenant : " + SUPER_TENANT_DOMAIN_NAME, e);
        }

        //migrating tenant configurations
        Tenant[] tenants = MigrationServiceDataHolder.getRealmService().getTenantManager().getAllTenants();
        for (Tenant tenant : tenants) {
            if (migrateActiveTenantsOnly && !tenant.isActive()) {
                log.info("Tenant " + tenant.getDomain() + " is inactive. Skipping Service Principle Password migration!");
                continue;
            }
            try {
                startTenantFlow(tenant);
                updateSecurityPolicyPassword(tenant.getId());
                log.info("Service Principle Passwords migrated for tenant : " + tenant.getDomain());
            } catch (XMLStreamException e) {
                log.error("Error while migrating Service Principle Passwords for tenant : " + tenant.getDomain(), e);
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }

    /**
     * Encrypt the security policy password by new algorithm and update
     *
     * @param tenantId
     * @throws RegistryException
     * @throws CryptoException
     * @throws XMLStreamException
     */
    private void updateSecurityPolicyPassword(int tenantId) throws RegistryException, CryptoException,
            XMLStreamException {

        InputStream resourceContent = null;
        XMLStreamReader parser = null;

        try {
            Registry registry = MigrationServiceDataHolder.getRegistryService().getConfigSystemRegistry(tenantId);
            List<String> policyPaths = getSTSPolicyPaths(registry);
            String newEncryptedPassword = null;
            for (String resourcePath : policyPaths) {
                if (registry.resourceExists(resourcePath)) {
                    Resource resource = registry.get(resourcePath);
                    resourceContent = resource.getContentStream();
                    parser = XMLInputFactory.newInstance().createXMLStreamReader(resourceContent);
                    StAXOMBuilder builder = new StAXOMBuilder(parser);
                    OMElement documentElement = builder.getDocumentElement();
                    Iterator it = documentElement.getChildrenWithName(new QName(Constant.CARBON_SEC_CONFIG));

                    while (it != null && it.hasNext()) {
                        OMElement secConfig = (OMElement) it.next();
                        Iterator kerberosProperties = secConfig.getChildrenWithName(new QName(Constant.KERBEROS));
                        Iterator propertySet = null;
                        if ((kerberosProperties != null && kerberosProperties.hasNext())) {
                            propertySet = ((OMElement) kerberosProperties.next()).getChildElements();
                        }
                        if (propertySet != null) {
                            while (propertySet.hasNext()) {
                                OMElement kbProperty = (OMElement) propertySet.next();
                                if (Constant.SERVICE_PRINCIPAL_PASSWORD
                                        .equals(kbProperty.getAttributeValue(Constant.NAME_Q))) {
                                    String encryptedPassword = kbProperty.getText();
                                    newEncryptedPassword = Utility.getNewEncryptedValue(encryptedPassword);
                                    if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                                        kbProperty.setText(newEncryptedPassword);
                                    }
                                }
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(newEncryptedPassword)) {
                        resource.setContent(RegistryUtils.encodeString(documentElement.toString()));
                        registry.beginTransaction();
                        registry.put(resourcePath, resource);
                        registry.commitTransaction();
                    }
                }
            }
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
                if (resourceContent != null) {
                    try {
                        resourceContent.close();
                    } catch (IOException e) {
                        log.error("Error occurred while closing Input stream", e);
                    }
                }
            } catch (XMLStreamException ex) {
                log.error("Error while closing XML stream", ex);
            }
        }

    }

    /**
     * Encrypt the registry properties by new algorithm and update
     *
     * @param registry
     * @param resource
     * @param properties
     * @throws RegistryException
     * @throws CryptoException
     */
    private void updateRegistryProperties(Registry registry, String resource, List<String> properties)
            throws RegistryException, CryptoException {

        if (registry == null || StringUtils.isEmpty(resource) || CollectionUtils.isEmpty(properties)) {
            return;
        }

        if (registry.resourceExists(resource)) {
            try {
                registry.beginTransaction();
                Resource resourceObj = registry.get(resource);
                for (String encryptedPropertyName : properties) {
                    String oldValue = resourceObj.getProperty(encryptedPropertyName);
                    String newValue = Utility.getNewEncryptedValue(oldValue);
                    if (StringUtils.isNotEmpty(newValue)) {
                        resourceObj.setProperty(encryptedPropertyName, newValue);
                    }
                }
                registry.put(resource, resourceObj);
                registry.commitTransaction();
            } catch (RegistryException e) {
                registry.rollbackTransaction();
                log.error("Unable to update the registry resource", e);
                throw e;
            }
        }
    }

    /**
     *  Obtain the STS policy paths from registry
     *
     * @param registry
     * @return
     * @throws RegistryException
     */
    private List<String> getSTSPolicyPaths(Registry registry) throws RegistryException {

        List<String> policyPaths = new ArrayList<>();
        if (registry.resourceExists(Constant.SERVICE_GROUPS_PATH)) {
            Collection serviceGroups = (Collection) registry.get(Constant.SERVICE_GROUPS_PATH);
            if (serviceGroups != null) {
                for (String serviceGroupPath : serviceGroups.getChildren()) {
                    if (StringUtils.isNotEmpty(serviceGroupPath) &&
                            serviceGroupPath.contains(Constant.STS_SERVICE_GROUP)) {
                        String policyCollectionPath = new StringBuilder().append(serviceGroupPath)
                                .append(Constant.SECURITY_POLICY_RESOURCE_PATH).toString();
                        Collection policies = (Collection) registry.get(policyCollectionPath);
                        if (policies != null) {
                            policyPaths.addAll(Arrays.asList(policies.getChildren()));
                        }
                    }
                }
            }
        }
        return policyPaths;
    }
}
