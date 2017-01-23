/*
*  Copyright (c) WSO2 Inc. (http://wso2.com) All Rights Reserved.

  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

package org.wso2.carbon.esb.localentry.test;

import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.localentry.LocalEntriesAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SourceURLLocalEntryAdditionTestCase extends ESBIntegrationTest {

    private static final String ENTRY_NAME = "SourceURLTestEntry";

    private LocalEntriesAdminClient localEntryAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        localEntryAdminServiceClient = new LocalEntriesAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = "wso2.esb", description = "Test adding a Source url Local Entry")
    public void testSourceURLLocalEntryAddition()
            throws Exception {

        String entryNames = localEntryAdminServiceClient.getEntryNamesString();
        //If an Entry by the name ENTRY_NAME already exists
        if (entryNames != null && entryNames.contains(ENTRY_NAME)) {
            //Remove the Entry
            assertTrue(localEntryAdminServiceClient.deleteLocalEntry(ENTRY_NAME));
        }

        int before = localEntryAdminServiceClient.getEntryDataCount();
        addLocalEntry(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                                      "<localEntry xmlns=\"http://ws.apache.org/ns/synapse\" key=\"" + ENTRY_NAME +
                                                      "\" src=\"file:repository/samples/resources/transform/transform.xslt\"/>"));
        int after = localEntryAdminServiceClient.getEntryDataCount();
        assertEquals(1, after - before);

        entryNames = localEntryAdminServiceClient.getEntryNamesString();
        //The Entry should be added to the entries list
        assertTrue(entryNames.contains(ENTRY_NAME));
    }


    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        localEntryAdminServiceClient = null;
        super.cleanup();
    }

}
