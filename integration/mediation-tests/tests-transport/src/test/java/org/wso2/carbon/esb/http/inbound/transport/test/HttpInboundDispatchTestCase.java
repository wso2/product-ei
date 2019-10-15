package org.wso2.carbon.esb.http.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.data.xsd.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class HttpInboundDispatchTestCase extends ESBIntegrationTest {
    private LogViewerClient logViewerClient = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();

        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        addSequence(getArtifactConfig("sequences", "fault.xml"));
        addSequence(getArtifactConfig("sequences", "main.xml"));
        addSequence(getArtifactConfig("sequences", "super.xml"));

        addApi(getArtifactConfig("api", "BOO.xml"));
        addApi(getArtifactConfig("api", "FOO.xml"));
        addApi(getArtifactConfig("api", "TenantContext.xml"));

        addProxyService(getArtifactConfig("proxy-services", "TestProxy.xml"));
        addInboundEndpoint(getArtifactConfig("inbound-endpoints", "inbound1.xml"));
        addInboundEndpoint(getArtifactConfig("inbound-endpoints", "inbound2.xml"));

    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant Sequence Dispatch" )
    public void inboundHttpSuperSequenceTest() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/", null, "WSO2");
        //this case matches with the regex but there is no api or proxy so dispatch to  super tenant main sequence
        Assert.assertTrue(stringExistsInLog("SUPER_MAIN"));
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant API Dispatch" )
    public void inboundHttpSuperAPITest() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/foo", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("FOO"));
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/boo", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("BOO"));

        /**
         * Test API dispatch to non existent API - this should trigger super tenant main sequence.
         * since this matches with inbound regex but no api or proxy found to be dispatched
         */
        logViewerClient.clearLogs();
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/idontexist", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("SUPER_MAIN"));
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant Default Main Sequence Dispatch" )
    public void inboundHttpSuperDefaultMainTest() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9091/", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("SUPER_MAIN"));
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Super Tenant Proxy Dispatch" )
    public void inboundHttpSuperProxyDispatchTest() throws Exception {
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/services/TestProxy", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("PROXY_HIT"));
    }

    @Test(groups = "wso2.esb", description = "Inbound HTTP Tenant Dispatch " +
            "(Shared Port between super tenant and regular tenant)" )
    public void inboundHttpTenantDispatchTests() throws Exception {
        super.init(TestUserMode.TENANT_ADMIN);

        List<OMElement> seqList = new ArrayList<>();
        seqList.add(getArtifactConfig("tenant/sequences", "main.xml"));
        seqList.add(getArtifactConfig("tenant/sequences", "fault.xml"));
        seqList.add(getArtifactConfig("tenant/sequences", "tenant.xml"));
        addSequence(seqList.get(0));
        addSequence(seqList.get(1));
        addSequence(seqList.get(2));

        List<OMElement> apiList = new ArrayList<>();
        apiList.add(getArtifactConfig("tenant/api", "tenantAPI.xml"));
        addApi(apiList.get(0));

        List<OMElement> proxyList = new ArrayList<>();
        proxyList.add(getArtifactConfig("tenant/proxy-services", "TestProxy.xml"));
        addProxyService(proxyList.get(0));

        List<OMElement> inboundList = new ArrayList<>();
        inboundList.add(getArtifactConfig("tenant/inbound-endpoints", "ie1.xml"));
        addInboundEndpoint(inboundList.get(0));

        Awaitility.await()
                  .pollInterval(50, TimeUnit.MILLISECONDS)
                  .atMost(300, TimeUnit.SECONDS)
                  .until(isServiceDeployed(seqList, inboundList, proxyList));
        logViewerClient.clearLogs();

        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/t/wso2.com/tenantAPI", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("TENANT_API"), "Dispatch to http://localhost:9090/t/wso2.com/tenantAPI");

        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/t/wso2.com/", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("SUB_TENANT"), "Dispatch to http://localhost:9090/t/wso2.com/");

        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/services/t/wso2.com/TestProxy", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("TENANT_PROXY_HIT"),
                "Dispatch to http://localhost:9090/services/t/wso2.com/TestProxy");

        /**
         * Test non existent tenant - should hit carbon super (or dispatch to any API with tenant like context on carbon super).
         */
        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/t/idontexist", null, "WSO2");
        //matches with inbound regex but no api or proxy should be dispatched to super tenant main sequence
        Assert.assertTrue(stringExistsInLog("SUPER_MAIN"), "Dispatch to http://localhost:9090/t/idontexist");

        axis2Client.sendSimpleStockQuoteRequest("http://localhost:9090/t/idoexistassupertenantapi", null, "WSO2");
        Assert.assertTrue(stringExistsInLog("SUPER_TENANT_API_WITH_TENANT_CONTEXT"),
                "Dispatch to http://localhost:9090/t/idoexistassupertenantapi");
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private OMElement getArtifactConfig(String directory, String fileName) throws Exception {
        OMElement synapseConfig = null;
        String path = "artifacts" + File.separator + "ESB" + File.separator
                + "http.inbound.transport" + File.separator + "dispatch" + File.separator + directory + File.separator +
                fileName;
        try {
            synapseConfig = esbUtils.loadResource(path);
        } catch (FileNotFoundException e) {
            throw new Exception("File Location " + path + " may be incorrect", e);
        } catch (XMLStreamException e) {
            throw new XMLStreamException("XML Stream Exception while reading file stream", e);
        }
        return synapseConfig;
    }

    protected boolean stringExistsInLog(String string) throws Exception {
        LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
        boolean logFound = false;
        for (LogEvent item : logs) {
            if (item.getPriority().equals("INFO")) {
                String message = item.getMessage();
                if (message.contains(string)) {
                    logFound = true;
                    break;
                }
            }
        }

        return logFound;
    }

    private Callable<Boolean> isServiceDeployed(final List<OMElement> omSeqList, final List<OMElement> omInboundList,
                                                final List<OMElement> omproxyList) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean isServicesDeployed;
                for(OMElement seqElement : omSeqList) {
                    isServicesDeployed = isSequenceDeployed(seqElement);
                    if (!isServicesDeployed) {
                        return false;
                    }
                }
                for(OMElement inElement : omInboundList) {
                    isServicesDeployed = isInboundEndpointDeployed(inElement);
                    if (!isServicesDeployed) {
                        return false;
                    }
                }
                for(OMElement proxyElement : omproxyList) {
                    isServicesDeployed = isProxyDeployed(proxyElement);
                    if (!isServicesDeployed) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
