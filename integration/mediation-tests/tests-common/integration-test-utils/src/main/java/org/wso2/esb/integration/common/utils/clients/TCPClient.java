/*
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
package org.wso2.esb.integration.common.utils.clients;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TCP Client
 */
public class TCPClient {
    private static final Log log = LogFactory.getLog(TCPClient.class);
    private String host;
    private int port;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Send a TCP message
     *
     * @param message message
     */
    public void sendMessage(String message) throws IOException {
        Socket socket = null;
        PrintWriter outToServer;

        try {
            socket = new Socket(host, port);
            outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            outToServer.print(message + '\n');
            outToServer.flush();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    socket.close();
                } catch (IOException ex) {
                    log.error("Error while closing the connection : " + ex.getMessage());
                }
            }
        }
    }
}
