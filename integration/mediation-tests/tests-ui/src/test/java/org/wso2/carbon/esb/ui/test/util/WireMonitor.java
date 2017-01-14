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

package org.wso2.carbon.esb.ui.test.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

class WireMonitor extends Thread {
    private Log log = LogFactory.getLog(WireMonitor.class);
    private static final int TIMEOUT_VALUE = 30000;
    private int port;
    private ServerSocket providerSocket;
    private Socket connection = null;
    private WireMonitorServer trigger;

    public void run() {
        try {

            // creating a server socket
            providerSocket = new ServerSocket(port, 10);

            log.info("Waiting for connection");
            connection = providerSocket.accept();
            log.info("Connection received from " +
                     connection.getInetAddress().getHostName());
            InputStream in = connection.getInputStream();
            int ch;
            StringBuffer buffer = new StringBuffer();
            Long time = System.currentTimeMillis();
            while ((ch = in.read()) != 1) {
               buffer.append((char) ch);
                // In this case no need of reading more than timeout value
                if (System.currentTimeMillis() > (time + TIMEOUT_VALUE) || buffer.toString().endsWith("</soapenv:Envelope>")) {
                    break;
                }
            }

            // Signaling Main thread to continue
            trigger.response = buffer.toString();
            trigger.isFinished = true;

            in.close();

        } catch (IOException ioException) {

        } finally {
            try {
                connection.close();
                providerSocket.close();
            } catch (Exception e) {

            }
        }

    }

    public WireMonitor(int listenPort, WireMonitorServer trigger) {
        port = listenPort;
        this.trigger = trigger;
    }

}
