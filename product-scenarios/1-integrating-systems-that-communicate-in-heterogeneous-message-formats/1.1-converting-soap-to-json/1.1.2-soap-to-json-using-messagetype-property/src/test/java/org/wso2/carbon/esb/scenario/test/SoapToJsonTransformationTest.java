package org.wso2.carbon.esb.scenario.test;

import flexjson.JSONSerializer;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import static org.wso2.esb.integration.common.utils.common.FileManager.readFile;

public class SoapToJsonTransformationTest extends ScenarioTestBase {

    private final String carFileName = "soap_to_json_using_messagetype_propertyCompositeApplication_1.0.0";

    private String proxyServiceUrl;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        proxyServiceUrl = infraProperties.getProperty(ESB_HTTP_URL) + "/" + "ToJSON";

        deployCarbonApplication(carFileName);
    }

    @Test(description = "1.1.2.1", enabled = true, dataProvider = "1.1.2.1")
    public void convertValidSoapToJson(String request, String expectedResponse) throws Exception {
        SimpleHttpClient httpClient=new SimpleHttpClient();

        HttpResponse httpResponse = httpClient.doPost(proxyServiceUrl, null, request, "application/xml");
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        JSONObject jsonExpectedResponse = new JSONObject(expectedResponse);
        JSONObject jsonActualResponse = new JSONObject(responsePayload);

        String expectedString = jsonExpectedResponse.toString();
        String actualString = jsonActualResponse.toString();

        Assert.assertEquals(serialize(expectedString),serialize(actualString) );

    }

    @AfterClass(description = "Server Cleanup")
    public void cleanup() throws Exception {

    }

    @DataProvider(name = "1.1.2.1")
    public Iterator<Object[]> soapToJson_1_1_2_1() throws Exception{

        File requestFolder = new File(getClass().getResource(File.separator + "source_files" + File.separator + "1.1.2.1" + File.separator + "request").getPath());
        File responseFolder = new File(getClass().getResource(File.separator + "source_files" + File.separator + "1.1.2.1" + File.separator + "response").getPath());

        List<String> requestFiles = getListOfFiles(requestFolder);
        List<String> responseFiles = getListOfFiles(responseFolder);

        java.util.Collections.sort(requestFiles, Collator.getInstance());
        java.util.Collections.sort(responseFiles, Collator.getInstance());

        ArrayList<String> requestArray = new ArrayList();
        ArrayList<String> responseArray = new ArrayList();


        for (String file : requestFiles) {
            File fileLocation = new File(getClass().getResource(File.separator + "source_files" + File.separator + "1.1.2.1" + File.separator + "request" + File.separator + file).getPath());
            String fileContent = getXmlContent(fileLocation);
            requestArray.add(fileContent);
        }

        for (String file : responseFiles) {
            String fileContent = readFile( getClass().getResource(File.separator + "source_files" + File.separator + "1.1.2.1" + File.separator + "response" + File.separator + file).getPath());
            responseArray.add(fileContent);
        }

        List<Object[]> requestResponseList = new ArrayList<>();

        for (int i = 0; i < requestArray.size(); i++) {
            String[] tmp = { requestArray.get(i) , responseArray.get(i) };
            requestResponseList.add(tmp);
        }
        return requestResponseList.iterator();
    }

    private String serialize(String content) {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.serialize(content);
    }

}
