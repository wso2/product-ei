package org.wso2.carbon.esb.mediator.test.aggregate;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * This class responsible to test on complete condition of the aggregate mediator where xpath expression should return
 * exception when evaluating. For an example . as xpath expression caused to OOM. We have prevent and throw an
 * exception if child element contains SOAPEnvelop.
 */
public class AggregateOnCompleteConditionErrorTestCase extends ESBIntegrationTest {

    /**
     * Initialize test case with deploying proxy service which has aggregator mediator
     * with on complete condition set to .
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
    }

    /**
     * Verify aggregate mediator return expected exception and expected error message
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    @Test(groups = {"wso2.esb"}, description = "verify proxy service return exception evaluating the on complete",
            expectedExceptions = org.apache.axis2.AxisFault.class,
            expectedExceptionsMessageRegExp = ".*Error evaluating expression.*")
    public void testAggregatorOnCompleteConditionError() throws IOException, XMLStreamException {
        OMElement payload = AXIOMUtil.stringToOM("<soapenv:Envelope\n" +
                "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ser=\"http://services.samples\"\n" +
                "    xmlns:xsd=\"http://services.samples/xsd\">\n" +
                "    <soapenv:Header/>\n" +
                "    <soapenv:Body>\n" +
                "        <ser:getQuotes>\n" +
                "            <ser:getQuote>\n" +
                "                <ser:request>\n" +
                "                    <xsd:symbol>IBM</xsd:symbol>\n" +
                "                </ser:request>\n" +
                "            </ser:getQuote>\n" +
                "            <ser:getQuote>\n" +
                "                <ser:request>\n" +
                "                    <xsd:symbol>WSO2</xsd:symbol>\n" +
                "                </ser:request>\n" +
                "            </ser:getQuote>\n" +
                "        </ser:getQuotes>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>");
        axis2Client.send(getProxyServiceURLHttp("aggregateMediatorOnCompleteErrorTestProxy"), null,
                "urn:getQuote", payload);
    }

    /**
     * Tare down test environment
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

}
