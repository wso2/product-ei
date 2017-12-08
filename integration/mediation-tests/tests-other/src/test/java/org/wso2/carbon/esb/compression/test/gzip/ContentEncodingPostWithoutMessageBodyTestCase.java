/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.compression.test.gzip;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;

/**
 * Test case for testing whether it is possible to use content-encoding gzip and invoke a post method without message body.
 */
public class ContentEncodingPostWithoutMessageBodyTestCase extends ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = { "wso2.esb" },
          description = "Testing POST request sent without message body with content-encoding header set to gzip")
    public void sendingPOSTRequestWithContentEncodingGzipTest() throws Exception {
        String url = getApiInvocationURL("contentapi");
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "text/xml");
        post.addHeader("Content-Encoding", "gzip");
        HttpResponse response = httpclient.execute(post);
        assertEquals(response.getStatusLine().getStatusCode(), 202, "Unable to process the post request!");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}