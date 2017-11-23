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
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.automation.test.utils.concurrency.test.ConcurrencyTest;
import org.wso2.carbon.automation.test.utils.concurrency.test.exception.ConcurrencyTestFailedError;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class ExcelSampleServiceTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(ExcelSampleServiceTestCase.class);
    private final String serviceName = "ExcelSampleService";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();

        deployService(serviceName,
                      AXIOMUtil.stringToOM(FileManager.readFile(getResourceLocation()
                                                                + File.separator + "dbs" + File.separator
                                                                + "excel" + File.separator + "ExcelSampleService.dbs")));
    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws RemoteException, XPathExpressionException {
        DSSTestCaseUtils dssTestCaseUtils = new DSSTestCaseUtils();
        assertTrue(dssTestCaseUtils.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(),
                                                      sessionCookie, serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, invocationCount = 5, dependsOnMethods = "testServiceDeployment")
    public void selectOperation() throws AxisFault, XPathExpressionException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
        OMElement payload = fac.createOMElement("getProducts", omNs);
        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "getProducts");
        log.info("Response :" + result);
        Assert.assertTrue((result.toString().indexOf("Products") == 1), "Expected Result Not found");
        Assert.assertTrue(result.toString().contains("<Product>"), "Expected Result Not found");
        Assert.assertTrue(result.toString().contains("<ID>"), "Expected Result Not found");
        Assert.assertTrue(result.toString().contains("<Name>"), "Expected Result Not found");
        log.info("Service invocation success");
    }

    @Test(groups = {"wso2.dss"}, invocationCount = 5, dependsOnMethods = "selectOperation")
    public void xsltTransformation() throws AxisFault, XPathExpressionException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
        OMElement payload = fac.createOMElement("getProductClassifications", omNs);
        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "getProductClassifications");
        if (log.isDebugEnabled()) {
            log.debug("Response :" + result);
        }
        Assert.assertTrue((result.toString().indexOf("Products") == 1), "Expected Result Not found");
        Assert.assertTrue(result.toString().contains("<product>"), "Expected Result Not found");
        Assert.assertTrue(result.toString().contains("<Product-Name>"), "Expected Result Not found");
        Assert.assertTrue(result.toString().contains("<Product-Classification>"), "Expected Result Not found");

        log.info("XSLT Transformation Success");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"xsltTransformation"}, timeOut = 1000 * 60 * 1)
    public void concurrencyTest()
            throws ConcurrencyTestFailedError, InterruptedException, XPathExpressionException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
        OMElement payload = fac.createOMElement("getProducts", omNs);
        ConcurrencyTest concurrencyTest = new ConcurrencyTest(5, 5);
        concurrencyTest.run(getServiceUrlHttp(serviceName), payload, "getProducts");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

}
