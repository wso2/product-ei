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
package org.wso2.carbon.esb.resource.test.proxy;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.proxyadmin.stub.ProxyServiceAdminProxyAdminException;
import org.wso2.carbon.registry.resource.stub.beans.xsd.MetadataBean;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.rmi.RemoteException;

public class ProxyServiceMediaTypeTestCase extends ESBIntegrationTest {
    private ResourceAdminServiceClient resourceAdmin;
    private final String proxyName = "testProxyMetaData123456";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        resourceAdmin = new ResourceAdminServiceClient(contextUrls.getBackEndUrl(),getSessionCookie());
    }
    //since Registry persistence is no longer available
    @Test(groups = {"wso2.esb"}, description = "Test Proxy Service media type", enabled = false)
    public void testProxyServiceMediaType() throws Exception {
        OMElement proxyService =AXIOMUtil.stringToOM("<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\""+proxyName+"\" transports=\"https,http\"" +
                "       statistics=\"disable\" trace=\"disable\" startOnLoad=\"true\">" +
                "        <target>" +
                "            <inSequence>" +
                "                <log level=\"full\"/>" +
                "                <send>" +
                "                    <endpoint>" +
                "                        <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>" +
                "                    </endpoint>" +
                "                </send>" +
                "            </inSequence>" +
                "            <outSequence>" +
                "                <send/>" +
                "            </outSequence>" +
                "        </target>" +
                " </proxy>");
        addProxyService(proxyService);
        //addEndpoint is a a asynchronous call, it will take some time to write to a registry
        MetadataBean metadata = resourceAdmin.getMetadata("/_system/config/repository/synapse/default/proxy-services/"+proxyName);
        Assert.assertEquals(metadata.getMediaType(), "text/xml", "Media Type mismatched for proxy service");
    }

    @AfterClass
    public void destroy() throws Exception {
        deleteProxyService(proxyName);
        resourceAdmin = null;
        super.cleanup();
    }
}
