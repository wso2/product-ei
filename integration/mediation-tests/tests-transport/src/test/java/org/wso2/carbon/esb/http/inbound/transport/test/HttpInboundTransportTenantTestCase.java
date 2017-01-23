/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.esb.http.inbound.transport.test;


import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

public class HttpInboundTransportTenantTestCase extends ESBIntegrationTest{

    private SampleAxis2Server axis2Server;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init(TestUserMode.TENANT_ADMIN);


        axis2Server = new SampleAxis2Server("test_axis2_server_9000.xml");
        axis2Server.deployService(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
        axis2Server.start();

        addSequence(getArtifactConfig("TestIn.xml"));
        addSequence(getArtifactConfig("reciveSeq.xml"));
        addSequence(getArtifactConfig("TestOut.xml"));
        addInboundEndpoint(getArtifactConfig("synapse.xml"));
        addApi(getArtifactConfig("Test.xml"));
        addInboundEndpoint(getArtifactConfig("apidispatch.xml"));
    }

    @Test(groups = "wso2.esb", description = "Inbound Http  test case for tenant" )
    public void inboundHttpTest() throws AxisFault {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:8081/t/wso2.com/", null, "IBM");
        Assert.assertNotNull(response);
        Assert.assertEquals("getQuoteResponse", response.getLocalName());
    }

    @Test(groups = "wso2.esb", description = "Inbound Http  test case for tenant API dispatching" )
    public void inboundHttpAPITest() throws AxisFault {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest("http://localhost:8082/t/wso2.com/test/map", null, "IBM");
        Assert.assertNotNull(response);
        Assert.assertEquals("getQuoteResponse", response.getLocalName());
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if (axis2Server.isStarted()) {
            axis2Server.stop();
        }
        axis2Server=null;
        super.cleanup();
    }

    private OMElement getArtifactConfig(String fileName) throws Exception {
        OMElement synapseConfig = null;
        String path = "artifacts" + File.separator + "ESB" + File.separator
                      + "http.inbound.transport" + File.separator + fileName;
        try {
            synapseConfig = esbUtils.loadResource(path);
        } catch (FileNotFoundException e) {
            throw new Exception("File Location " + path + " may be incorrect", e);
        } catch (XMLStreamException e) {
            throw new XMLStreamException("XML Stream Exception while reading file stream", e);
        }
        return synapseConfig;
    }

}
