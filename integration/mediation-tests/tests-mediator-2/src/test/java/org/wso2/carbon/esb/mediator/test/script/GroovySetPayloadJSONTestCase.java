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
package org.wso2.carbon.esb.mediator.test.script;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.JSONClient;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class GroovySetPayloadJSONTestCase extends ESBIntegrationTest {

    private final String GROOVY_JAR = "groovy-all-1.1-rc-1.jar";
    private String GROOVY_JAR_LOCATION = File.separator + "jar" + File.separator + GROOVY_JAR;

    private ServerConfigurationManager serverManager;
    private JSONClient jsonclient;

    @BeforeClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    public void setEnvironment() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(context);
        serverManager.copyToComponentLib(new File(getESBResourceLocation() + GROOVY_JAR_LOCATION));
        serverManager.restartGracefully();
        super.init();
        jsonclient = new JSONClient();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    @Test(groups = {"wso2.esb", "localOnly"}, description = "Script Mediator -Run a Groovy script with setPayloadJson")
    public void testGroovySetPayloadJson() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/synapseconfig/script_mediator/groovy_script_with_setPayloadJson.xml");

        String query = "{\"key\":\"value\"}";
        String addUrl = getProxyServiceURLHttps("MyMockProxy");
        String expectedResult = "{\"fileID\":\"89265\",\"mySiteID\":\"54571\"}";

        String actualResult = jsonclient.sendUserDefineRequest(addUrl, query).toString();
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode expectedJsonObject = mapper.readTree(expectedResult);
        final JsonNode actualJsonObject = mapper.readTree(actualResult);
        assertEquals(actualJsonObject, expectedJsonObject, "Fault: value 'symbol' mismatched");
    }

    @AfterClass(alwaysRun = true)
    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE
})
    public void destroy() throws Exception {
        try {
            super.cleanup();
            Thread.sleep(5000);
        } finally {
            serverManager.removeFromComponentLib(GROOVY_JAR);
            serverManager.restartGracefully();
            serverManager.restoreToLastConfiguration();
            serverManager = null;
        }

    }
}
