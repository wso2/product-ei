/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ei.bps.analytics.udf;

/**
 * AnalyticsUDFConstants class holds the constants of AnalyticsUDF class
 */
public class AnalyticsUDFConstants {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_WITHOUT_TIME = "yyyy-MM-dd";
	public static final String DATE_FORMAT_MONTH = "MMM yyyy";
	public static final String MONTH_FORMAT = "MMM";
	public static final String DATE_SEPARATOR = "-";
	public static final String SPACE_SEPARATOR = " ";
	public static final String LAST_COLON_SEPARATOR = ":\\d*$";
	public static final String COLON_SEPARATOR = ":";
	public static final int SECOND = 1000;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;
}
