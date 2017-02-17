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

package org.wso2.ei.dataservice.integration.test.services;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceFileUploaderClient;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;

import static org.testng.Assert.assertTrue;


public class DataServiceSqlDriverTestCase extends DSSIntegrationTest {

    private String serviceName = "sqlparsertest";
    private DSSTestCaseUtils dssTestCaseUtils;
    private String resourceFileLocation;
    private String serviceEPR;
    private long randomId;

    private static final Log log = LogFactory.getLog(DataServiceSqlDriverTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        resourceFileLocation = getResourceLocation();
        randomId = System.currentTimeMillis();
        dssTestCaseUtils = new DSSTestCaseUtils();
        DataServiceFileUploaderClient dataServiceAdminClient =
                new DataServiceFileUploaderClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        DataHandler dataHandler = modifyExcelURL(resourceFileLocation + File.separator + "dbs" + File.separator +
                                                 "sqldriver" + File.separator + "sqlparsertest.dbs");

        dataServiceAdminClient.uploadDataServiceFile("sqlparsertest.dbs", dataHandler);
        log.info(serviceName + " uploaded");
        serviceEPR = getServiceUrlHttp(serviceName);
    }

    @Test(groups = "wso2.dss", description = "Check whether the service is deployed or not", enabled = false)
    public void testServiceDeployment() throws RemoteException, XPathExpressionException {
        assertTrue(dssTestCaseUtils.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(),
                                                      sessionCookie, serviceName));
        log.info(serviceName + " is deployed");
    }



    @Test(groups = "wso2.dss", description = "insert and retrieve records", dependsOnMethods = "testServiceDeployment", enabled = false)
    public void testInsertRecordsAndGetBack() throws RemoteException {

        OMElement result = new AxisServiceClient().sendReceive(insertNewRecord(String.valueOf(randomId)), serviceEPR, "insertop");
        assertTrue(result.toString().contains("SUCCESSFUL"));

        OMElement resultDetails = new AxisServiceClient().sendReceive(getDetails(), serviceEPR, "Getdetails");
        assertTrue(resultDetails.toString().contains(String.valueOf(randomId)));
    }

    @Test(groups = "wso2.dss", description = "update and delete record", dependsOnMethods = "testInsertRecordsAndGetBack", enabled = false)
    public void testUpdateAndDelete() throws RemoteException {

        OMElement result = new AxisServiceClient().sendReceive(updateRecord(String.valueOf(randomId)), serviceEPR, "Update");
        assertTrue(result.toString().contains("SUCCESSFUL"));

        OMElement resultDetails = new AxisServiceClient().sendReceive(deleteRecord(String.valueOf(randomId)), serviceEPR, "delete");
        assertTrue(resultDetails.toString().contains("SUCCESSFUL"));
    }

    public DataHandler modifyExcelURL(String dbsFilePath) throws XMLStreamException, IOException {
        try {
            OMElement dbsFile = AXIOMUtil.stringToOM(FileManager.readFile(dbsFilePath));
            OMElement dbsConfig = dbsFile.getFirstChildWithName(new QName("config"));
            Iterator configElement1 = dbsConfig.getChildElements();
            String productFilePath = resourceFileLocation + File.separator + "resources" + File.separator +
                                     "Products-sql.xls";
            while (configElement1.hasNext()) {
                OMElement property = (OMElement) configElement1.next();
                String value = property.getAttributeValue(new QName("name"));
                if ("url".equals(value)) {
                    property.setText("jdbc:wso2:excel:filePath=" + productFilePath);
                }
            }
            log.debug(dbsFile);
            ByteArrayDataSource dbs = new ByteArrayDataSource(dbsFile.toString().getBytes());
            return new DataHandler(dbs);

        } catch (XMLStreamException e) {
            log.error("XMLStreamException when Reading Service File", e);
            throw new XMLStreamException("XMLStreamException when Reading Service File", e);
        } catch (IOException e) {
            log.error("IOException when Reading Service File", e);
            throw new IOException("IOException  when Reading Service File", e);
        }
    }

    private OMElement insertNewRecord(String idNum) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "dat");
        OMElement payload = fac.createOMElement("insertop", omNs);

        OMElement id = fac.createOMElement("id", omNs);
        OMElement mod = fac.createOMElement("mod", omNs);
        OMElement classname = fac.createOMElement("classname", omNs);
        id.setText(idNum);
        mod.setText("mod111");
        classname.setText("org.wso2.carbon.dss.test");
        payload.addChild(id);
        payload.addChild(mod);
        payload.addChild(classname);

        return payload;
    }

    private OMElement getDetails() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "dat");
        return fac.createOMElement("Getdetails", omNs);
    }

    private OMElement updateRecord(String idNum) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "dat");
        OMElement payload = fac.createOMElement("Update", omNs);

        OMElement id = fac.createOMElement("id", omNs);
        OMElement mod = fac.createOMElement("mod", omNs);
        OMElement classname = fac.createOMElement("classname", omNs);
        id.setText(idNum);
        mod.setText("mod1112" + idNum);
        classname.setText("org.wso2.carbon.dss.test2");
        payload.addChild(id);
        payload.addChild(mod);
        payload.addChild(classname);

        return payload;
    }

    private OMElement deleteRecord(String idNum) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "dat");
        OMElement payload = fac.createOMElement("delete", omNs);
        OMElement id = fac.createOMElement("id", omNs);
        id.setText(idNum);
        payload.addChild(id);

        return payload;
    }


    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        log.info(serviceName + " deleted");
    }

}
