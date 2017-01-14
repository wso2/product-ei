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
package org.wso2.carbon.esb.mediator.test.db.dbreport;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.test.utils.dbutils.MySqlDatabaseManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class DBMediatorUseTransaction extends ESBIntegrationTest {
    private MySqlDatabaseManager mySqlDatabaseManager1, mySqlDatabaseManager2;
    private ServerConfigurationManager serverConfigurationManager;
//    private final DataSource dbConfig = new EnvironmentBuilder().getFrameworkSettings().getDataSource();
//    private final String JDBC_URL = dbConfig.getDbUrl();
//    private final String DB_USER = dbConfig.getDbUser();
//    private final String DB_PASSWORD = dbConfig.getDbPassword();
//    private final String JDBC_DRIVER = dbConfig.get_dbDriverName();

    private String JDBC_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private String DATASOURCE_NAME;
    private String JDBC_DRIVER;

    private final String MYSQL_JAR = "mysql-connector-java-5.1.6.jar";
    private final String DB_NAME1 = "SampleDBForAutomation1";
    private final String DB_NAME2 = "SampleDBForAutomation2";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        OMElement updatedSynapseContent;

        super.init();
        AutomationContext automationContext = new AutomationContext();
        DATASOURCE_NAME = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_NAME);
        DB_PASSWORD = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        JDBC_URL = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        DB_USER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);
        JDBC_DRIVER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        serverConfigurationManager = new ServerConfigurationManager(context);
        copyJDBCDriverToClassPath();
        super.init();
        updatedSynapseContent = updateSynapseConfiguration();
        handlingMysqlDB();
        updateESBConfiguration(updatedSynapseContent);
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Test UseTransaction option .Use in conjunction with Transaction mediator "
    )
    public void testDBmediatorSuccessCase() throws AxisFault, SQLException {
        int IBMcountDB1, IBMcountDB2;

        IBMcountDB1 = getDatabaseResultsForDB1();
        assertEquals(IBMcountDB1, 1, "Fault, invalid response");
        IBMcountDB2 = getDatabaseResultsForDB2();
        assertEquals(IBMcountDB2, 0, "Fault, invalid response");
        axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "IBM");
        IBMcountDB1 = getDatabaseResultsForDB1();
        assertEquals(IBMcountDB1, 0, "Fault, Record Not Deleted from Database1");
        IBMcountDB2 = getDatabaseResultsForDB2();
        assertEquals(IBMcountDB2, 1, "Fault, Record Not Inserted to Database2");

    }
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    /*JIRA issue: https://wso2.org/jira/browse/ESBJAVA-1553*/
    @Test(groups = "wso2.esb", enabled = false, description = "Test UseTransaction option ." +
                                                                            "Use in conjunction with Transaction mediator. Fail casse"
    )
    public void testDBmediatorFailCase() throws AxisFault, SQLException {
        OMElement response;
        int SUNcountDB1, SUNcountDB2;

        SUNcountDB1 = getDatabaseResultsForDB1FailCase();
        assertEquals(SUNcountDB1, 1, "Fault, invalid response");
        SUNcountDB2 = getDatabaseResultsForDB2FailCase();
        assertEquals(SUNcountDB2, 1, "Fault, invalid response");
        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "SUN");
        SUNcountDB1 = getDatabaseResultsForDB1FailCase();
        assertEquals(SUNcountDB1, 1, "Fault, invalid response. Transaction is not rollbacked.");
        SUNcountDB2 = getDatabaseResultsForDB2FailCase();
        assertEquals(SUNcountDB2, 1, "Fault, invalid response.Transaction is not rollbacked.");

    }


    @AfterClass(alwaysRun = true)
    public void close() throws Exception {

        try {
            mySqlDatabaseManager1.executeUpdate("DROP DATABASE " + DB_NAME1);
            mySqlDatabaseManager2.executeUpdate("DROP DATABASE " + DB_NAME2);

        } finally {
            mySqlDatabaseManager1.disconnect();
            mySqlDatabaseManager2.disconnect();

        }
        super.cleanup();
        super.init();
        loadSampleESBConfiguration(0);
        serverConfigurationManager.removeFromComponentLib(MYSQL_JAR);
        serverConfigurationManager.restartGracefully();

    }

    private int getDatabaseResultsForDB1() throws SQLException {
        int count = 0;
        ResultSet rs = mySqlDatabaseManager1.executeQuery("SELECT * FROM company where name='IBM' ");

        while (rs.next()) {
            count++;
        }
        rs.close();
        return count;
    }

    private int getDatabaseResultsForDB1FailCase() throws SQLException {
        int count = 0;
        ResultSet rs = mySqlDatabaseManager1.executeQuery("SELECT * FROM company where name='SUN' ");

        while (rs.next()) {
            count++;
            // companyName=rs.getString("name");
        }
        rs.close();
        return count;
    }

    private int getDatabaseResultsForDB2FailCase() throws SQLException {
        int count = 0;
        ResultSet rs = mySqlDatabaseManager1.executeQuery("SELECT * FROM company where name='SUN' ");

        while (rs.next()) {
            count++;
        }
        rs.close();
        return count;
    }

    private int getDatabaseResultsForDB2() throws SQLException {
        int count = 0;
        ResultSet rs = mySqlDatabaseManager2.executeQuery("SELECT * FROM company where name='IBM' ");

        while (rs.next()) {
            count++;
        }
        rs.close();
        return count;
    }

    private void copyJDBCDriverToClassPath() throws Exception {
        File jarFile;

        jarFile = new File(getClass().getResource("/artifacts/ESB/jar/" + MYSQL_JAR + "").getPath());
        serverConfigurationManager.copyToComponentLib(jarFile);
        serverConfigurationManager.restartGracefully();
    }

    private OMElement updateSynapseConfiguration() throws Exception {
        OMElement synapseContent;
        URL url = getClass().getResource("/artifacts/ESB/mediatorconfig/dbreport/synapse_use_transaction.xml");
        String s = FileUtils.readFileToString(new File(url.toURI()));
        s = s.replace("$SampleDBForAutomation1", JDBC_URL + "/" + DB_NAME1);
        s = s.replace("$SampleDBForAutomation2", JDBC_URL + "/" + DB_NAME2);
        s = s.replace("####", DB_USER);
        s = s.replace("$$$$", DB_PASSWORD);
        synapseContent = AXIOMUtil.stringToOM(s);
        return synapseContent;

    }

    private void handlingMysqlDB() throws ClassNotFoundException, SQLException {
        mySqlDatabaseManager1 = new MySqlDatabaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
        mySqlDatabaseManager2 = new MySqlDatabaseManager(JDBC_URL, DB_USER, DB_PASSWORD);

        mySqlDatabaseManager1.executeUpdate("DROP DATABASE IF EXISTS " + DB_NAME1);
        mySqlDatabaseManager1.executeUpdate("Create DATABASE " + DB_NAME1);
        mySqlDatabaseManager1.executeUpdate("USE " +DB_NAME1);
        mySqlDatabaseManager1.executeUpdate("CREATE table company(name varchar(10) primary key, id varchar(10), price double) ENGINE= \"InnoDB\" ");
        mySqlDatabaseManager1.executeUpdate("INSERT into company values ('IBM','c1',0.0)");
        mySqlDatabaseManager1.executeUpdate("INSERT into company values ('SUN','c2',0.0)");

        mySqlDatabaseManager2.executeUpdate("DROP DATABASE IF EXISTS " + DB_NAME2);
        mySqlDatabaseManager2.executeUpdate("Create DATABASE " + DB_NAME2);
        mySqlDatabaseManager2.executeUpdate("USE " + DB_NAME2);
        mySqlDatabaseManager2.executeUpdate("CREATE table company(name varchar(10) primary key, id varchar(10), price double) ENGINE= \"InnoDB\" ");
        mySqlDatabaseManager2.executeUpdate("INSERT into company values ('SUN','c2',0.0)");
        mySqlDatabaseManager2.executeUpdate("INSERT into company values ('MSFT','c3',0.0)");
    }

}
