/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.bps.samples.internal;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;
import org.wso2.carbon.utils.NetworkUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

//Registry client
public class RegistryService {
    private static Properties prop = new Properties();

    //Get registry properties from the regPath location
    public static Properties getRegistryProperties(String location) {
        ResourceAdminServiceStub resourceAdminServiceStub;
        Properties properties = null;
        try {
            prop.load(new FileInputStream(System.getProperty("user.dir") + File.separator + "repository" + File.separator + "conf" + File.separator + "PropertyReaderExt.properties"));
            String clientTrustStorePath = prop.getProperty("clientTrustStorePath");
            String trustStorePassword = prop.getProperty("clientTrustStorePassword");
            String trustStoreType = prop.getProperty("clientTrustStoreType");
            setKeyStore(clientTrustStorePath, trustStorePassword, trustStoreType);
            String resourceAdminServiceURL = "https://" + prop.getProperty("bps.mgt.hostname") + ":" + prop.getProperty("bps.mgt.port") + "/services/ResourceAdminService";
            resourceAdminServiceStub = new ResourceAdminServiceStub(resourceAdminServiceURL);
            ServiceClient client = resourceAdminServiceStub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(HTTPConstants.COOKIE_STRING, login());
            resourceAdminServiceStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(600000);

            File temp = new File("tmp" + File.separator + "temp.properties");
            FileWriter fw = new FileWriter(temp);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(resourceAdminServiceStub.getTextContent(location).toString());
            bw.close();
            properties = new Properties();
            properties.load(new FileInputStream(temp.getAbsolutePath()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    //Setup key store according to the config.properties
    private static void setKeyStore(String clientTrustStorePath, String trustStorePassword, String trustStoreType) {

        System.setProperty("javax.net.ssl.trustStore", clientTrustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
    }

    //Creates the login session BPS login
    public static String login() throws Exception {

        AuthenticationAdminStub authenticationAdminStub;
        String authenticationAdminServiceURL = "https://" + prop.getProperty("bps.mgt.hostname") + ":" + prop.getProperty("bps.mgt.port") + "/services/AuthenticationAdmin";
        authenticationAdminStub = new AuthenticationAdminStub(authenticationAdminServiceURL);

        ServiceClient client = authenticationAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);

        String userName = prop.getProperty("wso2.bps.username");
        String password = prop.getProperty("wso2.bps.password");
        String hostName = NetworkUtils.getLocalHostname();

        authenticationAdminStub.login(userName, password, hostName);

        ServiceContext serviceContext = authenticationAdminStub.
                _getServiceClient().getLastOperationContext().getServiceContext();

        return (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
    }
}
