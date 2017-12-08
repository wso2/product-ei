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

package org.wso2.ei.connector.as2;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.ei.connector.as2.message.AS2MessageCreator;
import org.wso2.ei.connector.as2.util.AS2Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * AS2 Connector (AS2 Sender) implementation
 */
public class AS2Connector extends AbstractConnector {

    /**
     * Called in the mediation flow by the send_template
     *
     * @param messageContext synapse message context
     * @throws ConnectException
     */
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        // Take parameters from send_template
        Object as2toParam = getParameter(messageContext, AS2Constants.AS2_TO_PARAM);
        Object as2fromParam = getParameter(messageContext, AS2Constants.AS2_FROM_PARAM);
        Object subjectParam = getParameter(messageContext, AS2Constants.SUBJECT_PARAM);
        Object keyStoreNameParam = getParameter(messageContext, AS2Constants.KEY_STORE_NAME);
        Object publicKeyAliasParam = getParameter(messageContext, AS2Constants.PUBLIC_CERT);
        Object privateKeyAliasParam = getParameter(messageContext, AS2Constants.PRIVATE_KEY);
        Object certAliasListParam = getParameter(messageContext, AS2Constants.CERT_LIST);

        try {
            // Take all inputs to configurations map to pass to other functions
            Map<String, Object> configurations = new HashMap<String, Object>();
            configurations.put(AS2Constants.AS2_TO_PARAM, as2toParam.toString());
            configurations.put(AS2Constants.AS2_FROM_PARAM, as2fromParam.toString());
            configurations.put(AS2Constants.SUBJECT_PARAM, subjectParam.toString());
            configurations.put(AS2Constants.KEY_STORE_NAME, keyStoreNameParam.toString());
            configurations.put(AS2Constants.PUBLIC_CERT, publicKeyAliasParam.toString());
            configurations.put(AS2Constants.PRIVATE_KEY, privateKeyAliasParam.toString());
            // comma separated list of cert aliases
            configurations.put(AS2Constants.CERT_LIST, certAliasListParam.toString().split(","));

            AS2MessageCreator as2MessageCreator = new AS2MessageCreator();
            // Pass synapse message context and configs
            as2MessageCreator.createAS2Message(messageContext, configurations);

        } catch (Exception e) {
            throw new ConnectException(e);
        }
    }
}