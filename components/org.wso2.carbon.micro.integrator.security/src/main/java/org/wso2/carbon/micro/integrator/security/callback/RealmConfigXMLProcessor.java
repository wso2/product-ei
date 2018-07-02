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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.micro.integrator.security.callback;

import org.apache.axiom.om.*;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.builder.ClaimBuilder;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class RealmConfigXMLProcessor {
    public static final String REALM_CONFIG_FILE = "user-mgt.xml";
    private static final Log log = LogFactory.getLog(RealmConfigXMLProcessor.class);
    private static BundleContext bundleContext;
    InputStream inStream = null;
    private SecretResolver secretResolver;

    public RealmConfigXMLProcessor() {
    }

    public static void setBundleContext(BundleContext bundleContext) {
        bundleContext = bundleContext;
    }

    public static OMElement serialize(RealmConfiguration realmConfig) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement rootElement = factory.createOMElement(new QName("UserManager"));
        OMElement realmElement = factory.createOMElement(new QName("Realm"));
        String realmName = realmConfig.getRealmClassName();
        OMAttribute propAttr = factory.createOMAttribute("name", (OMNamespace)null, realmName);
        realmElement.addAttribute(propAttr);
        rootElement.addChild(realmElement);
        OMElement mainConfig = factory.createOMElement(new QName("Configuration"));
        realmElement.addChild(mainConfig);
        OMElement addAdmin = factory.createOMElement(new QName("AddAdmin"));
        OMElement adminUser = factory.createOMElement(new QName("AdminUser"));
        OMElement adminUserNameElem = factory.createOMElement(new QName("UserName"));
        adminUserNameElem.setText(realmConfig.getAdminUserName());
        OMElement adminPasswordElem = factory.createOMElement(new QName("Password"));
        addAdmin.setText(UserCoreUtil.removeDomainFromName(realmConfig.getAddAdmin()));
        adminPasswordElem.setText(realmConfig.getAdminPassword());
        adminUser.addChild(adminUserNameElem);
        adminUser.addChild(adminPasswordElem);
        mainConfig.addChild(addAdmin);
        mainConfig.addChild(adminUser);
        OMElement adminRoleNameElem = factory.createOMElement(new QName("AdminRole"));
        adminRoleNameElem.setText(UserCoreUtil.removeDomainFromName(realmConfig.getAdminRoleName()));
        mainConfig.addChild(adminRoleNameElem);
        OMElement systemUserNameElem = factory.createOMElement(new QName("SystemUserName"));
        mainConfig.addChild(systemUserNameElem);
        OMElement anonymousUserEle = factory.createOMElement(new QName("AnonymousUser"));
        OMElement anonymousUserNameElem = factory.createOMElement(new QName("UserName"));
        OMElement anonymousPasswordElem = factory.createOMElement(new QName("Password"));
        anonymousUserEle.addChild(anonymousUserNameElem);
        anonymousUserEle.addChild(anonymousPasswordElem);
        mainConfig.addChild(anonymousUserEle);
        OMElement everyoneRoleNameElem = factory.createOMElement(new QName("EveryOneRoleName"));
        everyoneRoleNameElem.setText(UserCoreUtil.removeDomainFromName(realmConfig.getEveryOneRoleName()));
        mainConfig.addChild(everyoneRoleNameElem);
        addPropertyElements(factory, mainConfig, (String)null, realmConfig.getDescription(), realmConfig.getRealmProperties());
        OMElement userStoreManagerElement = factory.createOMElement(new QName("UserStoreManager"));
        realmElement.addChild(userStoreManagerElement);
        addPropertyElements(factory, userStoreManagerElement, realmConfig.getUserStoreClass(), realmConfig.getDescription(), realmConfig.getUserStoreProperties());

        return rootElement;
    }

    private static void addPropertyElements(OMFactory factory, OMElement parent, String className, String description, Map<String, String> properties) {
        if (className != null) {
            parent.addAttribute("class", className, (OMNamespace)null);
        }

        if (description != null) {
            parent.addAttribute("Description", description, (OMNamespace)null);
        }

        Iterator ite = properties.entrySet().iterator();

        while(ite.hasNext()) {
            Entry<String, String> entry = (Entry)ite.next();
            String name = (String)entry.getKey();
            String value = (String)entry.getValue();
            OMElement propElem = factory.createOMElement(new QName("Property"));
            OMAttribute propAttr = factory.createOMAttribute("name", (OMNamespace)null, name);
            propElem.addAttribute(propAttr);
            propElem.setText(value);
            parent.addChild(propElem);
        }

    }

    public RealmConfiguration buildRealmConfigurationFromFile() throws UserStoreException {
        try {
            OMElement realmElement = this.getRealmElement();
            RealmConfiguration realmConfig = this.buildRealmConfiguration(realmElement);
            if (this.inStream != null) {
                this.inStream.close();
            }

            return realmConfig;
        } catch (Exception var4) {
            String message = "Error while reading realm configuration from file";
            if (log.isDebugEnabled()) {
                log.debug(message, var4);
            }

            throw new UserStoreException(message, var4);
        }
    }

    private OMElement preProcessRealmConfig(InputStream inStream) throws CarbonException, XMLStreamException {
        inStream = CarbonUtils.replaceSystemVariablesInXml(inStream);
        StAXOMBuilder builder = new StAXOMBuilder(inStream);
        OMElement documentElement = builder.getDocumentElement();
        OMElement realmElement = documentElement.getFirstChildWithName(new QName("Realm"));
        return realmElement;
    }

    public RealmConfiguration buildRealmConfiguration(InputStream inStream) throws UserStoreException {
        String message;
        try {
            OMElement realmElement = this.preProcessRealmConfig(inStream);
            RealmConfiguration realmConfig = this.buildRealmConfiguration(realmElement);
            if (inStream != null) {
                inStream.close();
            }

            return realmConfig;
        } catch (RuntimeException var5) {
            message = "An unexpected error occurred while building the realm configuration.";
            if (log.isDebugEnabled()) {
                log.debug(message, var5);
            }

            throw new UserStoreException(message, var5);
        } catch (Exception var6) {
            message = "Error while reading realm configuration from file";
            if (log.isDebugEnabled()) {
                log.debug(message, var6);
            }

            throw new UserStoreException(message, var6);
        }
    }

    public RealmConfiguration buildRealmConfiguration(OMElement realmElem) throws UserStoreException {
        return this.buildRealmConfiguration(realmElem, true);
    }

    public RealmConfiguration buildRealmConfiguration(OMElement realmElem, boolean supperTenant) throws UserStoreException {
        RealmConfiguration realmConfig = null;
        String userStoreClass = null;
        String addAdmin = null;
        String adminRoleName = null;
        String adminUserName = null;
        String adminPassword = null;
        String everyOneRoleName = null;
        String realmClass = null;
        String description = null;
        Map<String, String> userStoreProperties = null;
        Map<String, String> realmProperties = null;
        boolean passwordsExternallyManaged = false;
        realmClass = realmElem.getAttributeValue(new QName("class"));
        OMElement mainConfig = realmElem.getFirstChildWithName(new QName("Configuration"));
        realmProperties = this.getChildPropertyElements(mainConfig, this.secretResolver);
        String dbUrl = this.constructDatabaseURL((String)realmProperties.get("url"));
        realmProperties.put("url", dbUrl);
        if (mainConfig.getFirstChildWithName(new QName("AddAdmin")) != null && !mainConfig.getFirstChildWithName(new QName("AddAdmin")).getText().trim().equals("")) {
            addAdmin = mainConfig.getFirstChildWithName(new QName("AddAdmin")).getText().trim();
        } else {
            if (supperTenant) {
                log.error("AddAdmin configuration not found or invalid in user-mgt.xml. Cannot start server!");
                throw new UserStoreException("AddAdmin configuration not found or invalid user-mgt.xml. Cannot start server!");
            }

            log.debug("AddAdmin configuration not found");
            addAdmin = "true";
        }

        OMElement reservedRolesElm = mainConfig.getFirstChildWithName(new QName("ReservedRoleNames"));
        String[] reservedRoles = new String[0];
        if (reservedRolesElm != null && !reservedRolesElm.getText().trim().equals("")) {
            String rolesStr = reservedRolesElm.getText().trim();
            if (rolesStr.contains(",")) {
                reservedRoles = rolesStr.split(",");
            } else {
                reservedRoles = rolesStr.split(";");
            }
        }

        OMElement restrictedDomainsElm = mainConfig.getFirstChildWithName(new QName("RestrictedDomainsForSelfSignUp"));
        String[] restrictedDomains = new String[0];
        if (restrictedDomainsElm != null && !restrictedDomainsElm.getText().trim().equals("")) {
            String domain = restrictedDomainsElm.getText().trim();
            if (domain.contains(",")) {
                restrictedDomains = domain.split(",");
            } else {
                restrictedDomains = domain.split(";");
            }
        }

        OMElement adminUser = mainConfig.getFirstChildWithName(new QName("AdminUser"));
        adminUserName = adminUser.getFirstChildWithName(new QName("UserName")).getText().trim();
        adminPassword = adminUser.getFirstChildWithName(new QName("Password")).getText().trim();
        if (this.secretResolver != null && this.secretResolver.isInitialized() && this.secretResolver.isTokenProtected("UserManager.AdminUser.Password")) {
            adminPassword = this.secretResolver.resolve("UserManager.AdminUser.Password");
        }

        adminRoleName = mainConfig.getFirstChildWithName(new QName("AdminRole")).getText().trim();
        everyOneRoleName = mainConfig.getFirstChildWithName(new QName("EveryOneRoleName")).getText().trim();
        Iterator<OMElement> iterator = realmElem.getChildrenWithName(new QName("UserStoreManager"));
        RealmConfiguration primaryConfig = null;
        RealmConfiguration tmpConfig = null;

        String readOnly;
        String adminRoleDomain;
        while(iterator.hasNext()) {
            OMElement usaConfig = (OMElement)iterator.next();
            userStoreClass = usaConfig.getAttributeValue(new QName("class"));
            if (usaConfig.getFirstChildWithName(new QName("Description")) != null) {
                description = usaConfig.getFirstChildWithName(new QName("Description")).getText().trim();
            }

            userStoreProperties = this.getChildPropertyElements(usaConfig, this.secretResolver);
            readOnly = (String)userStoreProperties.get("PasswordsExternallyManaged");
            Map<String, String> multipleCredentialsProperties = this.getMultipleCredentialsProperties(usaConfig);
            if (null != readOnly && !readOnly.trim().equals("")) {
                passwordsExternallyManaged = Boolean.parseBoolean(readOnly);
            } else if (log.isDebugEnabled()) {
                log.debug("External password management is disabled.");
            }

            realmConfig = new RealmConfiguration();
            realmConfig.setRealmClassName(realmClass);
            realmConfig.setUserStoreClass(userStoreClass);
            realmConfig.setDescription(description);
            if (primaryConfig == null) {
                realmConfig.setPrimary(true);
                realmConfig.setAddAdmin(addAdmin);
                realmConfig.setAdminPassword(adminPassword);
                adminRoleDomain = (String)userStoreProperties.get("DomainName");
                if (adminRoleDomain == null) {
                    userStoreProperties.put("DomainName", "PRIMARY");
                }

                int i;
                for(i = 0; i < reservedRoles.length; ++i) {
                    realmConfig.addReservedRoleName(reservedRoles[i].trim().toUpperCase());
                }

                for(i = 0; i < restrictedDomains.length; ++i) {
                    realmConfig.addRestrictedDomainForSelfSignUp(restrictedDomains[i].trim().toUpperCase());
                }

            }

            adminRoleDomain = (String)userStoreProperties.get("DomainName");
            if (adminRoleDomain == null) {
                log.warn("Required property DomainName missing in secondary user store. Skip adding the user store.");
            } else {
                userStoreProperties.put("StaticUserStore", "true");
                realmConfig.setEveryOneRoleName("Internal" + CarbonConstants.DOMAIN_SEPARATOR + everyOneRoleName);
                realmConfig.setAdminRoleName(adminRoleName);
                realmConfig.setAdminUserName(adminUserName);
                realmConfig.setUserStoreProperties(userStoreProperties);
                realmConfig.setRealmProperties(realmProperties);
                realmConfig.setPasswordsExternallyManaged(passwordsExternallyManaged);
                realmConfig.addMultipleCredentialProperties(userStoreClass, multipleCredentialsProperties);
                if (realmConfig.getUserStoreProperty("MaxUserNameListLength") == null) {
                    realmConfig.getUserStoreProperties().put("MaxUserNameListLength", "100");
                }

                if (realmConfig.getUserStoreProperty("ReadOnly") == null) {
                    realmConfig.getUserStoreProperties().put("ReadOnly", "false");
                }

                if (primaryConfig == null) {
                    primaryConfig = realmConfig;
                } else {
                    tmpConfig.setSecondaryRealmConfig(realmConfig);
                }

                tmpConfig = realmConfig;
            }
        }

        if (primaryConfig != null && primaryConfig.isPrimary()) {
            String primaryDomainName = primaryConfig.getUserStoreProperty("DomainName");
            readOnly = primaryConfig.getUserStoreProperty("ReadOnly");
            Boolean isReadOnly = false;
            if (readOnly != null) {
                isReadOnly = Boolean.parseBoolean(readOnly);
            }

            if (primaryDomainName != null && primaryDomainName.trim().length() > 0) {
                if (adminUserName.indexOf(CarbonConstants.DOMAIN_SEPARATOR) > 0) {
                    adminRoleDomain = adminUserName.substring(0, adminUserName.indexOf(CarbonConstants.DOMAIN_SEPARATOR));
                    if (!primaryDomainName.equalsIgnoreCase(adminRoleDomain)) {
                        throw new UserStoreException("Admin User domain does not match primary user store domain.");
                    }
                } else {
                    primaryConfig.setAdminUserName(UserCoreUtil.addDomainToName(adminUserName, primaryDomainName));
                }

                if (adminRoleName.indexOf(CarbonConstants.DOMAIN_SEPARATOR) > 0) {
                    adminRoleDomain = adminRoleName.substring(0, adminRoleName.indexOf(CarbonConstants.DOMAIN_SEPARATOR));
                    if (!primaryDomainName.equalsIgnoreCase(adminRoleDomain) || isReadOnly && !primaryDomainName.equalsIgnoreCase("Internal")) {
                        throw new UserStoreException("Admin Role domain does not match primary user store domain.");
                    }
                }
            }

            primaryConfig.setAdminRoleName(UserCoreUtil.addDomainToName(adminRoleName, primaryDomainName));
        }

        return primaryConfig;
    }

    private String constructDatabaseURL(String url) {
        if (url != null && url.contains("${carbon.home}")) {
            File carbonHomeDir = new File(CarbonUtils.getCarbonHome());
            String path = carbonHomeDir.getPath();
            path = path.replaceAll(Pattern.quote("\\"), "/");
            if (carbonHomeDir.exists() && carbonHomeDir.isDirectory()) {
                url = url.replaceAll(Pattern.quote("${carbon.home}"), path);
            } else {
                log.warn("carbon home invalid");
                String[] tempStrings1 = url.split(Pattern.quote("${carbon.home}"));
                String dbUrl = tempStrings1[1];
                String[] tempStrings2 = dbUrl.split("/");

                for(int i = 0; i < tempStrings2.length - 1; ++i) {
                    url = tempStrings1[0] + tempStrings2[i] + "/";
                }

                url = url + tempStrings2[tempStrings2.length - 1];
            }
        }

        return url;
    }

    private Map<String, String> getChildPropertyElements(OMElement omElement, SecretResolver secretResolver) {
        Map<String, String> map = new HashMap();

        String propName;
        String propValue;
        for(Iterator ite = omElement.getChildrenWithName(new QName("Property")); ite.hasNext(); map.put(propName.trim(), propValue.trim())) {
            OMElement propElem = (OMElement)ite.next();
            propName = propElem.getAttributeValue(new QName("name"));
            propValue = propElem.getText();
            if (secretResolver != null && secretResolver.isInitialized()) {
                if (secretResolver.isTokenProtected("UserManager.Configuration.Property." + propName)) {
                    propValue = secretResolver.resolve("UserManager.Configuration.Property." + propName);
                }

                if (secretResolver.isTokenProtected("UserStoreManager.Property." + propName)) {
                    propValue = secretResolver.resolve("UserStoreManager.Property." + propName);
                }
            }
        }

        return map;
    }

    private Map<String, String> getMultipleCredentialsProperties(OMElement omElement) {
        Map<String, String> map = new HashMap();
        OMElement multipleCredentialsEl = omElement.getFirstChildWithName(new QName("MultipleCredentials"));
        if (multipleCredentialsEl != null) {
            Iterator ite = multipleCredentialsEl.getChildrenWithLocalName("Credential");

            while(ite.hasNext()) {
                Object OMObj = ite.next();
                if (OMObj instanceof OMElement) {
                    OMElement credsElem = (OMElement)OMObj;
                    String credsType = credsElem.getAttributeValue(new QName("type"));
                    String credsClassName = credsElem.getText();
                    map.put(credsType.trim(), credsClassName.trim());
                }
            }
        }

        return map;
    }

    private OMElement getRealmElement() throws XMLStreamException, IOException, UserStoreException {
        String carbonHome = CarbonUtils.getCarbonHome();
        StAXOMBuilder builder = null;
        if (carbonHome != null) {
            File profileConfigXml = new File(CarbonUtils.getCarbonConfigDirPath(), "user-mgt.xml");
            if (profileConfigXml.exists()) {
                this.inStream = new FileInputStream(profileConfigXml);
            }
        } else {
            this.inStream = RealmConfigXMLProcessor.class.getResourceAsStream("user-mgt.xml");
        }

        String warningMessage = "";
        if (this.inStream == null) {
            URL url;
            if (bundleContext != null) {
                if ((url = bundleContext.getBundle().getResource("user-mgt.xml")) != null) {
                    this.inStream = url.openStream();
                } else {
                    warningMessage = "Bundle context could not find resource user-mgt.xml or user does not have sufficient permission to access the resource.";
                }
            } else if ((url = ClaimBuilder.class.getResource("user-mgt.xml")) != null) {
                this.inStream = url.openStream();
                log.error("Using the internal realm configuration. Strictly for non-production purposes.");
            } else {
                warningMessage = "ClaimBuilder could not find resource user-mgt.xml or user does not have sufficient permission to access the resource.";
            }
        }

        if (this.inStream == null) {
            String message = "Profile configuration not found. Cause - " + warningMessage;
            if (log.isDebugEnabled()) {
                log.debug(message);
            }

            throw new FileNotFoundException(message);
        } else {
            try {
                this.inStream = CarbonUtils.replaceSystemVariablesInXml(this.inStream);
            } catch (CarbonException var6) {
                throw new UserStoreException(var6.getMessage(), var6);
            }

            builder = new StAXOMBuilder(this.inStream);
            OMElement documentElement = builder.getDocumentElement();
            this.setSecretResolver(documentElement);
            OMElement realmElement = documentElement.getFirstChildWithName(new QName("Realm"));
            return realmElement;
        }
    }

    public void setSecretResolver(OMElement rootElement) {
        this.secretResolver = SecretResolverFactory.create(rootElement, true);
    }
}
