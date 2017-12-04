/*
 * Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.mediator.test.xslt;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import static org.wso2.esb.integration.common.utils.Utils.assertIfSystemLogContains;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;

/**
 * Test for transform.xslt.result.disableBuild property in xslt mediator
 */
public class XSLTDisableBuildPropertyTestCase extends ESBIntegrationTest {

    private static final String TEST_PROXY = "xsltDisableBuildPropertyTestProxy";
    private static final String XML_CONTENT_TYPE = "application/xml";
    private static final String XSLT = "xslt";
    private static final String REG_RESOURCE_PATH = "/_system/config/";

    private LogViewerClient logViewerClient;
    private ResourceAdminServiceClient resourceAdminServiceStub;

    /**
     * Upload registry resources and deploy XSLTDisableBuildPropertyTestProxy proxy
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
        uploadResourcesToConfigRegistry();
        logViewerClient = new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());
    }

    /**
     * Test for the availability of the encoded value without building the message at xslt mediator
     *
     * @throws Exception
     */
    @Test(groups = "wso2.esb", description = "Test disable build property in XSLT mediator")
    public void disableBuildPropertyTest() throws Exception {
        String encodedValue = "&#10;\n" + "&#10;\n" + "&#10;\n" + "&#10;\n" + "&#10;\n" + "&#10;";

        HttpRequestUtil.doPost(new URL(getProxyServiceURLHttp(TEST_PROXY)), "");
        Assert.assertTrue(assertIfSystemLogContains(logViewerClient, encodedValue),
                          "Encoded values are not preserved without building the message at xslt mediator;");
    }

    /**
     * Uploads XsltDisableBuildPropertyRegResource.xslt to registry
     *
     * @throws Exception
     */
    private void uploadResourcesToConfigRegistry() throws Exception {
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(contextUrls.getBackEndUrl(), getSessionCookie());

        resourceAdminServiceStub.deleteResource(REG_RESOURCE_PATH + XSLT);
        resourceAdminServiceStub.addCollection(REG_RESOURCE_PATH, XSLT, "", "XSLT test resources");

        resourceAdminServiceStub.addResource(
                REG_RESOURCE_PATH + XSLT + "/xsltDisableBuildPropertyRegResource.xslt", XML_CONTENT_TYPE, "xslt file",
                new DataHandler(new URL("file:///" + getESBResourceLocation() + File.separator + "mediatorconfig"
                                                + File.separator + XSLT + File.separator
                                                + "xsltDisableBuildPropertyRegResource.xslt")));
        Thread.sleep(1000);
    }

    /**
     * Clean up and remove registry resources
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        resourceAdminServiceStub.deleteResource(REG_RESOURCE_PATH + XSLT);
        super.cleanup();
    }
}
