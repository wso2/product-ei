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

package org.wso2.ei.dataservice.integration.test.samples;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;


public class WebResourceSampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(WebResourceSampleTestCase.class);

    private final String serviceName = "WebResourceSample";


/*    @Factory(dataProvider = "userModeDataProvider")
    public WebResourceSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }*/

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        String resourceFileLocation;
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "web" + File.separator +
                                              "WebResourceSample.dbs")));
        log.info(serviceName + " uploaded");
    }


    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws Exception {
         assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }
    //disabled the test since the service referring external api
    @Test(groups = {"wso2.dss"}, invocationCount = 5, dependsOnMethods = "testServiceDeployment",
          description = "invoke the service five times", enabled = false)
    public void selectOperation() throws AxisFault, InterruptedException, XPathExpressionException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.w3.org/2005/08/addressing", "ns1");
        OMElement payload = fac.createOMElement("getAppInfo", omNs);
        Thread.sleep(1000);
        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "getAppInfo");
        Assert.assertNotNull(result, "Service invocation returns null response");
        Assert.assertTrue(result.toString().contains("<Title>"));
        Assert.assertTrue(result.toString().contains("<Description>"));

        log.info("Service invocation success");
    }

    @AfterClass(alwaysRun = true, groups = "wso2.dss", description = "delete service")
    public void deleteFaultyService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

}
