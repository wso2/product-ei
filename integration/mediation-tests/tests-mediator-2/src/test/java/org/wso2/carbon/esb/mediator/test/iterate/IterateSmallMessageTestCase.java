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

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FixedSizeSymbolGenerator;

/* Tests sending different number of small messages through iterate mediator */

public class IterateSmallMessageTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/iterate/simple_iterator.xml");

    }

    @Test(groups = "wso2.esb", description = "Tests small message in small number ~20")
    public void testSmallNumbers() throws Exception {

        String symbol = FixedSizeSymbolGenerator.generateMessageKB(5);
        OMElement response = null;
        for (int i = 0; i < 20; i++) {
            response =
                    axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                                                            null, symbol);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.toString().contains("WSO2"));
        }
        symbol = null;
    }

    @Test(groups = "wso2.esb", description = "Tests small message in small number ~100")
    public void testLargeNumbers() throws Exception {
        String symbol = FixedSizeSymbolGenerator.generateMessageKB(5);
        OMElement response = null;
        for (int i = 0; i < 25; i++) {
            response =
                    axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                                                            null, symbol);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.toString().contains("WSO2"));
        }
        symbol = null;
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
