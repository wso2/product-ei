package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/DS-1189
 */
public class DS1189LeagyTimeStampModeTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS1189LeagyTimeStampModeTestCase.class);

    private final String serviceName = "TimeStampDifferenceLegacy";
    private String backendUrl = "https://localhost:10653/services/";
    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
    private Map<String, String> startupParameterMap;

    private DSSTestServerManager testServerManager;
    private String backupUserDir;

    private String backupCarbonHome;

    private AuthenticatorClient loginClient;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();

        startupParameterMap = new HashMap<String, String>();
        startupParameterMap.put("-DportOffset", "1210");

        testServerManager = new DSSTestServerManager(dssContext, null, startupParameterMap) {

            public void configureServer() throws AutomationFrameworkException {

                try {
                    File sourceFile = new File(getResourceLocation() + File.separator + "serverConfigs" + File.separator
                            + getParameter("shFilename"));

                    //copying wso2server.sh file to bin folder
                    FileManager.copyFile(sourceFile, this.getCarbonHome() + File.separator + "bin" + File.separator + "wso2server.sh");


                } catch (IOException e) {
                    throw new AutomationFrameworkException(e.getMessage(), e);
                } catch (XPathExpressionException e) {
                    throw new AutomationFrameworkException(e.getMessage(), e);
                }

            }
        };

        testServerManager.setParameter("shFilename", "wso2serverLegacyMode.sh");

        String testServerCarbonHome = testServerManager.startServer();

        backupUserDir = System.getProperty("user.dir");
        backupCarbonHome = System.getProperty("carbon.home");

        System.out.println("****************  back up user dir   " + backupUserDir);
        System.out.println("****************  back up carbonhome " + backupCarbonHome);


        System.setProperty("carbon.home", testServerCarbonHome);

        loginClient = new AuthenticatorClient(backendUrl);
        sessionCookie = loginClient.login(userInfo.getUserName(), userInfo.getPassword(), "localhost");

        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTableTimeStamp.sql"));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                        + "rdbms" + File.separator + "h2" + File.separator
                        + serviceName + ".dbs", sqlFileLis));

        insertTimeStampToDb("Insert With America/New_York Time Zone", "1970-01-02T12:00:00.000+02:00");

        testServerManager.stopServer();
        testServerManager.removeParameter("shFilename");
        testServerManager.setParameter("shFilename", "wso2serverLegacyMode1.sh");
        testServerManager.configureServer();
        testServerManager.startServer();

        sessionCookie = loginClient.login(userInfo.getUserName(), userInfo.getPassword(), "localhost");

        insertTimeStampToDb("Insert With UTC Time Zone", "1970-01-02T12:00:00.000+02:00");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        System.setProperty("carbon.home", backupCarbonHome);
        System.setProperty("user.dir", backupUserDir);
        cleanup();
    }

    @Test(groups = {"wso2.dss"}, description = "insert timestamp in America/New_York timezone and UTC timezone, retrieve all and compare whether they are different", alwaysRun = true)
    public void insertAndTestTimeStampValuesInDbTest() throws Exception {
        OMElement payload = fac.createOMElement("getTimeStamps", omNs);

        OMElement result = new AxisServiceClient().sendReceive(payload, backendUrl + serviceName, "getTimeStamps");

        Iterator iterator = result.getChildrenWithLocalName("timeStamp");

        String timeStampString = null;

        while (iterator.hasNext()) {
            OMElement timeStamp = (OMElement) iterator.next();
            if (timeStampString == null) {
                timeStampString = timeStamp.getChildrenWithLocalName("testTimeStamp").next().toString();
                log.info("TimeStamp Recv:"+timeStampString);
                Assert.assertTrue(timeStampString.contains("1970-01-02T05:00:00.000+00:00"));
            } else {
                String tempTimeStamp = timeStamp.getChildrenWithLocalName("testTimeStamp").next().toString();
                log.info("Timestamp Comapre:"+timeStampString+"|"+tempTimeStamp);
                Assert.assertFalse(timeStampString.equals(tempTimeStamp));
            }
        }
        Assert.assertNotNull(result, "Response message null ");
        log.debug(result);
        log.info("data service insert different timestamp to the database when the server is in different timezones");
    }

    /**
     * helper method to insert timestamp values to the database
     *
     * @param idString
     * @param timeStamp
     * @throws org.apache.axis2.AxisFault
     */
    private void insertTimeStampToDb(String idString, String timeStamp) throws Exception {
        OMElement payload = fac.createOMElement("insertTimeStamp", omNs);

        OMElement idStringElmnt = fac.createOMElement("idString", omNs);
        idStringElmnt.setText(idString + "");
        payload.addChild(idStringElmnt);

        OMElement timeStampElmnt = fac.createOMElement("testTimeStamp", omNs);
        timeStampElmnt.setText(timeStamp + "");
        payload.addChild(timeStampElmnt);

        new AxisServiceClient().sendRobust(payload, backendUrl + serviceName, "insertTimeStamp");
    }

    /**
     * helper method to deploy the service in custom new server
     *
     * @param serviceName
     * @param dssConfiguration
     * @throws Exception
     */
    protected void deployService(String serviceName, DataHandler dssConfiguration)
            throws Exception {
        DSSTestCaseUtils dssTest = new DSSTestCaseUtils();
        Assert.assertTrue(dssTest.uploadArtifact(backendUrl, sessionCookie, serviceName,
                        dssConfiguration),
                "Service File Uploading failed");
        Assert.assertTrue(dssTest.isServiceDeployed(backendUrl, sessionCookie, serviceName),
                "Service Not Found, Deployment time out ");
    }
}
