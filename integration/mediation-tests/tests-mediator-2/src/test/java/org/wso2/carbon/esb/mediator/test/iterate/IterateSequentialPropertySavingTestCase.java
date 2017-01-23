/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.mediator.test.iterate;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * This Test Case Tests the issue reported in the Public JIRA https://wso2.org/jira/browse/ESBJAVA-1740
 *
 * Description :
 * When using Iterate mediator, it is possible to add sequential='true' parameter in that. Once we added
 * this parameter and save it from the UI , it got removed. That bug fixed with the revision 152691. This
 * test case added to track that problem.
 * */
public class IterateSequentialPropertySavingTestCase extends ESBIntegrationTest {
    private ProxyServiceAdminClient proxyServiceAdminClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        addProxyService(esbUtils.loadResource("/artifacts/ESB/mediatorconfig/iterate/iterateSequentialTruePropertyWithOutProperty.xml"));
        proxyServiceAdminClient = new ProxyServiceAdminClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }



    @Test(groups = "wso2.esb", description = "Tests updating the sequential='true' property")
    public void testSavingSequentialTrueProperty() throws Exception {
        proxyServiceAdminClient.updateProxy(esbUtils.loadResource("/artifacts/ESB/mediatorconfig/iterate/iterateSequentialTrueProperty.xml"));
        isProxyDeployed("IterateSequentialTrueCheckProxy");
        String afterConfig  = proxyServiceAdminClient.getProxyDetails("IterateSequentialTrueCheckProxy").getInSeqXML();
        Assert.assertTrue(afterConfig.contains("sequential=\"true\""), "Synapse Configuration doesn't contain sequential=true after updating");
    }


    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        super.cleanup();
    }

}
