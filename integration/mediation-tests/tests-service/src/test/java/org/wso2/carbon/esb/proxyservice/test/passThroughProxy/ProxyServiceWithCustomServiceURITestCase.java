/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *//*

package org.wso2.carbon.esb.proxyservice.test.passThroughProxy;

import java.io.File;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.service.mgt.ServiceAdminClient;
import org.wso2.carbon.automation.core.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.core.annotations.SetEnvironment;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.service.mgt.stub.ServiceAdminException;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ProxyServiceWithCustomServiceURITestCase extends ESBIntegrationTest {

    */
/**
     * This definition will create a proxy service with 'ServiceURI' parameter
     * and exposes it over https
     *//*

    private final String PROXY_DEFINITION_HTTPS_CUSTOM_URI =
            "/artifacts/ESB/proxyconfig/proxy/passThroughProxy/customServiceURI/custom_service_uri_enabling_only_https.xml";

    */
/**
     * This configuration contains CustomURIBasedDispatcher in Dispatch phase of
     * "InFlow"
     *//*

    private final String AXIS2_CONFIG_URI_BASED_DISPATCH =
            "/proxyconfig/proxy/passThroughProxy/customServiceURI/axis2.xml";

    */
/**
     * Custom URI Fragment defined in Proxy definition
     *//*

    private final String CUSTOM_URI_FRAGMENT = "/CustomURL/Part1/Part2";
    private ServerConfigurationManager configurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        configurationManager = new ServerConfigurationManager(esbServer.getBackEndUrl());
        File customAxisConfig = new File(getESBResourceLocation() + AXIS2_CONFIG_URI_BASED_DISPATCH);

        // restart the esb with new customized axis2 configuration
        configurationManager.applyConfiguration(customAxisConfig);
        super.init(); // After restarting, this will establish the sessions.

        // deploy a proxy service in esb with 'ServiceURI' parameter with value
        // '/CustomURL/Part1/Part2' on https transport
        loadESBConfigurationFromClasspath(PROXY_DEFINITION_HTTPS_CUSTOM_URI);
    }

    */
/**
     * This test case will deploy a proxy service with 'ServiceURI' parameter
     * over https and see weather it can be invoked using the customized url
     *
     * @throws org.apache.axis2.AxisFault
     *//*

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.integration_all})
    @Test(groups = "wso2.esb", description = "- Pass through proxy with a custom service URI  over https")
    public void testCustomServiceURIPassThroughProxy() throws Exception {

        String proxyUrl = getCustmizedProxyServiceSecuredURL(CUSTOM_URI_FRAGMENT);
        String symbol = "WSO2";

        OMElement response = axis2Client.sendSimpleQuoteRequest(proxyUrl, null, symbol);

        assertNotNull(response, "Response not received");

        String symbolReturned =
                response.getFirstElement()
                        .getFirstChildWithName(new QName(
                                "http://services.samples/xsd",
                                "symbol")).getText();

        assertEquals(symbolReturned, symbol, "Unexpected symbol returned");

    }

    private String getCustmizedProxyServiceSecuredURL(String customURI)
            throws RemoteException, ServiceAdminException {
        String serviceEndPoint = null;
        ServiceAdminClient adminServiceService;
        ServiceMetaData serviceMetaData;
        String[] endpoints;

        adminServiceService = new ServiceAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        serviceMetaData = adminServiceService.getServicesData("CustomServiceURIProxy");

        endpoints = serviceMetaData.getEprs();
        assertNotNull(endpoints, "Service Endpoint object null");
        assertTrue((endpoints.length > 0), "No service endpoint found");
        for (String epr : endpoints) {
            if (epr.startsWith("https://")) {
                serviceEndPoint = epr;
                break;
            }
        }
        assertNotNull(serviceEndPoint, "service endpoint null");
        assertTrue(serviceEndPoint.contains(customURI), "Service Endpoint not contain Custom URI Fragment");
        return serviceEndPoint;
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        // Restore the axis2 configuration altered by this test case
        try {
            super.cleanup();
        } finally {
            Thread.sleep(3000);
            configurationManager.restoreToLastConfiguration();
        }
    }
}
*/
