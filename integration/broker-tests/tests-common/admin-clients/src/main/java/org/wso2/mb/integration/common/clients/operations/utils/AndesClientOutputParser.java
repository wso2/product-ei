/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.common.clients.operations.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to get Andes Client outputs from parse file. The class provides evaluation
 * functions for testing purposes.
 */
public class AndesClientOutputParser {

    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Log log = LogFactory.getLog(AndesClientOutputParser.class);

    /**
     * Map that stored received messages. Used to check message duplication.
     */
    private Map<Long, Integer> mapOfReceivedMessages = new HashMap<Long, Integer>();

    /**
     * List of received messages.
     */
    private List<Long> messages = new ArrayList<Long>();

    /**
     * File path to parse received messages
     */
    private String filePath = "";

    /**
     * Creates an output parse for andes with a give file path.
     *
     * @param filePath The file path for received messages.
     * @throws IOException
     */
    public AndesClientOutputParser(String filePath) throws IOException {
        this.filePath = filePath;
        parseFile();
    }

    /**
     * Reads received messages from a file path and store the message ID in necessary data
     * structures.
     *
     * @throws IOException
     */
    private void parseFile() throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            try {
                String line = br.readLine();
                while (line != null) {
                    String tempSendMessageString = line.substring(AndesClientConstants.PUBLISH_MESSAGE_FORMAT.indexOf("Sending Message:") + "Sending Message:".length());
                    long messageIdentifier = Long.valueOf(tempSendMessageString.substring(0, tempSendMessageString
                            .indexOf(" ")).replace(",",""));
                    this.addMessage(messageIdentifier);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        } catch (FileNotFoundException e) {
            log.error("Error " + filePath + " the file containing received messages couldn't found", e);
            throw e;
        } catch (IOException e) {
            log.error("Error " + filePath + " the file cannot be read", e);
            throw e;
        }
    }

    /**
     * Gets the map used for message duplication.
     *
     * @return A map of duplicated message IDs as key.
     */
    public Map<Long, Integer> getDuplicatedMessages() {
        Map<Long, Integer> messagesDuplicated = new HashMap<Long, Integer>();
        for (Long messageIdentifier : mapOfReceivedMessages.keySet()) {
            if (mapOfReceivedMessages.get(messageIdentifier) > 1) {
                messagesDuplicated.put(messageIdentifier, mapOfReceivedMessages.get(messageIdentifier));
            }
        }
        return messagesDuplicated;
    }

    /**
     * Checks if messages are received in the correct order.
     *
     * @return true if messages are in order, false otherwise.
     */
    public boolean checkIfMessagesAreInOrder() {
        boolean result = true;
        for (int count = 0; count < messages.size(); count++) {
            if (messages.get(count) != (count)) {
                result = false;
                log.warn("Message order is broken at message " + messages.get(count));
                break;
            }
        }
        return result;
    }

    /**
     * Prints missing message IDs.
     * Suppressing "UnusedDeclaration" as this could be used for debugging purposes
     *
     * @param numberOfSentMessages Number of messages to print.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void printMissingMessages(int numberOfSentMessages) {
        log.info("Printing Missing Messages");
        for (long count = 0; count < numberOfSentMessages; count++) {
            if (mapOfReceivedMessages.get(count) == null) {
                log.info("Missing message id:" + count + 1 + "\n");
            }
        }
    }

    /**
     * Prints duplicated message IDs
     * Suppressing "UnusedDeclaration" as this could be used for debugging purposes
     */
    @SuppressWarnings("UnusedDeclaration")
    public void printDuplicateMessages() {
        log.info("Printing Duplicated Messages");
        log.info(this.getDuplicatedMessages());
    }

    /**
     * Prints the map that contains received messages.
     * Suppressing "UnusedDeclaration" as this could be used for debugging purposes
     */
    @SuppressWarnings("UnusedDeclaration")
    public void printMessagesMap() {
        log.info("Printing Received Messages");
        log.info(mapOfReceivedMessages);
    }

    /**
     * Adds received message IDs to a list and a map.
     *
     * @param messageIdentifier Received message ID.
     */
    private void addMessage(Long messageIdentifier) {
        if (mapOfReceivedMessages.get(messageIdentifier) == null) {
            mapOfReceivedMessages.put(messageIdentifier, 1);
        } else {
            int currentCount = mapOfReceivedMessages.get(messageIdentifier);
            mapOfReceivedMessages.put(messageIdentifier, currentCount + 1);
        }

        messages.add(messageIdentifier);
    }

    /**
     * Prints received message IDs in sorted.
     * Suppressing "UnusedDeclaration" as this could be used for debugging purposes
     */
    @SuppressWarnings("UnusedDeclaration")
    public void printMessagesSorted() {
        log.info("Printing Sorted Messages");
        List<Long> cloneOfMessages = new ArrayList<Long>();
        cloneOfMessages.addAll(messages);
        Collections.sort(cloneOfMessages);
        log.info(cloneOfMessages);
    }

    /**
     * Check whether all the messages are transacted
     *
     * @param operationOccurredIndex Index of the operation occurred message
     * @return transactedResult
     */
    public boolean transactedOperations(long operationOccurredIndex) throws FileNotFoundException {
        boolean result = false;
        int count = 0;
        long firstMessageIdentifier = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            //Needed try/finally to close the file
            try {
                String line = br.readLine();
                while (line != null) {
                    String tempSendMessageString = line.substring(AndesClientConstants.PUBLISH_MESSAGE_FORMAT.indexOf("Sending Message:") + "Sending Message:".length());
                    long messageIdentifier = Long.parseLong(tempSendMessageString.substring(0, tempSendMessageString.indexOf(" ")));
                    if (count == 0) {
                        firstMessageIdentifier = messageIdentifier;
                    }
                    if (count == (operationOccurredIndex)) {
                        if (messageIdentifier == firstMessageIdentifier) {
                            result = true;
                        }
                    }
                    line = br.readLine();
                    count++;
                }
            } catch (IOException e) {
                log.error("Error while parsing the file containing received messages", e);
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Error while closing the file containing received messages", e);
                }
            }

        } catch (FileNotFoundException e) {
            log.error("Error " + filePath + " the file containing received messages couldn't found", e);
            throw e;
        }

        AndesClientUtils.flushPrintWriters();
        return result;
    }

    /**
     * Check whether all the messages are received in-order when subscriber rollbacks
     * after certain number of messages.
     *
     * @param messagesPerRollback Number of messages received per rollback
     * @return Result as true if transacted rollback operation has successfully
     * preserved order of all messages.
     */
    public boolean checkIfTransactedRollbackPreservesOrder(long messagesPerRollback)
            throws FileNotFoundException {

        boolean result = true;
        int count = 0;
        Long messageIdentifier;

        int rollbackBatchIteration = 1;
        boolean isFirstMessageBatch = true;
        LineIterator iterator = null;

        Map<Integer, Long> firstMessageBatch = new HashMap<Integer, Long>();
        try {
            iterator = FileUtils.lineIterator(new File(filePath));

            String line = iterator.nextLine();
            while (iterator.hasNext()) {

                messageIdentifier = getMessageIdentifier(line);

                if (firstMessageBatch.containsValue(messageIdentifier)) {
                    isFirstMessageBatch = false;
                    if (count != messagesPerRollback * rollbackBatchIteration) {
                        log.error("failed to check if rollback messages are received in order" +
                                  " to due to first duplicated message count " + count +
                                  " not equal to messages per each rollback." +
                                  messagesPerRollback + ".");
                        result = false;
                    }
                    rollbackBatchIteration++;
                }

                if (!isFirstMessageBatch) {
                    for (int i = 0; i < messagesPerRollback; i++) {
                        messageIdentifier = getMessageIdentifier(line);
                        if (null != firstMessageBatch.get(i)) {
                            if (!firstMessageBatch.get(i).equals(messageIdentifier)) {
                                result = false;
                                log.error("Error in line :" + line +
                                          ".Rollback operation failed to keep message order.");
                            }
                        }
                        if (iterator.hasNext()) {
                            line = iterator.nextLine();
                            count++;
                        }
                    }
                } else if (isFirstMessageBatch) {
                    firstMessageBatch.put(count, messageIdentifier);
                    line = iterator.nextLine();
                    count++;
                }
            }

        } catch (FileNotFoundException e) {
            log.error("Error " + filePath + " the file containing received messages couldn't found", e);
            throw e;
        } catch (IOException e) {
            log.error("Error while parsing the file containing received messages", e);
        } finally {
            iterator.close();
        }

        AndesClientUtils.flushPrintWriters();
        return result;
    }


    /**
     * This method will return message identifier number extracted from given string line.
     * sample string line "Sending Message:36 ThreadID:7,335".
     *
     * @param line string parameter which contains given line of a text file.
     * @return messageIdentifier which can identify messages uniquely.
     */
    private Long getMessageIdentifier(String line) {

        String tempSendMessageString = line.substring(AndesClientConstants.
                PUBLISH_MESSAGE_FORMAT.indexOf("Sending Message:") + "Sending Message:".length());
        Long messageIdentifier = Long.parseLong(tempSendMessageString.
                substring(0, tempSendMessageString.indexOf(" ")));

        return messageIdentifier;
    }


    /**
     * Parse the file and get the number of duplicate message IDs.
     *
     * @return Duplicated message ID count.
     */
    public long numberDuplicatedMessages() {
        long duplicateCount = 0;
        List<Long> messagesDuplicated = new ArrayList<Long>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            //Needed try/finally to close the file
            try {
                String line = br.readLine();
                while (line != null) {
                    String tempSendMessageString = line.substring(AndesClientConstants.PUBLISH_MESSAGE_FORMAT.indexOf("Sending Message:") + "Sending Message:".length());
                    long messageIdentifier = Long.parseLong(tempSendMessageString.substring(0, tempSendMessageString.indexOf(" ")));
                    if (messagesDuplicated.contains(messageIdentifier)) {
                        duplicateCount++;
                    } else {
                        messagesDuplicated.add(messageIdentifier);
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                log.error("Error while parsing the file containing received messages", e);
            } finally {

                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Error while closing the file containing received messages", e);
                }

            }
        } catch (FileNotFoundException e) {
            log.error("Error " + filePath + " the file containing received messages couldn't found", e);
        }
        return duplicateCount;
    }
}
