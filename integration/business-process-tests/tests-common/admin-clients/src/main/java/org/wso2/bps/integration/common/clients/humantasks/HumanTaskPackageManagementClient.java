/*
*Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.bps.integration.common.clients.humantasks;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.integration.common.clients.AuthenticateStubUtil;
import org.wso2.carbon.humantask.stub.mgt.HumanTaskPackageManagementStub;
import org.wso2.carbon.humantask.stub.mgt.PackageManagementException;
import org.wso2.carbon.humantask.stub.mgt.types.*;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

public class HumanTaskPackageManagementClient {

    private static final Log log = LogFactory.getLog(HumanTaskPackageManagementClient.class);

    protected final static String HUMANTASK_PACKAGE_MANAGEMENT_SERVICE = "HumanTaskPackageManagement";


    private HumanTaskPackageManagementStub humanTaskPackageManagementStub = null;

    public HumanTaskPackageManagementClient(String serviceEndPoint, String sessionCookie) throws AxisFault {
        final String packageMgtServiceUrl = serviceEndPoint + HUMANTASK_PACKAGE_MANAGEMENT_SERVICE;
        humanTaskPackageManagementStub = new HumanTaskPackageManagementStub(packageMgtServiceUrl);
        AuthenticateStubUtil.authenticateStub(sessionCookie, humanTaskPackageManagementStub);
    }

    public HumanTaskPackageManagementClient(String serviceEndPoint, String username, String password) throws AxisFault {
        final String packageMgtServiceUrl = serviceEndPoint + HUMANTASK_PACKAGE_MANAGEMENT_SERVICE;
        humanTaskPackageManagementStub = new HumanTaskPackageManagementStub(packageMgtServiceUrl);
        AuthenticateStubUtil.authenticateStub(username, password, humanTaskPackageManagementStub);
    }


    public void unDeployHumanTask(String humantaskPackageName, String aTaskDefinitionName) throws PackageManagementException, InterruptedException, RemoteException {
        humanTaskPackageManagementStub.undeployHumanTaskPackage(humantaskPackageName);
        Thread.sleep(10000);
        //TODO Implement this logic using humanTaskPackageManagementStub.listDeployedPackagesPaginated()
        DeployedTaskDefinitionsPaginated deployedTaskDefinitions = humanTaskPackageManagementStub.listDeployedTaskDefinitionsPaginated(0);
        boolean packageUndeployed = true;
        if (deployedTaskDefinitions != null && deployedTaskDefinitions.getTaskDefinition() != null) {
            for (TaskDefinition_type0 definitionType : deployedTaskDefinitions.getTaskDefinition()) {
                if (definitionType != null && aTaskDefinitionName.equals(definitionType.getTaskName())) {
                    packageUndeployed = false;
                    log.error("Service still exists, Undeployment failed");
                    break;
                }
            }
        }
        if (packageUndeployed) {
            log.info(humantaskPackageName + " has undeployed successfully");
        }

    }

    public Task_type0[] listTasksInPackage(String packageName) throws Exception {
        try {
            return humanTaskPackageManagementStub.listTasksInPackage(packageName);
        } catch (Exception e) {
            String errorMsg = "Unable to get Task list in Package for " + packageName;
            log.error(errorMsg, e);
            throw new Exception(errorMsg, e);
        }
    }

    public DeployedTaskDefinitionsPaginated listDeployedTaskDefinitionsPaginated(int page) throws Exception {
        try {
            return humanTaskPackageManagementStub.listDeployedTaskDefinitionsPaginated(page);
        } catch (Exception e) {
            String errorMsg = "Unable to get list Deployed Task Definitions for page " + page;
            log.error(errorMsg, e);
            throw new Exception(errorMsg, e);
        }
    }

    public TaskInfoType getTaskInfo(QName taskId) throws Exception {
        try {
            return humanTaskPackageManagementStub.getTaskInfo(taskId);
        } catch (Exception e) {
            String errorMsg = "Unable to get task info task ID" + taskId.toString();
            log.error(errorMsg, e);
            throw new Exception(errorMsg, e);
        }
    }

    public HumanTaskPackageDownloadData downloadHumanTaskPackage(String packageName) throws Exception {
        try {
            return humanTaskPackageManagementStub.downloadHumanTaskPackage(packageName);
        } catch (Exception e) {
            String errorMsg = "Unable to get HumanTask Package DownloadData for package name " + packageName;
            log.error(errorMsg, e);
            throw new Exception(errorMsg, e);
        }
    }


}
