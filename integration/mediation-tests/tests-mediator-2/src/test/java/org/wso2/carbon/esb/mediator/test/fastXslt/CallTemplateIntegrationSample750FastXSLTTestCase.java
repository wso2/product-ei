package org.wso2.carbon.esb.mediator.test.fastXslt;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CallTemplateIntegrationSample750FastXSLTTestCase extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception, IOException {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fast_xslt/calltemplate_integration_fastxslt_sample750_synapse.xml");
    }

    @Test(groups = {"wso2.esb"}, description = "Stereotyping Fast XSLT Transformations with Templates :Test using sample 750")
    public void testFastXSLTTransformationWithTemplates() throws IOException, XMLStreamException {
        OMElement response=axis2Client.sendCustomQuoteRequest(getProxyServiceURLHttp("StockQuoteProxy")
                ,null,"WSO2");
        assertNotNull(response,"Response message is null");
        assertEquals(response.getLocalName(),"CheckPriceResponse","CheckPriceResponse not match");
        assertTrue(response.toString().contains("Price"),"No price tag in response");
        assertTrue(response.toString().contains("Code"),"No code tag in response");
        assertEquals(response.getFirstChildWithName
                (new QName("http://services.samples/xsd","Code")).getText(),"WSO2","Symbol not matched");

    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        super.cleanup();
    }
}
