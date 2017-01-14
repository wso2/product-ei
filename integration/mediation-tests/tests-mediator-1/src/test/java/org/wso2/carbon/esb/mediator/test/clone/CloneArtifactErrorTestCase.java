package org.wso2.carbon.esb.mediator.test.clone;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * This test scenario test if esb continue running without hanging up with an uncaught exception in clone artifact.
 * https://wso2.org/jira/browse/CARBON-13759
 */
public class CloneArtifactErrorTestCase extends ESBIntegrationTest {


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/patch_automation/CloneArtifactTestCase.xml");
    }

    /**
     * First send a error request using clone mediator and with the AxisFault response,
     * sending another correct request to esb and checking if esb working without stopping mediation.
     */
    @Test(groups = {"wso2.esb"}, description = "This test case to varify if esb continue working without hang up with an error request")
    public void TesPatchAutomation1() throws AxisFault {
        OMElement response1 = null;
        try {
            axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("proxyFault1"), null, "WSO2");
            fail("Giving response for error sequence of soap message");
        } catch (AxisFault axisFault) {
            //For the first fault request it should return an Axis Fault as response.
        }
            //Checking if the esb running after giving an AxisFault error response
        response1 = axis2Client.sendSimpleStockQuoteRequest(getProxyServiceURLHttp("proxyFault2"),null, "WSO2");
        assertTrue(response1.toString().contains("WSO2"), "Fails to return a correct response. ESB stopped mediation");
    }


    @AfterClass(groups = "wso2.esb")
    public void close() throws Exception {
        super.cleanup();
    }
}
