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

package org.wso2.carbon.esb.samples.test.mediation.db;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.SqlDataSourceUtil;

import java.io.File;
import java.util.ArrayList;

public class Sample360TestCase extends ESBIntegrationTest {

    private SqlDataSourceUtil sqlDataSourceUtil = null;


    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {

        super.init();
        sqlDataSourceUtil = new SqlDataSourceUtil(getSessionCookie(), contextUrls.getBackEndUrl());
        addDB();

    }


    public void addDB() throws Exception {
        ArrayList<File> sqlFileList = new ArrayList<File>();

        File h2file = new File(FrameworkPathUtil.getCarbonHome() + File.separator
                + "dbscripts" + File.separator + "h2.sql");

        File stock = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "artifacts" + File.separator + "ESB" + File.separator + "sql" + File.separator
                + "stock.sql");

        sqlFileList.add(h2file);
        sqlFileList.add(stock);

        sqlDataSourceUtil.createDataSource("WSO2_CARBON_DB", sqlFileList);

        OMElement synapseConfig = AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- Introduction to dblookp mediator -->\n" +
                "<definitions xmlns=\"http://ws.apache.org/ns/synapse\">\n" +
                "\n" +
                "    <sequence name=\"myFaultHandler\">\n" +
                "        <makefault response=\"true\">\n" +
                "            <code xmlns:tns=\"http://www.w3.org/2003/05/soap-envelope\" value=\"tns:Receiver\"/>\n" +
                "            <reason expression=\"get-property('ERROR_MESSAGE')\"/>\n" +
                "        </makefault>\n" +
                "        <send/>\n" +
                "        <drop/>\n" +
                "    </sequence>\n" +
                "\n" +
                "    <sequence name=\"main\" onError=\"myFaultHandler\">\n" +
                "        <in>\n" +
                "            <log level=\"custom\">\n" +
                "                <property name=\"text\" value=\"** Looking up from the Database **\"/>\n" +
                "            </log>\n" +
                "            <dblookup>\n" +
                "                <connection>\n" +
                "                    <pool>\n" +
                "                        <driver>" + sqlDataSourceUtil.getDriver() + "</driver>\n" +
                "                        <url>" + sqlDataSourceUtil.getJdbcUrl() + "</url>\n" +
                "                        <user>" + sqlDataSourceUtil.getDatabaseUser() +"</user>\n" +
                "                        <password>"+ sqlDataSourceUtil.getDatabasePassword() +"</password>\n" +
                "                    </pool>\n" +
                "                </connection>\n" +
                "                <statement>\n" +
                "                    <sql>select * from company where name =?</sql>\n" +
                "                    <parameter xmlns:m0=\"http://services.samples\" expression=\"//m0:getQuote/m0:request/m0:symbol\"\n" +
                "                               type=\"VARCHAR\"/>\n" +
                "                    <result name=\"company_id\" column=\"id\"/>\n" +
                "                </statement>\n" +
                "            </dblookup>\n" +
                "\n" +
                "            <switch source=\"get-property('company_id')\">\n" +
                "                <case regex=\"c1\">\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" expression=\"fn:concat('Company ID - ',get-property('company_id'))\"/>\n" +
                "                    </log>\n" +
                "                    <send>\n" +
                "                        <endpoint>\n" +
                "                            <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                "                        </endpoint>\n" +
                "                    </send>\n" +
                "                </case>\n" +
                "                <case regex=\"c2\">\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" expression=\"fn:concat('Company ID - ',get-property('company_id'))\"/>\n" +
                "                    </log>\n" +
                "                    <send>\n" +
                "                        <endpoint>\n" +
                "                            <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                "                        </endpoint>\n" +
                "                    </send>\n" +
                "                </case>\n" +
                "                <case regex=\"c3\">\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" expression=\"fn:concat('Company ID - ',get-property('company_id'))\"/>\n" +
                "                    </log>\n" +
                "                    <send>\n" +
                "                        <endpoint>\n" +
                "                            <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
                "                        </endpoint>\n" +
                "                    </send>\n" +
                "                </case>\n" +
                "                <default>\n" +
                "                    <log level=\"custom\">\n" +
                "                        <property name=\"text\" value=\"** Unrecognized Company ID **\"/>\n" +
                "                    </log>\n" +
                "                    <makefault response=\"true\">\n" +
                "                        <code xmlns:tns=\"http://www.w3.org/2003/05/soap-envelope\" value=\"tns:Receiver\"/>\n" +
                "                        <reason value=\"** Unrecognized Company ID **\"/>\n" +
                "                    </makefault>\n" +
                "                    <send/>\n" +
                "                    <drop/>\n" +
                "                </default>\n" +
                "            </switch>\n" +
                "            <drop/>\n" +
                "        </in>\n" +
                "\n" +
                "        <out>\n" +
                "            <send/>\n" +
                "        </out>\n" +
                "\n" +
                "    </sequence>\n" +
                "\n" +
                "</definitions>");

        updateESBConfiguration(synapseConfig);
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = {"wso2.esb"}, description = "testDBMediator ")
    public void testDBMediator() throws Exception {

        OMElement response = null;
        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                getBackEndServiceUrl("SimpleStockQuoteService"), "IBM");

        Assert.assertTrue(response.toString().contains("IBM Company"), "Response is invalid for IBM");

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                getBackEndServiceUrl("SimpleStockQuoteService"), "SUN");

        Assert.assertTrue(response.toString().contains("SUN Company"), "Response is invalid for SUN");

        response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),
                getBackEndServiceUrl("SimpleStockQuoteService"), "MSFT");

        Assert.assertTrue(response.toString().contains("MSFT Company"), "Response is invalid for MSFT");

    }

    @AfterClass(alwaysRun = true,  enabled = false)
    public void deleteService() throws Exception {

        super.cleanup();
    }


}
