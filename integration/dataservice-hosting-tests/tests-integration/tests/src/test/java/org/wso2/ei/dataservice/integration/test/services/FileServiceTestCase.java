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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileServiceTestCase extends DSSIntegrationTest {
    private static final Log log = LogFactory.getLog(FileServiceTestCase.class);

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/file_service", "ns1");

    private final String txtFileName = "TestFile.txt";
    private final String txtFileType = "txt";
    private final String serviceName = "FileServiceTest";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(selectSqlFile("CreateTables.sql"));
        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "MySql" + File.separator
                                     + "FileServiceTest.dbs", sqlFileLis));

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }


    @Test(groups = {"wso2.dss"})
    public void createNewFile() throws AxisFault, XPathExpressionException {
        OMElement response;
        OMElement payload;

        payload = fac.createOMElement("_getcreatenewfile", omNs);

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        OMElement fileType = fac.createOMElement("fileType", omNs);
        fileType.setText(txtFileType);
        payload.addChild(fileType);

        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "_getcreatenewfile");
        response = checkFileExists();
        Assert.assertEquals("1", response.getFirstElement().getFirstElement().getText(), "Expected Not same .File Not Exists");
        log.info("New File Created");


    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"createNewFile"})
    public void checkFileType() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("_getgetfiletype", omNs);

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "_getgetgetfiletype");
        Assert.assertNotNull(result, "Response message null ");
        Assert.assertEquals(txtFileType, result.getFirstElement().getFirstElement().getText(), "Expected not same");
        log.info("File type verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"createNewFile"})
    public void checkFileName() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("_getgetfilenames", omNs);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "_getgetfilenames");
        Assert.assertNotNull(result, "Response message null ");
        Assert.assertTrue(result.toString().contains("<fileName>" + txtFileName + "</fileName>"), "File name not found ");
        log.info("File Name Verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"createNewFile"})
    public void addRecord() throws IOException, XPathExpressionException {
        OMElement payload = fac.createOMElement("_postappenddatatofile", omNs);
        String recordsExpected = "";

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        OMElement fileRecord = fac.createOMElement("data", omNs);
        AxisServiceClient axisClient = new AxisServiceClient();
        Base64 encoder = new Base64();
//        encoder.e
//        BASE64Decoder decoder = new BASE64Decoder();
        for (int i = 0; i < 5; i++) {
            String record = "TestFileRecord" + i;
            fileRecord.setText(new String(encoder.encode(record.getBytes())));
            payload.addChild(fileRecord);
            recordsExpected = recordsExpected + record + ";";
            axisClient.sendRobust(payload, getServiceUrlHttp(serviceName), "_postappenddatatofile");

        }
        log.info("Records Added");
        OMElement response = getRecord();
        Iterator file = response.getChildrenWithLocalName("File");
        String recordData = "";
        while (file.hasNext()) {
            OMElement data = (OMElement) file.next();
            recordData = recordData + new String(encoder.decode(data.getFirstElement().getText().getBytes())) + ";";

        }
        Assert.assertNotSame("", recordsExpected, "No Record added to file. add records to file");
        Assert.assertEquals(recordData, recordsExpected, "Record Data Mismatched");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"addRecord"})
    public void checkFileSize() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("_getgetfilesize", omNs);

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "_getgetfilesize");
        System.out.println(result);
        Assert.assertNotNull(result, "Response message null ");
        Assert.assertTrue(Integer.parseInt(result.getFirstElement().getFirstElement().getText()) > 1, "Expected not same");
        log.info("File Size Verified");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = {"checkFileSize"})
    public void deleteFile() throws AxisFault, XPathExpressionException {
        OMElement response;
        OMElement payload = fac.createOMElement("_getdeletefile", omNs);

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        new AxisServiceClient().sendRobust(payload, getServiceUrlHttp(serviceName), "_getdeletefile");
        response = checkFileExists();
        Assert.assertEquals("0", response.getFirstElement().getFirstElement().getText(), "Expected Not same .File not deleted");
        log.info("File Deleted");
    }


    private OMElement getRecord() throws AxisFault, XPathExpressionException {
        OMElement payload = fac.createOMElement("_getgetfilerecords", omNs);

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "_getgetfilerecords");
        Assert.assertNotNull(result, "Response message null ");
        return result;
    }

    private OMElement checkFileExists() throws AxisFault, XPathExpressionException {

        OMElement payload = fac.createOMElement("_getcheckfileexists", omNs);

        OMElement fileName = fac.createOMElement("fileName", omNs);
        fileName.setText(txtFileName);
        payload.addChild(fileName);

        OMElement result = new AxisServiceClient().sendReceive(payload, getServiceUrlHttp(serviceName), "_getcheckfileexists");
        Assert.assertNotNull(result, "Response message null ");
        Assert.assertTrue(result.toString().indexOf("Files") == 1, "Expected not same");
        return result;

    }

}
