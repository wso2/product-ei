/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.bps.integration.tests.bpmn;

/**
 * This class is used to hold the BPMN constants used throughout the tests
 */
public class BPMNTestConstants {

    public static final String DIR_ARTIFACTS = "artifacts";
    public static final String DIR_BPMN = "bpmn";
    public static final String HTTP = "http";
    public static final String DEFAULT = "default";

    public static final String USER_CLAIM = "paul";
    public static final String USER_DELEGATE = "will";
    public static final String COMMENT_MESSAGE = "Testing 123";
    public final static String NOT_AVAILABLE = "Not Available";
    public static final String CAR_EXTENSION =".car";


    public static final String CREATED = "201";
    public static final String OK = "200";
    public static final String NO_CONTENT = "204";
    public static final String NOT_FOUND = "404";
    public static final String INTERNAL_SERVER_ERROR = "500";

    public static final int PROCESS_DEPLOYMENT_MAX_RETRY_COUNT = 200;
    public static final int PROCESS_DEPLOYMENT_WAIT_TIME_PER_RETRY = 500;
    public static final String ACTIVITI_CONFIGURATION_FILE_NAME = "activiti.xml";

    public static final String DIR_CONFIG = "configs";
}
