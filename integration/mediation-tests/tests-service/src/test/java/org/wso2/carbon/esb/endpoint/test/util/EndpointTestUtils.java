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
package org.wso2.carbon.esb.endpoint.test.util;

import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class EndpointTestUtils {
    public static void cleanupDefaultEndpoint(String endpointName,
                                              EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {
        String[] endpointNames = endPointAdminClient.getEndpointNames();
        List endpointList;
        if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
            endpointList = Arrays.asList(endpointNames);
            if (endpointList.contains(endpointName)) {
                endPointAdminClient.deleteEndpoint(endpointName);
            }
        }
    }

    public static void cleanupDynamicEndpoint(String endpointPath,
                                              EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {
        String[] endpointNames = endPointAdminClient.getDynamicEndpoints();
        List endpointList;
        if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
            endpointList = Arrays.asList(endpointNames);

            if (endpointList.contains(endpointPath)) {
                endPointAdminClient.deleteDynamicEndpoint(endpointPath);
            }

        }
    }

    public static void assertDynamicEndpointAddition(String path, int beforeCount,
                                                     EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {

        int afterCount = endPointAdminClient.getDynamicEndpointCount();
        assertEquals(afterCount - beforeCount, 1, "Endpoint addition failed. Endpoint count mismatched");

        String[] endpointNames = endPointAdminClient.getDynamicEndpoints();
        if (endpointNames != null && endpointNames.length > 0 && endpointNames[0] != null) {
            assertTrue(Arrays.asList(endpointNames).contains(path), path + " Endpoint not found in Dynamic Endpoint List");
        } else {
            fail("Dynamic endpoint hasn't been added successfully");
        }
    }

    public static void assertDefaultEndpointAddition(String endpointName, int beforeCount,
                                                     EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {

        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(afterCount - beforeCount, 1, "Endpoint addition failed. Endpoint count mismatched");

        String[] endpoints = endPointAdminClient.getEndpointNames();
        if (endpoints != null && endpoints.length > 0 && endpoints[0] != null) {
            List endpointList = Arrays.asList(endpoints);
            assertTrue(endpointList.contains(endpointName), endpointName + " Endpoint not found in Default Endpoint List");
        } else {
            fail("Endpoint has not been added to the system properly");
        }
    }

    public static void assertDynamicEndpointDeletion(int beforeCount,
                                                     EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {
        int afterCount = endPointAdminClient.getDynamicEndpointCount();
        assertEquals(beforeCount - afterCount, 1, "Endpoint deletion failed. Endpoint count mismatched");
    }

    public static void assertDefaultEndpointDeletion(int beforeCount,
                                                     EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {
        int afterCount = endPointAdminClient.getEndpointCount();
        assertEquals(beforeCount - afterCount, 1, "Endpoint deletion failed. Endpoint count mismatched");
    }

    public static void enableEndpointStatistics(String endpointName,
                                                EndPointAdminClient endPointAdminClient)
            throws RemoteException, EndpointAdminEndpointAdminException {
        endPointAdminClient.enableEndpointStatistics(endpointName);
        String endpoint = endPointAdminClient.getEndpointConfiguration(endpointName);
        assertTrue(endpoint.contains("statistics=\"enable\""), "Statistics not enabled");
    }
}
