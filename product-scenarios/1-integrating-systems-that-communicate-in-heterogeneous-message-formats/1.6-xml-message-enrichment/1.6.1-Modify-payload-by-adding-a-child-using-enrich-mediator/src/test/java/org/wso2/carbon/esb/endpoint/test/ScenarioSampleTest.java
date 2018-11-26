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

package org.wso2.carbon.esb.endpoint.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

public class ScenarioSampleTest extends ScenarioTestBase {

    protected Log log = LogFactory.getLog(getClass());
    Properties infraProperties;

    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final String carFileName = "SOAPToJSONCarbonApplication_1.0.0";
    int timeout = 20;
    RequestConfig requestConfig = RequestConfig.custom()
                                               .setConnectTimeout(timeout * 100)
                                               .setConnectionRequestTimeout(timeout * 1000)
                                               .setSocketTimeout(timeout * 1000).build();

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        infraProperties = getDeploymentProperties();
        String backendURL = infraProperties.getProperty(CARBON_SERVER_URL) + "/";
        setKeyStoreProperties();
        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);
        String sessionCookie = authenticatorClient.login("admin", "admin", getServerHost());
        log.info("The Backend service URL : " + backendURL + ". session cookie: " + sessionCookie);

        carbonAppUploaderClient = new CarbonAppUploaderClient(backendURL, sessionCookie);
        DataHandler dh = new DataHandler(new FileDataSource(new File(resourceLocation + File.separator + "artifacts" +
                                                                     File.separator + carFileName + ".car")));
        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileName + ".car", dh);
        applicationAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
        // Wait for Capp to sync
        Thread.sleep(60000);
        Assert.assertTrue(isCarFileDeployed(carFileName), "Car file deployment failed");
    }

    @Test(description = "1.1.1.1", enabled = true)
    public void testMessageTransformation() throws Exception {
        // Invoke the service and invoke
        String restURL = infraProperties.getProperty(ESB_HTTP_URL) + "/city/lookup/60601";
        log.info("The API Endpoint : " + restURL);
        HttpGet httpGet = new HttpGet(restURL);
        Thread.sleep(1000);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                log.info("The response status : " + response.getStatusLine());
                String responseString = "{\n" +
                                        "  \"LookupCityResult\": {\n" +
                                        "    \"City\": \"Chicago\",\n" +
                                        "    \"State\": \"IL\",\n" +
                                        "    \"Zip\": 60601\n" +
                                        "  }\n" +
                                        "}";
                Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
                Assert.assertEquals(IOUtils.toString(response.getEntity().getContent()), responseString);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    @Test(description = "1.1.1.2")
    public void testMessageTransformationForInvalidCode() throws Exception {
        // Invoke the service and invoke
        String restURL = infraProperties.getProperty(ESB_HTTP_URL) + "/city/lookup/6060100";
        HttpGet httpHead = new HttpGet(restURL);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpHead)) {
                log.info(response.getStatusLine());
                String responseString = "{\n" +
                                        "  \"Error\": {\n" +
                                        "    \"message\": \"Error while processing the request\",\n" +
                                        "    \"code\": \"0\",\n" +
                                        "    \"description\": \"Error while building message. Error while building Passthrough stream\"\n" +
                                        "  }\n" +
                                        "}";
                Assert.assertEquals(IOUtils.toString(response.getEntity().getContent()), responseString);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    @Test(description = "Test HTTP the transformation when a invalid status code is given", enabled = false)
    public void testMessageTransformationFailure() throws Exception {
        Assert.assertTrue(false, "This test is intentionally failed!");
    }

    @AfterClass(description = "Test HTTP the transformation")
    public void close() throws Exception {
        // Clean up if required
    }

    private boolean isCarFileDeployed(String carFileName) throws Exception {

        log.info("waiting " + ARTIFACT_DEPLOYMENT_WAIT_TIME_MS + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) <
               ARTIFACT_DEPLOYMENT_WAIT_TIME_MS) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileDeployed = true;
                    log.info("car file deployed in " + time + " mills");
                    return isCarFileDeployed;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
        return isCarFileDeployed;
    }

    private String getServerHost() {
        String bucketLocation = System.getenv("DATA_BUCKET_LOCATION");
        log.info("Data Bucket location is set : " + bucketLocation);
        String url = infraProperties.getProperty(MGT_CONSOLE_URL);
        if (url != null && url.contains("/")) {
            url = url.split("/")[2].split(":")[0];
        } else
        if (url == null) {
            url = "localhost";
        }
        log.info("Backend URL is set as : " + url);
        return url;
    }

    private void setKeyStoreProperties() {
        System.setProperty("javax.net.ssl.trustStore", resourceLocation + "/keystores/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }
}
