/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mediator.test.classMediator;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import javax.xml.namespace.QName;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import org.awaitility.Awaitility;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PropertyPersistenceDeletingAndAddingTstCase extends ESBIntegrationTest {
    private static final Log log = LogFactory.getLog(PropertyPersistenceDeletingAndAddingTstCase.class);
    private static final String CLASS_JAR_FIVE_PROPERTIES="org.wso2.carbon.test.mediator.stockmediator-v1.0.jar";
    private static final String CLASS_JAR_FOUR_PROPERTIES="org.wso2.carbon.test.mediator.stockmediator-v1.0.2.jar";
    private static final String JAR_LOCATION= "/artifacts/ESB/jar";

    private static final String osname = System.getProperty("os.name").toLowerCase().toString();
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        if (!(osname.contains("windows"))) {

            super.init();
            serverConfigurationManager = new ServerConfigurationManager(context);
            serverConfigurationManager.copyToComponentLib
                    (new File(getClass().getResource(JAR_LOCATION + File.separator + CLASS_JAR_FIVE_PROPERTIES).toURI()));
            serverConfigurationManager.restartGracefully();

            super.init();
            loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/class/class_property_persistence_five_properties.xml");
        } else {
            log.info("Skip the test execution in Windows. [Unable to delete dropins in Winodws]");
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb","localOnly"}, description = "Class Mediator " +
                                                           " -Class mediator property persistence -deleting and adding different properties")
    public void testMediationPersistenceDeletingAndAdding() throws Exception {
        if (!(osname.contains("windows"))) {
            OMElement response = axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(), null, "WSO2");

            String lastPrice=response.getFirstElement()
                                     .getFirstChildWithName(new QName("http://services.samples/xsd","last")).getText();
            assertNotNull(lastPrice, "Fault: response message 'last' price null");

            String symbol=response.getFirstElement()
                                  .getFirstChildWithName(new QName("http://services.samples/xsd","symbol")).getText();
            assertEquals(symbol, "WSO2", "Fault: value 'symbol' mismatched");

            /*
            INFO - StockQuoteMediator Starting Mediation -ClassMediator
            INFO - StockQuoteMediator Initialized with User:[esb user]
            INFO - StockQuoteMediator E-MAIL:[user@wso2.org]
            INFO - StockQuoteMediator Massage Id:
            INFO - StockQuoteMediator Original price:
            INFO - StockQuoteMediator Discounted price:
            INFO - StockQuoteMediator After taxed price:
            INFO - StockQuoteMediator Logged....

            Deleting User and email   refer: https://wso2.org/jira/browse/TA-532           param 5
            */

            serverConfigurationManager.removeFromComponentLib(CLASS_JAR_FIVE_PROPERTIES);
            serverConfigurationManager.copyToComponentLib
                    (new File(getClass().getResource(JAR_LOCATION + File.separator + CLASS_JAR_FOUR_PROPERTIES).toURI()));
            loadSampleESBConfiguration(0);

            String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
            String filePath = Paths.get(carbonHome, "lib", CLASS_JAR_FOUR_PROPERTIES).toString();
            File jarFile = new File(filePath);

            Awaitility.await()
                    .pollInterval(50, TimeUnit.MILLISECONDS)
                    .atMost(10000, TimeUnit.MILLISECONDS)
                    .until(isFileWrittenInDisk(jarFile));

            /* waiting for the new config file to be written to the disk */
            serverConfigurationManager.restartGracefully();

            super.init();
            loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/class/class_property_persistence_four_properties.xml");

            response=axis2Client.sendSimpleStockQuoteRequest(getMainSequenceURL(),null, "IBM");

            lastPrice=response.getFirstElement()
                              .getFirstChildWithName(new QName("http://services.samples/xsd","last")).getText();
            assertNotNull(lastPrice, "Fault: response message 'last' price null");

            symbol=response.getFirstElement()
                           .getFirstChildWithName(new QName("http://services.samples/xsd","symbol")).getText();
            assertEquals(symbol, "IBM", "Fault: value 'symbol' mismatched");

            /*
            INFO - StockQuoteMediator Starting Mediation -ClassMediator
            INFO - StockQuoteMediator Massage Id:
            INFO - StockQuoteMediator Original price:
            INFO - StockQuoteMediator Discounted price:
            INFO - StockQuoteMediator After taxed price:
            INFO - StockQuoteMediator Final price:              //added
            INFO - StockQuoteMediator Logged....

            added "servicesDeduction"  refer: https://wso2.org/jira/browse/TA-532            param 4
             */

        } else {
            log.info("Skip the test execution in Windows. [Unable to delete dropins in Winodws]");
        }
    }

    private Callable<Boolean> isFileWrittenInDisk(final File jar) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (jar.exists()) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if (!(osname.contains("windows"))) {
            super.cleanup();
            serverConfigurationManager.removeFromComponentLib(CLASS_JAR_FOUR_PROPERTIES);
            serverConfigurationManager = null;
        } else {
            log.info("Skip the test execution in Windows. [Unable to delete dropins in Winodws]");
        }
    }
}
