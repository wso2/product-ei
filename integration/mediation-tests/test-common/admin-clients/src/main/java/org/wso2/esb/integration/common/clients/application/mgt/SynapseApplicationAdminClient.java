/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.esb.integration.common.clients.application.mgt;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.wso2.carbon.application.mgt.synapse.stub.ExceptionException;
import org.wso2.carbon.application.mgt.synapse.stub.SynapseApplicationAdminStub;
import org.wso2.carbon.application.mgt.synapse.stub.types.carbon.SynapseApplicationMetadata;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import java.rmi.RemoteException;

public class SynapseApplicationAdminClient {
    private Logger log = Logger.getLogger(SynapseApplicationAdminClient.class);

    private SynapseApplicationAdminStub applicationAdminStub;
    private String serviceName = "SynapseApplicationAdmin";


    public SynapseApplicationAdminClient(String backendUrl, String sessionCookie) throws AxisFault {
        String endpoint = backendUrl + serviceName;
        try {
            applicationAdminStub = new SynapseApplicationAdminStub(endpoint);
            AuthenticateStub.authenticateStub(sessionCookie, applicationAdminStub);
        } catch (AxisFault axisFault) {
            String msg = "SynapseApplicationAdminStub Initialization fail ";
            log.error(msg, axisFault);
            throw new AxisFault(msg, axisFault);
        }
    }


    public SynapseApplicationMetadata getSynapseAppData(String appName) throws RemoteException, ExceptionException {
        try {
            return applicationAdminStub.getSynapseAppData(appName);
        } catch (RemoteException e) {
            String msg = "AppName may be incorect";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        } catch (ExceptionException e) {
            String msg = "Unkown Exception occured";
            log.error(msg, e);
            throw new ExceptionException(msg, e);
        }

    }

}
