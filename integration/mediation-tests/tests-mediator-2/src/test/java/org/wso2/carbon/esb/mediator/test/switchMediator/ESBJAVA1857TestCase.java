package org.wso2.carbon.esb.mediator.test.switchMediator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
public class ESBJAVA1857TestCase extends ESBIntegrationTest {
	@BeforeClass(alwaysRun = true)
	public void beforeClass() throws Exception {
		super.init();
		loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/filters/switchMediator/ESBJAVA_1857_switch_case_synapse.xml");
	}

	@Test(groups = { "wso2.esb" }, description = "ESBJAVA1857 SwitchMediator:Negative Case 2: Invalid prefix")
	public void testSample2() throws RemoteException {

		try {
			 HttpResponse httpResponse = this.sendGetRequest("http://localhost:8280", null);
			 Assert.assertTrue(httpResponse.getData().contains("WSO2"));
		} catch (AxisFault e) {
			Assert.assertEquals(e.getReason(), ESBTestConstant.INCOMING_MESSAGE_IS_NULL,
			                    "Error while invoking Parameter string content Message mismatched");
		} catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Exception {
		super.cleanup();
	}
	
	
	/**
     * Sends an HTTP GET request to a url
     *
     * @param endpoint          - The URL of the server. (Example: " http://www.yahoo.com/search")
     * @param requestParameters - all the request parameters (Example: "param1=val1&param2=val2").
     *                          Note: This method will add the question mark (?) to the request - DO NOT add it yourself
     * @return - The response from the end point
     * @throws java.io.IOException If an error occurs while sending the GET request
     */
    public  HttpResponse sendGetRequest(String endpoint,
                                              String requestParameters) throws IOException {
        if (endpoint.startsWith("http://")) {
            String urlStr = endpoint;
            if (requestParameters != null && requestParameters.length() > 0) {
                urlStr += "?" + requestParameters;
            }
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(10000);
            conn.connect();

            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            return new HttpResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }
    
    
    public static void main (String [] ar) {
    	
    	ESBJAVA1857TestCase case1 = new ESBJAVA1857TestCase();
    	try {
	        case1.testSample2();
        } catch (RemoteException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    	
    }
}
