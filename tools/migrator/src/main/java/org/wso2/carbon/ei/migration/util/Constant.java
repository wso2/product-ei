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
package org.wso2.carbon.ei.migration.util;

import javax.xml.namespace.QName;

/**
 * Holds common constants in migration service.
 */
public class Constant {
    public static final String RSA = "RSA" ;
    public static final String MIGRATION_LOG = " WSO2 Product Migration Service Task : ";
    public static final String CARBON_HOME = "carbon.home";
    public static final int SUPER_TENANT_ID = -1234;
    public static final String MIGRATION_RESOURCE_HOME = "migration-resources";
    public static final String EM_ENCRYPTED_PASSWORD_PREFIX = "enc:";
    public static final String IGNORE_INACTIVE_TENANTS = "ignoreInactiveTenants";
    public static final QName TARGET_Q = new QName("target");
    public static final QName IN_SEQUENCE_Q = new QName("inSequence");
    public static final QName OUT_SEQUENCE_Q = new QName("inSequence");
    public static final QName FAULT_SEQUENCE_Q = new QName("inSequence");
    public static final QName ENTITLEMENT_SERVICE_Q = new QName("entitlementService");
    public static final QName REMOTE_SERVICE_PASSWORD_Q = new QName("remoteServicePassword");
    public static final QName DATA_SOURCES_Q = new QName("datasources");
    public static final QName DATA_SOURCE_Q = new QName("datasource");
    public static final QName PASSWORD_Q = new QName("password");
    public static final QName DEFINITION_Q = new QName("definition");
    public static final QName CONFIGURATION_Q = new QName("configuration");
    public static final QName SEQUENCE_Q = new QName("sequence");
    public static final QName RESOURCE_Q = new QName("resource");
    public static final QName TO_Q = new QName("to");
    public static final QName ENCRYPTED_Q = new QName("encrypted");

    public static final String EVENT_PUBLISHER_PATH="/repository/deployment/server/eventpublishers";
    public static final String EVENT_RECIEVER_PATH = "/repository/deployment/server/eventreceivers";

    public static final String PASSWORD = "password";
    public static final String PRIVATE_KEY_PASS = "privatekeyPass";
    public static final String KEYSTORE_RESOURCE_PATH = "/repository/security/key-stores/";
    public static final String SYSLOG = "/repository/components/org.wso2.carbon.logging/loggers/syslog/SYSLOG_PROPERTIES";
    public static final String SERVICE_PRINCIPAL_PASSWORD = "service.principal.password";
    public static final String CARBON_SEC_CONFIG = "CarbonSecConfig";
    public static final String KERBEROS = "Kerberos";
    public static final String SERVICE_GROUPS_PATH = "/repository/axis2/service-groups/";
    public static final String STS_SERVICE_GROUP = "org.wso2.carbon.sts";
    public static final String SECURITY_POLICY_RESOURCE_PATH = "/services/wso2carbon-sts/policies/";
    public static final QName NAME_Q = new QName("name");
    public static final String CONNECTION_PASSWORD = "ConnectionPassword";

    public static final QName SECURE_PASSWORD_Q = new QName("securePassword");
}
