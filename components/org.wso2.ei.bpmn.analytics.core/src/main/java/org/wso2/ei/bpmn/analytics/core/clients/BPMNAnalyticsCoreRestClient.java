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
package org.wso2.ei.bpmn.analytics.core.clients;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.CharSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.ei.bpmn.analytics.core.utils.BPMNAnalyticsCoreUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLStreamException;

/**
 * BPMNAnalyticsCoreRestClient class is used to invoke the DAS REST API
 */
public class BPMNAnalyticsCoreRestClient {
	private static final Log log = LogFactory.getLog(BPMNAnalyticsCoreRestClient.class);

	/**
	 * Send post request to a DAS rest web service
	 * @param url used to locate the webservice functionality
	 * @param message is the request message that need to be sent to the web service
	 * @return the result as a String
	 */
	public static String post(String url, String message) throws IOException, XMLStreamException {
		RegistryUtils.setTrustStoreSystemProperties();
		HttpClient httpClient = new HttpClient();
		PostMethod postRequest = new PostMethod(url);
		postRequest.setRequestHeader("Authorization", BPMNAnalyticsCoreUtils
				.getAuthorizationHeader());
		BufferedReader br = null;
		try {
			StringRequestEntity input =
					new StringRequestEntity(message, "application/json", "UTF-8");
			postRequest.setRequestEntity(input);

			int returnCode = httpClient.executeMethod(postRequest);

			if (returnCode != HttpStatus.SC_OK) {
				String errorCode = "Failed : HTTP error code : " + returnCode;
				throw new RuntimeException(errorCode);
			}

			String charsetname = StandardCharsets.UTF_8.name();
			if (postRequest.getResponseCharSet() != null) {
				charsetname = postRequest.getResponseCharSet();
			}
			InputStreamReader reader =
					new InputStreamReader((postRequest.getResponseBodyAsStream()), charsetname);
			br = new BufferedReader(reader);

			String output = null;
			StringBuilder totalOutput = new StringBuilder();

			if (log.isDebugEnabled()) {
				log.debug("Output from Server .... \n");
			}

			while ((output = br.readLine()) != null) {
				totalOutput.append(output);
			}
			return totalOutput.toString();

		} catch (UnsupportedEncodingException e) {
			String errMsg = "Async DAS client unsupported encoding exception.";
			throw new UnsupportedEncodingException(errMsg);
		} catch (UnsupportedOperationException e) {
			String errMsg = "Async DAS client unsupported operation exception.";
			throw new UnsupportedOperationException(errMsg);
		} catch (IOException e) {
			String errMsg = "Async DAS client I/O exception.";
			log.error(errMsg, e);
		} finally {
			postRequest.releaseConnection();
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					String errMsg = "Async DAS rest client BufferedReader close exception.";
					log.error(errMsg, e);
				}
			}
		}
		return null;
	}

}

