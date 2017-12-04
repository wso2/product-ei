/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.esb.passthru.transport.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.IOException;

/**
 * Insert a comma in the 'Expires' cookie header and check whether it is processed.
 */
public class CookieHeaderExpiresHavingCommaTestCase extends ESBIntegrationTest {

    @BeforeClass
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.esb",
          description = "Test SetCookieHeader With Expires having a comma")
    public void testSetCookieHeaderWithExpires() throws IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(getApiInvocationURL("cookieHeaderTestAPI"));
        HttpResponse httpResponse = httpclient.execute(httpGet);
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200,
                "Request failed. Response received is : " + httpResponse.toString());
    }

    @AfterClass
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}