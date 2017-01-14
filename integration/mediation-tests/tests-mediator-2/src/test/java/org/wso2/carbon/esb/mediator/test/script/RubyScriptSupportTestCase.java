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
package org.wso2.carbon.esb.mediator.test.script;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.esb.integration.common.clients.logging.LoggingAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RubyScriptSupportTestCase extends ESBIntegrationTest {

    private final String JRUBY_JAR = "jruby-complete-1.3.0.jar";
    private final String JRUBY_JAR_LOCATION = "/artifacts/ESB/jar/";

    private ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        serverManager = new ServerConfigurationManager(context);
        serverManager.copyToComponentDropins(new File(getClass().getResource(JRUBY_JAR_LOCATION + JRUBY_JAR).toURI()));
        serverManager.restartGracefully();
        super.init(TestUserMode.SUPER_TENANT_ADMIN);

    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Script Mediator -Run a Ruby script with the mediator")
    public void testJRubyScriptMediation() throws Exception {
        loadSampleESBConfiguration(353);
        OMElement response = axis2Client.sendCustomQuoteRequest(getMainSequenceURL(), null, "WSO2");

        assertNotNull(response, "Fault response message null");

        assertNotNull(response.getQName().getLocalPart(), "Fault response null localpart");
        assertEquals(response.getQName().getLocalPart(), "CheckPriceResponse", "Fault localpart mismatched");

        assertNotNull(response.getFirstElement().getQName().getLocalPart(), " Fault response null localpart");
        assertEquals(response.getFirstElement().getQName().getLocalPart(), "Code", "Fault localpart mismatched");

        assertNotNull(response.getFirstChildWithName(
                new QName("http://services.samples/xsd", "Price")), "Fault response null localpart");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Script Mediator -Run a Ruby script with the mediator" +
                                                            " -Script from gov registry")
    public void testJRubyScriptMediationScriptFromGovRegistry() throws Exception {
        enableDebugLogging();
        uploadResourcesToConfigRegistry();
        loadESBConfigurationFromClasspath
                ("/artifacts/ESB/synapseconfig/script_mediator/retrieve_script_from_gov_reg_mediation.xml");

        OMElement response = axis2Client.sendCustomQuoteRequest(getMainSequenceURL(), null, "WSO2");

        assertNotNull(response, "Fault response message null");

        assertNotNull(response.getQName().getLocalPart(), "Fault response null localpart");
        assertEquals(response.getQName().getLocalPart(), "CheckPriceResponse", "Fault localpart mismatched");

        assertNotNull(response.getFirstElement().getQName().getLocalPart(), " Fault response null localpart");
        assertEquals(response.getFirstElement().getQName().getLocalPart(), "Code", "Fault localpart mismatched");

        assertNotNull(response.getFirstChildWithName(
                new QName("http://services.samples/xsd", "Price")), "Fault response null localpart");
        clearUploadedResource();
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            deleteSequence("main");
            Thread.sleep(5000);
            super.cleanup();
        } finally {
            serverManager.removeFromComponentDropins(JRUBY_JAR);
            serverManager.restartGracefully();
            serverManager = null;
        }

    }

    private void uploadResourcesToConfigRegistry() throws Exception {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/governance/script");
        resourceAdminServiceStub.addCollection("/_system/governance/", "script", "",
                                               "Contains test script files");

        resourceAdminServiceStub.addResource(
                "/_system/governance/script/stockquoteTransform.rb", "application/xml", "script files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/mediatorconfig/script/stockquoteTransform.rb").getPath())));

    }


    private void enableDebugLogging() throws Exception {
        LoggingAdminClient logAdminClient = new LoggingAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
        logAdminClient.updateLoggerData("org.apache.synapse", "DEBUG", true, false);
    }


    private void clearUploadedResource()
            throws InterruptedException, ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName()
, context.getContextTenant().getContextUser().getPassword());

        resourceAdminServiceStub.deleteResource("/_system/governance/script");
    }


}
