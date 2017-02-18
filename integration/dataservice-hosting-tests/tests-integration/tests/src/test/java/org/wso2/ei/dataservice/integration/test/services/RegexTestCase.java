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
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * This class has been written to verify the functionality of the regex support in RDBMS.
 */
public class RegexTestCase extends DSSIntegrationTest {

	private final OMFactory factory = OMAbstractFactory.getOMFactory();
	private final OMNamespace omNs = factory.createOMNamespace("http://ws.wso2.org/dataservice/samples/rdbms_sample",
	                                                           "ns1");
	private final String serviceName = "RegexTest";

	@BeforeClass(alwaysRun = true)
	public void serviceDeployment() throws Exception {

		super.init();
		List<File> sqlFileLis = new ArrayList<>();
		sqlFileLis.add(selectSqlFile("RegexTable.sql"));
		deployService(serviceName, createArtifact(
				              getResourceLocation() + File.separator + "dbs" + File.separator + "rdbms" +
				              File.separator + "h2" + File.separator + "RegexTest.dbs", sqlFileLis));

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		deleteService(serviceName);
		cleanup();
	}

	@Test(groups = "wso2.dss", description = "Invoking select operation to verify regex support with sequence operators")
	public void performRegexTestWithSequenceOperators() throws AxisFault, XPathExpressionException {
		OMElement payload = factory.createOMElement("select_regex1", omNs);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
		                                                       "select_regex1");
		boolean id = "1".equals(result.getFirstElement().getFirstChildWithName(
				new QName("http://ws.wso2.org/dataservice/samples/rdbms_sample", "studentNumber")).getText());
		Assert.assertTrue(id, "Regex sequence operators testing is failed.");
	}

	@Test(groups = "wso2.dss", description = "Invoking select operation to verify regex support with bracket expressions")
	public void performRegexTestWithBracketExpressions() throws AxisFault, XPathExpressionException {
		OMElement payload = factory.createOMElement("select_regex2", omNs);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
		                                                       "select_regex2");
		assertNotNull(result);
		Assert.assertEquals(result.getLocalName(), "Entries");
	}

	@Test(groups = "wso2.dss", description = "Invoking select operation to verify regex support with character classes")
	public void performRegexTestWithCharacterClasses() throws AxisFault, XPathExpressionException {
		OMElement payload = factory.createOMElement("select_regex3", omNs);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
		                                                       "select_regex3");
		boolean id = "5".equals(result.getFirstElement().getFirstChildWithName(
				new QName("http://ws.wso2.org/dataservice/samples/rdbms_sample", "Count")).getText());
		Assert.assertTrue(id, "Regex character class testing is failed.");
	}

	@Test(groups = "wso2.dss", description = "Invoking select operation to verify regex support with nameparams and bracket expressions")
	public void performRegexTestWithBracketExpressionsAndNameParams() throws AxisFault, XPathExpressionException {
		OMElement payload = factory.createOMElement("select_regex5", omNs);
		OMElement queryElement = factory.createOMElement("name", omNs);
		queryElement.setText("name");
		payload.addChild(queryElement);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
		                                                       "select_regex5");
		assertNotNull("Response null " + result);
		Assert.assertEquals(result.getLocalName(), "Entries");
	}

	@Test(groups = "wso2.dss", description = "Invoking select operation to verify regex support with nameparams and sequence operators")
	public void performRegexTestWithSequenceOperatorsAndNameParams() throws AxisFault, XPathExpressionException {
		OMElement payload = factory.createOMElement("select_regex4", omNs);
		OMElement queryElement = factory.createOMElement("name", omNs);
		queryElement.setText("name");
		payload.addChild(queryElement);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName),
		                                                       "select_regex4");
		boolean name = "Madhawa Gunasekara".equals(result.getFirstElement().getFirstChildWithName(
				new QName("http://ws.wso2.org/dataservice/samples/rdbms_sample", "name")).getText());
		Assert.assertTrue(name, "Regex bracket expressions testing with nameparams is failed");
	}

	@Test(groups = "wso2.dss", description = "Invoking insert operation to verify regex support")
	public void performInsertQueryWithQMarks() throws XPathExpressionException, AxisFault {
		OMElement payload = factory.createOMElement("insert", omNs);
		OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "insert");
		boolean name = "SUCCESSFUL".equals(result.getText());
		Assert.assertTrue(name, "insert query testing with question marks is failed");
	}
}
