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

package org.wso2.carbon.esb.car.deployment.test;


import org.awaitility.Awaitility;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.application.mgt.synapse.stub.ExceptionException;
import org.wso2.carbon.application.mgt.synapse.stub.types.carbon.EndpointMetadata;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.io.File;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

public class ESBJAVA3611EndpointTestCase extends ESBIntegrationTest {

    String carFileName = "ESBCApp-3.2.2.car";

    @BeforeClass(alwaysRun = true)
    protected void uploadCarFileTest() throws Exception {
        super.init(TestUserMode.TENANT_ADMIN);
        uploadCapp(carFileName
                   , new DataHandler(new FileDataSource(new File(getESBResourceLocation() +
                                                                File.separator + "car" + File.separator +
                                                                carFileName))));
        log.info(carFileName + " uploaded successfully");
    }

    @Test(groups = "wso2.esb", enabled = true, description = "Test whether Endpoint get deployed in tenant through  capp")
    public void testEndpointDeployed() throws Exception {
        Awaitility.await().atMost(60, TimeUnit.SECONDS).until(checkEndpointExistence());
    }


    @AfterTest(alwaysRun = true)
    public void cleanupEnvironment() throws Exception {
        super.cleanup();
    }

    private Callable<Boolean> checkEndpointExistence() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() {
                EndpointMetadata[] endpointMetadatas;
                try {
                    endpointMetadatas = getSynapseAppData("ESBCApp_3.2.2").getEndpoints();
                    boolean endpointExist = false;
                    for (EndpointMetadata endpointMetadata : endpointMetadatas) {
                        if (endpointMetadata.getName().equals("Axis2ServiceCSEndPoint")) {
                            endpointExist = true;
                        }
                    }
                    return endpointExist;
                } catch (RemoteException | ExceptionException e) {
                    // Since this is invoked periodically by Awaitility debug log is used to log the exception to avoid
                    // excessive error logging even in the positive test.
                    log.debug("Error occurred", e);
                    return false;
                }
            }
        };
    }
}
