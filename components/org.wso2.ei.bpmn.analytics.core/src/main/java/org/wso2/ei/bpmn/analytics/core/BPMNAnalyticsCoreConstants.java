/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */
package org.wso2.ei.bpmn.analytics.core;

/**
 * Keep all the constants of analytics core
 */
public class BPMNAnalyticsCoreConstants {

    public static final String COLUMN_FINISHED_TIME = "finishedTime";
    public static final String ANALYTICS_AGGREGATE = "analytics/aggregates";
    public static final String ANALYTICS_SEARCH = "analytics/search";
    public static final String AVG = "AVG";
    public static final String COUNT = "COUNT";
    public static final String SUM = "SUM";
    public static final String DURATION = "duration";
    public static final String AVG_EXECUTION_TIME = "avgExecutionTime";
    public static final String PROCESS_INSTANCE_COUNT = "processInstanceCount";
    public static final String TASK_INSTANCE_COUNT = "taskInstanceCount";
    public static final String PROCESS_USAGE_TABLE = "PROCESS_USAGE_SUMMARY_DATA";
    public static final String TASK_USAGE_TABLE = "TASK_USAGE_SUMMARY_DATA";
    public static final String USER_INVOLVE_TABLE = "USER_INVOLVE_SUMMARY_DATA";
    public static final String PROCESS_DEFINITION_KEY = "processDefKey";
    public static final String PROCESS_KEY = "processKey";
    public static final String TASK_DEFINITION_KEY = "taskDefId";
    public static final String PROCESS_VERSION = "processVer";
    public static final String ASSIGN_USER = "assignUser";
    public static final String AVG_WAITING_TIME = "avgWaitingTime";
    public static final String TOTAL_INVOLVED_TIME = "totalInvolvedTime";
    public static final String FINISHED_TIME = "finishTime";
    public static final String AGGREGATE_BY_MONTH = "aggregateByMonth";
    public static final String MONTH = "month";
    public static final String COMPLETED_TOTAL_TASKS = "completedTotalTasks";
    public static final String TOTAL_INSTANCE_COUNT = "totalInstanceCount";
    public static final String ALL = "*";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String ORDER = "order";
    public static final String NUM_COUNT = "count";
    public static final String PROCESS_ID = "processId";
    public static final String TASK_ID = "taskId";
    public static final String USER_ID = "userId";
    public static final String PROCESS_ID_LIST = "processIdList";
    public static final String TASK_ID_LIST = "taskIdList";
    public static final String LIMIT = "limit";
    public static final String DATE_FORMAT_WITHOUT_TIME = "yyyy-MM-dd";
    public static final String MONTH_FORMAT = "MMM";
    public static final String DATE_SEPARATOR = "-";
    public static final String SPACE_SEPARATOR = " ";
    public static final String VALUES = "values";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String TASK_INSTANCE_ID = "taskInstanceId";
    public static final int MIN_COUNT = 0;
    public static final int MAX_COUNT = 100000;
    // Analytics Rest Api Header Constants
    public static final String AUTH_BASIC_HEADER = "Basic";

    public static final String PROCESS_CENTER_CONFIGURATION_FILE_NAME = "process-center.xml";
    public static final String ANALYTICS_SERVER_PASSWORD_SECRET_ALIAS = "PC.Analytics.Password";
    public static final String RUNTIME_ENVIRONMENT_PASSWORD_SECRET_ALIAS = "PC.Runtime.Environment.Password";

    public static final String BPS_BPMN_ANALYTICS_SERVER_PASSWORD_SECRET_ALIAS = "BPS.Analytics.Server.Password";
    public static final String BPS_ANALYTICS_CONFIGURATION_FILE_NAME = "bps-analytics.xml";

    // Make the constructor private, since it is a utility class
    private BPMNAnalyticsCoreConstants() {
    }
}
