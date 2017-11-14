/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.esb.integration.common.utils.servers;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

/**
 * Util class for GreenMail Server.
 */
public class GreenMailServer {
    protected static Log log = LogFactory.getLog(GreenMailServer.class);
    private static GreenMail greenMail;
    private static GreenMailUser primaryUser;
    private static final String EMAIL_INBOX = "INBOX";
    private static final String EMAIL_TRASH = "Trash";
    private static final int WAIT_TIME_MS = 180 * 1000;
    private static final String USER_EMAIL = "wso2@localhost";
    private static final String USER_LOGIN = "wso2";
    private static final String USER_PW = "wso2";
    private static final String PROTOCOL_IMAP = "imap";
    private static final String PROTOCOL_POP3 = "pop3";
    private static final String BIND_ADDRESS = "127.0.0.1";

    /**
     * Start the server and add the user.
     */
    public void startServer() {
        greenMail = new GreenMail();
        greenMail.start();
        primaryUser = greenMail.setUser(USER_EMAIL, USER_LOGIN, USER_PW);
        log.info("GreenMail Server started and user added!");
    }

    /**
     * Stop server.
     */
    public void stopServer() {
        greenMail.stop();
        log.info("GreenMail Server stopped!");
    }

    /**
     * Add new user to the server
     *
     * @param email    user email
     * @param login    user name
     * @param password password
     * @return
     */
    public static GreenMailUser addUser(String email, String login, String password) {
        GreenMailUser greenMailUser = greenMail.setUser(email, login, password);
        return greenMailUser;
    }

    public static GreenMailUser getPrimaryUser() {
        return primaryUser;
    }

    /**
     * Get the connection to a mail store
     *
     * @param user     whose mail store should be connected
     * @param protocol protocol used to connect
     * @return
     * @throws MessagingException when unable to connect to the store
     */
    private static Store getConnection(GreenMailUser user, String protocol) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getInstance(props);
        int port;
        if (PROTOCOL_POP3.equals(protocol)) {
            port = 3110;
        } else if (PROTOCOL_IMAP.equals(protocol)) {
            port = 3143;
        } else {
            port = 3025;
            props.put("mail.smtp.auth", "true");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "localhost");
            props.put("mail.smtp.port", "3025");
        }
        URLName urlName = new URLName(protocol, BIND_ADDRESS, port, null, user.getLogin(), user.getPassword());
        Store store = session.getStore(urlName);
        store.connect();
        return store;
    }

    /**
     * Check mail folder for an email using subject.
     *
     * @param emailSubject Email subject
     * @param folder       mail folder to check for an email
     * @param protocol     protocol used to connect to the server
     * @return whether mail received or not
     * @throws MessagingException if we're unable to connect to the store
     */
    private static boolean isMailReceivedBySubject(String emailSubject, String folder, String protocol,
            GreenMailUser user) throws MessagingException {
        boolean emailReceived = false;
        Folder mailFolder;
        Store store = getConnection(user, protocol);
        try {
            mailFolder = store.getFolder(folder);
            mailFolder.open(Folder.READ_WRITE);
            SearchTerm searchTerm = new AndTerm(new SubjectTerm(emailSubject), new BodyTerm(emailSubject));
            Message[] messages = mailFolder.search(searchTerm);
            for (Message message : messages) {
                if (message.getSubject().contains(emailSubject)) {
                    log.info("Found the Email with Subject : " + emailSubject);
                    emailReceived = true;
                    break;
                }
            }
        } finally {
            if (store != null) {
                store.close();
            }
        }
        return emailReceived;
    }

    /**
     * Check inbox and make sure a particular email is deleted.
     *
     * @param emailSubject Email subject
     * @param protocol     protocol used to connect to the server
     * @return
     * @throws MessagingException if we're unable to connect to the store
     */
    public static boolean checkEmailDeleted(String emailSubject, String protocol, GreenMailUser user)
            throws MessagingException {
        boolean isEmailDeleted = false;
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < WAIT_TIME_MS) {
            if (!isMailReceivedBySubject(emailSubject, EMAIL_INBOX, protocol, user)) {
                log.info("Email has been deleted successfully!");
                isEmailDeleted = true;
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.warn("Error during thread sleep for 0.5 sec");
            }
        }
        return isEmailDeleted;
    }

    public static boolean checkEmailDeleted(String emailSubject, String protocol) throws MessagingException {
        return checkEmailDeleted(emailSubject, protocol, primaryUser);
    }

    /**
     * Check trash and make sure a particular email is moved to trash.
     *
     * @param emailSubject Email subject
     * @param protocol     protocol used to connect to the server
     * @return
     * @throws MessagingException if we're unable to connect to the store
     */
    public static boolean checkEmailMoved(String emailSubject, String protocol, GreenMailUser user)
            throws MessagingException {
        boolean mailReceived = false;
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < WAIT_TIME_MS) {
            if (isMailReceivedBySubject(emailSubject, EMAIL_TRASH, protocol, user)) {
                log.info("Found the moved email in mailbox : " + emailSubject);
                mailReceived = true;
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.warn("Error during thread sleep for 0.5 sec");
            }
        }
        return mailReceived;
    }

    public static boolean checkEmailMoved(String emailSubject, String protocol) throws MessagingException {
        return checkEmailMoved(emailSubject, protocol, primaryUser);
    }

    /**
     * Delete all emails in the inbox.
     *
     * @param protocol protocol used to connect to the server
     * @throws MessagingException if we're unable to connect to the store
     */
    public static void deleteAllEmails(String protocol, GreenMailUser user) throws MessagingException {
        Folder inbox = null;
        Store store = getConnection(user, protocol);
        try {
            inbox = store.getFolder(EMAIL_INBOX);
            inbox.open(Folder.READ_WRITE);
            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                message.setFlag(Flags.Flag.DELETED, true);
                log.info("Deleted email Subject : " + message.getSubject());
            }
        } finally {
            if (inbox != null) {
                inbox.close(true);
            }
            if (store != null) {
                store.close();
            }
        }
    }

    public static void deleteAllEmails(String protocol) throws MessagingException {
        deleteAllEmails(protocol, primaryUser);
    }

    /**
     * Check whether email received by reading the emails.
     *
     * @param protocol to connect to the store
     * @param user whose mail store should be connected
     * @param subject the subject of the mail to search
     * @return
     * @throws MessagingException when unable to connect to the store
     */
    public static boolean isMailReceived(String protocol, GreenMailUser user, String subject)
            throws MessagingException {
        Store store = getConnection(user, protocol);
        Folder folder = store.getFolder(EMAIL_INBOX);
        folder.open(Folder.READ_ONLY);
        boolean isReceived = false;
        Message[] messages = folder.getMessages();
        for (Message message : messages) {
            if (message.getSubject().contains(subject)) {
                log.info("Found the Email with Subject : " + subject);
                isReceived = true;
                break;
            }
        }
        return isReceived;
    }
}