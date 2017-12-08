/*
*Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.tcp.transport.test;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.esb.tcp.transport.test.util.NativeTCPClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

/**
 * Test TCP session persistance splitting by a special charachter.
 */
public class TCPSessionPersistenceSplitBySpecialCharacterTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private static String message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap"
            + ".org/soap/envelope/\"><soapenv:Header/><soapenv:Body/></soapenv:Envelope>";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/tcp/transport/tcpProxy_splitBySpecialCharacter.xml");
    }

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
    @Test(groups = "wso2.esb", description = "Tcp proxy service which configured to split by special character")
    public void tcpTransportSplitBySpecialCharacterProxy() throws Exception {
        int messageCount = 3;
        Character aByte = 0x03;
        NativeTCPClient tcpClient = new NativeTCPClient(NativeTCPClient.DelimiterTypeEnum.BYTE.getDelimiterType(), messageCount);
        tcpClient.setMessage(message);
        tcpClient.setByteDelimiter(aByte);
        tcpClient.sendToServer();
        String[] responses = tcpClient.receiveCharactorTypeDelimiterResonse();
        Assert.assertEquals(messageCount, responses.length);
        for(String response: responses) {
            Assert.assertNotNull(response, "Received a null response from the server");
            Assert.assertNotEquals(StringUtils.EMPTY, response, "Received an empty response from the server");
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}