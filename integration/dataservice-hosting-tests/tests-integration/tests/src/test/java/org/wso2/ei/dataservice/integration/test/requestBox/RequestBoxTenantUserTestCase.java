/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ei.dataservice.integration.test.requestBox;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test class to test the functionality of request box in Tenant Mode.
 *
 * Send multiple request.
 * Export param from one request to another.
 * Return results at the end of the request.
 * Test for transactional manner.
 */
public class RequestBoxTenantUserTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(RequestBoxTenantUserTestCase.class);

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");
    private final String serviceName = "RequestBoxTenantTest";

    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init(TestUserMode.TENANT_USER);
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("RequestBoxTestTables.sql"));
        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "h2" + File.separator
                                     + "RequestBoxTenantTest.dbs", sqlFileLis));
        serviceEndPoint = getServiceUrlHttp(serviceName);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

    /**
     * Method to test operations which needs to be successful.
     *
     * Id range starts from 1.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.dss"}, description = "Send Request box requests which should be successful and check whether they are successful", alwaysRun = true)
    public void requestBoxTenantModeSuccessRequests() throws Exception {
        // **************** test insert to two tables ******************
        OMElement payloadInsertOnly = fac.createOMElement("request_box", omNs);

        payloadInsertOnly.addChild(generateInsertProducts(1));
        payloadInsertOnly.addChild(generateInsertOrders(1));

        new AxisServiceClient().sendRobust(payloadInsertOnly, getServiceUrlHttp(serviceName), "request_box");

        //retrieve and see whether inserted correctly
        OMElement responseProduct = new AxisServiceClient().sendReceive(generateSelectProductByCodeElement(1), getServiceUrlHttp(serviceName), "select_product_by_code_operation");
        assertNotNull(responseProduct, "Response null " + responseProduct);
        assertTrue(responseProduct.toString().contains("<productName>productName1</productName>"), "'productName1' should have exist in the response");

        OMElement responseOrder = new AxisServiceClient().sendReceive(generateSelectOrderByNumberElement(1), getServiceUrlHttp(serviceName), "select_order_by_number_operation");
        assertNotNull(responseOrder, "Response null " + responseOrder);
        assertTrue(responseOrder.toString().contains("<productName>productName1</productName>"), "'productName1' should have exist in the response");

        // **************** test insert and select operation ******************
        OMElement payloadInsertAndSelect = fac.createOMElement("request_box", omNs);

        payloadInsertAndSelect.addChild(generateInsertProducts(2));
        payloadInsertAndSelect.addChild(generateInsertOrders(2));
        payloadInsertAndSelect.addChild(generateSelectProductByCodeElement(1));

        OMElement responseInsertAndSelect = new AxisServiceClient().sendReceive(payloadInsertAndSelect, getServiceUrlHttp(serviceName), "request_box");
        assertNotNull(responseInsertAndSelect, "Response null " + responseInsertAndSelect);
        assertTrue(responseInsertAndSelect.toString().contains("<productName>productName1</productName>"), "'productName1' should have exist in the response");

        //retrieve and see whether inserted correctly
        responseProduct = new AxisServiceClient().sendReceive(generateSelectProductByCodeElement(2), getServiceUrlHttp(serviceName), "select_product_by_code_operation");
        assertNotNull(responseProduct, "Response null " + responseProduct);
        assertTrue(responseProduct.toString().contains("<productName>productName2</productName>"), "'productName2' should have exist in the response");

        responseOrder = new AxisServiceClient().sendReceive(generateSelectOrderByNumberElement(2), getServiceUrlHttp(serviceName), "select_order_by_number_operation");
        assertNotNull(responseOrder, "Response null " + responseOrder);
        assertTrue(responseOrder.toString().contains("<productName>productName2</productName>"), "'productName2' should have exist in the response");

        // **************** test select and insert operation ******************
        OMElement payloadSelectAndInsert = fac.createOMElement("request_box", omNs);

        payloadSelectAndInsert.addChild(generateSelectProductByCodeElement(1));
        payloadSelectAndInsert.addChild(generateInsertProducts(3));
        payloadSelectAndInsert.addChild(generateInsertOrders(3));

        OMElement responseSelectAndInsert = new AxisServiceClient().sendReceive(payloadSelectAndInsert, getServiceUrlHttp(serviceName), "request_box");
        assertNotNull(responseSelectAndInsert, "Response null " + responseSelectAndInsert);
        assertTrue(!responseSelectAndInsert.toString().contains("product"), "response shouldn't contain any result" );

        //retrieve and see whether inserted correctly
        responseProduct = new AxisServiceClient().sendReceive(generateSelectProductByCodeElement(3), getServiceUrlHttp(serviceName), "select_product_by_code_operation");
        assertNotNull(responseProduct, "Response null " + responseProduct);
        assertTrue(responseProduct.toString().contains("<productName>productName3</productName>"), "'productName3' should have exist in the response");

        responseOrder = new AxisServiceClient().sendReceive(generateSelectOrderByNumberElement(3), getServiceUrlHttp(serviceName), "select_order_by_number_operation");
        assertNotNull(responseOrder, "Response null " + responseOrder);
        assertTrue(responseOrder.toString().contains("<productName>productName3</productName>"), "'productName3' should have exist in the response");

        // **************** test selectExport insert select operation ******************
        OMElement payloadSelectExportInsertSelect = fac.createOMElement("request_box", omNs);

        payloadSelectExportInsertSelect.addChild(generateSelectProductByCodeAndExportElement(1));
        payloadSelectExportInsertSelect.addChild(generateInsertToOrdersWithImportElement(4));
        payloadSelectExportInsertSelect.addChild(generateSelectOrderByNumberElement(4));

        OMElement responseSelectExportInsertSelect = new AxisServiceClient().sendReceive(payloadSelectExportInsertSelect, getServiceUrlHttp(serviceName), "request_box");
        assertNotNull(responseSelectExportInsertSelect, "Response null " + responseSelectExportInsertSelect);
        assertTrue(responseSelectExportInsertSelect.toString().contains("<productName>productName1</productName>"), "'productName1' should have exist in the response");

        //retrieve and see whether inserted correctly
        responseOrder = new AxisServiceClient().sendReceive(generateSelectOrderByNumberElement(4), getServiceUrlHttp(serviceName), "select_order_by_number_operation");
        assertNotNull(responseOrder, "Response null " + responseOrder);
        assertTrue(responseOrder.toString().contains("<productName>productName1</productName>"), "'productName1' should have exist in the response");
    }

    /**
     * Test operations which needs to be failed.
     *
     * Id range starts from 100.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.dss"}, description = "Send Request box requests which should be failed and check whether they are really failed", alwaysRun = true)
    public void requestBoxTenantModeFailRequests() throws Exception {
        // **************** test insert to two tables(table1 entry already exist) ******************
        new AxisServiceClient().sendRobust(generateInsertProducts(100), getServiceUrlHttp(serviceName), "insert_into_products_operation"); //first insert the value to table1 - productCode100
        OMElement payloadInsertOnly = fac.createOMElement("request_box", omNs);

        payloadInsertOnly.addChild(generateInsertProducts(100));
        payloadInsertOnly.addChild(generateInsertOrders(100));

        try {
            new AxisServiceClient().sendRobust(payloadInsertOnly, getServiceUrlHttp(serviceName), "request_box");
            assertTrue(false, "Insert operation should have failed as 'productCode100' should already exist in the table");
        } catch (Exception e) {
            assertTrue(true);
        }

        //retrieve and see whether order inserted (if inserted that's wrong)
        OMElement responseOrder = new AxisServiceClient().sendReceive(generateSelectOrderByNumberElement(100), getServiceUrlHttp(serviceName), "select_order_by_number_operation");
        assertNotNull(responseOrder, "Response null " + responseOrder);
        assertTrue(!responseOrder.toString().contains("<productName>productName100</productName>"), "'productName100' shouldn't have exist in the response");




        // **************** test insert to two tables(table2 entry already exist) ******************
        //order changed so second table entry should already be there
        payloadInsertOnly.addChild(generateInsertOrders(100));
        payloadInsertOnly.addChild(generateInsertProducts(100));

        try {
            new AxisServiceClient().sendRobust(payloadInsertOnly, getServiceUrlHttp(serviceName), "request_box");
            assertTrue(false, "Insert operation should have failed as 'productCode100' should already exist in the table");
        } catch (Exception e) {
            assertTrue(true);
        }

        //retrieve and see whether order inserted (if inserted that's wrong)
        responseOrder = new AxisServiceClient().sendReceive(generateSelectOrderByNumberElement(100), getServiceUrlHttp(serviceName), "select_order_by_number_operation");
        assertNotNull(responseOrder, "Response null " + responseOrder);
        assertTrue(!responseOrder.toString().contains("<productName>productName100</productName>"), "'productName100' shouldn't have exist in the response");
    }



    /**
     * Helper method to generate insertProduct operation Request OME element.
     *
     * @param id
     * @return
     */
    private OMElement generateInsertProducts(int id) {
        OMElement insertProductOpEl = fac.createOMElement("insert_into_products_operation", omNs);

        OMElement productCodeEl = fac.createOMElement("productCode", omNs);
        productCodeEl.setText("productCode" + id);
        insertProductOpEl.addChild(productCodeEl);

        OMElement productNameEl = fac.createOMElement("productName", omNs);
        productNameEl.setText("productName" + id);
        insertProductOpEl.addChild(productNameEl);

        OMElement productLineEl = fac.createOMElement("productLine", omNs);
        productLineEl.setText("productLine" + id);
        insertProductOpEl.addChild(productLineEl);

        OMElement productDescriptionEl = fac.createOMElement("productDescription", omNs);
        productDescriptionEl.setText("productDescription" + id);
        insertProductOpEl.addChild(productDescriptionEl);

        OMElement quantityInStockEl = fac.createOMElement("quantityInStock", omNs);
        quantityInStockEl.setText((((id * 3) + 13) * 5) + 15 + "");
        insertProductOpEl.addChild(quantityInStockEl);

        return insertProductOpEl;
    }

    /**
     * Helper method to generate SelectProductByCodeAndExport operation Request OME element.
     *
     * @param id
     * @return
     */
    private OMElement generateSelectProductByCodeAndExportElement(int id) {
        return generateSelectProductByCodeElement("select_product_by_code_nExport_operation", id);
    }


    /**
     * Helper method to generate InsertToOrdersWithImport operation Request OME element.
     *
     * @param id
     * @return
     */
    private OMElement generateInsertToOrdersWithImportElement(int id) {
        OMElement insertToOrderWithImportEl = fac.createOMElement("insert_into_orders_with_imports_operation", omNs);

        OMElement orderNumberEl = fac.createOMElement("orderNumber", omNs);
        orderNumberEl.setText("" + id);
        insertToOrderWithImportEl.addChild(orderNumberEl);

        OMElement commentsEl = fac.createOMElement("comments", omNs);
        commentsEl.setText("comments" + id);
        insertToOrderWithImportEl.addChild(commentsEl);

        OMElement quantityInStockEl = fac.createOMElement("quantityInStock", omNs);
        quantityInStockEl.setText("" + (id * 3) + 12);
        insertToOrderWithImportEl.addChild(quantityInStockEl);

        return insertToOrderWithImportEl;
    }

    /**
     * Helper method to generate insertOrders operation Request OME element.
     *
     * @param id
     * @return
     */
    private OMElement generateInsertOrders(int id) {
        OMElement insertOrderOpEl = fac.createOMElement("insert_into_orders_operation", omNs);

        OMElement orderNumberEl = fac.createOMElement("orderNumber", omNs);
        orderNumberEl.setText("" + id);
        insertOrderOpEl.addChild(orderNumberEl);

        OMElement productCodeEl = fac.createOMElement("productCode", omNs);
        productCodeEl.setText("productCode" + id);
        insertOrderOpEl.addChild(productCodeEl);

        OMElement productNameEl = fac.createOMElement("productName", omNs);
        productNameEl.setText("productName" + id);
        insertOrderOpEl.addChild(productNameEl);

        OMElement productLineEl = fac.createOMElement("productLine", omNs);
        productLineEl.setText("productLine" + id);
        insertOrderOpEl.addChild(productLineEl);

        OMElement commentsEl = fac.createOMElement("comments", omNs);
        commentsEl.setText("comments" + id);
        insertOrderOpEl.addChild(commentsEl);

        return insertOrderOpEl;
    }


    /**
     * Helper method to generate SelectProductByCode and return response operation Request OME element.
     *
     * @param id
     * @return
     */
    private OMElement generateSelectProductByCodeElement(int id) {
        return generateSelectProductByCodeElement("select_product_by_code_operation", id);
    }

    /**
     * Helper method to generate SelectProductByCode operation Request OME element.
     *
     * @param opName
     * @param id
     * @return
     */
    private OMElement generateSelectProductByCodeElement(String opName, int id) {
        OMElement selectProductByCodeOpEl = fac.createOMElement(opName, omNs);

        OMElement productCodeEl = fac.createOMElement("productCode", omNs);
        productCodeEl.setText("productCode" + id);
        selectProductByCodeOpEl.addChild(productCodeEl);

        return selectProductByCodeOpEl;
    }

    /**
     * Helper method to generate SelectOrderByNumber operation Request OME element.
     *
     * @param id
     * @return
     */
    private OMElement generateSelectOrderByNumberElement(int id) {
        OMElement selectOrderByNumberOpEl = fac.createOMElement("select_order_by_number_operation", omNs);

        OMElement orderNumberEl = fac.createOMElement("orderNumber", omNs);
        orderNumberEl.setText("" + id);
        selectOrderByNumberOpEl.addChild(orderNumberEl);

        return selectOrderByNumberOpEl;
    }
}
