/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ei.dataservice.integration.test.services;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.concurrency.test.ConcurrencyTest;
import org.wso2.carbon.automation.test.utils.concurrency.test.exception.ConcurrencyTestFailedError;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class performs tests related to Return Updated Row Count.
 */
public class ReturnUpdatedRowCountTestCase extends DSSIntegrationTest {
	private final OMFactory fac = OMAbstractFactory.getOMFactory();
	private final OMNamespace omNs =
			fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/returnUpdatedRowCountSample", "ns1");
	private final String serviceName = "H2ReturnUpdatedRowCountTest";

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CreateTables.sql"));
		sqlFileLis.add(selectSqlFile("Accounts.sql"));
		deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
		                                          "rdbms" + File.separator + "h2" + File.separator +
		                                          "H2ReturnUpdatedRowCountTest.dbs", sqlFileLis));
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "Invoking update operation with Return Updated Row Count")
	public void performUpdateWithReturnUpdatedRowCountTest() throws AxisFault, XPathExpressionException {
		OMElement payload = fac.createOMElement("update_Accounts", omNs);
		OMElement newBalanceElement = fac.createOMElement("newBalance", omNs);
		newBalanceElement.setText("22.184");
		payload.addChild(newBalanceElement);
		OMElement oldBalanceElement = fac.createOMElement("oldBalance", omNs);
		oldBalanceElement.setText("10.2");
		payload.addChild(oldBalanceElement);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
		                                                       "update_Accounts");
		boolean id = "10".equals(result.getFirstElement().getFirstChildWithName(
				new QName("http://ws.wso2.org/dataservice/samples/returnUpdatedRowCountSample", "COUNT")).getText());
		Assert.assertTrue(id, "Update operation with return update row count is failed");
	}

	@Test(groups =  "wso2.dss" , description = "Concurrency Test for Return Updated Row Count" , dependsOnMethods = "performUpdateWithReturnUpdatedRowCountTest")
	public void performConcurrencyTest() throws ConcurrencyTestFailedError, InterruptedException,
	                                            XPathExpressionException {
		ConcurrencyTest concurrencyTest = new ConcurrencyTest(5, 5);
		OMElement payload = fac.createOMElement("update_Accounts", omNs);
		OMElement newBalanceElement = fac.createOMElement("newBalance", omNs);
		newBalanceElement.setText("22.184");
		payload.addChild(newBalanceElement);
		OMElement oldBalanceElement = fac.createOMElement("oldBalance", omNs);
		oldBalanceElement.setText("10.2");
		payload.addChild(oldBalanceElement);
		concurrencyTest.run(getServiceUrlHttp(serviceName), payload, "update_Accounts");
	}

}
