package org.wso2.carbon.esb.mediators.cache;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.http.impl.client.DefaultHttpClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.HashMap;
import java.util.Map;

/**
 * This test can be used make sure cache mediator can cache payloads with Processing instructions (PIs).
 */
public class ESBJAVA4318Testcase extends ESBIntegrationTest {

    private final DefaultHttpClient httpClient = new DefaultHttpClient();
    private final Map<String, String> headers = new HashMap<String, String>(1);

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();

        String proxy = "<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n"
                + "       name=\"PF\"\n"
                + "       transports=\"https http\"\n" + "       startOnLoad=\"true\"\n" + "       trace=\"disable\">\n"
                + "    <description/>\n" + "    <target>\n" + "        <inSequence>\n"
                + "            <cache scope=\"per-host\"\n" + "                   collector=\"false\"\n"
                + "                   hashGenerator=\"org.wso2.carbon.mediator.cache.digest.DOMHASHGenerator\"\n"
                + "                   timeout=\"60\">\n" + "                <onCacheHit>\n"
                + "                    <log level=\"full\"/>\n"
                + "                    <property name=\"messageType\" value=\"application/xml\" scope=\"axis2\"/>\n"
                + "                    <respond/>\n" + "                </onCacheHit>\n"
                + "                <implementation type=\"memory\" maxSize=\"100\"/>\n" + "            </cache>\n"
                + "            <send>\n" + "                <endpoint>\n"
                + "                    <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n"
                + "                </endpoint>\n" + "            </send>\n" + "        </inSequence>\n"
                + "        <outSequence>\n" + "            <payloadFactory media-type=\"xml\">\n"
                + "                <format>\n" + "                    <b>\n"
                + "                        <?xml-multiple array?>\n" + "                        <xyz>\n"
                + "                            <a xmlns=\"\">after cache</a>\n" + "                        </xyz>\n"
                + "                    </b>\n" + "                </format>\n" + "                <args/>\n"
                + "            </payloadFactory>\n" + "            <cache scope=\"per-host\" collector=\"true\"/>\n"
                + "            <property name=\"messageType\" value=\"application/xml\" scope=\"axis2\"/>\n"
                + "            <send>\n" + "                <endpoint>\n" + "                    <default/>\n"
                + "                </endpoint>\n" + "            </send>\n" + "        </outSequence>\n"
                + "    </target>\n" + "</proxy>";

        OMElement omProxy = AXIOMUtil.stringToOM(proxy);
        addProxyService(omProxy);
        isProxyDeployed("PF");
    }
    @Test(groups = "wso2.esb", description = "cache meditor with payloads including Processing Insturctions")
    public void testCacheMediatorWithPIs() throws Exception {

        String requestXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">\n"
                + "   <soapenv:Header>\n"
                + "      <m:Trans xmlns:m=\"http://www.w3schools.com/transaction/\">234</m:Trans>\n"
                + "   </soapenv:Header>\n" + "   <soapenv:Body>\n" + "      <ser:getFullQuote>\n"
                + "         <ser:request>\n" + "            <xsd:symbol>IBM</xsd:symbol>\n"
                + "         </ser:request>\n" + "      </ser:getFullQuote>\n" + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        final String expectedValue = "<b xmlns=\"http://ws.apache.org/ns/synapse\"><?xml-multiple  array?><xyz><a xmlns=\"\">after cache</a></xyz></b>";

        DefaultHttpClient httpclient = new DefaultHttpClient();

        // this is to populate the cache mediator
        HttpPost httpPost = new HttpPost(getProxyServiceURLHttp("PF"));
        httpPost.addHeader("SOAPAction", "urn:getFullQuote");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8");
        HttpEntity stringEntity = new StringEntity(requestXml, CharEncoding.UTF_8);
        httpPost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httpPost);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        response.getEntity().writeTo(baos);
        String actualValue = baos.toString();

        // this is to get the actual cache response
        HttpPost httpPost2 = new HttpPost(getProxyServiceURLHttp("PF"));
        httpPost2.addHeader("SOAPAction", "urn:getFullQuote");
        httpPost2.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8");
        HttpEntity stringEntity2 = new StringEntity(requestXml, CharEncoding.UTF_8);
        httpPost2.setEntity(stringEntity2);
        HttpResponse response2 = httpclient.execute(httpPost);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        response2.getEntity().writeTo(baos2);
        String actualValue2 = baos.toString();
        Assert.assertEquals(actualValue2, expectedValue);
    }
}