/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.mediators.aggregate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This test class will test the correlateOn expression of aggregate mediator.
 * This will evaluate correlateOn expression and aggregate responses into the result.
 */
public class ESBJAVA5103CorrelateOnExpressionTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                + "mediatorconfig" + File.separator + "aggregate" + File.separator
                + "CorrelateOnExpressionTest.xml");
    }

    @Test(groups = "wso2.esb", description = "Test CorrelateOn in Aggregate mediator ")
    public void testAggregateWithCorrelateExpression() throws IOException{
        String expectedOutput1 = "<result><value>value1</value><value>value2</value></result>";
        String expectedOutput2 = "<result><value>value2</value><value>value1</value></result>";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(getApiInvocationURL("testAggregate"));

        try {
            HttpResponse httpResponse = httpclient.execute(httpget);
            HttpEntity entity = httpResponse.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String result = "";
            String line;
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            Assert.assertTrue(expectedOutput1.equals(result) || expectedOutput2.equals(result), "Aggregated response is not correct.");
        }
        finally {
            httpclient.clearRequestInterceptors();
        }
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        super.cleanup();
    }
}

