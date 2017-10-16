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
import org.apache.axis2.AxisFault;
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
    private static final String dynamicSeq1 =
            "<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"DynamicServiceSequence\">\n" + "</sequence>";
    private static final String dynamicSeq2 =
            "<sequence xmlns=\"http://ws.apache.org/ns/synapse\" name=\"DynamicUpdatedSequence\">\n" + "</sequence>";
    private static final String seqName = "AdminServiceSequence";
    private static final String seqName2 = "AdminTenantSequence";
    private static final String dynamicSeqName = "DynamicServiceSequence";
    private static final String updatedSeqName = "DynamicUpdatedSequence";
    private static final String dynamicKey = "conf:/dynamicSeq";
    private static final String tenantDomain = "carbon.super";

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
        int prevSeqCount = seqAdminClient.getSequenceCount();
        OMElement sequenceElm = AXIOMUtil.stringToOM(seq2);
        seqAdminClient.addSequenceForTenant(sequenceElm, tenantDomain);
        int latestSeqCount = seqAdminClient.getSequenceCount();
        verifySequenceExistence(seqName2);
        Assert.assertTrue(prevSeqCount < latestSeqCount, "Sequence count is not increased");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence list service for tenant",
          priority = 4)
    public void testListSequenceForTenant() throws Exception {

        OMElement seqElm = seqAdminClient.getSequenceForTenant(seqName2, tenantDomain);
        Assert.assertNotNull(seqElm, "Unable to list requested sequence");
        seqAdminClient.isExistingSequenceForTenant(seqName2, tenantDomain);
        verifySequenceExistence(seqName2);

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence statistics enable service",
          priority = 5)
    public void testSequenceStatisticsEnabler() throws Exception {

        seqAdminClient.enableStatistics(seqName2);
        Assert.assertTrue(seqAdminClient.getSequence(seqName2).toString().contains("statistics=\"enable\""),
                "Stats not enabled for sequence");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence statistics disable service",
          priority = 6)
    public void testSequenceStatisticsDisabler() throws Exception {

        seqAdminClient.disableStatistics(seqName2);
        Assert.assertFalse(seqAdminClient.getSequence(seqName2).toString().contains("statistics=\"enable\""),
                "Stats not disabled for sequence");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence tracing enable service",
          priority = 7)
    public void testSequenceTracingEnabler() throws Exception {

        seqAdminClient.enableTracing(seqName2);
        Assert.assertTrue(seqAdminClient.getSequence(seqName2).toString().contains("trace=\"enable\""),
                "Tracing not enabled for sequence");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence tracing disable service",
          priority = 8)
    public void testSequenceTracingDisabler() throws Exception {

        seqAdminClient.disableTracing(seqName2);
        Assert.assertFalse(seqAdminClient.getSequence(seqName2).toString().contains("trace=\"enable\""),
                "Tracing not disabled for sequence");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test sequence delete service",
          priority = 9)
    public void deleteSequenceForTenant() throws Exception {
        seqAdminClient.deleteSequenceForTenant(seqName2, tenantDomain);
        Assert.assertFalse(seqAdminClient.isExistingSequence(seqName2), "Sequence not removed");
    }

    @Test(groups = { "wso2.esb" },
          description = "Test listing invalid service",
          priority = 10)
    public void listNonExistentSequence() throws Exception {
        try {
            seqAdminClient.getSequence(seqName2);
            Assert.fail("Expected exception was not thrown when listing invalid sequence");
        } catch (AxisFault e) {
            Assert.assertTrue(e.getMessage().contains("Couldn't get the Synapse Configuration to get the sequence"),
                    "Expected message not available in error");
        }
    }

    @Test(groups = { "wso2.esb" },
          description = "Test  dynamic sequence creation service",
          priority = 11)
    public void testCreateDynamicSequence() throws Exception {
        int prevDynamicCount = seqAdminClient.getDynamicSequenceCount();
        OMElement sequenceElm = AXIOMUtil.stringToOM(dynamicSeq1);
        seqAdminClient.addDynamicSequence(dynamicKey, sequenceElm);
        int newDynamicCount = seqAdminClient.getDynamicSequenceCount();
        Assert.assertTrue(newDynamicCount > prevDynamicCount, "Dynamic sequence not added");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test  dynamic sequence get service",
          priority = 12)
    public void testListDynamicSequence() throws Exception {
        OMElement seq = seqAdminClient.getDynamicSequence(dynamicKey);
        Assert.assertNotNull(seq, "Unable to list dynamic sequence");
        Assert.assertTrue(seq.toString().contains(dynamicSeqName), "Dynamic sequence was not listed");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test  dynamic sequence update service",
          priority = 13)
    public void testUpdateDynamicSequence() throws Exception {
        OMElement sequenceElm = AXIOMUtil.stringToOM(dynamicSeq2);
        seqAdminClient.updateDynamicSequence(dynamicKey, sequenceElm);
        Assert.assertTrue(seqAdminClient.getDynamicSequence(dynamicKey).toString().contains(updatedSeqName),
                "Dynamic sequence was not updated");

    }

    @Test(groups = { "wso2.esb" },
          description = "Test  dynamic sequence delete service",
          priority = 14)
    public void testDeleteDynamicSequence() throws Exception {
        int prevDynamicCount = seqAdminClient.getDynamicSequenceCount();
        seqAdminClient.deleteDynamicSequence(dynamicKey);
        int newDynamicCount = seqAdminClient.getDynamicSequenceCount();
        Assert.assertTrue(prevDynamicCount > newDynamicCount, "Dynamic sequence not removed");

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        seqAdminClient = null;
        super.cleanup();
    }

}
