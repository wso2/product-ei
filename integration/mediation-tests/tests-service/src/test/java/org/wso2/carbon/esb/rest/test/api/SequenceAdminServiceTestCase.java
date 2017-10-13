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
package org.wso2.carbon.esb.rest.test.api;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.sequences.SequenceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Test SequenceAdmin relation operations
 */
public class SequenceAdminServiceTestCase extends ESBIntegrationTest {
    private SequenceAdminServiceClient seqAdminClient;
    private static final String seq1 =
            "<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"AdminServiceSequence\">\n" + "</sequence>";
    private static final String seq2 =
            "<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"AdminTenantSequence\">\n" + "</sequence>";
    private static final String seqName = "AdminServiceSequence";
    private static final String seqName2 = "AdminTenantSequence";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        seqAdminClient = new SequenceAdminServiceClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence creation service",
          priority = 1)
    public void testCreateSequence() throws Exception {

        OMElement sequenceElm = AXIOMUtil.stringToOM(seq1);
        seqAdminClient.addSequence(sequenceElm);
        seqAdminClient.isExistingSequence(seqName);
        verifySequenceExistence(seqName);

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence list service",
          priority = 2)
    public void getSequence() throws Exception {
        OMElement result = seqAdminClient.getSequence(seqName);
        Assert.assertTrue(result.toString().contains(seqName), "Sequence listing failed");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence creation service for tenant",
          priority = 3)
    public void testCreateSequenceForTenant() throws Exception {

        OMElement sequenceElm = AXIOMUtil.stringToOM(seq2);
        seqAdminClient.addSequenceForTenant(sequenceElm, "carbon.super");
        verifySequenceExistence(seqName2);

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence list service for tenant",
          priority = 4)
    public void testListSequenceForTenant() throws Exception {

        OMElement seqElm = seqAdminClient.getSequenceForTenant(seqName2, "carbon.super");
        Assert.assertNotNull(seqElm, "Unable to list requested sequence");
        seqAdminClient.isExistingSequenceForTenant(seqName2, "carbon.super");
        verifySequenceExistence(seqName2);

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence delete service",
          priority = 5)
    public void deleteSequenceForTenant() throws Exception {
        seqAdminClient.deleteSequenceForTenant(seqName2, "carbon.super");
        Assert.assertFalse(seqAdminClient.isExistingSequence(seqName2), "Sequence not removed");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        seqAdminClient = null;
        super.cleanup();
    }

}
