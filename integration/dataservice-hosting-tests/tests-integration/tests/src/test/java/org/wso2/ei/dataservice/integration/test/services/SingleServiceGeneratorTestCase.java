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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SingleServiceGeneratorTestCase extends DSSIntegrationTest {
    private DataServiceAdminClient dataServiceAdminClient;
    private final String SCHEMA_NAME = "PUBLIC";
    private final String SERVICE_NAME = "RegPathService";
    private DSSTestCaseUtils dssTestCaseUtils;
    private static final Log log = LogFactory.getLog(SingleServiceGeneratorTestCase.class);
    private String serviceEPR;

    @BeforeClass(alwaysRun = true)
    public void initializeTest() throws Exception {
        super.init();
        dssTestCaseUtils = new DSSTestCaseUtils();
        dataServiceAdminClient =
                new DataServiceAdminClient(dssContext.getContextUrls().getBackEndUrl(), userInfo.getUserName(),
                                           userInfo.getPassword());
        serviceEPR = getServiceUrlHttp(SERVICE_NAME);

    }

    @Test()
    public void testServiceGeneration() throws Exception {
        String[] dataSourceNames = dataServiceAdminClient.getCarbonDataSources();
        String status = null;
        for (String dataSourceName : dataSourceNames) {
            String CARBON_DATA_SOURCE = "WSO2_CARBON_DB";
            if (dataSourceName.equals(CARBON_DATA_SOURCE)) {
                String[] schemaList = dataServiceAdminClient.getdbSchemaList(CARBON_DATA_SOURCE);
                for (String schema : schemaList) {
                    if (schema.equals(SCHEMA_NAME)) {
                        String[] schemaName = {SCHEMA_NAME};
                        String DB_NAME = "WSO2CARBON_DB";
                        String[] tablesData = dataServiceAdminClient.getTableInfo(CARBON_DATA_SOURCE,
                                                                                  DB_NAME, schemaName);
                        for (String table : tablesData) {
                            String TABLE_NAME = "REG_PATH";
                            if (table.equals(TABLE_NAME)) {
                                String NAMESPACE = "http://wso2.example.org";
                                status =
                                        dataServiceAdminClient.getDSService(CARBON_DATA_SOURCE, DB_NAME,
                                                                            schemaName, new String[]{TABLE_NAME},
                                                                            SERVICE_NAME, NAMESPACE);

                                break;
                            }
                        }
                    }
                }
            }
        }
        assertEquals(status, SERVICE_NAME);
    }

    @Test(groups = "wso2.dss", description = "Check whether the service is deployed or not",
          dependsOnMethods = "testServiceGeneration")
    public void testServiceDeployment() throws Exception {
        assertTrue(dssTestCaseUtils.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(),
                                                      sessionCookie, SERVICE_NAME));
        log.info(SERVICE_NAME + " is deployed");
    }

    @Test(groups = "wso2.dss", description = "invoke the generated service",
          dependsOnMethods = "testServiceDeployment")
    public void testRequest() throws RemoteException {

        OMElement result = new AxisServiceClient().sendReceive(getPayload(), serviceEPR, "select_all_REG_PATH_operation");
        assertTrue(result.toString().contains("<REG_PATH_VALUE>/_system/config</REG_PATH_VALUE>"));
    }


    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(SERVICE_NAME);
        log.info(SCHEMA_NAME + " deleted");
    }

    private OMElement getPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://wso2.example.org", "wso2");
        return fac.createOMElement("select_all_REG_PATH_operation", omNs);
    }

}
