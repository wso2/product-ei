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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ODataTestUtils {

	public static final int ACCEPTED = 202;
	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int NO_CONTENT = 204;
	public static final int PRE_CONDITION_FAILED = 412;
	public static final int NOT_FOUND = 404;


	public static Object[] sendPOST(String endpoint, String content, Map<String, String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(endpoint);
		for (String headerType : headers.keySet()) {
			httpPost.setHeader(headerType, headers.get(headerType));
		}
		if (null != content) {
			HttpEntity httpEntity = new ByteArrayEntity(content.getBytes("UTF-8"));
			if (headers.get("Content-Type") == null) {
				httpPost.setHeader("Content-Type", "application/json");
			}
			httpPost.setEntity(httpEntity);
		}
		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse.getEntity() != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
			reader.close();
			return new Object[] { httpResponse.getStatusLine().getStatusCode(), response.toString() };
		} else {
			return new Object[] { httpResponse.getStatusLine().getStatusCode() };
		}
	}

	public static Object[] sendGET(String endpoint, Map<String, String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(endpoint);
		for (String headerType : headers.keySet()) {
			httpGet.setHeader(headerType, headers.get(headerType));
		}
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getEntity() != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
			reader.close();
			return new Object[] { httpResponse.getStatusLine().getStatusCode(), response.toString() };
		} else {
			return new Object[] { httpResponse.getStatusLine().getStatusCode() };
		}
	}

	public static int sendPUT(String endpoint, String content, Map<String, String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(endpoint);
		for (String headerType : headers.keySet()) {
			httpPut.setHeader(headerType, headers.get(headerType));
		}
		if (null != content) {
			HttpEntity httpEntity = new ByteArrayEntity(content.getBytes("UTF-8"));
			if (headers.get("Content-Type") == null) {
				httpPut.setHeader("Content-Type", "application/json");
			}
			httpPut.setEntity(httpEntity);
		}
		HttpResponse httpResponse = httpClient.execute(httpPut);
		return httpResponse.getStatusLine().getStatusCode();
	}

	public static int sendPATCH(String endpoint, String content, Map<String, String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPatch httpPatch = new HttpPatch(endpoint);
		for (String headerType : headers.keySet()) {
			httpPatch.setHeader(headerType, headers.get(headerType));
		}
		if (null != content) {
			HttpEntity httpEntity = new ByteArrayEntity(content.getBytes("UTF-8"));
			if (headers.get("Content-Type") == null) {
				httpPatch.setHeader("Content-Type", "application/json");
			}
			httpPatch.setEntity(httpEntity);
		}
		HttpResponse httpResponse = httpClient.execute(httpPatch);
		return httpResponse.getStatusLine().getStatusCode();
	}

	public static int sendDELETE(String endpoint, Map<String, String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(endpoint);
		for (String headerType : headers.keySet()) {
			httpDelete.setHeader(headerType, headers.get(headerType));
		}
		HttpResponse httpResponse = httpClient.execute(httpDelete);
		return httpResponse.getStatusLine().getStatusCode();
	}

	public static String getETag(String content) throws ParseException, JSONException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(content);
		if (((JSONObject) obj).get("@odata.etag") != null) {
			return ((JSONObject) obj).get("@odata.etag").toString();
		} else {
			return ((JSONObject) ((JSONArray) ((JSONObject) obj).get("value")).get(0)).get("@odata.etag").toString();
		}
	}

}
