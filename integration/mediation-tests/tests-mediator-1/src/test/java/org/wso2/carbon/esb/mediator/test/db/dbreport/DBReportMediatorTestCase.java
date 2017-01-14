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
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.test.utils.dbutils.MySqlDatabaseManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class DBReportMediatorTestCase extends ESBIntegrationTest {

    private final String MYSQL_JAR = "mysql-connector-java-5.1.6.jar";
    private ServerConfigurationManager serverConfigurationManager;
    private MySqlDatabaseManager mySqlDatabaseManager;
//    private final DataSource dbConfig = new EnvironmentBuilder().getFrameworkSettings()
//            .getDataSource();
//    private final String JDBC_DRIVER = dbConfig.get_dbDriverName();
//    private final String JDBC_URL = dbConfig.getDbUrl();
//    private final String DB_USER = dbConfig.getDbUser();
//    private final String DB_PASSWORD = dbConfig.getDbPassword();

    private String JDBC_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private String DATASOURCE_NAME;
    private String JDBC_DRIVER;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        AutomationContext automationContext = new AutomationContext();
        DATASOURCE_NAME = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_NAME);
        DB_PASSWORD = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        JDBC_URL = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        DB_USER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);
        JDBC_DRIVER = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        serverConfigurationManager = new ServerConfigurationManager(context);
        copyJDBCDriverToClassPath();
        mySqlDatabaseManager = new MySqlDatabaseManager(JDBC_URL, DB_USER, DB_PASSWORD);
        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS SampleDBForAutomation");



        super.init();
    }

    @BeforeMethod(alwaysRun = true)
    public void createDatabase() throws SQLException {
        mySqlDatabaseManager.executeUpdate("DROP DATABASE IF EXISTS SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("Create DATABASE SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("USE SampleDBForAutomation");
        mySqlDatabaseManager.executeUpdate("CREATE TABLE company(price double, name varchar(20))");

    }

    /*  before a request is sent to the db mediator the count of price rows greater than 1000 should
be 3. After the request is gone through db mediator the count shoud be zero. price values
greater than 1000 will remain with the count of one */
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "DBLookup/DBReport mediator should replace a" +
                                             " &lt;/&gt; with </>")
    public void DBMediatorReplaceLessThanAndGreaterThanSignTestCase() throws Exception {
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100,'ABC')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(200,'CDE')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300,'MNO')");

        File synapseFile = new File(getClass().getResource("/artifacts/ESB/mediatorconfig/dbreport/synapse_dbReport.xml").getPath());
        updateESBConfiguration(updateSynapseConfiguration(synapseFile));

        int numOfPrice, numOfPriceGreaterThan;
        numOfPrice = getRecordCount("SELECT price from company WHERE price < 1000 ");
        numOfPriceGreaterThan = getRecordCount("SELECT price from company WHERE price > 1000 ");
        assertEquals(numOfPrice, 3, "Fault, invalid response");
        assertEquals(numOfPriceGreaterThan, 1, "Fault, invalid response");
        axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        numOfPrice = getRecordCount("SELECT price from company WHERE price < 1000 ");
        numOfPriceGreaterThan = getRecordCount("SELECT price from company WHERE price > 1000 ");
        assertEquals(numOfPrice, 0, "Fault, invalid response");
        assertEquals(numOfPriceGreaterThan, 1, "Fault, invalid response");

    }

    /*  before a request is sent the database has "200.0"(WSO2_PRICE) as the value corresponding to
the 'name' "WSO2".
* After the request is sent, the value 200.0 is replaced by the value given by xpath to response
* message content. */

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.esb", description = "Insert or update DB table using message coontents."
    )
    public void DBReportUseMessageContentTestCase() throws Exception {
        double price = 200.0;
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(100.0,'ABC')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(2000.0,'XYZ')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(" + price + ",'WSO2')");
        mySqlDatabaseManager.executeUpdate("INSERT INTO company VALUES(300.0,'MNO')");

        File synapseFile = new File(getClass().getResource("/artifacts/ESB/mediatorconfig/dbreport/synapse_sample_361.xml").getPath());
        updateESBConfiguration(updateSynapseConfiguration(synapseFile));
        OMElement response;
        String priceMessageContent;

        priceMessageContent = getPrice();
        assertEquals(priceMessageContent, Double.toString(price), "Fault, invalid response");
        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");
        priceMessageContent = getPrice();
        OMElement returnElement = response.getFirstElement();
        OMElement lastElement = returnElement.getFirstChildWithName(new QName("http://services.samples/xsd", "last"));
        assertEquals(priceMessageContent, lastElement.getText(), "Fault, invalid response");
    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {

        try {
            mySqlDatabaseManager.executeUpdate("DROP DATABASE SampleDBForAutomation");
        } finally {
            mySqlDatabaseManager.disconnect();


        }
        super.cleanup();
        super.init();
        loadSampleESBConfiguration(0);
        serverConfigurationManager.removeFromComponentLib(MYSQL_JAR);
        serverConfigurationManager.restartGracefully();
    }

    private OMElement updateSynapseConfiguration(File synapseFile)
            throws IOException, XMLStreamException {

        OMElement synapseContent;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(synapseFile));
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(bufferedInputStream);
        StAXOMBuilder stAXOMBuilder = new StAXOMBuilder(xmlStreamReader);
        synapseContent = stAXOMBuilder.getDocumentElement();
        synapseContent.build();
        bufferedInputStream.close();

        OMElement sequenceElemnt = synapseContent.getFirstElement();
        OMElement outElement = sequenceElemnt.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "out"));
        OMElement dbReportElemnet = outElement.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "dbreport"));
        OMElement connectionElement = dbReportElemnet.getFirstChildWithName(
                new QName("http://ws.apache.org/ns/synapse", "connection"));
        OMElement poolElement = connectionElement.getFirstElement();
        OMElement driverElemnt = poolElement.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "driver"));
        OMElement urlElemnt = poolElement.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "url"));
        OMElement userElemnt = poolElement.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "user"));
        OMElement passwordElemnt = poolElement.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "password"));

        driverElemnt.setText(JDBC_DRIVER);
        urlElemnt.setText(JDBC_URL + "/SampleDBForAutomation");
        userElemnt.setText(DB_USER);
        passwordElemnt.setText(DB_PASSWORD);
        return synapseContent;
    }

    private int getRecordCount(String sql) throws SQLException {
        ResultSet rs = mySqlDatabaseManager.executeQuery(sql);
        int count = 0;
        while (rs.next()) {
            count++;
        }
        rs.close();
        return count;
    }

    private String getPrice() throws SQLException {
        String price = null;
        ResultSet rs = mySqlDatabaseManager.executeQuery("SELECT price from company WHERE name = 'WSO2'");

        while (rs.next()) {
            price = Double.toString(rs.getDouble("price"));
        }
        rs.close();
        return price;
    }

    private void copyJDBCDriverToClassPath() throws Exception {
        File jarFile;
        jarFile = new File(getClass().getResource("/artifacts/ESB/jar/" + MYSQL_JAR + "").getPath());
        System.out.println(jarFile.getName());
        serverConfigurationManager.copyToComponentLib(jarFile);
        serverConfigurationManager.restartGracefully();
    }
}
