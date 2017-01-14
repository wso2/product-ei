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
package org.wso2.carbon.esb.ui.test.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class MultiMessageReceiver extends Thread {
    private Log log = LogFactory.getLog(MultiMessageReceiver.class);
    private int port;
    private ServerSocket providerSocket;
    private Socket connection = null;
    private boolean stopServer=false;
    private List<String> incomingMessages;

    /**
     * Initialization
     * @param port  Server startup port
     */
    public MultiMessageReceiver(int port)
    {
        this.port=port;
        incomingMessages=new ArrayList<String>();
    }

    /**
     * Stop server
     */
    public void stopServer()
    {
        stopServer=true;
    }

    /**
     * Start the server
     */
    public void startServer()
    {
        new Thread(this).start();
    }

    /**
     * Get the incoming message list
     * @return  list of incoming messages
     * @throws InterruptedException
     */
    public List<String> getIncomingMessages() throws InterruptedException {
        return incomingMessages;

    }

    /**
     * Get the current message queue count
     * @return
     */
    public  synchronized int getMessageQueueSize()
    {
        return incomingMessages.size();
    }

    public void run() {
        try {
            providerSocket = new ServerSocket(port, 10);
            providerSocket.setSoTimeout(5000);
            while(!stopServer){
                try{
                    connection = providerSocket.accept();
                }
                catch (SocketTimeoutException e)
                {
                    log.info("........Socket is timed out..........");
                    continue;
                }
                Reader reader=new Reader();
                reader.connectionReceived =this.connection;
                new Thread(reader).start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (Exception e) {

            }
        }
        log.info("Closing connection");
    }

    /**
     * Reading socket input stream
     */
    class Reader implements Runnable {
        private Socket connectionReceived;
        private String response="";
        public void run()
        {
            log.info("Connection received from " +
                               connectionReceived.getInetAddress().getHostName());
            BufferedReader rd =  null;
            try {
                rd = new BufferedReader(
                        new InputStreamReader(
                                connectionReceived.getInputStream()));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    response+=line;
                }
            } catch (IOException e) {
                log.error("Error while reading the input stream"+e.getMessage());
            }
            log.info("..............................................");
            log.info(response);
            log.info("..............................................");
            incomingMessages.add(response);
            try {
                rd.close();
                connectionReceived.close();
            } catch (IOException e) {
                log.error("Error while closing the connection");
            }
        }
    }
}
