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
package org.wso2.ei.businessprocess.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.businessprocess.integration.common.clients.bpel.BpelUploaderClient;
import org.wso2.ei.businessprocess.integration.common.clients.bpmn.BPMNUploaderClient;
import org.wso2.ei.businessprocess.integration.common.clients.humantasks.HumanTaskUploaderClient;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.bpel.stub.mgt.PackageManagementException;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

public class BPSMasterTest {
    private static final Log log = LogFactory.getLog(BPSMasterTest.class);

    protected AutomationContext bpsServer;
    protected String sessionCookie = null;
    protected String backEndUrl = null;
    protected String serviceUrl = null;
    protected SecurityAdminServiceClient securityAdminServiceClient;
    protected BpelUploaderClient bpelUploaderClient;
    protected HumanTaskUploaderClient humanTaskUploaderClient;
    protected BPMNUploaderClient bpmnUploaderClient;
    protected LoginLogoutClient loginLogoutClient;


    protected void init(TestUserMode testUserMode) throws Exception {
        bpsServer = new AutomationContext("BPS", testUserMode);
        loginLogoutClient = new LoginLogoutClient(bpsServer);
        sessionCookie = loginLogoutClient.login();
        backEndUrl = bpsServer.getContextUrls().getBackEndUrl();
        serviceUrl = bpsServer.getContextUrls().getServiceUrl();
        bpelUploaderClient = new BpelUploaderClient(backEndUrl, sessionCookie);
        humanTaskUploaderClient = new HumanTaskUploaderClient(backEndUrl, sessionCookie);
        bpmnUploaderClient = new BPMNUploaderClient(backEndUrl, sessionCookie);
    }

    protected void init() throws Exception {
        bpsServer = new AutomationContext("BPS", TestUserMode.SUPER_TENANT_ADMIN);
        loginLogoutClient = new LoginLogoutClient(bpsServer);
        sessionCookie = loginLogoutClient.login();
        backEndUrl = bpsServer.getContextUrls().getBackEndUrl();
        serviceUrl = bpsServer.getContextUrls().getServiceUrl();
        bpelUploaderClient = new BpelUploaderClient(backEndUrl, sessionCookie);
        humanTaskUploaderClient = new HumanTaskUploaderClient(backEndUrl, sessionCookie);
        bpmnUploaderClient = new BPMNUploaderClient(backEndUrl, sessionCookie);
    }

    protected void init(String domainKey, String userKey) throws Exception {
        bpsServer = new AutomationContext("BPS", "bpsServerInstance0001", domainKey, userKey);
        loginLogoutClient = new LoginLogoutClient(bpsServer);
        sessionCookie = loginLogoutClient.login();
        backEndUrl = bpsServer.getContextUrls().getBackEndUrl();
        serviceUrl = bpsServer.getContextUrls().getServiceUrl();
        bpelUploaderClient = new BpelUploaderClient(backEndUrl, sessionCookie);
        humanTaskUploaderClient = new HumanTaskUploaderClient(backEndUrl, sessionCookie);
        bpmnUploaderClient = new BPMNUploaderClient(backEndUrl, sessionCookie);
    }

    protected void uploadBpelForTest(String bpelFileName) throws Exception {
        String dirPath = FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS +
                File.separator + BPSTestConstants.DIR_BPEL;
        uploadBpelForTest(bpelFileName, dirPath);

    }

    protected void uploadBpelForTest(String bpelPackageName, String artifactLocation) throws RemoteException, InterruptedException, PackageManagementException {
        bpelUploaderClient.deployBPEL(bpelPackageName, artifactLocation);
    }

    protected void uploadHumanTaskForTest(String humantaskName) throws Exception {
        uploadHumanTaskForTest(humantaskName, FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS
                + File.separator + BPSTestConstants.DIR_HUMAN_TASK);
    }

    protected void uploadBPMNForTest(String bpmnPackageNam) throws Exception {
        uploadBPMNForTest(bpmnPackageNam, FrameworkPathUtil.getSystemResourceLocation() + BPSTestConstants.DIR_ARTIFACTS
                + File.separator + BPSTestConstants.DIR_BPMN);
    }

    protected void uploadHumanTaskForTest(String taskPackageName, String taskPackageLocation) throws InterruptedException, RemoteException, org.wso2.carbon.humantask.stub.mgt.PackageManagementException {
        humanTaskUploaderClient.deployHumantask(taskPackageName, taskPackageLocation);
    }

    protected void uploadBPMNForTest(String taskPackageName, String taskPackageLocation) throws InterruptedException, RemoteException, org.wso2.carbon.humantask.stub.mgt.PackageManagementException {
        bpmnUploaderClient.deployBPMN(taskPackageName, taskPackageLocation);
    }

    protected String getServiceUrl(String serviceName) throws XPathExpressionException {
        return bpsServer.getContextUrls().getServiceUrl() + "/" + serviceName;
    }


}
