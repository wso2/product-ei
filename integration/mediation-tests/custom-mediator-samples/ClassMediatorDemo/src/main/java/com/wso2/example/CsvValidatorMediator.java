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

package com.wso2.example;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;

/**
 * Class mediator which throws a org.apache.synapse.SynapseException so that the fault sequence will be triggered.
 */
public class CsvValidatorMediator extends AbstractMediator {

    String ERROR_CODE="ERROR_CODE";
    String ERROR_MESSAGE="ERROR_MESSAGE";
    String ERROR_DETAIL="ERROR_DETAIL";

    public boolean mediate(MessageContext context) {

        MediatorException mediatorExp = new MediatorException();
        mediatorExp.handle(ERROR_CODE, ERROR_MESSAGE, ERROR_DETAIL, context);
        return true;
    }
}

class MediatorException {

    /**
     * Throw Synapse Exception for any exception in class mediator
     * so that the fault handler will be invoked
     *
     * @param ERROR_CODE
     * @param ERROR_MESSAGE
     * @param ERROR_DETAIL
     * @param context
     */
    public static void handle(String ERROR_CODE, String ERROR_MESSAGE, String ERROR_DETAIL, MessageContext context) {

        int array[] = {20, 20, 40};
        int total = 0;
        try {
            for (int i = 5; i >= 0; i--) {
                total += array[i];
            }
        } catch (Exception e) {
            context.setProperty(ERROR_CODE, "AB005");
            context.setProperty(ERROR_MESSAGE, "Error Message from class CsvValidatorMediator");
            context.setProperty(ERROR_DETAIL, "Error Details from class");

            String messageContextErrorCode = (String) context.getProperty(ERROR_CODE);
            String messageContextErrorMessage = (String) context.getProperty(ERROR_MESSAGE);
            String messageContextErrorDetail = (String) context.getProperty(ERROR_DETAIL);
            String separator = "?";

            String concatenatedMessage = (messageContextErrorCode + separator + messageContextErrorMessage + separator + messageContextErrorDetail);
            throw new SynapseException(concatenatedMessage);
        }
    }
}