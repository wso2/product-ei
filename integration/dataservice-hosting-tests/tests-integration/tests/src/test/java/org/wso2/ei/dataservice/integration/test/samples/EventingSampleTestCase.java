/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.ei.dataservice.integration.test.samples;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.dataservices.samples.eventing_sample.DataServiceFault;
import org.wso2.carbon.dataservices.samples.eventing_sample.EventingSampleStub;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Iterator;

import static org.testng.Assert.assertTrue;

public abstract class EventingSampleTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(GSpreadSampleTestCase.class);

    private final String serviceName = "EventingSample";
    private final String GMAIL_USER_NAME = "test.automation.dummy";
    private final String GMAIL_PASSWORD = "automation.test";
    private String serverEpr;
    private String productCode;
    private EventingSampleStub eventingSampleStub;
    private int mailCountBeforeTestStart = 0;
    private String modifiedTime;
    private String feedURL;


    @Factory(dataProvider = "userModeDataProvider")
    public EventingSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true, enabled = false)
    public void initialize() throws Exception {
        super.init(userMode);
        String resourceFileLocation;
        feedURL = "https://mail.google.com/mail/feed/atom";
        mailCountBeforeTestStart = getMailCount(feedURL);
        modifiedTime = getModifiedTime(feedURL);
        eventingSampleStub = new EventingSampleStub(serverEpr);
        serverEpr = getServiceUrlHttp(serviceName);

//        productCode = "code" + System.currentTimeMillis();
        productCode = "999";

        updateAxis2_ClientXML();
        new ServerConfigurationManager("DSS", TestUserMode.SUPER_TENANT_ADMIN).restartGracefully();

        super.init();
        resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "rdbms" + File.separator +
                                              "EventingSample.dbs")));
        log.info(serviceName + " uploaded");
    }

    @AfterClass(alwaysRun = true,  enabled = false)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not",  enabled = false)
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = "wso2.dss", description = "Add new product", dependsOnMethods = "testServiceDeployment",
            enabled = false)
    public void testAddProduct() throws RemoteException, DataServiceFault {
//        eventingSampleStub.addProduct(productCode, "Line1", "BVK-Name", 100, 123.00);
//        getProductByCode();
        new AxisServiceClient().sendRobust(getAddPayload(), serverEpr, "addProduct");
        OMElement result = new AxisServiceClient().sendReceive(getProductByCodePayload(), serverEpr,
                                                               "getProductByCode");
        assertTrue(result.toString().contains("<productCode>999</productCode>"));

    }


    @Test(groups = "wso2.dss", description = "update product quantity",
          dependsOnMethods = "testAddProduct",  enabled = false)
    public void testUpdateQuantity() throws RemoteException, DataServiceFault {
//        eventingSampleStub.updateProductQuantity(productCode, 1);
//        getProductByCode();
        new AxisServiceClient().sendRobust(updateProductPayload(), serverEpr, "updateProductQuantity");
        OMElement result = new AxisServiceClient().sendReceive(getProductByCodePayload(), serverEpr,
                                                               "getProductByCode");
        assertTrue(result.toString().contains("<productCode>999</productCode>"));
        assertTrue(result.toString().contains("<quantityInStock>1</quantityInStock>"));

    }

   /* @Test(groups = "wso2.dss", description = "update product quantity",
          dependsOnMethods = "testUpdateQuantity")
    public void testMailFeeds()
            throws IOException, DataServiceFault, XMLStreamException, InterruptedException {
        assertTrue(waitForMailArrival(), "Mail not received to the account");
    }

    private void getProductByCode() throws RemoteException, DataServiceFault {

        Product[] products = eventingSampleStub.getProductByCode(productCode);
        boolean status = false;
        for (Product product : products) {
            if (product.getProductName().equals("BVK-Name")) {
                status = true;
            }
        }
        assertTrue(status, "product has been int");
    }*/

    private OMElement getAtomFeedContent(String atomURL) throws IOException,
                                                                       XMLStreamException {
        StringBuilder sb;
        InputStream inputStream = null;
        URL url = new URL(atomURL);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            String userPassword = GMAIL_USER_NAME + ":" + GMAIL_PASSWORD;
            String encodedAuthorization = Base64Utils.encode(userPassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic " +
                                                           encodedAuthorization);
            connection.connect();

            inputStream = connection.getInputStream();
            sb = new StringBuilder();
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            assert inputStream != null;
            inputStream.close();
        }

        return AXIOMUtil.stringToOM(sb.toString());

    }

    private boolean waitForMailArrival()
            throws XMLStreamException, IOException, InterruptedException {
        long waitTime = 30 * 1000;
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < waitTime) {
            if ((((getMailCount(feedURL) - mailCountBeforeTestStart) >= 1)) || !(getModifiedTime(feedURL).equals(modifiedTime))) {
                return true;
            }
            Thread.sleep(5000);
        }
        return false;
    }

    private int getMailCount(String feedURL) throws XMLStreamException, IOException {
        OMElement mailFeed = getAtomFeedContent(feedURL);
        Iterator itr = mailFeed.getChildrenWithName(new QName("fullcount"));
        int count = 0;
        if (itr.hasNext()) {
            OMElement countOm = (OMElement) itr.next();
            return Integer.parseInt(countOm.getText());
        }
        return count;
    }

    private String getModifiedTime(String feedURL) throws XMLStreamException, IOException {
        OMElement mailFeed = getAtomFeedContent(feedURL);
        Iterator itr = mailFeed.getChildrenWithName(new QName("entry"));
        if (itr.hasNext()) {
            OMElement countOm = (OMElement) itr.next();
            for (Iterator itrTitle = countOm.getChildrenWithName(new QName("title")); itrTitle.hasNext(); ) {
                OMElement title = (OMElement) itrTitle.next();
                if (title.getText().equals("SOAPAction: http://ws.apache.org/ws/2007/05/eventing-extended/Publish")) {
                    OMElement modified = (OMElement) countOm.getChildrenWithName(new QName("modified")).next();
                    return modified.getText();
                }

            }
        }
        return null;
    }

    private OMElement getAddPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/eventing_sample", "even");

        OMElement productCodeId = fac.createOMElement("productCode", omNs);
        OMElement productLine = fac.createOMElement("productLine", omNs);
        OMElement productName = fac.createOMElement("productName", omNs);
        OMElement quantityInStock = fac.createOMElement("quantityInStock", omNs);
        OMElement buyPrice = fac.createOMElement("buyPrice", omNs);

        productCodeId.setText(productCode);
        productLine.setText("Line1");
        productName.setText("TestProduct");
        quantityInStock.setText("100");
        buyPrice.setText("100.00");

        OMElement addProduct = fac.createOMElement("addProduct", omNs);
        addProduct.addChild(productCodeId);
        addProduct.addChild(productLine);
        addProduct.addChild(productName);
        addProduct.addChild(quantityInStock);
        addProduct.addChild(buyPrice);

        return addProduct;
    }

    private OMElement getProductByCodePayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/eventing_sample", "even");
        OMElement productCodeId = fac.createOMElement("productCode", omNs);
        productCodeId.setText(productCode);

        OMElement getProduct = fac.createOMElement("getProductByCode", omNs);
        getProduct.addChild(productCodeId);
        return getProduct;
    }

    private OMElement updateProductPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/eventing_sample", "even");
        OMElement productCodeId = fac.createOMElement("productCode", omNs);
        OMElement quantityInStock = fac.createOMElement("quantityInStock", omNs);
        productCodeId.setText(productCode);
        quantityInStock.setText("1");

        OMElement updateProduct = fac.createOMElement("updateProductQuantity", omNs);
        updateProduct.addChild(productCodeId);
        updateProduct.addChild(quantityInStock);
        return updateProduct;
    }

    private void updateAxis2_ClientXML() throws Exception {
        String axis2_client_path = CarbonUtils.getCarbonHome() + File.separator + "conf" + File.separator + "axis2" + File.separator + "axis2_client.xml";

        String mail_transport_config = getResourceLocation()+ File.separator + "resources" + File.separator + "mailTransport.xml";


        FileOutputStream fileOutputStream = null;
        XMLStreamWriter writer = null;

        try {
            OMElement axis2_client_xml = AXIOMUtil.stringToOM(FileManager.readFile(axis2_client_path));

            axis2_client_xml.addChild(AXIOMUtil.stringToOM(FileManager.readFile(mail_transport_config)));
            axis2_client_xml.build();
            fileOutputStream = new FileOutputStream(axis2_client_path);
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fileOutputStream);
            axis2_client_xml.serialize(writer);

        } catch (Exception e) {
            throw new Exception("axis2_client.xml update fails");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (writer != null) {
                writer.flush();
            }
        }
    }
}
