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
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.common.admin.client.NDataSourceAdminServiceClient;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertTrue;


public class InMemoryDSSampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(InMemoryDSSampleTestCase.class);
    private final String serviceName = "InMemoryDSSample";
    private String serviceUrl;


    @Factory(dataProvider = "userModeDataProvider")
    public InMemoryDSSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(userMode);
        String resourceFileLocation;
        resourceFileLocation = getResourceLocation();
        //DataSource already exist by default for super user.
        if (userMode == TestUserMode.TENANT_ADMIN || userMode == TestUserMode.TENANT_USER) {
            addDataSources();
        }
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "inmemory" + File.separator +
                                              "InMemoryDSSample.dbs")));
        log.info(serviceName + " uploaded");
        serviceUrl = getServiceUrlHttp(serviceName);
    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, invocationCount = 5, dependsOnMethods = "testServiceDeployment")
    public void selectOperation() throws AxisFault, XPathExpressionException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.w3.org/2005/08/addressing", "ns1");
        OMElement payload = fac.createOMElement("getAllUsers", omNs);

        OMElement result = new AxisServiceClient().sendReceive(payload, serviceUrl, "getAllUsers");
        Assert.assertTrue(result.toString().contains("Will Smith"));
        Assert.assertTrue(result.toString().contains("Denzel Washington"));

        log.info("Service invocation success");
    }

    @AfterClass(alwaysRun = true, groups = "wso2.dss", description = "delete service")
    public void deleteFaultyService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    private void addDataSources() throws Exception {
        NDataSourceAdminServiceClient
                dataSourceAdminClient = new NDataSourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        //Adding IN_MEMORY_SAMPLE_DS data source
        WSDataSourceMetaInfo sampleDSSourceMetaInfo = new WSDataSourceMetaInfo();
        sampleDSSourceMetaInfo.setName("IN_MEMORY_SAMPLE_DS");
        sampleDSSourceMetaInfo.setDescription("A sample in-memory datasource");
        WSDataSourceMetaInfo_WSDataSourceDefinition dataSourceDefinition = new WSDataSourceMetaInfo_WSDataSourceDefinition();

        dataSourceDefinition.setType("DS_CUSTOM_TABULAR");
        String sampleDsConfig = "<configuration xmlns:svns=\"http://org.wso2.securevault/configuration\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">" +
                                "   <customDataSourceClass>org.wso2.carbon.dataservices.core.custom.datasource.InMemoryDataSource</customDataSourceClass>" +
                                "   <customDataSourceProps><property name=\"inmemory_datasource_schema\">{Vehicles:[ID,Model,Classification,Year]}</property>" +
                                "                        <property name=\"inmemory_datasource_records\">" +
                                "                          {Vehicles:[[\"S10_1678\",\"Harley Davidson Ultimate Chopper\",\"Motorcycles\",\"1969\"]," +
                                "                                     [\"S10_1949\",\"Alpine Renault 1300\",\"Classic Cars\",\"1952\"]," +
                                "                                     [\"S10_2016\",\"Moto Guzzi 1100i\",\"Motorcycles\",\"1996\"]," +
                                "                                     [\"S10_4698\",\"Harley-Davidson Eagle Drag Bike\",\"Motorcycles\",\"2003\"]," +
                                "                                     [\"S10_4757\",\"Alfa Romeo GTA\",\"Classic Cars\",\"1972\"]," +
                                "                                     [\"S10_4962\",\"LanciaA Delta 16V\",\"Classic Cars\",\"1962\"]," +
                                "                                     [\"S12_1099\",\"Ford Mustang\",\"Classic Cars\",\"1968\"]," +
                                "                                     [\"S12_1108\",\"Ferrari Enzo\",\"Classic Cars\",\"2001\"]]}" +
                                "                        </property></customDataSourceProps>" +
                                "</configuration>";


        dataSourceDefinition.setDsXMLConfiguration(sampleDsConfig);

        sampleDSSourceMetaInfo.setDefinition(dataSourceDefinition);
        dataSourceAdminClient.addDataSource(sampleDSSourceMetaInfo);
        Thread.sleep(1000);

        //Adding  ECHO_SAMPLE_DS data source
        WSDataSourceMetaInfo echoSampleSourceMetaInfo = new WSDataSourceMetaInfo();
        echoSampleSourceMetaInfo.setName("ECHO_SAMPLE_DS");
        echoSampleSourceMetaInfo.setDescription("A sample in-memory datasource");
        WSDataSourceMetaInfo_WSDataSourceDefinition echoSampleDSDefinition = new WSDataSourceMetaInfo_WSDataSourceDefinition();

        echoSampleDSDefinition.setType("DS_CUSTOM_TABULAR");
        String echoSampleDsConfig = "<configuration xmlns:svns=\"http://org.wso2.securevault/configuration\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">" +
                                    "<customDataSourceClass>org.wso2.carbon.dataservices.core.custom.datasource.InMemoryDataSource</customDataSourceClass>" +
                                    "<customDataSourceProps>" +
                                    "<property name=\"p1\">val1</property>" +
                                    "<property name=\"p2\">val2</property>" +
                                    "</customDataSourceProps>" +
                                    "</configuration>";


        echoSampleDSDefinition.setDsXMLConfiguration(echoSampleDsConfig);

        echoSampleSourceMetaInfo.setDefinition(echoSampleDSDefinition);
        dataSourceAdminClient.addDataSource(echoSampleSourceMetaInfo);
        Thread.sleep(1000);
    }

    private void removeDataSources() throws Exception {
        NDataSourceAdminServiceClient
                dataSourceAdminClient = new NDataSourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        dataSourceAdminClient.deleteDataSource("IN_MEMORY_SAMPLE_DS");
        dataSourceAdminClient.deleteDataSource("ECHO_SAMPLE_DS");
        Thread.sleep(1000);
    }
}
