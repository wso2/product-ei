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
package org.wso2.esb.integration.common.clients.rest.api;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.rest.api.stub.RestApiAdminAPIException;
import org.wso2.carbon.rest.api.stub.RestApiAdminStub;
import org.wso2.carbon.rest.api.stub.types.carbon.APIData;
import org.wso2.esb.integration.common.clients.client.utils.AuthenticateStub;

import java.rmi.RemoteException;

public class RestApiAdminClient {
    private static final Log log = LogFactory.getLog(RestApiAdminClient.class);

    private RestApiAdminStub restApiAdminStub;
    private final String serviceName = "RestApiAdmin";

    public RestApiAdminClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        restApiAdminStub = new RestApiAdminStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, restApiAdminStub);
    }

    public RestApiAdminClient(String backEndUrl, String userName, String password) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        restApiAdminStub = new RestApiAdminStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, restApiAdminStub);
    }

    public boolean add(OMElement apiData) throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.addApiFromString(apiData.toString());
    }

    public boolean deleteApi(String apiName) throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.deleteApi(apiName);
    }

    public boolean deleteApiForTenant(String apiName, String tenant) throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.deleteApiForTenant(apiName, tenant);
    }

    public String[] getApiNames() throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.getApiNames();
    }

    public String getServerContext() throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.getServerContext();
    }

    public boolean addAPI(APIData apiData) throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.addApi(apiData);
    }

    public boolean addAPIFromTenant(String apiData, String tenantDomain)
            throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.addApiForTenant(apiData, tenantDomain);
    }

    public APIData getAPIbyName(String apiName) throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.getApiByName(apiName);
    }

    public APIData getAPIForTenantByName(String apiName, String tenantDomain)
            throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.getApiForTenant(apiName, tenantDomain);
    }

    public boolean updateAPIFromString(String apiName, String updateData)
            throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.updateApiFromString(apiName, updateData);
    }

    public boolean updateAPIForTenant(String apiName, String updateData, String tenant)
            throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.updateApiForTenant(apiName, updateData, tenant);
    }

    public void deleteAllApis() throws RestApiAdminAPIException, RemoteException {
        restApiAdminStub.deleteAllApi();
    }

    public int getAPICount() throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.getAPICount();
    }

    public String getAPISource(APIData apiData) throws RestApiAdminAPIException, RemoteException {
        return restApiAdminStub.getApiSource(apiData);
    }
}
