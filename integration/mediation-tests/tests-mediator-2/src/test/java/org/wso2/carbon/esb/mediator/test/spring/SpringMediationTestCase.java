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
package org.wso2.carbon.esb.mediator.test.spring;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;

import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class SpringMediationTestCase extends ESBIntegrationTest {
    private final String SIMPLE_BEAN_JAR = "org.wso2.carbon.test.simplebean.jar";
    private final String JAR_LOCATION = "/artifacts/ESB/jar";

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        clearUploadedResource();
        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.copyToComponentLib
                (new File(getClass().getResource(JAR_LOCATION + File.separator + SIMPLE_BEAN_JAR).toURI()));
        serverConfigurationManager.restartGracefully();

        super.init();
        uploadResourcesToConfigRegistry();

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            deleteSequence("main");
            clearUploadedResource();
            Thread.sleep(5000);
        } finally {
            super.cleanup();
            serverConfigurationManager.removeFromComponentLib(SIMPLE_BEAN_JAR);
            serverConfigurationManager.restartGracefully();

            serverConfigurationManager = null;
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Spring Mediator " +
                                                            "- Change the spring xml and see whether message context is changed")
    public void changeSpringXmlAndCheckMessageContextTest() throws Exception {

        OMElement response;
        String lastPrice;
        String symbol;

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/spring/spring_mediation.xml");

        response = axis2Client.sendSimpleStockQuoteRequest
                (getMainSequenceURL(), null, "IBM");

        lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");

        //TODO Log Assertion
        /*
       INFO - SpringCustomBean Starting Spring Mediator
       INFO - SpringCustomBean Bean in Initialized with User:["esb user"]
       INFO - SpringCustomBean E-MAIL:["usr@wso2.org"]
       INFO - SpringCustomBean Massage Id:  urn:
       INFO - SpringCustomBean Logged....
        */

        updateSpringBeanXML();


        response = axis2Client.sendSimpleStockQuoteRequest
                (getMainSequenceURL(), null, "WSO2");

        lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

        //TODO Log Assertion for Updated spring xml
        /*
        INFO - SpringCustomBean Bean in Initialized with User:["synapse user"]
        INFO - SpringCustomBean E-MAIL:["usr@synapse.org"]
        */

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Spring Mediator " +
                                                            "-Added Simple bean into lib -referring to an invalid spring xml")
    public void uploadSequenceHavingInvalidSpringXMLTest() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/spring/spring_mediation_invalid_spring_bean.xml");
        try {
            axis2Client.sendSimpleStockQuoteRequest
                    (getMainSequenceURL(), null, "WSO2");
            Assert.fail("Request must failed since it refers invalid spring bean");
        } catch (Exception expected) {
            assertEquals(expected.getMessage(), "Cannot reference application context with key : conf:/spring/invalidSpringbeammmn.xml"
                    , "Error Message Mismatched when referring invalid springbean in sequence");

        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Spring Mediator " +
                                                            "- referring to an non existing spring xml")
    public void uploadSequenceHavingNonExistingSpringXMLResourceTest() throws Exception {

        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/spring/spring_mediation_springBean_resource_not_exist.xml");
        try {
            axis2Client.sendSimpleStockQuoteRequest
                    (getMainSequenceURL(), null, "WSO2");
            Assert.fail("Request must failed since it refers non existing spring bean");
        } catch (Exception expected) {
            assertEquals(expected.getMessage(), "Cannot reference application context with key : conf:/spring/NonExistingSpringbean.xml"
                    , "Error Message Mismatched when referring non existing springbean in sequence");

        }
    }


    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Spring Mediator " +
                                                            "-Added Simple bean into lib " +
                                                            "-Different bean ids in spring xml")
    public void providingNonExistingBeanNamesTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/spring/spring_mediation_different_bean_id.xml");
        try {
            axis2Client.sendSimpleStockQuoteRequest
                    (getMainSequenceURL(), null, "IBM");
            fail("Request must throw a axisFault since sequence refers a non existing bean id");
        } catch (AxisFault axisFault) {
            assertEquals(axisFault.getMessage(), "No bean named 'springtestNonExisting' is defined", "Fault: Error message mismatched");
        }

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Spring Mediator -Added Simple bean into lib")
    public void springBeanMediationTest() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/spring/spring_mediation.xml");
        OMElement response = axis2Client.sendSimpleStockQuoteRequest
                (getMainSequenceURL(), null, "IBM");

        String lastPrice = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "last"))
                .getText();
        assertNotNull(lastPrice, "Fault: response message 'last' price null");

        String symbol = response.getFirstElement().getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");

        //TODO Log Assertion
    }

    private void updateSpringBeanXML()
            throws ResourceAdminServiceExceptionException, IOException, InterruptedException {

        ResourceAdminServiceClient resourceAdminServiceClient =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
        String filePath = getESBResourceLocation() + File.separator + "mediatorconfig" + File.separator
                          + "spring" + File.separator + "utils" + File.separator + "updating_spring.xml";

        resourceAdminServiceClient
                .updateTextContent("/_system/config/spring/springbean.xml", FileManager.readFile(filePath));
        Thread.sleep(20000);           //need to persist changes done to spring xml
    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceStub.deleteResource("/_system/config/spring");
        resourceAdminServiceStub.addCollection("/_system/config/", "spring", "",
                                               "Contains spring bean config files");

        resourceAdminServiceStub.addResource(
                "/_system/config/spring/springbean.xml", "application/xml", "spring bean config files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/mediatorconfig/spring/utils/springbean.xml").getPath())));

        resourceAdminServiceStub.addResource(
                "/_system/config/spring/invalidSpringbean.xml", "application/xml", "spring bean config files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/mediatorconfig/spring/utils/invalid_spring_bean.xml").getPath())));

    }


    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceStub.deleteResource("/_system/config/spring");
    }
}
