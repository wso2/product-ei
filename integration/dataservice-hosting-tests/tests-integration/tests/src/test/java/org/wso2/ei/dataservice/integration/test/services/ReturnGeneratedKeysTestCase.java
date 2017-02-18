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
 * This class performs tests, which related to Return Generated Keys.
 */
public class ReturnGeneratedKeysTestCase extends DSSIntegrationTest {
	private final OMFactory factory = OMAbstractFactory.getOMFactory();
	private final OMNamespace omNs =
			factory.createOMNamespace("http://ws.wso2.org/dataservice/samples/returnGeneratedKeysSample", "ns1");
	private final String serviceName = "H2ReturnGeneratedKeysTest";

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {
		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("CreateTables.sql"));
		deployService(serviceName, createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator +
		                                          "rdbms" + File.separator + "h2" + File.separator +
		                                          "H2ReturnGeneratedKeysTest.dbs", sqlFileLis));
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "Invoking insert operation with Return Generated Keys" , dependsOnMethods = "performConcurrencyTest")
	public void performInsertWithReturnGeneratedKeysTest() throws AxisFault, XPathExpressionException {
		OMElement payload = factory.createOMElement("insertBalance", omNs);
		OMElement queryElement = factory.createOMElement("balance", omNs);
		queryElement.setText("22.184");
		payload.addChild(queryElement);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "insertBalance");
		/* id should be 26 because concurrency test has been performed before this method */
		boolean id = "26".equals(result.getFirstElement().getFirstChildWithName(
				new QName("http://ws.wso2.org/dataservice/samples/returnGeneratedKeysSample", "ID")).getText());
		Assert.assertTrue(id, "Insert operation with return generated keys is failed");
	}

	@Test(groups = "wso2.dss", description = "Concurrency Test for Return Generated Keys")
	public void performConcurrencyTest() throws ConcurrencyTestFailedError, InterruptedException,
	                                            XPathExpressionException {
		ConcurrencyTest concurrencyTest = new ConcurrencyTest(5, 5);
		OMElement payload = factory.createOMElement("insertBalance", omNs);
		OMElement queryElement = factory.createOMElement("balance", omNs);
		queryElement.setText("144.184");
		payload.addChild(queryElement);
		concurrencyTest.run(getServiceUrlHttp(serviceName), payload, "insertBalance");
	}

}
