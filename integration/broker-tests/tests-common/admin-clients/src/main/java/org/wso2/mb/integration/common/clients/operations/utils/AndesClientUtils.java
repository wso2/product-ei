/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mb.integration.common.clients.operations.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.mb.integration.common.clients.AndesClient;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * This class provides functionality to evaluate Andes Client consumers and publishers.
 */
public class AndesClientUtils {
    /**
     * The logger used in logging information, warnings, errors and etc.
     */
    private static Log log = LogFactory.getLog(AndesClientUtils.class);

    /**
     * The print writer to print received messages to a file.
     */
    private static PrintWriter receivedMessagePrintWriter;

    /**
     * The print writer to print statistics such as TPS for consumers and producers and also the
     * average latency to a file.
     */
    private static PrintWriter statisticsPrintWriter;

    /**
     * The print writer to print messages that are being sent by the publisher.
     */
    private static PrintWriter publishedMessagePrintWriter;

    /**
     * Waits until no messages are received. The waiting is done by using a loop checking whether
     * any new messages are received than the previous iteration. In each iteration it will wait for
     * a certain time to make sure that message counter changes until no change is detected in the
     * message counters.
     *
     * @param client                            The consumer client
     * @param waitTimeTillMessageCounterChanges The amount of milliseconds to wait for new messages
     *                                          are received.
     * @param expectedMessageCount              Number of messages expected from the consumer
     * @throws JMSException
     */
    public static void waitForMessagesAndShutdown(AndesClient client,
                                                  long waitTimeTillMessageCounterChanges, long expectedMessageCount)
            throws JMSException {
        long previousMessageCount = 0;
        long currentMessageCount = -1;

        /**
         * At each iteration it will check whether the message count has changed than the previous
         * iteration
         */
        while (currentMessageCount != previousMessageCount) {
            try {
                // Waits till the consumer client received more messages.
                TimeUnit.MILLISECONDS.sleep(waitTimeTillMessageCounterChanges);
            } catch (InterruptedException e) {
                log.error("Error waiting for receiving messages.", e);
            }
            // Updating message counters
            previousMessageCount = currentMessageCount;
            currentMessageCount = client.getReceivedMessageCount();
        }

        log.info("Message count received by consumer : " + Long
                .toString(client.getReceivedMessageCount()));

        if (expectedMessageCount != currentMessageCount) {
            // Stopping the consumer client
            client.stopClient();
        }

        // Prints print writer contents to files.
        flushPrintWriters();
    }

    /**
     * Waits until no messages are received. The waiting is done by using a loop checking whether
     * any new messages are received than the previous iteration. In each iteration it will wait for
     * a certain time to make sure that message counter changes until no change is detected in the
     * message counters.
     *
     * @param client                            The consumer client
     * @param waitTimeTillMessageCounterChanges The amount of milliseconds to wait for new messages
     *                                          are received.
     * @throws JMSException
     */
    public static void waitForMessagesAndShutdown(AndesClient client,
                                                  long waitTimeTillMessageCounterChanges)
            throws JMSException {
        long previousMessageCount = 0;
        long currentMessageCount = -1;

        /**
         * At each iteration it will check whether the message count has changed than the previous
         * iteration
         */
        while (currentMessageCount != previousMessageCount) {
            try {
                // Waits till the consumer client received more messages.
                TimeUnit.MILLISECONDS.sleep(waitTimeTillMessageCounterChanges);
            } catch (InterruptedException e) {
                log.error("Error waiting for receiving messages.", e);
            }
            // Updating message counters
            previousMessageCount = currentMessageCount;
            currentMessageCount = client.getReceivedMessageCount();
        }

        log.info("Message count received by consumer : " + Long
                .toString(client.getReceivedMessageCount()));
        // Stopping the consumer client
        client.stopClient();
        // Prints print writer contents to files.
        flushPrintWriters();
    }

    /**
     * Shutdown the client gracefully without waiting.
     *
     * @param client The client to shutdown
     * @throws JMSException
     */
    public static void shutdownClient(AndesClient client) throws JMSException {
        client.stopClient();
        flushPrintWriters();
    }

    /**
     * Sleeps for a certain time.
     *
     * @param milliseconds Sleep time in milliseconds.
     */
    public static void sleepForInterval(long milliseconds) {
        if (0 < milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Writes received messages to a file.
     *
     * @param content  Message content to write.
     * @param filePath File path where the message content should be written.
     */
    public static void writeReceivedMessagesToFile(String content, String filePath)
            throws IOException {

        if (receivedMessagePrintWriter == null) {
            initializeReceivedMessagesPrintWriter(filePath);
        }
        receivedMessagePrintWriter.println(content);

    }

    /**
     * Writes statistics to a file.
     *
     * @param content  Statistic content.
     * @param filePath File path where the statistics should be written.
     */
    public static void writeStatisticsToFile(String content, String filePath) throws IOException {
        if (statisticsPrintWriter == null) {
            initializeStatisticsPrintWriter(filePath);
        }

        statisticsPrintWriter.println(content);

    }

    /**
     * Writes published messages to a file.
     *
     * @param content  Statistic content.
     * @param filePath File path where the statistics should be written.
     */
    public static void writePublishedMessagesToFile(String content, String filePath) throws IOException {
        if (publishedMessagePrintWriter == null) {
            initializePublishedPrintWriter(filePath);
        }

        publishedMessagePrintWriter.println(content);
    }

    /**
     * Initialize the message content print writer. This needs to be invoked before each test case.
     *
     * @param filePath The file path to write to.
     */
    public static void initializeReceivedMessagesPrintWriter(String filePath) throws IOException {
        if (StringUtils.isNotEmpty(filePath)) {
            File writerFile = new File(filePath);
            if (writerFile.exists() || writerFile.createNewFile()) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
                receivedMessagePrintWriter = new PrintWriter(bufferedWriter);
            }
        }
    }

    /**
     * Initialize the statistics print writer. This needs to be invoked before each test case.
     *
     * @param filePath The file path to write to.
     */
    public static void initializeStatisticsPrintWriter(String filePath) throws IOException {
        if (StringUtils.isNotEmpty(filePath)) {
            File writerFile = new File(filePath);
            if (writerFile.exists() || writerFile.createNewFile()) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
                statisticsPrintWriter = new PrintWriter(bufferedWriter);
                statisticsPrintWriter
                        .println("TIMESTAMP,CONSUMER_TPS,AVERAGE_LATENCY,,TIMESTAMP,PUBLISHER_TPS");
            }
        }
    }

    /**
     * Initialize the published messages print writer. This needs to be invoked before each test
     * case.
     *
     * @param filePath The file path to write to.
     */
    public static void initializePublishedPrintWriter(String filePath) throws IOException {
        if (StringUtils.isNotEmpty(filePath)) {
            File writerFile = new File(filePath);
            if (writerFile.exists() || writerFile.createNewFile()) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
                publishedMessagePrintWriter = new PrintWriter(bufferedWriter);
            }
        }
    }

    /**
     * Prints print writers to file paths.
     */
    public static void flushPrintWriters() {
        if (null != receivedMessagePrintWriter) {
            receivedMessagePrintWriter.flush();
        }

        if (null != statisticsPrintWriter) {
            statisticsPrintWriter.flush();
        }

        if (null != publishedMessagePrintWriter) {
            publishedMessagePrintWriter.flush();
        }
    }

    /**
     * Creates a file using a given file and a size.
     *
     * @param filePathToRead   The file path to read contents from.
     * @param filePathToCreate The path in which the contents should be written with a given size.
     * @param sizeInKB         The size of the file to be written in kilobytes.
     */
    public static void createMockFile(String filePathToRead, String filePathToCreate,
                                      int sizeInKB) throws IOException {
        String fileContentToBeWritten = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePathToRead));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            fileContentToBeWritten = sb.toString();
        } finally {

            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("Error while closing buffered reader", e);
            }

        }

        //If already exists, deleting the file
        deleteRandomFile(filePathToCreate);

        try {
            File fileToCreate = new File(filePathToCreate);

            boolean createFileSuccess = fileToCreate.createNewFile();
            if (createFileSuccess) {
                log.info("Successfully created a file to append content for sending at " + filePathToCreate);
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePathToCreate));
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            for (int count = 0; count < sizeInKB; count++) {
                printWriter.append(fileContentToBeWritten);
            }

            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            log.error("Error. File to print received messages is not provided", e);
        }
    }

    /**
     * Creates a random string.
     *
     * @param sizeInBytes    The size of the file to be written in bytes.
     * @param maxLineSize    The maximum size of one line of the file to be written in bytes.
     */
    public static String createRandomString(int sizeInBytes, int maxLineSize) {
        String messageContent = "";
        int noOfLines = (int) Math.ceil(sizeInBytes / ((long) maxLineSize));
        int written = 0;
        int remaining = sizeInBytes;

        for (int i = 0; i < noOfLines; i++) {
            if (remaining <= maxLineSize) {
                messageContent = messageContent + RandomStringUtils.randomAlphabetic(remaining);
                break;
            }
            messageContent = messageContent + RandomStringUtils.randomAlphanumeric(maxLineSize - 1);
            messageContent = messageContent + "\n";
            written = written + maxLineSize;
            remaining = remaining - maxLineSize;
        }
        return messageContent;
    }

    /**
     * Creates a one line random string
     *
     * @param sizeInBytes    The size of the file to be written in bytes.
     */
    public static String createRandomString(int sizeInBytes) {
        return createRandomString(sizeInBytes, sizeInBytes);
    }

    /**
     * Deletes a random file.
     *
     * @param filePathToDelete The path in which the contents should be written with a given size.
     */
    public static void deleteRandomFile(String filePathToDelete) throws IOException {
        File fileToDelete = new File(filePathToDelete);

        if (fileToDelete.exists()) {
            if (!fileToDelete.delete()) {
                throw new IOException("Unable to delete random file, " + filePathToDelete);
            }
        }

    }
}
