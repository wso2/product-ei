package org.wso2.esb.integration.common.utils.servers;/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * + * This class can write a response to a server socket with a specific port.
 * + * This can be used in scenarios where you want to check the
 * + * out path (i.e. from ESB --> Client)
 * +
 */

public class ResponsePushingSocketServer extends Thread {

    private Log log = LogFactory.getLog(ResponsePushingSocketServer.class);
    private int port;
    private ServerSocket providerSocket;
    private Socket connection = null;
    public String response = "";

    public void run() {
        if (response != null) {
            try {
                // creating a server socket
                providerSocket = new ServerSocket(port, 10);

                log.info("Waiting for connection");
                connection = providerSocket.accept();
                log.info("Connection received from " + connection.getInetAddress().getHostName());

                byte[] responseMsg = new String(response).getBytes();

                connection.getOutputStream().write(responseMsg);

            } catch (IOException ioException) {

            } finally {
                try {
                    connection.close();
                    providerSocket.close();
                } catch (Exception e) {

                }
            }
        }

    }

    public ResponsePushingSocketServer(int listenPort, String response) {
        port = listenPort;
        this.response = response;
    }
}