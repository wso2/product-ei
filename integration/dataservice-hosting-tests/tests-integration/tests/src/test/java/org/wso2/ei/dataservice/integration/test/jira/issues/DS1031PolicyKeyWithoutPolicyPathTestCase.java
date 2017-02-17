/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ei.dataservice.integration.test.jira.issues;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ei.dataservices.integration.common.clients.DataServiceFileUploaderClient;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This test case is written to verify the fix for https://wso2.org/jira/browse/DS-1031
 */

public class DS1031PolicyKeyWithoutPolicyPathTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(DS1031PolicyKeyWithoutPolicyPathTestCase.class);

    private final String serviceName = "PolicyKeyWithoutPolicyPathTest";
    OMFactory fac = OMAbstractFactory.getOMFactory();
    OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice", "ns1");

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {

        super.init();
        DataServiceFileUploaderClient dataServiceAdminClient =
                new DataServiceFileUploaderClient(dssContext.getContextUrls().getBackEndUrl(), sessionCookie);
        dataServiceAdminClient.uploadDataServiceFile(serviceName + ".dbs",
                new DataHandler(new URL("file:///" + getResourceLocation() +
                        File.separator + "dbs" + File.separator +
                        "rdbms" + File.separator + "h2" + File.separator +
                        serviceName + ".dbs")));
        log.info(serviceName + " uploaded");

    }

    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void isFaultyService() throws Exception {
        assertTrue(isServiceFaulty(serviceName));
        log.info(serviceName + " is faulty");
    }

    @Test(groups = "wso2.dss", description = "Check whether service is listed as a deployed service")
    public void testServiceDeployment() throws Exception {
        assertFalse(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @AfterClass
    public void clean() throws Exception {
        deleteService(serviceName);
        cleanup();
    }

}
