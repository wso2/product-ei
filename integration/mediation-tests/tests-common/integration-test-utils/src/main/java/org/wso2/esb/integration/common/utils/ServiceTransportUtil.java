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

package org.wso2.esb.integration.common.utils;

import org.apache.commons.lang.ArrayUtils;
import org.wso2.esb.integration.common.clients.service.mgt.ServiceAdminClient;


public class ServiceTransportUtil {

    public static boolean isHttpTransportEnable(String backEndUrl, String sessionCookie,
                                         String serviceName)
            throws Exception {
        ServiceAdminClient serviceAdminClient
                = new ServiceAdminClient(backEndUrl, sessionCookie);
        return ArrayUtils.contains(serviceAdminClient.getExposedTransports(serviceName), "http");
    }

    public static String[] getExposedTransports(String backEndUrl, String sessionCookie,
                                         String serviceName)
            throws Exception {
        ServiceAdminClient serviceAdminClient
                = new ServiceAdminClient(backEndUrl, sessionCookie);
        return serviceAdminClient.getExposedTransports(serviceName);
    }

// TODO:Enable after transport management handler imlpementation
//    public static void addExposedTransports(String backEndUrl, String sessionCookie, String serviceName,
//                                     String transport)
//            throws Exception {
//        TransportManagementAdminServiceClient transportManagementAdminServiceClient
//                = new TransportManagementAdminServiceClient(backEndUrl, sessionCookie);
//        transportManagementAdminServiceClient.addExposedTransports(serviceName, transport);
//
//    }
//
//    public static void addTransportHttp(String backEndUrl, String sessionCookie, String serviceName)
//            throws Exception {
//        TransportManagementAdminServiceClient transportManagementAdminServiceClient
//                = new TransportManagementAdminServiceClient(backEndUrl, sessionCookie);
//        transportManagementAdminServiceClient.addExposedTransports(serviceName, "http");
//
//    }
}
