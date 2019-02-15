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

package org.wso2.carbon.esb.scenario.test.common;

/**
 * This class holds common constants used scenario tests and framework
 */
public class ScenarioConstants {

    public static final String SOURCE_FILES = "source_files";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String MESSAGE_ID = "messageId";
    public static final String VERSION_490 = "ESB-4.9.0";
    public static final String VERSION_500 = "ESB-5.0.0";
    public static final String VERSION_600 = "EI-6.0.0";
    public static final String VERSION_610 = "EI-6.1.0";
    public static final String VERSION_611 = "EI-6.1.1";
    public static final String VERSION_620 = "EI-6.2.0";
    public static final String VERSION_630 = "EI-6.3.0";
    public static final String VERSION_640 = "EI-6.4.0";
    public static final String VERSION_650_SNAPSHOT = "EI-6.5.0-SNAPSHOT";
    public static final String BASIC_JSON_MESSAGE = "{ \"name\":\"John\", \"age\":30, \"car\":null}";
    public static final String COMMON_ROUTING_REQUEST = "<m:GetStockPrice xmlns:m=\"http://www.example.org/stock\">\n"
                                                        + "   <m:StockName>IBM</m:StockName>\n"
                                                        + "</m:GetStockPrice>";
    public static final String COMMON_ROUTING_RESPONSE = "<m:GetStockPriceResponse xmlns:m=\"http://www.example.org/stock\">\n"
                                                         + "    <m:Price>34.5</m:Price>\n"
                                                         + "</m:GetStockPriceResponse>";

    /**
     * This regular expression matches numbers with exponents
     */
    public static final String REGEX_EXPONENT = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
  
    public static final String BACKEND_REST_SERVICE1_URL = "http://ei-backend.scenarios.wso2.org:9090/eiTests/";

    /**
     *  StandaloneDeployment property define whether to skip CApp deployment by the test case or not
     *  If true, test case will deploy the CApp
     *  If false, infra will take care CApp deployment
     */
    public static final String STANDALONE_DEPLOYMENT = "StandaloneDeployment";

    public static final String TEST_RESOURCES_DIR = "test.resources.dir";
    public static final String COMMON_RESOURCES_DIR = "common.resources.dir";

    public static final String TEST_RESOURCES_CARBON_APPLICATIONS_LIST = "test.resources.carbonApplications.list";
    public static final String TEST_RESOURCES_CARBON_APPLICATIONS_DIR = "test.resources.carbonApplications.dir";

    public static final String MGT_CONSOLE_URL = "MgtConsoleUrl";
    public static final String CARBON_SERVER_URL = "CarbonServerUrl";
    public static final String ESB_HTTP_URL = "ESBHttpUrl";
    public static final String ESB_HTTPS_URL = "ESBHttpsUrl";
    public static final String LOCAL_VFS_LOCATION = "localVfsLocation";

    public static final String CAPP_EXTENSION = ".car";

    public static final int ARTIFACT_DEPLOYMENT_WAIT_TIME_MS = 120000;
    public static final int FILE_WRITE_WAIT_TIME_MS = 120000;
}
