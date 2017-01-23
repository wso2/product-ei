/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.esb.integration.common.clients.connector;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceException;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import java.rmi.RemoteException;

public class MediationLibraryAdminServiceClient {

    private final String serviceName = "MediationLibraryAdminService";

    private MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub;


    public MediationLibraryAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        mediationLibraryAdminServiceStub = new MediationLibraryAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, mediationLibraryAdminServiceStub);
    }

    public MediationLibraryAdminServiceClient(String backEndUrl, String userName, String password) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        mediationLibraryAdminServiceStub = new MediationLibraryAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, mediationLibraryAdminServiceStub);

    }

    public void updateStatus(String libQName, String libName, String packageName, String status)
            throws RemoteException {
        mediationLibraryAdminServiceStub.updateStatus(libQName, libName, packageName, status);
    }

    public void deleteLibrary(String libQualifiedName) throws MediationLibraryAdminServiceException, RemoteException {
        mediationLibraryAdminServiceStub.deleteLibrary(libQualifiedName);
    }

    public String[] getAllImports() throws RemoteException {
        return mediationLibraryAdminServiceStub.getAllImports();
    }

}
