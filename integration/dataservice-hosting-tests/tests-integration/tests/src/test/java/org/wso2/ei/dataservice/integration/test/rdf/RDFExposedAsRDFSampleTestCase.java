/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.dataservice.integration.test.rdf;

import org.apache.axiom.om.OMAttribute;
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
import java.util.Iterator;

public class RDFExposedAsRDFSampleTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(RDFExposedAsRDFSampleTestCase.class);

    private final String serviceName = "RDFExposeAsRDFSample";
    private String serviceEndPoint;

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        super.init();
        String resourceFileLocation = getResourceLocation();
        Assert.assertTrue(isServiceDeployed("RDFExposeAsRDFSample"));
        log.info(serviceName + " uploaded");
        serviceEndPoint = getServiceUrlHttp(serviceName) + "/";
    }

    @Test(groups = {"wso2.dss"})
    public void testGetVehicles() throws Exception {
        listVehicles();
        log.info("GET Request to retrieve vehicle data verified");
    }

    private void listVehicles() throws Exception {

        HttpClientUtil httpClient = new HttpClientUtil();
        OMElement result = httpClient.get(serviceEndPoint + "_getvehicles");
        Assert.assertNotNull(result, "Response null");
        Iterator itr = result.getChildren();
        while (itr.hasNext()) {
            OMElement product = (OMElement) itr.next();
            OMElement productModel = (OMElement) product.getChildrenWithLocalName("Model").next();
            OMAttribute modelResource = (OMAttribute) productModel.getAllAttributes().next();
            Assert.assertEquals(modelResource.getAttributeValue().startsWith("http://productlines/"),
                    true, "Model rdf resource value is correct");
        }
    }


    @AfterClass(alwaysRun = true, groups = "wso2.dss", description = "delete service")
    public void deleteService() throws Exception {
        cleanup();
    }

}
