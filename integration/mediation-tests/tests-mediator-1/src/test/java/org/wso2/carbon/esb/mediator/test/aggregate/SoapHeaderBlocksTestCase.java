package org.wso2.carbon.esb.mediator.test.aggregate;

import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SoapHeaderBlocksTestCase extends ESBIntegrationTest {

    private CarbonAppUploaderClient carbonAppUploaderClient;
    private ApplicationAdminClient applicationAdminClient;
    private final int MAX_TIME = 120000;
    private final String carFileName = "SoapHeaderTestRegFiles_1.0.0";
    private final String carFileNameWithExtension = "SoapHeaderTestRegFiles_1.0.0.car";
    private final String serviceName="TestProxy";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        carbonAppUploaderClient = new CarbonAppUploaderClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        carbonAppUploaderClient.uploadCarbonAppArtifact(carFileNameWithExtension
                , new DataHandler(new URL("file:" + File.separator + File.separator + getESBResourceLocation()
                + File.separator + "car" + File.separator + carFileNameWithExtension)));
        applicationAdminClient = new ApplicationAdminClient(context.getContextUrls().getBackEndUrl(), getSessionCookie());
        Assert.assertTrue(isCarFileDeployed(carFileName), "Car file deployment failed");
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/requestWithSoapHeaderBlockConfig/synapse.xml");
        TimeUnit.SECONDS.sleep(5);
    }

    @Test(groups = {"wso2.esb"})
    public void aggregateMediatorSoapHeaderBlockTestCase() throws Exception {

        HttpsResponse response = postWithBasicAuth(getProxyServiceURLHttps(serviceName),
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                        + "xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<s:Header>"
                        + "<VsDebugger "
                        + "xmlns=\"http://schemas.microsoft.com/vstudio/diagnostics/servicemodelsink\">"
                        + "uIDPo0Mttttvvvvvvv</VsDebugger>"
                        + "</s:Header>"
                        + "<s:Body xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                        + "<sendLetter xmlns=\"http://ws.cts.deg.gov.ae/\">" + "<letter xmlns=\"\">"
                        + "<body/>" + "<confidentiality>Public</confidentiality>"
                        + "<date>201d6-02-29T15:22:14.88dd7</date>" + "<from>"
                        + "<code>ADdddSG</code>" + "<id>AAdd7</id>" + "</from>"
                        + "<importance>Normal</importance>"
                        + "<outgoingRef>DSssssG/ddddOUT/2016TEST/0uy0099</outgoingRef>"
                        + "<priority>Normal</priority>" + "<replyTo>218602</replyTo>"
                        + "<signedCopy>" + "<filename>Test.pdf</filename>"
                        + "<format>pdf</format>" + "</signedCopy>"
                        + "<subject>Test 1</subject>" + "<to>"
                        + "<code>DM</code>" + "<id>eeeeeeeeeeeee@dd.com</id>"
                        + "</to>" + "</letter>" + "</sendLetter>" + "</s:Body>"
                        + "</s:Envelope>", "text/xml;charset=UTF-8", "admin", "admin");
        Assert.assertEquals(response.getResponseCode(), 200);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }

    private boolean isCarFileDeployed(String carFileName) throws Exception {

        log.info("waiting " + MAX_TIME + " millis for car deployment " + carFileName);
        boolean isCarFileDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < MAX_TIME) {
            String[] applicationList = applicationAdminClient.listAllApplications();
            if (applicationList != null) {
                if (ArrayUtils.contains(applicationList, carFileName)) {
                    isCarFileDeployed = true;
                    log.info("car file deployed in " + time + " mills");
                    return isCarFileDeployed;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
        return isCarFileDeployed;
    }
    public HttpsResponse postWithBasicAuth(String uri, String requestQuery, String contentType, String userName,
            String password) throws IOException {
        if (uri.startsWith("https://")) {
            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            String encode = new String(new org.apache.commons.codec.binary.Base64()
                    .encode((userName + ":" + password).getBytes(Charset.defaultCharset())), Charset.defaultCharset())
                    .replaceAll("\n", "");
            conn.setRequestProperty("Authorization", "Basic " + encode);
            conn.setDoOutput(true); // Triggers POST.
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("SOAPAction", "http://test/sendLetterRequest");
            conn.setRequestProperty("Content-Length",
                    "" + Integer.toString(requestQuery.getBytes(Charset.defaultCharset()).length));
            conn.setUseCaches(false);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(requestQuery);
            conn.setReadTimeout(3000);
            conn.connect();
            System.out.println(conn.getRequestMethod());
            // Get the response
            boolean responseRecieved = false;
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    responseRecieved = true;
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                if(!responseRecieved){
                    return new HttpsResponse(sb.toString(), 500);
                }
                wr.flush();
                wr.close();
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }
}
