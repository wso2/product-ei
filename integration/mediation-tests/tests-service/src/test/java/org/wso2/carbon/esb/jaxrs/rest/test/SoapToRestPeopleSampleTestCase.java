/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.jaxrs.rest.test;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.catalina.LifecycleException;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.services.jaxrs.peoplesample.AppConfig;


public class SoapToRestPeopleSampleTestCase extends ESBIntegrationTest {
    TomcatServerManager tomcatServerManager;
    String personFirstName = "Dharshana";
    String personLastName = "Warusavitharana";
    String personEmail = null;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
       super.init();
       loadESBConfigurationFromClasspath("artifacts/ESB/jaxrs/putpeopleproxy.xml");
        tomcatServerManager = new TomcatServerManager(AppConfig.class.getName(), "jaxrs", 8080);
        tomcatServerManager.startServer();
        personEmail = "testName" + RandomStringUtils.randomAlphanumeric(6) + "@wso2.com";
    }

    @Test(groups = {"wso2.esb"}, priority = 1, description = "Tests POST method with application/jason content type")
    public void addPeople() throws Exception {

        Thread.sleep(5000);
        if (tomcatServerManager.isRunning()) {
            AxisServiceClient axisServiceClient = new AxisServiceClient();
            OMElement putRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://samples.esb.wso2.org\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "\t<per:person>\n" +
                    "\t\t<per:email>" + personEmail + "</per:email>\n" +
                    "\t\t<per:firstname>" + personFirstName + "</per:firstname>\n" +
                    "\t\t<per:lastname>" + personLastName + "</per:lastname>\n" +
                    "\t</per:person>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>");
            OMElement response = axisServiceClient.sendReceive(putRequest, getProxyServiceURLHttp("peoplePostProxy"), "mediate");
            Assert.assertTrue(response.toString().contains("OK"));
            Assert.assertTrue(response.toString().contains("201"));
        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }

    @Test(groups = {"wso2.esb"}, priority = 2, description = "Tests GET method with application/jason content type")
    public void getPeople() throws Exception {
        if (tomcatServerManager.isRunning()) {
            AxisServiceClient axisServiceClient2 = new AxisServiceClient();
            OMElement getRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://samples.esb.wso2.org\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <per:person>\n" +
                    "         <per:email>" + personEmail + "</per:email>\n" +
                    "      </per:person>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>");
            OMElement response = axisServiceClient2.sendReceive(getRequest, getProxyServiceURLHttp("peopleGetProxy"), "mediate");
            Assert.assertTrue(response.toString().contains(personEmail));
            Assert.assertTrue(response.toString().contains(personFirstName));
            Assert.assertTrue(response.toString().contains(personLastName));
        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }
    //todo: Adding HEAD request Test method
    @Test(groups = {"wso2.esb"}, priority = 3, description = "Tests PUT method with application/jason content type")
    public void putPeople() throws Exception {
        String updatedFirstName = "Dharshana";
        String updatedLastName = "Warusavitharana";
        if (tomcatServerManager.isRunning()) {
            AxisServiceClient axisServiceClient = new AxisServiceClient();
            OMElement getRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://samples.esb.wso2.org\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <per:person>\n" +
                    "         <per:email>" + personEmail + "</per:email>\n" +
                    "         <per:firstname>" + updatedFirstName + "</per:firstname>\n" +
                    "         <per:lastname>" + updatedLastName + "</per:lastname>\n" +
                    "      </per:person>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>");
            OMElement response = axisServiceClient.sendReceive(getRequest, getProxyServiceURLHttp("peoplePutProxy"), "mediate");
            Assert.assertTrue(response.toString().contains(personEmail));
            Assert.assertTrue(response.toString().contains(updatedFirstName));
            Assert.assertTrue(response.toString().contains(updatedLastName));
        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }

    @Test(groups = {"wso2.esb"}, priority = 4, description = "Tests Delete method with application/jason content type")
    public void getOptionsPeople() throws Exception {

        if (tomcatServerManager.isRunning()) {
            AxisServiceClient axisServiceClient = new AxisServiceClient();
            OMElement getRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://samples.esb.wso2.org\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <per:person>\n" +
                    "      </per:person>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>");
            OMElement response = axisServiceClient.sendReceive(getRequest, getProxyServiceURLHttp("peopleOptionProxy"), "mediate");
            Assert.assertTrue(response.toString().contains("200"));
            Assert.assertTrue(response.toString().contains("GET POST DELETE PUT OPTIONS"));

        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }

    @Test(groups = {"wso2.esb"}, priority = 5, description = "Tests Delete method with application/jason content type")
    public void deletePeople() throws Exception {

        if (tomcatServerManager.isRunning()) {
            AxisServiceClient axisServiceClient = new AxisServiceClient();
            OMElement getRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://samples.esb.wso2.org\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <per:person>\n" +
                    "         <per:email>" + personEmail + "</per:email>\n" +
                    "      </per:person>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>");
            OMElement response = axisServiceClient.sendReceive(getRequest, getProxyServiceURLHttp("peopleDeleteProxy"), "mediate");
            Assert.assertTrue(response.toString().contains("Deleted"));
            Assert.assertTrue(response.toString().contains("200"));
        } else {
            Assert.fail("Jaxrs Service Startup failed");
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws InterruptedException, LifecycleException {
        Thread.sleep(5000);
        tomcatServerManager.stop();
    }
}
