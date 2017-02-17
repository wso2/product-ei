/*
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.dataservices.samples;

public class BaseSample {

	public static String HOST_IP;
	
	public static String HOST_HTTP_PORT;
	
	public static String HOST_HTTPS_PORT;
	
	public static String SECURITY_POLICY_PATH;
	
	public static String CLIENT_JKS_PATH;
	
	static {
		HOST_IP = System.getProperty("host.ip");
		HOST_HTTP_PORT = System.getProperty("host.http.port");
		HOST_HTTPS_PORT = System.getProperty("host.https.port");
		SECURITY_POLICY_PATH = System.getProperty("security.policy.path");
		CLIENT_JKS_PATH = System.getProperty("client_jks_path");
	}
	
}
