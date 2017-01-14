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
package org.wso2.carbon.esb.samples.test.messaging;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.TestConfigurationProvider;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class Sample256TestCase extends ESBIntegrationTest {

//    private String SMTP_HOST = "smtp.gmail.com";
//    private static String GMAIL_USER_NAME = "test.automation.dummy";
//    private static String GMAIL_PASSWORD = "automation.test";
//    private String EMAIL_lID = GMAIL_USER_NAME + "@gmail.com";
//
//    private int mailCountBeforeTestStart = 0;
//    private String modifiedTime;
//    private String feedURL;
//    private ServerConfigurationManager serverConfigurationManager = null;
//
//    @BeforeClass(alwaysRun = true)
//    public void initialize() throws Exception {
//
//        super.init();
//        serverConfigurationManager = new ServerConfigurationManager(context);
//
//        feedURL = "https://mail.google.com/mail/feed/atom";
//        mailCountBeforeTestStart = getMailCount(feedURL);
//        modifiedTime = getModifiedTime(feedURL);
//
//        serverConfigurationManager.applyConfiguration(new File(TestConfigurationProvider.getResourceLocation()
//                + File.separator + "artifacts" + File.separator + "ESB"
//                + File.separator + "mail" + File.separator + "axis2.xml"));
//
//        super.init();
//        loadSampleESBConfiguration(256);
//
//    }
//
//    @Test(groups = {"wso2.esb"}, description = "Test email transport ")
//    public void testEmailTransport() throws Exception {
//
//        Properties props = new Properties();
//        props.put("mail.smtp.host", SMTP_HOST);
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.debug", "true");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.fallback", "false");
//        props.put("mail.smtp.starttls.enable", "true");
//
//        Authenticator authenticator = new GmailPasswordAuthenticator(GMAIL_USER_NAME, GMAIL_PASSWORD);
//
//        Session session = Session.getInstance(props, authenticator);
//
//        sendEmail(session, EMAIL_lID, "POX message", "<m0:getQuote xmlns:m0=\"http://services.samples\">\n" +
//                "    <m0:request>\n" +
//                "        <m0:symbol>IBM</m0:symbol>\n" +
//                "    </m0:request>\n" +
//                "</m0:getQuote>");
//
//        Thread.sleep(30000);
//
//        Assert.assertTrue("mail not delivered", getMailCount(feedURL) == (mailCountBeforeTestStart + 1));
//    }
//
//    @AfterClass(alwaysRun = true,  enabled = false)
//    public void deleteService() throws Exception {
//
//        super.cleanup();
//
//        if (serverConfigurationManager != null) {
//            serverConfigurationManager.restoreToLastConfiguration();
//        }
//    }
//
//    private static OMElement getAtomFeedContent(String atomURL) throws IOException,
//            XMLStreamException {
//
//        StringBuilder sb;
//        InputStream inputStream = null;
//        URL url = new URL(atomURL);
//
//        try {
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            connection.setRequestMethod("GET");
//            String userPassword = GMAIL_USER_NAME + ":" + GMAIL_PASSWORD;
//            String encodedAuthorization = Base64Utils.encode(userPassword.getBytes());
//            connection.setRequestProperty("Authorization", "Basic " +
//                    encodedAuthorization);
//            connection.connect();
//
//            inputStream = connection.getInputStream();
//            sb = new StringBuilder();
//            String line;
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append("\n");
//            }
//        } finally {
//            assert inputStream != null;
//            inputStream.close();
//        }
//
//        return AXIOMUtil.stringToOM(sb.toString());
//
//    }
//
//    private int getMailCount(String feedURL) throws XMLStreamException, IOException {
//        OMElement mailFeed = getAtomFeedContent(feedURL);
//        Iterator itr = mailFeed.getChildrenWithName(new QName("fullcount"));
//        int count = 0;
//        if (itr.hasNext()) {
//            OMElement countOm = (OMElement) itr.next();
//            return Integer.parseInt(countOm.getText());
//        }
//        return count;
//    }
//
//    private static String getModifiedTime(String feedURL) throws XMLStreamException, IOException {
//        OMElement mailFeed = getAtomFeedContent(feedURL);
//        Iterator itr = mailFeed.getChildrenWithName(new QName("entry"));
//        if (itr.hasNext()) {
//            OMElement countOm = (OMElement) itr.next();
//            for (Iterator itrTitle = countOm.getChildrenWithName(new QName("title")); itrTitle.hasNext(); ) {
//                OMElement title = (OMElement) itrTitle.next();
//                if (title.getText().equals("SOAPAction: http://ws.apache.org/ws/2007/05/eventing-extended/Publish")) {
//                    OMElement modified = (OMElement) countOm.getChildrenWithName(new QName("modified")).next();
//                    return modified.getText();
//                }
//
//            }
//        }
//        return null;
//    }
//
//    public void sendEmail(Session session, String toEmail, String subject, String body){
//
//        try
//        {
//            Transport transport = session.getTransport("smtp");
//            transport.connect();
//            Message message = new MimeMessage(session);
//            // Set from
//            message.setFrom(new InternetAddress(toEmail));
//            // Set to
//            InternetAddress[] address = { new InternetAddress(toEmail) };
//            message.setRecipients(Message.RecipientType.TO, address);
//            // Set subject
//            message.setSubject(subject);
//            // Set time
//            message.setSentDate(new Date());
//            // Set content// In this example multipart of// part1 is email body
//            MimeBodyPart bodyPart1 = new MimeBodyPart();
//            bodyPart1.setText(body);
//            MimeBodyPart bodyPart2 = new MimeBodyPart();
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(bodyPart1);
//            message.setContent(multipart);
//            // Set complete
//            message.saveChanges();
//            // Send the message
//            transport.sendMessage(message, address);
//            transport.close();
//
//            log.info("EMail Sent Successfully!!");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

class GmailPasswordAuthenticator extends Authenticator {
    protected PasswordAuthentication passwordAuthentication;

    public GmailPasswordAuthenticator(String user, String password)
    {this.passwordAuthentication = new PasswordAuthentication(user, password);

    }

    protected PasswordAuthentication getPasswordAuthentication()
    {
        return passwordAuthentication;
    }
}
