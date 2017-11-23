package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpClientUtil;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

/**
 * This test is to verify the fix for https://wso2.org/jira/browse/DS-721
 */
public class ResourcesServiceTestWithSameContextNameTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(ResourcesServiceTestWithSameContextNameTestCase.class);
    private final String serviceName = "ResourcesServiceTestWithSameContextName";
    private String serviceEndPoint;
    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        serviceEndPoint = getServiceUrlHttp(serviceName);
        Assert.assertTrue(isServiceDeployed(serviceName), "Data service not deployed");
    }
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        cleanup();
    }
    private void getProduct(String id) throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint +  ".HTTPEndpoint/"+"product"+"/"+id);
        Assert.assertNotNull(result, "Response null");
        System.out.println("Result "+result.toString());
        Assert.assertTrue(result.toString().contains("<productName>product" + id + "</productName>"), "Expected result not found");


    }
    private void getProducts() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint +".HTTPEndpoint/"+ "product");
        Assert.assertNotNull(result, "Response null");
        for (int i = 1; i < 6; i++) {
            Assert.assertTrue(result.toString().contains("<productName>product" + i + "</productName>"), "Expected result not found");
        }


    }
    private void addProduct()throws  Exception{
        HttpClientUtil httpClient = new HttpClientUtil();
        for (int i = 1; i < 6; i++) {

            String para = "productCode=" + i
                    + "&" + "productName=" + "product" + i
                    + "&" + "productLine=2"
                    + "&" + "quantityInStock=200"
                    + "&" + "buyPrice=10";
            httpClient.post(serviceEndPoint +".HTTPEndpoint/"+ "product", para);

        }
    }
    @Test(groups = {"wso2.dss"}, enabled = false)
    public void addRequest() throws Exception {
    addProduct();
    log.info("Verified POST successfully");
    }

    @Test(groups = {"wso2.dss"},dependsOnMethods = {"addRequest"}, enabled = false)
    public void getRequest() throws Exception {
         getProduct("1");
        log.info("Verified GET /product/{id} successfully");

    }
    @Test(groups = {"wso2.dss"},dependsOnMethods = {"addRequest"}, enabled = false)
    public void getAllRequest() throws Exception {
        getProducts();
        log.info("Verified GET /product successfully");
    }
}
