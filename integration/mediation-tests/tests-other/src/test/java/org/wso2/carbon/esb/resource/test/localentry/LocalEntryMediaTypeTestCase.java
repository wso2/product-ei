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
package org.wso2.carbon.esb.resource.test.localentry;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.localentry.LocalEntriesAdminClient;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

public class LocalEntryMediaTypeTestCase extends ESBIntegrationTest{
    private LocalEntriesAdminClient localEntriesAdminClient;
    private Log log = LogFactory.getLog(LocalEntryMediaTypeTestCase.class);
    private ResourceAdminServiceClient resourceAdmin;
    private final String ENTRY_NAME = "mediaTypeTest";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        localEntriesAdminClient = new LocalEntriesAdminClient(contextUrls.getBackEndUrl(),getSessionCookie());
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test Local Entry media type", enabled = false)
    public void testLocalEntryMediaType() throws Exception {
        OMElement localEntry = AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                                    "<localEntry xmlns=\"http://ws.apache.org/ns/synapse\" key=\"" + ENTRY_NAME +
                                                    "\" src=\"file:repository/samples/resources/transform/transform.xslt\"/>");
        localEntriesAdminClient.addLocalEntry(localEntry);
        //add Local Entry is a a asynchronous call, it will take some time to write to a registry
        Thread.sleep(10000);
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/local-entries/" + ENTRY_NAME);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for proxy service");
    }

    @AfterClass
    public void destroy() throws Exception {
        localEntriesAdminClient.deleteLocalEntry(ENTRY_NAME);
        super.cleanup();
    }
}
