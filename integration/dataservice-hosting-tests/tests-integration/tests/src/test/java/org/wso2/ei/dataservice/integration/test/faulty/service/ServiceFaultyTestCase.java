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

package org.wso2.ei.dataservice.integration.test.faulty.service;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceAdminClient;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceFileUploaderClient;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import static org.testng.Assert.assertTrue;


public class ServiceFaultyTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(ServiceFaultyTestCase.class);

    private String serviceName = "FaultyDataService";
    private DataServiceAdminClient dataServiceAdminClient;
    private String resourceFileLocation;


    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        DataHandler dhArtifact;
        resourceFileLocation = getResourceLocation();
        super.init();
        String serviceFile = "FaultyDataService.dbs";
        String serviceFilePath = resourceFileLocation + File.separator + "dbs" + File.separator +
                                 "rdbms" + File.separator + "MySql" + File.separator + serviceFile;
        dhArtifact = createArtifact(serviceFilePath, getSqlScript());

        DataServiceFileUploaderClient dataServiceFileUploaderClient =
                new DataServiceFileUploaderClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        dataServiceAdminClient =
                new DataServiceAdminClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);

        dataServiceFileUploaderClient.uploadDataServiceFile(serviceFile, dhArtifact);
        Assert.assertTrue(isServiceDeployed(serviceName));

    }

    @Test(groups = "wso2.dss", description = "invoke the deployed service")
    public void serviceInvocation() throws AxisFault, XPathExpressionException {
        OMElement response;
        String serviceEndPoint = getServiceUrlHttp(serviceName);
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        for (int i = 0; i < 5; i++) {
            response = axisServiceClient.sendReceive(getPayload(), serviceEndPoint, "showAllOffices");
            Assert.assertTrue(response.toString().contains("<Office>"), "Expected Result not Found");
            Assert.assertTrue(response.toString().contains("<officeCode>"), "Expected Result not Found");
            Assert.assertTrue(response.toString().contains("<city>"), "Expected Result not Found");
            Assert.assertTrue(response.toString().contains("<phone>"), "Expected Result not Found");
            Assert.assertTrue(response.toString().contains("</Office>"), "Expected Result not Found");
        }
        log.info("Service invocation success");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"serviceInvocation"}, description = "make the service invalie")
    public void faultyService() throws Exception {
        String serviceContent;
        String newServiceContent;
        Assert.assertTrue(isServiceDeployed(serviceName),
                          "Service not in faulty service list");
        serviceContent = dataServiceAdminClient.getDataServiceContent(serviceName);

        try {
            OMElement dbsFile = AXIOMUtil.stringToOM(serviceContent);
            OMElement dbsConfig = dbsFile.getFirstChildWithName(new QName("config"));
            Iterator configElement1 = dbsConfig.getChildElements();
            while (configElement1.hasNext()) {
                OMElement property = (OMElement) configElement1.next();
                String value = property.getAttributeValue(new QName("name"));
                if ("org.wso2.ws.dataservice.user".equals(value)) {
                    property.setText("invalidUser");

                } else if ("org.wso2.ws.dataservice.password".equals(value)) {
                    property.setText("password");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug(dbsFile);
            }
            newServiceContent = dbsFile.toString();
        } catch (XMLStreamException e) {
            log.error("XMLStreamException while handling data service content ", e);
            throw new XMLStreamException("XMLStreamException while handling data service content ", e);
        }
        Assert.assertNotNull("Could not edited service content", newServiceContent);
        dataServiceAdminClient.editDataService(serviceName, "", newServiceContent);
        log.info(serviceName + " edited");
    }

    @Test(groups = "wso2.dss", dependsOnMethods = {"faultyService"}, description = "check faulty service availability")
    public void isServiceFaulty() throws Exception {
        assertTrue(isServiceFaulty( serviceName));
        log.info(serviceName + " is faulty");
    }


    @AfterClass(alwaysRun = true, groups = "wso2.dss", description = "delete service")
    public void deleteFaultyService() throws Exception {
        deleteService(serviceName);
    }

    private OMElement getPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/faulty_dataservice", "ns1");
        return fac.createOMElement("showAllOffices", omNs);
    }


    private ArrayList<File> getSqlScript() throws XPathExpressionException {
        ArrayList<File> al = new ArrayList<File>();
        al.add(selectSqlFile("CreateTables.sql"));
        al.add(selectSqlFile("Offices.sql"));
        return al;
    }

}
