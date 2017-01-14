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

package org.wso2.carbon.esb.https.inbound.transport.test;


import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.SecureServiceClient;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

public class HttpsInboundTransportTestCase extends ESBIntegrationTest {

    private SecureServiceClient secureAxisServiceClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        secureAxisServiceClient = new SecureServiceClient();
        addSequence(getArtifactConfig("TestIn.xml"));
        addSequence(getArtifactConfig("reciveSeq.xml"));
        addSequence(getArtifactConfig("TestOut.xml"));
        addInboundEndpoint(getArtifactConfig("synapse.xml"));
        Thread.sleep(30000);

    }

    @Test(groups = "wso2.esb", description = "" )
    public void testSecureProxyEndPointThruUri() throws Exception {

        OMElement response = secureAxisServiceClient.
                   sendSecuredStockQuoteRequest(userInfo, "https://localhost:8081/", "WSO2", false);
        Assert.assertNotNull(response);
        Assert.assertEquals("getQuoteResponse", response.getLocalName());
    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }


    private OMElement getArtifactConfig(String fileName) throws Exception {
        OMElement synapseConfig = null;
        String path = "artifacts" + File.separator + "ESB" + File.separator
                      + "https.inbound.transport" + File.separator + fileName;
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
