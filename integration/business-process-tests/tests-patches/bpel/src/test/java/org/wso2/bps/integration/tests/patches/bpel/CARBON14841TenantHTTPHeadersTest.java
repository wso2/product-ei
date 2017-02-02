/*
 *
 *   Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.bps.integration.tests.patches.bpel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.bps.integration.common.clients.bpel.BpelPackageManagementClient;
import org.wso2.bps.integration.common.utils.BPSMasterTest;
import org.wso2.bps.integration.common.utils.RequestSender;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class CARBON14841TenantHTTPHeadersTest extends BPSMasterTest {

    private static final Log log = LogFactory.getLog(CARBON14841TenantHTTPHeadersTest.class);

    BpelPackageManagementClient bpelPackageManagementClient;
    RequestSender requestSender;

    public void setEnvironment() throws Exception {
        init(TestUserMode.TENANT_USER);
        bpelPackageManagementClient = new BpelPackageManagementClient(backEndUrl, sessionCookie);
        requestSender = new RequestSender();
    }

    @BeforeClass(alwaysRun = true, groups = "wso2.bps.testPatches")
    public void deployArtifact()
            throws Exception {
        setEnvironment();
        uploadBpelForTest("AdminServiceInvoke");
        String domain = this.bpsServer.getContextTenant().getDomain();
        String eprUrl = backEndUrl + "/t/" + domain + "/AdminServiceInvoke";
        requestSender.waitForProcessDeployment(eprUrl);
    }

    @Test(groups = {"wso2.bps", "wso2.bps.testPatches"}, description = "CARBON14841TenantHTTPHeaders test case", priority = 0)
    public void process() throws InterruptedException, RemoteException, PackageManagementException,
            MalformedURLException, XMLStreamException, XPathExpressionException {

        String payload = "   <p:AdminServiceInvokeRequest xmlns:p=\"http://wso2.org/bps/sample\">\n" +
                         "      <input xmlns=\"http://wso2.org/bps/sample\">test</input>\n" +
                         "   </p:AdminServiceInvokeRequest>";

        String operation = "process";
        String serviceName = "AdminServiceInvoke";
        String expectedOutput = "RDBMS";
        String domain = this.bpsServer.getContextTenant().getDomain();
        String eprUrl = backEndUrl + "/t/" + domain + "/"+ serviceName;
        requestSender.assertRequest(eprUrl, operation, payload, 1, expectedOutput, true);
    }

    @AfterClass(alwaysRun = true)
    public void removeArtifacts()
            throws PackageManagementException, InterruptedException, RemoteException,
                   LogoutAuthenticationExceptionException {
        bpelPackageManagementClient.undeployBPEL("AdminServiceInvoke");
        this.loginLogoutClient.logout();
    }
}
