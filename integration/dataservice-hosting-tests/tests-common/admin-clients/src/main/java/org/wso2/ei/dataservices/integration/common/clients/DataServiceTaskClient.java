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

package org.wso2.ei.dataservices.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.dataservices.task.ui.stub.DSTaskAdminStub;
import org.wso2.carbon.dataservices.task.ui.stub.xsd.DSTaskInfo;
import org.wso2.ei.dataservices.integration.common.clients.utils.AuthenticateStubUtil;

import java.rmi.RemoteException;

public class DataServiceTaskClient {

    private static final Log log = LogFactory.getLog(DataServiceAdminClient.class);

    private final String serviceName = "DSTaskAdmin";
    private DSTaskAdminStub dsTaskAdminStub;

    public DataServiceTaskClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
            dsTaskAdminStub = new DSTaskAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, dsTaskAdminStub);
    }

    public DataServiceTaskClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
            dsTaskAdminStub = new DSTaskAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, dsTaskAdminStub);
    }

    public void deleteTask(String taskName) throws RemoteException {
        try {
            dsTaskAdminStub.deleteTask(taskName);
        } catch (RemoteException e) {
            log.error("Unable to delete the task", e);
            throw new RemoteException("Unable to delete the task", e);
        }
    }

    public String[] getAllTaskNames() throws RemoteException {
        try {
            return dsTaskAdminStub.getAllTaskNames();
        } catch (RemoteException e) {
            log.error("Unable to get all tasks", e);
            throw new RemoteException("Unable get all tasks", e);
        }
    }

    public void scheduleTask(DSTaskInfo dsTaskInfo) throws RemoteException {
        try {
            dsTaskAdminStub.scheduleTask(dsTaskInfo);
        } catch (RemoteException e) {
            log.error("Unable to add new task", e);
            throw new RemoteException("Unable to add new task", e);
        }
    }

    public boolean rescheduleTask(DSTaskInfo dsTaskInfo) throws RemoteException {
        try {
            return dsTaskAdminStub.rescheduleTask(dsTaskInfo);
        } catch (RemoteException e) {
            log.error("Unable to rescheduleTask", e);
            throw new RemoteException("Unable to rescheduleTask", e);
        }
    }
}
