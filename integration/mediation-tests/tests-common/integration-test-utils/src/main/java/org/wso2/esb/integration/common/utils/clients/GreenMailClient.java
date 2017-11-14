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

package org.wso2.esb.integration.common.utils.clients;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;

import java.util.Date;
import java.util.Map;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Client class for GreenMail Server.
 */
public class GreenMailClient {
    private GreenMailUser greenMailUser;
    private static final String FROM_ADDRESS = "some@mail.com";
    private static final String TO_ADDRESS = "wso2@localhost";

    public GreenMailClient(GreenMailUser greenMailUser) {
        this.greenMailUser = greenMailUser;
    }

    /**
     * Sending email to the user account in the server.
     *
     * @param subject Email subject
     * @throws MessagingException if the properties set to the message are not valid
     * @throws UserException when no such user or user is null
     */
    public void sendMail(String subject) throws MessagingException, UserException {
        MimeMessage message = createBasicMessage(subject);
        greenMailUser.deliver(message);
    }

    /**
     * Sending email to the user account in the server with additional headers.
     *
     * @param subject Email subject
     * @throws MessagingException if the properties set to the message are not valid
     * @throws UserException when no such user or user is null
     */
    public void sendMail(String subject, Map<String, String> headers)
            throws MessagingException, UserException {
        MimeMessage message = createBasicMessage(subject);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            message.addHeader(entry.getKey(), entry.getValue());
        }
        greenMailUser.deliver(message);
    }

    /**
     * Building the message needed for sending as an email.
     *
     * @param subject Email subject
     * @return built mime message
     * @throws MessagingException if the properties set to the message are not valid
     */
    private MimeMessage createBasicMessage(String subject) throws MessagingException {
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(FROM_ADDRESS));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO_ADDRESS));
        message.setSubject(subject);
        message.setText("Content :" + subject);
        message.setSentDate(new Date());
        return message;
    }
}