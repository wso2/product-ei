/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.esb.integration.common.utils.servers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.esb.integration.common.utils.exception.ESBIntegrationTestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class WireMonitor extends Thread {
    private Log log = LogFactory.getLog(WireMonitor.class);
    private static final int TIMEOUT_VALUE = 30000;
    private int port;
    private ServerSocket providerSocket;
    private Socket connection = null;
    private WireMonitorServer trigger;
    private CountDownLatch countDownLatch;
    private boolean started;


    public void run() {
        try {
            // creating a server socket
            providerSocket = new ServerSocket(port, 10);

            log.info("WireMonitor Server started on port " + port);
            log.info("Waiting for connection");
            this.countDownLatch.countDown();
            started = true;
            connection = providerSocket.accept();
            log.info("Connection received from " + connection.getInetAddress().getHostName());
            InputStream in = connection.getInputStream();
            int ch;
            StringBuffer buffer = new StringBuffer();
            StringBuffer headerBuffer = new StringBuffer();
            Long time = System.currentTimeMillis();
            int contentLength = -1;
            log.info("Reading message........");
            while ((ch = in.read()) != 1) {
                buffer.append((char) ch);
                //message headers end with
                if (contentLength == -1 && buffer.toString().endsWith("\r\n\r\n")) {
                    headerBuffer = new StringBuffer(buffer.toString());
                    if (buffer.toString().contains("Content-Length")) {
                        String headers = buffer.toString();
                        //getting content-length header
                        String contentLengthHeader = headers.substring(headers.indexOf("Content-Length:"));
                        contentLengthHeader = contentLengthHeader.substring(0, contentLengthHeader.indexOf("\r\n"));
                        contentLength = Integer.parseInt(contentLengthHeader.split(":")[1].trim());
                        //clear the buffer
                        buffer.setLength(0);
                    }
                }

                //braking loop since whole message is red
                if (buffer.toString().length() == contentLength) {
                    break;
                }
                // In this case no need of reading more than timeout value
                if (System.currentTimeMillis() > (time + TIMEOUT_VALUE) || buffer.toString().contains("</soapenv:Envelope>")) {
                    break;
                }
            }
            log.info("Message received");
            // Signaling Main thread to continue
            trigger.response = headerBuffer.toString() + buffer.toString();
            OutputStream out = connection.getOutputStream();
            out.write(("HTTP/1.1 202 Accepted" + "\r\n\r\n").getBytes());
            out.flush();
            log.info("Ack sent");
            out.close();
            in.close();

        } catch (IOException ioException) {
            log.warn(ioException.getMessage());
        } finally {
            try {
                connection.close();
                providerSocket.close();
                log.info("Connection closed ");
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        trigger.isFinished = true;

    }

    public WireMonitor(int listenPort, WireMonitorServer trigger) {
        this.countDownLatch = new CountDownLatch(1);
        port = listenPort;
        this.trigger = trigger;
    }
    public void awaitStartServer() throws ESBIntegrationTestException {
        try {
            if (!this.countDownLatch.await(60, TimeUnit.SECONDS)) {
                throw new ESBIntegrationTestException(
                        "Error while waiting to create Wire monitor socket due to timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ESBIntegrationTestException(
                    "Error while waiting to create Wire monitor socket due to interrupted exception ", e);
        }
    }

    /**
     * Method to return the status of the wire monitor.
     *
     * @return true is the wire monitor is started and accepting connections
     */
    public boolean isStarted() {
        return started;
    }

}
