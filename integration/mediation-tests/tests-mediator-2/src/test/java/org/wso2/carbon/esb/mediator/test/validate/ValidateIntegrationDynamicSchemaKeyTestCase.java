package org.wso2.carbon.esb.mediator.test.validate;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;

public class ValidateIntegrationDynamicSchemaKeyTestCase extends
        ESBIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath(File.separator + "artifacts"
                + File.separator + "ESB" + File.separator + "synapseconfig"
                + File.separator + "filters" + File.separator + "validate"
                + File.separator + "synapse_config.xml");
    }

    /**
     * Test Scenario: Add two schemas as local entries. Create a sequence
     * template and add a validate mediator inside it. (Here, the schema key is
     * calculated dynamically by template input parameters.) Add two proxy
     * services that call the same template using two different schema keys.
     * Send a request using "testProxy" and check whether validation happens
     * according to schema "a" Then send a request using "testProxy2" and check
     * whether validation happens according to schema "b"
     * <p/>
     * Test artifacts: /synapseconfig/filters/validate/synapse_config.xml
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb")
    public void validateMediatorDynamicSchemaChangeTest() throws Exception {
        String requestPayload1 = "<level1><a><b>222</b></a></level1>";
        String requestPayload2 = "<level1><c><d>333</d></c></level1>";

        OMElement payload1 = AXIOMUtil.stringToOM(requestPayload1);
        OMElement payload2 = AXIOMUtil.stringToOM(requestPayload2);

        OMElement response1 = axis2Client.send(getProxyServiceURLHttps("testProxy"),
                null, "mediate", payload1);
        Assert.assertTrue(response1.toString().contains("ValidateSuccess"),
                "Validate failed with schema a.");

        OMElement response2 = axis2Client.send(
                getProxyServiceURLHttps("testProxy2"), null, "mediate", payload2);
        Assert.assertTrue(response2.toString().contains("ValidateSuccess"),
                "Validate failed with schema b.");
    }

    @AfterClass(alwaysRun = true)
    public void clear() throws Exception {
        super.cleanup();
    }
}
