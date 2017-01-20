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

import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.util.CommandLineOption;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.util.OptionsValidator;
//import org.apache.axis2.clustering.ClusteringAgent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SampleAxis2ServerManager {

    private static final Log log = LogFactory.getLog(SampleAxis2ServerManager.class);

    private static SampleAxis2ServerManager ourInstance = new SampleAxis2ServerManager();

    public static final int DEFAULT_PORT = 9000;

    private ConfigurationContext configctx;
    
    private ListenerManager listenerManager;

    public static SampleAxis2ServerManager getInstance() {
        return ourInstance;
    }

    private SampleAxis2ServerManager() {
    }

    public void start(String[] args) throws Exception {
        String repoLocation = null;
        String confLocation = null;

        CommandLineOptionParser optionsParser = new CommandLineOptionParser(args);
        List invalidOptionsList = optionsParser.getInvalidOptions(new OptionsValidator() {
            public boolean isInvalid(CommandLineOption option) {
                String optionType = option.getOptionType();
                return !("repo".equalsIgnoreCase(optionType) || "conf"
                    .equalsIgnoreCase(optionType));
            }
        });

        if ((invalidOptionsList.size() > 0) || (args.length > 4)) {
            printUsage();
        }

        Map optionsMap = optionsParser.getAllOptions();

        CommandLineOption repoOption = (CommandLineOption) optionsMap
            .get("repo");
        CommandLineOption confOption = (CommandLineOption) optionsMap
            .get("conf");

        log.info("[SimpleAxisServer] Starting");
        if (repoOption != null) {
            repoLocation = repoOption.getOptionValue();
            System.out.println("[SimpleAxisServer] Using the Axis2 Repository : "
                + new File(repoLocation).getAbsolutePath());
        }
        if (confOption != null) {
            confLocation = confOption.getOptionValue();
            System.out
                .println("[SimpleAxisServer] Using the Axis2 Configuration File : "
                    + new File(confLocation).getAbsolutePath());
        }
        try {
            configctx = ConfigurationContextFactory
                .createConfigurationContextFromFileSystem(repoLocation,
                    confLocation);

            configurePort(configctx);

            // Need to initialize the cluster manager at last since we are changing the servers
            // HTTP/S ports above. In the axis2.xml file, we need to set the "AvoidInitiation" param
            // to "true"
            ClusteringAgent clusteringAgent =
                    configctx.getAxisConfiguration().getClusteringAgent();
            if(clusteringAgent != null) {
                clusteringAgent.setConfigurationContext(configctx);
                clusteringAgent.init();
            }

            // Finally start the transport listeners
            listenerManager = new ListenerManager();
            listenerManager.init(configctx);
            listenerManager.start();
            log.info("[SimpleAxisServer] Started");
        } catch (Throwable t) {
            log.fatal("[SimpleAxisServer] Shutting down. Error starting SimpleAxisServer", t);
            System.exit(1); // must stop application
        }
    }

    public void stop() throws Exception {
        if (listenerManager != null) {
            listenerManager.stop();
            listenerManager.destroy();
        }
        //we need to call this method to clean the team fils we created.
        if (configctx != null) {
            configctx.terminate();
        }
    }


    private void configurePort(ConfigurationContext configCtx) {

        TransportInDescription trsIn = configCtx.getAxisConfiguration().getTransportsIn().get("http");

        if (trsIn != null) {
            String port = System.getProperty("http_port");
            if (port != null) {
                try {
                    new Integer(port);
                    trsIn.getParameter("port").setValue(port);
                } catch (NumberFormatException e) {
                    log.error("Given port is not a valid integer. Using 9000 for port.");
                    trsIn.getParameter("port").setValue("9000");
                }
            } else {
                trsIn.getParameter("port").setValue("9000");
            }
        }

        TransportInDescription httpsTrsIn = configCtx.getAxisConfiguration().
                getTransportsIn().get("https");

        if (httpsTrsIn != null) {
            String port = System.getProperty("https_port");
            if (port != null) {
                try {
                    new Integer(port);
                    httpsTrsIn.getParameter("port").setValue(port);
                } catch (NumberFormatException e) {
                    log.error("Given port is not a valid integer. Using 9000 for port.");
                    httpsTrsIn.getParameter("port").setValue("9002");
                }
            } else {
                httpsTrsIn.getParameter("port").setValue("9002");
            }
        }
    }

    public static void printUsage() {
        System.out.println("Usage: SampleAxisServer -repo <repository>  -conf <axis2 configuration file>");
        System.out.println();
        System.exit(1);
    }
}
