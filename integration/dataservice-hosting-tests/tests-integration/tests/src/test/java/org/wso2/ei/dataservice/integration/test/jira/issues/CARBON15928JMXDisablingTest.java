/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.NDataSourceAdminServiceClient;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.ei.dataservice.integration.common.utils.SqlDataSourceUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceAdminClient;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Requires kernel 4.4.6
public class CARBON15928JMXDisablingTest extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(CARBON15928JMXDisablingTest.class);

    private String carbonDataSourceName;
    private SqlDataSourceUtil sqlDataSource;
    private String dataSourceName = "";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        carbonDataSourceName = createDataSource();
        log.info(carbonDataSourceName + " carbon Datasource Created");
    }

    @Test(groups = { "wso2.dss" }, expectedExceptions = InstanceNotFoundException.class)
    public void testMBeanForDatasourceBeforeRestart()
            throws Exception {
        testMBeanForDatasource();
    }

    private MBeanInfo testMBeanForDatasource() throws Exception {
        Map<String, String[]> env = new HashMap<>();
        String[] credentials = { "admin", "admin" };
        env.put(JMXConnector.CREDENTIALS, credentials);
        try {
            String url = "service:jmx:rmi://localhost:12311/jndi/rmi://localhost:11199/jmxrmi";
            JMXServiceURL jmxUrl = new JMXServiceURL(url);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxUrl, env);
            MBeanServerConnection mBeanServer = jmxConnector.getMBeanServerConnection();
            ObjectName mbeanObject = new ObjectName(dataSourceName + ",-1234:type=DataSource");
            MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(mbeanObject);
            return mBeanInfo;
        } catch (MalformedURLException | MalformedObjectNameException | IntrospectionException |
                ReflectionException e) {
            throw new AxisFault("Error while connecting to MBean Server " + e.getMessage(), e);
        }
    }

    private String createDataSource() throws Exception {

        DataServiceAdminClient dataServiceAdminService = new DataServiceAdminClient(
                dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        NDataSourceAdminServiceClient dataSourceAdminService = new NDataSourceAdminServiceClient(
                dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        String[] list = dataServiceAdminService.getCarbonDataSources();
        WSDataSourceMetaInfo dataSourceInfo;
        String carbonDataSourceName = null;

        sqlDataSource = new SqlDataSourceUtil(sessionCookie, dssContext.getContextUrls().getBackEndUrl());

        sqlDataSource.createDataSource(new ArrayList<File>());
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

        list = dataServiceAdminService.getCarbonDataSources();
        Assert.assertNotNull(list, "Datasource list null");
        for (String ds : list) {
            if (ds.equals(dataSourceName)) {
                carbonDataSourceName = ds;
                break;
            }
        }

        Assert.assertNotNull(carbonDataSourceName, "Datasource Not found in Datasource List");
        return carbonDataSourceName;
    }

    private WSDataSourceMetaInfo getDataSourceInformation(String dataSourceName) throws XMLStreamException {
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

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
}
