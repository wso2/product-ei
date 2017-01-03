package org.wso2.ei.dataservice.integration.test.jira.issues;

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
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This test case is to verify https://wso2.org/jira/browse/DS-1090,
 * to validate escape non printable characters with null values
 */

public class DS1090EscapeNonPrintableCharactersTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS1090EscapeNonPrintableCharactersTestCase.class);

    private final String serviceName = "EscapeNonPrintableCharactersTest";
    OMFactory fac = OMAbstractFactory.getOMFactory();
    OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        sqlFileLis.add(selectSqlFile("Students.sql"));
        deployService(serviceName,
                createArtifact(getResourceLocation() + File.separator + "dbs"
                        + File.separator + "rdbms" + File.separator + "h2"
                        + File.separator + serviceName + ".dbs", sqlFileLis));
    }

    @AfterClass
    public void clean() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = { "wso2.dss" })
    public void testForNullResultSet() throws AxisFault {
        OMElement payload = fac.createOMElement("select_all_Customers_operation", omNs);
        OMElement result = null;
        try {
            result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "select_all_Customers_operation");
        } catch (XPathExpressionException e) {
            log.info("EscapeNonPrintableCharactersTestCase failed ",e);
        }
        Assert.assertNotNull(result, "Response message null ");
    }

}
