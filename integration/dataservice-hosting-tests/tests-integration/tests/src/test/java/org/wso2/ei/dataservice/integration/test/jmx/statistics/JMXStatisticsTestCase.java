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
package org.wso2.ei.dataservice.integration.test.jmx.statistics;

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
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservice.integration.test.jmx.statistics.utils.JMXClient;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class JMXStatisticsTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(JMXStatisticsTestCase.class);
    private final String serviceName = "GSpreadDataService";
    private JMXClient jmxClient;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        deployService(serviceName,
                      AXIOMUtil.stringToOM(FileManager.readFile(getResourceLocation() + File.separator
                                                                + "dbs" + File.separator + "gspread" + File.separator
                                                                + "GSpreadDataService.dbs")));
        //todo getting hostname
        jmxClient = new JMXClient(serviceName, "localhost",
                                  userInfo.getUserName(), userInfo.getPassword());
        jmxClient.connect();

    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not", enabled = false)
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, invocationCount = 5, description = "invoke service",
          dependsOnMethods = "testServiceDeployment", enabled = false)
    public void selectOperation() throws AxisFault, XPathExpressionException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/gspread_sample_service", "ns1");
        OMElement payload = fac.createOMElement("getCustomers", omNs);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
                                                               "getProducts");
        log.info("Response : " + result);
        Assert.assertTrue((result.toString().indexOf("Customers") == 1),
                          "Expected Result not found on response message");
        Assert.assertTrue(result.toString().contains("<customerNumber>"),
                          "Expected Result not found on response message");
        Assert.assertTrue(result.toString().contains("</Customer>"),
                          "Expected Result not found on response message");

        log.info("Service Invocation success");
    }

    @Test(groups = "wso2.dss", description = "Verify service attributes exposed via jmx interface",
          dependsOnMethods = "selectOperation", enabled = false)
    public void testVerifyServiceAttributesViaJMX() throws Exception {
        assertEquals(jmxClient.getAttribute("ServiceName"), serviceName);
        assertTrue(((String[]) jmxClient.getAttribute("ResourcePaths"))[0].equals("customers"));
        assertTrue(((String[]) jmxClient.getAttribute("OperationNames"))[0].equals("getCustomers"));
        assertTrue(((String[]) jmxClient.getAttribute("ConfigIds"))[0].equals("GSpreadDataSource"));
        assertTrue(((String[]) jmxClient.getAttribute("QueryIds"))[0].equals("aa"));
        assertTrue(jmxClient.getAttribute("DataServiceDescriptorPath").toString().
                contains("repository" + File.separator + "deployment" + File.separator +
                         "server" + File.separator + "dataservices" + File.separator +
                         "GSpreadDataService.dbs"));
    }

    @Test(groups = "wso2.dss", description = "Verify service operations exposed via jmx interface",
          dependsOnMethods = "testVerifyServiceAttributesViaJMX", enabled = false)
    public void testVerifyServiceOperationsViaJMX() throws Exception {
        assertTrue(jmxClient.invoke("getQueryIdFromOperationName", new Object[]{"getCustomers"}).equals("aa"));
        assertTrue(jmxClient.invoke("getConfigIdFromQueryId", new Object[]{"aa"}).equals("GSpreadDataSource"));
        assertFalse(jmxClient.invoke("getHTTPMethodsForResourcePath", new Object[]{"dummy"}).equals("dummy"));
        assertTrue(jmxClient.invoke("isConfigActive", new Object[]{"GSpreadDataSource"}).toString().equals("true"));
        assertTrue(jmxClient.invoke("getConfigTypeFromId", new Object[]{"GSpreadDataSource"}).equals("GDATA_SPREADSHEET"));
        assertTrue(jmxClient.invoke("isDatabaseConnectionStatsAvailable", new Object[]{"GSpreadDataSource"}).toString().equals("false"));
        assertTrue(jmxClient.invoke("getOpenDatabaseConnectionsCount", new Object[]{""}).toString().equals("-1"));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        jmxClient.disconnect();
        cleanup();
    }

}
