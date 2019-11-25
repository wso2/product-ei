/*
*  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ei.admin.ui;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.server.admin.common.IServerAdmin;
import org.wso2.carbon.server.admin.common.ServerUpTime;
import org.wso2.carbon.server.admin.stub.ServerAdminStub;
import org.wso2.carbon.server.admin.stub.types.carbon.ServerData;
import org.wso2.carbon.utils.CarbonUtils;

import javax.servlet.http.HttpSession;
import java.rmi.RemoteException;

public class ServerAdminClient implements IServerAdmin {

    private static final Log log = LogFactory.getLog(ServerAdminClient.class);

    private ServerAdminStub serverAdminStub;
    private HttpSession session;

    public ServerAdminClient(ConfigurationContext ctx, String serverURL,
                             String cookie, HttpSession session) throws AxisFault {
        this.session = session;
        String serviceEPR = serverURL + "ServerAdmin";
        serverAdminStub = new ServerAdminStub(ctx, serviceEPR);
        ServiceClient client = serverAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        options.setTimeOutInMilliSeconds(10000);
    }

    public ServerAdminClient(String serverURL, String userName, String password) throws AxisFault {
        this.session = null;
        String serviceEPR = serverURL + "ServerAdmin";
        serverAdminStub = new ServerAdminStub(serviceEPR);
        ServiceClient client = serverAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setTimeOutInMilliSeconds(10000);

        CarbonUtils.setBasicAccessSecurityHeaders(userName, password, client);
    }

    public boolean restart() throws Exception {
        boolean result;
        try {
            result = serverAdminStub.restart();
            if (result) {
                invalidateSession();
            }
        } catch (Exception e) {
            String msg = "Cannot restart the server." + e.getMessage();
            log.error(msg, e);
            throw new Exception(msg, e);
        }
        return result;
    }

    public boolean restartGracefully() throws Exception {
        boolean result;
        try {
            result = serverAdminStub.restartGracefully();
            if (result) {
                invalidateSession();
            }
        } catch (Exception e) {
            String msg = "Cannot restart the server. " + e.getMessage();
            log.error(msg, e);
            throw new Exception(msg, e);
        }
        return result;
    }

    public boolean shutdown() throws Exception {
        boolean result;
        try {
            result =  serverAdminStub.shutdown();
            if (result) {
                invalidateSession();
            }
        } catch (RemoteException e) {
            String msg = "Cannot shutdown the server. "  + e.getMessage();
            log.error(msg, e);
            throw new Exception(msg, e);
        }
        return result;
    }

    public boolean shutdownGracefully() throws Exception {
        boolean result;
        try {
            result = serverAdminStub.shutdownGracefully();
            if (result) {
                invalidateSession();
            }
        } catch (RemoteException e) {
            String msg = "Cannot shutdown the server. "  + e.getMessage();
            log.error(msg, e);
            throw new Exception(msg, e);
        }
        return result;
    }

    public org.wso2.carbon.server.admin.common.ServerData getServerData() throws Exception {
        org.wso2.carbon.server.admin.common.ServerData result =
                new org.wso2.carbon.server.admin.common.ServerData();
        try {
            ServerData original = serverAdminStub.getServerData();

            result.setAxis2Location(original.getAxis2Location());
            result.setDbDriverName(original.getDbDriverName());
            result.setDbDriverVersion(original.getDbDriverVersion());
            result.setDbName(original.getDbName());
            result.setDbURL(original.getDbURL());
            result.setDbVersion(original.getDbVersion());
            result.setJavaHome(original.getJavaHome());
            result.setJavaRuntimeName(original.getJavaRuntimeName());
            result.setJavaVersion(original.getJavaVersion());
            result.setJavaVMVendor(original.getJavaVMVendor());
            result.setJavaVMVersion(original.getJavaVMVersion());
            result.setOsName(original.getOsName());
            result.setOsVersion(original.getOsVersion());
            result.setRegistryType(original.getRegistryType());
            result.setRemoteRegistryChroot(original.getRemoteRegistryChroot());
            result.setRemoteRegistryURL(original.getRemoteRegistryURL());
            result.setRepoLocation(original.getRepoLocation());
            result.setServerIp(original.getServerIp());
            result.setServerName(original.getServerName());
            result.setServerStartTime(original.getServerStartTime());
            if (original.getServerUpTime() != null) {
                ServerUpTime serverUpTime = 
                        new ServerUpTime(original.getServerUpTime().getDays(),
                                         original.getServerUpTime().getHours(),
                                         original.getServerUpTime().getMinutes(),
                                         original.getServerUpTime().getSeconds());
                result.setServerUpTime(serverUpTime);
            }
            result.setUserCountry(original.getUserCountry());
            result.setUserHome(original.getUserHome());
            result.setUserName(original.getUserName());
            result.setUserTimezone(original.getUserTimezone());
            result.setCarbonVersion(original.getCarbonVersion());
        } catch (RemoteException e) {
            String msg = "Cannot get server data. Backend service may be unavailable";
            log.error(msg, e);
            throw new Exception(msg, e);
        }
        return result;
    }

    private void invalidateSession() {
        try {
            session.invalidate();
        } catch (Exception ignored) { // Ignore invalidation of invalidated sessions
        }
    }

}
