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
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.mediation.library.stub.upload.types.carbon.LibraryFileItem;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import java.rmi.RemoteException;

public class MediationLibraryUploaderClient {

    private final String serviceName = "MediationLibraryUploader";

    private MediationLibraryUploaderStub mediationLibraryUploaderStub;

    public MediationLibraryUploaderClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        mediationLibraryUploaderStub = new MediationLibraryUploaderStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, mediationLibraryUploaderStub);
    }

    public MediationLibraryUploaderClient(String backEndUrl, String userName, String password) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        mediationLibraryUploaderStub = new MediationLibraryUploaderStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, mediationLibraryUploaderStub);
    }

    public void uploadConnector(LibraryFileItem[] libraryFileItems) throws RemoteException {
        mediationLibraryUploaderStub.uploadLibrary(libraryFileItems);
    }

}
