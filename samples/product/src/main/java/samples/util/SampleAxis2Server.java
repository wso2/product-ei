/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SampleAxis2Server {

    private static final Log log = LogFactory.getLog(SampleAxis2Server.class);

    /**
     * Expected system properties
     * http_port: Port to bind HTTP transport (default is 9000)
     * https_port: Port to bind HTTPS transport (default is 9002)
     * server_name: Name of this instance of the server (optional)
     *
     * @param args 1: Axis2 repository
     *             2: Axis2 configuration file (axis2.xml)
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        startServer(args);
        addShutdownHook();
    }

    private static void addShutdownHook() {
        Thread shutdownHook = new Thread() {
            public void run() {
                log.info("Shutting down SimpleAxisServer ...");
                try {
                    stopServer();
                    log.info("Shutdown complete");
                    log.info("Halting JVM");
                } catch (Exception e) {
                    log.warn("Error occurred while shutting down SimpleAxisServer : " + e);
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public static void startServer(String[] args) throws Exception {
        SampleAxis2ServerManager.getInstance().start(args);
    }
    public static void stopServer() throws Exception {
        SampleAxis2ServerManager.getInstance().stop();
    }
}
