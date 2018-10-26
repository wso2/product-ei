/*
 *Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.NDataSourceAdminServiceClient;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.ei.dataservice.integration.common.utils.SqlDataSourceUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// The testcase is to test https://github.com/wso2/product-ei/issues/2772

public class DataSourceAddAndCheckAfterServerStartupTestCase extends DSSIntegrationTest {

    private String carbonDataSourceName;
    private SqlDataSourceUtil sqlDataSource;
    ServerConfigurationManager serverManager;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        carbonDataSourceName = createDataSource();
        serverManager = new ServerConfigurationManager(dssContext);
        serverManager.restartGracefully();
        super.init();
    }

    @Test(groups = {"wso2.dss"}, description = "Test the data source after server restart", alwaysRun = true)
    public void testDataSourceAfterServerRestart() throws Exception {
        NDataSourceAdminServiceClient dataSourceAdminService =
                new NDataSourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        WSDataSourceInfo dataSourceFound = dataSourceAdminService.getDataSource(carbonDataSourceName);
        Assert.assertNotNull(dataSourceFound, "The dataSource is not found");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }

    private String createDataSource() throws Exception {
        sqlDataSource = new SqlDataSourceUtil(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        sqlDataSource.createDataSource(getSqlScript());
        String databaseName = sqlDataSource.getDatabaseName();
        carbonDataSourceName = databaseName + "DataSource1";
        WSDataSourceMetaInfo dataSourceInfo = getDataSourceInformation(carbonDataSourceName);
        NDataSourceAdminServiceClient dataSourceAdminService =
                new NDataSourceAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        dataSourceAdminService.addDataSource(dataSourceInfo);
        return carbonDataSourceName;
    }

    private WSDataSourceMetaInfo getDataSourceInformation(String dataSourceName)
            throws XMLStreamException {
        WSDataSourceMetaInfo dataSourceInfo = new WSDataSourceMetaInfo();
        dataSourceInfo.setName(dataSourceName);
        WSDataSourceMetaInfo_WSDataSourceDefinition dataSourceDefinition =
                new WSDataSourceMetaInfo_WSDataSourceDefinition();
        dataSourceDefinition.setType("RDBMS");
        OMElement dsConfig = AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\" " +
                "standalone=\"yes\"?>\n" +
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

    private List<File> getSqlScript() throws XPathExpressionException {
        ArrayList<File> al = new ArrayList<File>();
        al.add(selectSqlFile("CreateTables.sql"));
        al.add(selectSqlFile("Customers.sql"));
        return al;
    }

}
