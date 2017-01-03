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

package org.wso2.ei.dataservice.integration.test.services;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceAdminClient;

import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class MultipleServicesGeneratorTestCase extends DSSIntegrationTest {

    private DataServiceAdminClient dataServiceAdminClient;
    private final String SCHEMA_NAME = "PUBLIC";
    private final String TABLE_NAME_1 = "REG_PATH";
    private final String TABLE_NAME_2 = "REG_PROPERTY";

    private final String SERVICE_NAME_1 = TABLE_NAME_1 + "_DataService";
    private final String SERVICE_NAME_2 = TABLE_NAME_2 + "_DataService";
    private DSSTestCaseUtils dssTestCaseUtils;
    private static final Log log = LogFactory.getLog(MultipleServicesGeneratorTestCase.class);
    private String serviceEPR_1;
    private String serviceEPR_2;

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init();
        dssTestCaseUtils = new DSSTestCaseUtils();
        dataServiceAdminClient =
                new DataServiceAdminClient(dssContext.getContextUrls().getBackEndUrl(), userInfo.getUserName(),
                                           userInfo.getPassword());
        serviceEPR_1 = dssContext.getContextUrls().getBackEndUrl() + SERVICE_NAME_1;
        serviceEPR_2 = dssContext.getContextUrls().getBackEndUrl() + SERVICE_NAME_2;

    }

    @Test()
    public void testServiceGeneration() throws Exception {
        String[] datasourceNames = dataServiceAdminClient.getCarbonDataSources();
        String[] serviceList = new String[0];
        for (String datasourceName : datasourceNames) {
            String CARBON_DATA_SOURCE = "WSO2_CARBON_DB";
            if (datasourceName.equals(CARBON_DATA_SOURCE)) {
                String[] schemaList = dataServiceAdminClient.getdbSchemaList(CARBON_DATA_SOURCE);
                for (String schema : schemaList) {
                    if (schema.equals(SCHEMA_NAME)) {
                        String[] schemaName = {SCHEMA_NAME};
                        String DB_NAME = "WSO2CARBON_DB";
                        String[] tablesdata = dataServiceAdminClient.getTableInfo(CARBON_DATA_SOURCE,
                                                                                  DB_NAME, schemaName);
                        int count = 0;
                        for (String table : tablesdata) {

                            if (table.equals(TABLE_NAME_1) || table.equals(TABLE_NAME_2)) {
                                count++;
                                if (count == 2) {
                                    String NAMESPACE = "http://wso2.example.org";
                                    serviceList = dataServiceAdminClient.getDSServiceList(CARBON_DATA_SOURCE, DB_NAME,
                                                                                          schemaName, new String[]{TABLE_NAME_1,
                                                                                                                   TABLE_NAME_2},
                                                                                          NAMESPACE);
                                    break;
                                }

                            }

                        }

                    }
                }

            }
        }

        int serviceCount = 0;
        for (String service : serviceList) {
            if ((service.equals(TABLE_NAME_1 + "_DataService") || (service.equals(TABLE_NAME_2 + "_DataService")))) {
                serviceCount++;
                if (serviceCount == 2) {
                    break;
                }
            }
        }
        assertTrue(serviceCount == 2, "two services not deployed");
    }

    @Test(groups = "wso2.dss", description = "Check whether the service is deployed or not",
          dependsOnMethods = "testServiceGeneration")
    public void testServiceDeployment() throws Exception {
        assertTrue(dssTestCaseUtils.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(),
                                                      sessionCookie, SERVICE_NAME_1));
        assertTrue(dssTestCaseUtils.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(),
                                                      sessionCookie, SERVICE_NAME_2));
        log.info(SERVICE_NAME_1 + "and" + SERVICE_NAME_2 + " are deployed");
    }

    @Test(groups = "wso2.dss", description = "invoke the generated service",
          dependsOnMethods = "testServiceDeployment")
    public void testRequest() throws RemoteException {

        OMElement result = new AxisServiceClient().sendReceive(getPayloadService1(), serviceEPR_1,
                                                               "select_all_REG_PATH_operation");

        assertTrue(result.toString().contains("<REG_PATH_VALUE>/_system/config</REG_PATH_VALUE>"));

        OMElement result2 = new AxisServiceClient().sendReceive(getPayloadService2(), serviceEPR_2,
                                                                "select_with_key_REG_PROPERTY_operation");

        assertTrue(result2.toString().contains("<REG_ID>1</REG_ID>"));
    }


    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(SERVICE_NAME_1);
        deleteService(SERVICE_NAME_2);
        log.info(SCHEMA_NAME + " deleted");
    }

    private OMElement getPayloadService1() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://wso2.example.org", "wso2");
        return fac.createOMElement("select_all_REG_PATH_operation", omNs);
    }

    private OMElement getPayloadService2() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://wso2.example.org", "wso2");

        OMElement payload = fac.createOMElement("select_with_key_REG_PROPERTY_operation", omNs);
        OMElement id = fac.createOMElement("REG_ID", omNs);
        id.setText("1");
        payload.addChild(id);
        return payload;
    }

}
