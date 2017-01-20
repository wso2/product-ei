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

package org.wso2.esb.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.common.EmailSender;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.exception.ESBMailTransportIntegrationTestException;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * This Provides utility methods to sending, receiving and verifying emails by reading logs
 * By using javax mail api to send emails and LogViewerClient to read the log
 */
public class MailToTransportUtil {

    protected static Log log = LogFactory.getLog(MailToTransportUtil.class);
    private static String sender;
    private static char[] senderPassword;
    private static String receiver;
    private static char[] receiverPassword;
    private static String domain;
    private static int WAIT_TIME_MS = 180 * 1000; // Max time to wait for a email and string search in log
    private static final String EMAIL_INBOX = "INBOX";
    private static final String EMAIL_CREDENTIAL_PARENT_XPATH = "//emailCredentials";
    private static final String EMAIL_CREDENTIAL_SENDER_XPATH = "//emailCredentials/sender";
    private static final String EMAIL_CREDENTIAL_SENDER_PASSWORD_XPATH = "//emailCredentials/senderPassword";
    private static final String EMAIL_CREDENTIAL_RECEIVER_XPATH = "//emailCredentials/receiver";
    private static final String EMAIL_CREDENTIAL_RECEIVER_PASSWORD_XPATH = "//emailCredentials/receiverPassword";
    private static final String EMAIL_CREDENTIAL_DOMAIN_XPATH = "//emailCredentials/domain";

    /**
     * @return SMTP properties of Gmail server
     */
    public static Properties getSMTPProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    /**
     * Send email and check email received successfully
     *
     * @param emailSubject - Massage to send in email
     * @return Email - Email sent successfully or not
     * @throws Exception - Error while sending the gmail
     */
    public static boolean sendMailAndCheckReceived(String emailSubject) throws Exception {
        Properties props = getSMTPProperties();
        boolean isEmailReceived = false;
        EmailSender emailSender =
                new EmailSender(props, sender, String.valueOf(senderPassword), domain, receiver + "@" + domain);
        if (emailSender.createSession()) {
            emailSender.setSubject(emailSubject);
            emailSender.setBody("Body : " + emailSubject);
            emailSender.sendEmail();
            log.info("Email send by Mail API successfully : " + emailSubject);
            isEmailReceived = waitToCheckEmailReceived(emailSubject,EMAIL_INBOX);
        }
        return isEmailReceived;
    }

    /**
     * Check a particular email has received to a given email folder by email subject.
     *
     * @param emailSubject - Email emailSubject to find email is in inbox or not
     * @return - found the email or not
     * @throws ESBMailTransportIntegrationTestException - Is thrown if an error occurred while reading the emails
     */
    public static boolean isMailReceivedBySubject(String emailSubject, String folder)
            throws ESBMailTransportIntegrationTestException {
        boolean emailReceived = false;
        Folder mailFolder;
        Store store = getConnection();
        try {
            mailFolder = store.getFolder(folder);
            mailFolder.open(Folder.READ_WRITE);
            SearchTerm searchTerm = new AndTerm(new SubjectTerm(emailSubject), new BodyTerm(emailSubject));
            Message[] messages = mailFolder.search(searchTerm);
            for (Message message : messages) {
                if (message.getSubject().contains(emailSubject)) {
                    log.info("Found the email emailSubject : " + emailSubject);
                    emailReceived = true;
                    break;
                }
            }
            return emailReceived;
        } catch (MessagingException ex) {
            log.error("Error when getting mail count ", ex);
            throw new ESBMailTransportIntegrationTestException("Error when getting mail count ", ex);
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    log.warn("Error when closing the store ", e);
                }
            }
        }
    }

    /**
     * @param emailSubject - Subject of the email which should be deleted by ESB
     * @return - Email has deleted successfully or not
     * @throws ESBMailTransportIntegrationTestException - Is thrown if an error occurred when reading the emails
     */
    public static boolean checkDeletedEmail(String emailSubject)
            throws ESBMailTransportIntegrationTestException {
        boolean isEmailDeleted = false;
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < WAIT_TIME_MS) {
            if (!isMailReceivedBySubject(emailSubject, EMAIL_INBOX)) {
                log.info("Email has deleted successfully");
                isEmailDeleted = true;
                break;
            }
            try {
                Thread.sleep(500); // Email has not deleted wait for 0.5 seconds and check again
            } catch (InterruptedException e) {
                log.warn("Error while sleep the thread for 0.5 sec");
            }
        }

        return isEmailDeleted;

    }

    /**
     * Getting the connection to email using Mail API
     *
     * @return - Email message Store
     * @throws ESBMailTransportIntegrationTestException - Is thrown if an error while connecting to email store
     */
    private static Store getConnection() throws ESBMailTransportIntegrationTestException {
        Properties properties;
        Session session;

        properties = new Properties();
        properties.setProperty("mail.host", "imap.gmail.com");
        properties.setProperty("mail.port", "995");
        properties.setProperty("mail.transport.protocol", "imaps");

        session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(receiver + "@" + domain,
                                                  String.valueOf(receiverPassword));
            }
        });
        try {
            Store store = session.getStore("imaps");
            store.connect();
            return store;
        } catch (MessagingException e) {
            log.error("Error when creating the email store ", e);
            throw new ESBMailTransportIntegrationTestException("Error when creating the email store ", e);
        }
    }

    /**
     * Delete all unread emails from inbox
     *
     * @throws ESBMailTransportIntegrationTestException - Is thrown if an error when deleting the emails
     */
    public static void deleteAllUnreadEmailsFromGmail()
            throws ESBMailTransportIntegrationTestException {
        Folder inbox = null;
        Store store = getConnection();
        try {

            inbox = store.getFolder(EMAIL_INBOX);
            inbox.open(Folder.READ_WRITE);
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (Message message : messages) {
                message.setFlag(Flags.Flag.DELETED, true);
                log.info("Deleted email Subject : " + message.getSubject());
            }

        } catch (MessagingException e) {
            log.error("Error when deleting emails from inbox", e);
            throw new ESBMailTransportIntegrationTestException("Error when deleting emails from inbox ", e);
        } finally {
            if (inbox != null) {
                try {
                    inbox.close(true);
                } catch (MessagingException e) {
                    log.warn("Error when closing the email folder : ", e);
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    log.warn("Error when closing the email store : ", e);
                }
            }
        }
    }

    /**
     * @param backendURL   - server backend URL
     * @param stringToFind - string to find in the logs
     * @param cookie       - cookie
     * @return - found the string or not
     * @throws ESBMailTransportIntegrationTestException - Is thrown if an error when getting or reading the log.
     */

    public static boolean searchStringInLog(String backendURL, String stringToFind, String cookie)
            throws ESBMailTransportIntegrationTestException {
        boolean expectedStringFound = false;
        LogViewerClient logViewerClient;
        long startTime = System.currentTimeMillis();

        try {
            while ((System.currentTimeMillis() - startTime) < WAIT_TIME_MS) {
                logViewerClient = new LogViewerClient(backendURL, cookie);
                LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
                for (LogEvent item : logs) {
                    String message = item.getMessage();
                    if (message.contains(stringToFind)) {
                        log.info("Found the expected message in log : " + message);
                        expectedStringFound = true;
                        break;
                    }
                }
                if (expectedStringFound) {
                    break;
                }
                try {
                    Thread.sleep(500); // wait for 0.5 second to check the log again.
                } catch (InterruptedException e) {
                    log.warn("Error while sleep the thread for 0.5 sec");
                }
            }
            return expectedStringFound;
        } catch (LogViewerLogViewerException e) {
            log.error("Error when reading the log to find a string ", e);
            throw new ESBMailTransportIntegrationTestException("Error when reading the log to find a string ", e);
        } catch (RemoteException e) {
            log.error("Error when getting the log ", e);
            throw new ESBMailTransportIntegrationTestException("Error when getting the log ", e);
        }
    }

    /**
     * This method wait to check the expected mail subject.
     *
     * @param subjectToCheck - Email Subject to check in the inbox
     * @return boolean - If found the email true , else false
     * @throws InterruptedException                     - Error occurred in thread sleep
     * @throws ESBMailTransportIntegrationTestException - Is thrown if an error while reading the emails
     */
    public static boolean waitToCheckEmailReceived(String subjectToCheck, String emailFolder)
            throws ESBMailTransportIntegrationTestException {
        boolean mailReceived = false;
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < WAIT_TIME_MS) {
            if (isMailReceivedBySubject(subjectToCheck, emailFolder)) {
                log.info("Found the expected email in mailbox : " + subjectToCheck);
                mailReceived = true;
                break;
            }
            try {
                Thread.sleep(500); // wait for 0.5 second to check email in email mail box
            } catch (InterruptedException e) {
                log.warn("Error while sleep the thread for 0.5 sec");
            }
        }
        return mailReceived;
    }

    /**
     * Read automation.xml to set email credentials to relevant variables.
     */
    public static void readXMLforEmailCredentials()
            throws ESBMailTransportIntegrationTestException {
        try {

            AutomationContext automationContext = new AutomationContext();
            automationContext.getConfigurationNodeList(EMAIL_CREDENTIAL_PARENT_XPATH);

            sender = automationContext.getConfigurationValue(EMAIL_CREDENTIAL_SENDER_XPATH);
            senderPassword = automationContext.getConfigurationValue(EMAIL_CREDENTIAL_SENDER_PASSWORD_XPATH).toCharArray();
            receiver = automationContext.getConfigurationValue(EMAIL_CREDENTIAL_RECEIVER_XPATH);
            receiverPassword = automationContext.getConfigurationValue(EMAIL_CREDENTIAL_RECEIVER_PASSWORD_XPATH).toCharArray();
            domain = automationContext.getConfigurationValue(EMAIL_CREDENTIAL_DOMAIN_XPATH);

        } catch (XPathExpressionException e) {
            log.error("Error when getting value from automation.xml ", e);
            throw new ESBMailTransportIntegrationTestException("Error when getting value from automation.xml ", e);
        }
    }
}

