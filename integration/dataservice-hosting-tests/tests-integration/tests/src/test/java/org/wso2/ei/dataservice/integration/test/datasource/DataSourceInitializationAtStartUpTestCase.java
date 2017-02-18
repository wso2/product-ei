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
package org.wso2.ei.dataservice.integration.test.datasource;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.integration.common.admin.client.NDataSourceAdminServiceClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.common.utils.SampleDataServiceClient;
import org.wso2.ei.dataservice.integration.common.utils.SqlDataSourceUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceAdminClient;

import javax.activation.DataHandler;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

//https://wso2.org/jira/browse/STRATOS-1631
public class DataSourceInitializationAtStartUpTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DataSourceInitializationAtStartUpTestCase.class);
    private final String serviceFile = "CarbonDSDataServiceTest.dbs";
    private final String serviceName = "CarbonDSDataServiceTest";

    private String carbonDataSourceName;
    private SqlDataSourceUtil sqlDataSource;
    private SampleDataServiceClient client;
    private String dataSourceName = "";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        carbonDataSourceName = createDataSource();
        log.info(carbonDataSourceName + " carbon Datasource Created");
        DataHandler dhArtifact = createArtifactWithDataSource(serviceFile);
        deployService(serviceName, dhArtifact);
        client = new SampleDataServiceClient(getServiceUrlHttp(serviceName));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = {"wso2.dss"})
    public void selectOperation() throws AxisFault {
        for (int i = 0; i < 5; i++) {
            client.getCustomerInBoston();
        }
        log.info("Select Operation Success");
    }

    @Test(dependsOnMethods = {"selectOperation"}, timeOut = 1000 * 60 * 5)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void testServerRestarting()
            throws Exception {
        log.info("Restarting Server.....");
        new ServerConfigurationManager("DSS", TestUserMode.SUPER_TENANT_ADMIN);

    }

    @Test(dependsOnMethods = {"testServerRestarting"})
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void isServiceExistAfterRestarting() throws Exception {
        DSSTestCaseUtils dssTest = new DSSTestCaseUtils();
        super.init();
        dssTest.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(), sessionCookie, serviceName);
    }

    @Test(dependsOnMethods = {"isServiceExistAfterRestarting"})
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void invokeOperation() throws AxisFault {
        for (int i = 0; i < 5; i++) {
            client.getCustomerInBoston();
        }
        log.info("Service Invocation Success");
    }

    //TestCase for https://wso2.org/jira/browse/CARBON-15172
    @Test(dependsOnMethods = {"isServiceExistAfterRestarting"}, enabled = false)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    public void testMBeanForDatasource() throws AxisFault {
        Map<String, String[]> env = new HashMap<String, String[]>();
        String[] credentials = {"admin", "admin"};
        env.put(JMXConnector.CREDENTIALS, credentials);
        try {
            String url = "service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi";
            JMXServiceURL jmxUrl = new JMXServiceURL(url);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxUrl, env);
            MBeanServerConnection mBeanServer = jmxConnector.getMBeanServerConnection();
            ObjectName mbeanObject = new ObjectName(dataSourceName + ",-1234:type=DataSource");
            MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(mbeanObject);
            Assert.assertNotNull(mBeanInfo, "Datasource is registered in the MBean server");
        } catch (MalformedURLException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(),e);
        } catch (IOException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(),e);
        } catch (MalformedObjectNameException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(),e);
        } catch (IntrospectionException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(),e);
        } catch (ReflectionException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(),e);
        } catch (InstanceNotFoundException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(),e);
        }

    }

    private String createDataSource() throws Exception {

        DataServiceAdminClient dataServiceAdminService =
                new DataServiceAdminClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        NDataSourceAdminServiceClient dataSourceAdminService =
                new NDataSourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        String[] list = dataServiceAdminService.getCarbonDataSources();
        String createDataSourceResponse = null;
        WSDataSourceMetaInfo dataSourceInfo;
        String carbonDataSourceName = null;

        sqlDataSource =
                new SqlDataSourceUtil(sessionCookie,dssContext.getContextUrls().getBackEndUrl());

        sqlDataSource.createDataSource(getSqlScript());
        String databaseName = sqlDataSource.getDatabaseName();
        dataSourceName = databaseName + "DataSource";


            if (list != null) {
                for (String ds : list) {
                    if (dataSourceName.equalsIgnoreCase(ds)) {
                        dataSourceAdminService.deleteDataSource(ds);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            log.error("InterruptedException : " + e);
                            Assert.fail("InterruptedException : " + e);
                        }
                    }
                }
            }

            dataSourceInfo = getDataSourceInformation(dataSourceName);

            dataSourceAdminService.addDataSource(dataSourceInfo);
            createDataSourceResponse = dataSourceName;


        list = dataServiceAdminService.getCarbonDataSources();
        Assert.assertNotNull(list, "Datasource list null");
        for (String ds : list) {
            if (ds.equals(createDataSourceResponse)) {
                carbonDataSourceName = ds;
                break;
            }
        }

        Assert.assertNotNull("Datasource Not found in Datasource List", carbonDataSourceName);
        return carbonDataSourceName;
    }

    private List<File> getSqlScript() throws XPathExpressionException {
        ArrayList<File> al = new ArrayList<File>();
        al.add(selectSqlFile("CreateTables.sql"));
        al.add(selectSqlFile("Customers.sql"));
        return al;
    }

    private DataHandler createArtifactWithDataSource(String serviceFileName)
            throws XMLStreamException, IOException, XPathExpressionException {
        Assert.assertNotNull("Carbon datasource name null. create carbon datasource first", carbonDataSourceName);
        try {

            OMElement dbsFile = AXIOMUtil.stringToOM(FileManager.readFile(getResourceLocation() + File.separator + "dbs" + File.separator
                                                                          + "rdbms" + File.separator + "MySql"
                                                                          + File.separator + serviceFileName).trim());
            OMElement dbsConfig = dbsFile.getFirstChildWithName(new QName("config"));

            Iterator configElement1 = dbsConfig.getChildElements();
            while (configElement1.hasNext()) {
                OMElement property = (OMElement) configElement1.next();
                String value = property.getAttributeValue(new QName("name"));
                if ("carbon_datasource_name".equals(value)) {
                    property.setText(carbonDataSourceName);

                }
            }
            if (log.isDebugEnabled()) {
                log.debug(dbsFile);
            }
            ByteArrayDataSource dbs = new ByteArrayDataSource(dbsFile.toString().getBytes());
            return new DataHandler(dbs);

        } catch (XMLStreamException e) {
            log.error("XMLStreamException when Reading Service File", e);
            throw new XMLStreamException("XMLStreamException when Reading Service File", e);
        } catch (IOException e) {
            log.error("IOException when Reading Service File", e);
            throw new IOException("IOException  when Reading Service File", e);
        }
    }

    private WSDataSourceMetaInfo getDataSourceInformation(String dataSourceName)
            throws XMLStreamException {
        WSDataSourceMetaInfo dataSourceInfo = new WSDataSourceMetaInfo();

        dataSourceInfo.setName(dataSourceName);

        WSDataSourceMetaInfo_WSDataSourceDefinition dataSourceDefinition = new WSDataSourceMetaInfo_WSDataSourceDefinition();

        dataSourceDefinition.setType("RDBMS");
        OMElement dsConfig = AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                                  "<configuration>\n" +
                                                  "<driverClassName>" + sqlDataSource.getDriver() + "</driverClassName>\n" +
                                                  "<url>" + sqlDataSource.getJdbcUrl() + "</url>\n" +
                                                  "<username>" + sqlDataSource.getDatabaseUser() + "</username>\n" +
                                                  "<password encrypted=\"true\">" + sqlDataSource.getDatabasePassword() + "</password>\n" +
                                                  "</configuration>");


        dataSourceDefinition.setDsXMLConfiguration(dsConfig.toString());

        dataSourceInfo.setDefinition(dataSourceDefinition);

        return dataSourceInfo;
    }

}
