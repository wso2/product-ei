/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.odata;

import java.io.IOException;
import java.util.Map;

/**
 * This class was written for ease of test writting where we need to modify data using threads.
 */
public class ODataRequestThreadExecutor extends Thread {

	private String httpMethod;
	private String content;
	private Map<String, String> headers;
	private String endpoint;

	public ODataRequestThreadExecutor(String httpMethod, String content, Map<String, String> headers,
	                                  String endpoint) {
		this.content = content;
		this.endpoint = endpoint;
		this.headers = headers;
		this.httpMethod = httpMethod;
	}

	public void run() {
		switch (httpMethod) {
			case "POST":
				try {
					ODataTestUtils.sendPOST(endpoint, content, headers);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "PUT":
				try {
					ODataTestUtils.sendPUT(endpoint, content, headers);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "PATCH":
				try {
					ODataTestUtils.sendPATCH(endpoint, content, headers);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "DELETE":
				try {
					ODataTestUtils.sendDELETE(endpoint, headers);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}
}
