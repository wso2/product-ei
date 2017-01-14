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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.mailto.transport.sender.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.MailToTransportUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;
import org.wso2.esb.integration.common.utils.exception.ESBMailTransportIntegrationTestException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import static org.testng.Assert.assertTrue;

/**
 * This class is to test emails with attachment send though ESB
 */
public class SendMailWithMultipartThroughESBTestCase extends ESBIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
        serverConfigurationManager = new ServerConfigurationManager(context);

        serverConfigurationManager.applyConfiguration(
                new File(TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" +
                         File.separator + "ESB" + File.separator + "mailTransport" + File.separator +
                         "mailTransportSender" + File.separator + "multipart" + File.separator + "axis2.xml"));

        //Need to restart the server after restart with configuration changes.
        super.reloadSessionCookie();

        loadESBConfigurationFromClasspath(
                File.separator + "artifacts" + File.separator + "ESB" + File.separator +
                "mailTransport" + File.separator + "mailTransportSender" + File.separator + "multipart" +
                File.separator + "mail_sender_multipart.xml");
        MailToTransportUtil.readXMLforEmailCredentials();

        //Since ESB reads all unread emails one by one,
        //we have to delete the all unread emails to run the test
        MailToTransportUtil.deleteAllUnreadEmailsFromGmail();
    }

    @Test(groups = {"wso2.esb"}, description = "Test email transport with multipart email" , enabled = false)
    public void testEmailTransport()
            throws ESBMailTransportIntegrationTestException, AxisFault, XMLStreamException {
        Date date = new Date();
        String message = "Multipart Email : " + new Timestamp(date.getTime());
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement request = AXIOMUtil.stringToOM(
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "<emailSubject> Subject :" + message + "</emailSubject>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>");

        axisServiceClient.sendReceive(request, getProxyServiceURLHttp("MailToTransportSenderMultipart"), "mediate");
        assertTrue(MailToTransportUtil.waitToCheckEmailReceived(message,"INBOX"), "Mail not received");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        try {
            super.cleanup();
        } finally {
            if (serverConfigurationManager != null) {
                serverConfigurationManager.restoreToLastConfiguration();
            }
        }
    }
}
