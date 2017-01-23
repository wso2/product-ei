/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediator.test.iterate;

import java.util.Iterator;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;


/**
 * Tests a sequence with a iterate mediator that calls only an 'anonymous'
 * sequences and named sequences in the iterate target
 */

public class IterateTargetSequenceTypeTestCase extends ESBIntegrationTest {

    private IterateClient client;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        client = new IterateClient();
    }

    @Test(groups = "wso2.esb", description = "Tests for anaonymous sequences")
    public void testAnonymousSequences() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/simple_iterator.xml");
        String response = client.getMultipleResponse(getMainSequenceURL(), "WSO2", 2);
        Assert.assertNotNull(response);
        OMElement envelope = client.toOMElement(response);
        OMElement soapBody = envelope.getFirstElement();
        Iterator iterator =
                soapBody.getChildrenWithName(new QName("http://services.samples",
                                                       "getQuoteResponse"));
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2"));
        }
        Assert.assertEquals(i , 2, "Child Element Count mismatched");

    }

    @Test(groups = "wso2.esb", description = "Tests for named sequences")
    public void testNamedSequences() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/iterate_named_sequence.xml");
        String response = client.getMultipleResponse(getMainSequenceURL(), "WSO2", 2);
        Assert.assertNotNull(response);
        OMElement envelope = client.toOMElement(response);
        OMElement soapBody = envelope.getFirstElement();
        Iterator iterator =
                soapBody.getChildrenWithName(new QName("http://services.samples",
                                                       "getQuoteResponse"));
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            OMElement getQuote = (OMElement) iterator.next();
            Assert.assertTrue(getQuote.toString().contains("WSO2"));
        }
        Assert.assertTrue(i == 2);

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        client = null;
        super.cleanup();
    }

}
