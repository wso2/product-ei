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
package org.wso2.ei.dataservice.integration.test.syntax;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DataTypesTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(DataTypesTestCase.class);

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
    private final String serviceName = "Developer";

    private String serviceEndPoint;

    @Factory(dataProvider = "userModeDataProvider")
    public DataTypesTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init(userMode);
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("DataTypes.sql"));
        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                              + "rdbms" + File.separator
                                              + "h2" + File.separator + "Developer.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName);
    }


    /**
     * Method to test insertion of timestamp value.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.dss"}, description = "insert a timestamp value and check it's successful", alwaysRun = true)
    public void insertTimestampTest() throws Exception {
        OMElement insertTimeStampPayload = fac.createOMElement("addDeveloper", omNs);

        OMElement devId = fac.createOMElement("devId", omNs);
        devId.setText(1 + "");
        insertTimeStampPayload.addChild(devId);

        OMElement devName = fac.createOMElement("devName", omNs);
        devName.setText("name1");
        insertTimeStampPayload.addChild(devName);

        OMElement devdob = fac.createOMElement("devdob", omNs);
        devdob.setText("2002-01-01T06:00:00.000+00:00");
        insertTimeStampPayload.addChild(devdob);

        new AxisServiceClient().sendRobust(insertTimeStampPayload, getServiceUrlHttp(serviceName), "addDeveloper");

        OMElement getDeveloperByIdPayload = fac.createOMElement("select_developers_by_id_operation", omNs);
        getDeveloperByIdPayload.addChild(devId);

        //retrieve and see whether inserted correctly
        OMElement responseProduct = new AxisServiceClient().sendReceive(getDeveloperByIdPayload, getServiceUrlHttp(serviceName), "select_developers_by_id_operation");
        assertNotNull(responseProduct, "Response null " + responseProduct);
        assertTrue(responseProduct.toString().contains("<devdob>2002-01"), "'devdob' should have exist in the response");
        log.info("Insert TimeStamp Operation Success");
    }

    /**
     * Method to test insertion of timestamp value.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.dss"}, description = "insert null value as timestamp value and check it's successful", alwaysRun = true)
    public void insertTimestampNullTest() throws Exception {
        OMElement insertTimeStampPayload = fac.createOMElement("addDeveloper", omNs);

        OMNamespace nullNameSpace = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        insertTimeStampPayload.declareNamespace(nullNameSpace);

        OMElement devId = fac.createOMElement("devId", omNs);
        devId.setText(2 + "");
        insertTimeStampPayload.addChild(devId);

        OMElement devName = fac.createOMElement("devName", omNs);
        devName.setText("name2");
        insertTimeStampPayload.addChild(devName);

        OMElement devdob = fac.createOMElement("devdob", omNs);
        OMAttribute nullAttribute = fac.createOMAttribute("nil", nullNameSpace,"true");
        devdob.addAttribute(nullAttribute);
        insertTimeStampPayload.addChild(devdob);

        new AxisServiceClient().sendRobust(insertTimeStampPayload, getServiceUrlHttp(serviceName), "addDeveloper");

        OMElement getDeveloperByIdPayload = fac.createOMElement("select_developers_by_id_operation", omNs);
        getDeveloperByIdPayload.addChild(devId);

        //retrieve and see whether inserted correctly
        OMElement responseProduct = new AxisServiceClient().sendReceive(getDeveloperByIdPayload, getServiceUrlHttp(serviceName), "select_developers_by_id_operation");
        assertNotNull(responseProduct, "Response null " + responseProduct);
        assertTrue(responseProduct.toString().contains("<devdob"), "'devdob' should have exist in the response");
        log.info("Insert TimeStamp Operation Success");
    }



    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }
}
