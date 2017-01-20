/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.esb.mediators.cache;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerManager;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.services.jaxrs.peoplesample.AppConfig;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


/**
 * This test case is for using REQUESTHASHGenerator as the caching Algorithm. For different requests with no request body
 * the above algorithm returns different responses. Where as when DOMHASHGenerator it will return the same response
 */
public class APIMANAGER1838RequestHashGeneratorTestCase extends ESBIntegrationTest {

    TomcatServerManager tomcatServerManager;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB"
                + File.separator + "synapseconfig" + File.separator + "cacheMediator"
                + File.separator + "APIMANAGER1838RequestHashGenerator.xml");
        tomcatServerManager = new TomcatServerManager(AppConfig.class.getName(), "jaxrs", 8080);
        tomcatServerManager.startServer();
    }


    @Test(groups = {"wso2.esb"}, description = "Adding people to PeopleRestService" , enabled = false)
    public void addPeople() throws Exception {
        Thread.sleep(5000);
        if (tomcatServerManager.isRunning()) {
            HttpResponse response1 = HttpRequestUtil.sendGetRequest(getApiInvocationURL("addPerson") + "/person?email=john@wso2.com&firstName=John&lastName=Doe", null);
            assertEquals(response1.getResponseCode(), 201, "Response code mismatch");

            HttpResponse response2 = HttpRequestUtil.sendGetRequest(getApiInvocationURL("addPerson") + "/person?email=jane@wso2.com&firstName=Jane&lastName=Doe", null);
            assertEquals(response2.getResponseCode(), 201, "Response code mismatch");
        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }
    @Test(groups = {"wso2.esb"}, dependsOnMethods = "addPeople", description = "Retrieving people from PeopleRestService")
    public void getPeople() throws Exception {

        Thread.sleep(5000);
        if (tomcatServerManager.isRunning()) {
        HttpResponse response1 = HttpRequestUtil.sendGetRequest(getApiInvocationURL("getPerson") + "/person?email=john@wso2.com", null);
        assertEquals(response1.getResponseCode(), 200, "Response code mismatch");
        assertTrue(response1.getData().contains("John"), "Response message is not as expected.");
        assertTrue(response1.getData().contains("Doe"), "Response message is not as expected");

        HttpResponse response2 = HttpRequestUtil.sendGetRequest(getApiInvocationURL("getPerson") + "/person?email=jane@wso2.com", null);
        assertEquals(response2.getResponseCode(), 200, "Response code mismatch");
        assertTrue(response2.getData().contains("Jane"), "Response message is not as expected.");
        assertTrue(response2.getData().contains("Doe"), "Response message is not as expected");
        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        tomcatServerManager.stop();
        super.cleanup();
    }
}