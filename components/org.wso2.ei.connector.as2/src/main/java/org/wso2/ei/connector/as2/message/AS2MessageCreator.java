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
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.connector.as2.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.ei.connector.as2.util.AS2Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * AS2 Message Creator class - adds headers to the message context, generates AS2-ID and creates SMIME message
 */
public class AS2MessageCreator {

    public static final Log log = LogFactory.getLog(AS2MessageCreator.class);

    /**
     * Create AS2 Message
     *
     * @param messageContext synapse message context
     * @param configurations configs taken from send_template
     */
    public void createAS2Message(MessageContext messageContext, Map configurations) {

        // Add outer HTTP headers
        addHeaders(messageContext, configurations);

        SMIMEMessageCreator smimeMessageCreator = new SMIMEMessageCreator();
        // pass synapse message context and configs
        smimeMessageCreator.createMimeMessage(messageContext, configurations);

    }

    /**
     * Add the AS2 headers to the message context transport headers Map
     *
     * @param messageContext synapse message context
     * @param configurations configs taken from send_template
     */
    private void addHeaders(MessageContext messageContext, Map configurations) {
        // Take axis2 message context
        org.apache.axis2.context.MessageContext axis2MessageCtx
                = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        // Take the HTTP Headers map to add AS2 outer headers
        Object httpHeaders = axis2MessageCtx.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        Map headersMap = (Map) httpHeaders;

        // Adding headers
        headersMap.put(AS2Constants.USER_AGENT, AS2Constants.USER_AGENT_NAME);
        // Format date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = formatter.format(new Date());
        headersMap.put(AS2Constants.DATE, date);
        headersMap.put(AS2Constants.FROM, AS2Constants.FROM_MDN_EMAIL);
        headersMap.put(AS2Constants.AS2_VERSION, AS2Constants.AS2_VERSION_NUMBER);
        headersMap.put(AS2Constants.AS2_FROM, (String) configurations.get(AS2Constants.AS2_FROM));
        headersMap.put(AS2Constants.AS2_TO, (String) configurations.get(AS2Constants.AS2_TO));
        headersMap.put(AS2Constants.SUBJECT, (String) configurations.get(AS2Constants.SUBJECT));
        headersMap.put(AS2Constants.MESSAGE_ID, generateAS2MessageID(messageContext.getMessageID(),
                (String) configurations.get(AS2Constants.AS2_FROM)));
        headersMap.put(AS2Constants.MDN_TO, AS2Constants.FROM_MDN_EMAIL);
        headersMap.put(AS2Constants.DISPOSITION_NOTIFICATION_OPTIONS,
                AS2Constants.DISPOSITION_NOTIFICATION_OPTIONS_VALUE);
        headersMap.put(AS2Constants.MIME_VERSION, AS2Constants.MIME_VERSION_NUMBER);
        headersMap.put(AS2Constants.CONTENT_TYPE, AS2Constants.APPLICATION_PKCS7_MIME);

        // To select the formatter
        axis2MessageCtx.setProperty("ContentType", AS2Constants.APPLICATION_PKCS7_MIME);
        axis2MessageCtx.setProperty("messageType", AS2Constants.APPLICATION_PKCS7_MIME);
    }

    /**
     * Converts message context messageID to AS2 MessageID format
     *
     * @param messageID messageID of the message context
     * @param as2From as2-from name config
     * @return formatted as2 message id
     */
    private String generateAS2MessageID(String messageID, String as2From) {
        String AS2MessageID = "<AS2-" + messageID + "@wso2ei." + as2From + ">";
        return AS2MessageID;
    }
}
