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

package org.wso2.esb.integration.common.extensions.carbonserver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.integration.common.utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.xml.xpath.XPathExpressionException;

public class CarbonServerExtension extends ExecutionListenerExtension {
    private TestServerManager serverManager;
    private static final Log log = LogFactory.getLog(CarbonServerExtension.class);
    private String executionEnvironment;

    public void initiate() {
        try {
            if (getParameters().get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) == null) {
                getParameters().put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, "0");
            }
            serverManager = new TestServerManager(getAutomationContext(), null, getParameters()) {
                @Override
                public void configureServer() {
                    if ("ESB".equalsIgnoreCase(System.getProperty("server.list"))) {
                        //copying the files before server start. Ex: synapse artifacts, conf, etc...
                        String carbonHome = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts"
                                + File.separator + "ESB" + File.separator + "server";
                        String repository = carbonHome + File.separator + "repository";
                        File deploymentSource = new File(repository + File.separator + "deployment");
                        File confSource = new File(carbonHome + File.separator + "conf");
                        File libDirectorySource = new File(carbonHome + File.separator + "lib");
                        File dropinsDirectorySource = new File(carbonHome + File.separator + "dropins");
                        File deploymentDestination = new File(
                                this.getCarbonHome() + File.separator + "repository" + File.separator + "deployment");
                        File confDestination = new File(this.getCarbonHome() + File.separator + "conf");
                        File libDestination = new File(this.getCarbonHome() + File.separator + "lib");
                        File dropinsDestination = new File(this.getCarbonHome() + File.separator + "dropins");
                        if (confSource.exists() && confSource.isDirectory()) {
                            try {
                                log.info("Copying " + confSource.getPath() + " to " + confDestination.getPath());
                                FileUtils.copyDirectory(confSource, confDestination, true);
                            } catch (IOException e) {
                                log.error("Error while copying conf directory.", e);
                            }
                        }
                        if (deploymentSource.exists() && deploymentSource.isDirectory()) {
                            try {
                                log.info("Copying " + deploymentSource.getPath() + " to " + deploymentDestination
                                        .getPath());
                                FileUtils.copyDirectory(deploymentSource, deploymentDestination);
                            } catch (IOException e) {
                                log.error("Error while copying deployment directory.", e);
                            }
                        }
                        if (libDirectorySource.exists() && libDirectorySource.isDirectory()) {
                            try {
                                log.info("Copying " + libDirectorySource.getPath() + " to " + libDestination.getPath());
                                FileUtils.copyDirectory(libDirectorySource, libDestination);
                            } catch (IOException e) {
                                log.error("Error while copying lib directory.", e);
                            }
                        }
                        if (dropinsDirectorySource.exists() && dropinsDirectorySource.isDirectory()) {
                            try {
                                log.info("Copying " + dropinsDirectorySource.getPath() + " to "
                                         + dropinsDestination.getPath());
                                FileUtils.copyDirectory(dropinsDirectorySource, dropinsDestination);
                            } catch (IOException e) {
                                log.error("Error while copying lib directory.", e);
                            }
                        }
                    } else if ("BPS".equalsIgnoreCase(System.getProperty("server.list"))) {
                        //copying the files before server start. Ex: bpel, bpmn, etc...
                        String artifactPath = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts"
                                + File.separator + "BPS" + File.separator + "wso2" + File.separator + "business-process"
                                + File.separator + "repository" + File.separator + "deployment" + File.separator + "server";
                        String bpelHome = artifactPath + File.separator + "bpel";
                        String bpmnHome = artifactPath + File.separator + "bpmn";
                        String humanTaskHome = artifactPath + File.separator + "humantasks";

                        File deploymentSourceBpel = new File(bpelHome);
                        File deploymentSourceBpmn = new File(bpmnHome);
                        File deploymentSourceHumanTask = new File(humanTaskHome);

                        String deploymentDestination = this.getCarbonHome() + File.separator + "wso2" + File.separator + "business-process"
                                + File.separator + "repository" + File.separator + "deployment" + File.separator
                                + "server";

                        File deploymentDestinationBpel = new File(deploymentDestination + File.separator + "bpel");
                        File deploymentDestinationBpmn = new File(deploymentDestination + File.separator + "bpmn");
                        File deploymentDestinationHumanTask = new File(deploymentDestination + File.separator + "humantasks");

                        if (deploymentSourceBpel.exists() && deploymentSourceBpel.isDirectory()) {
                            try {
                                log.info("Copying " + deploymentSourceBpel.getPath() + " to " + deploymentDestinationBpel
                                        .getPath());
                                FileUtils.copyDirectory(deploymentSourceBpel, deploymentDestinationBpel);
                            } catch (IOException e) {
                                log.error("Error while copying bpel deployment directory.", e);
                            }
                        }
                        if (deploymentSourceBpmn.exists() && deploymentSourceBpmn.isDirectory()) {
                            try {
                                log.info("Copying " + deploymentSourceBpmn.getPath() + " to " + deploymentDestinationBpmn
                                        .getPath());
                                FileUtils.copyDirectory(deploymentSourceBpmn, deploymentDestinationBpmn);
                            } catch (IOException e) {
                                log.error("Error while copying bpmn deployment directory.", e);
                            }
                        }
                        if (deploymentSourceHumanTask.exists() && deploymentSourceHumanTask.isDirectory()) {
                            try {
                                log.info("Copying " + deploymentSourceHumanTask.getPath() + " to " + deploymentDestinationHumanTask
                                        .getPath());
                                FileUtils.copyDirectory(deploymentSourceHumanTask, deploymentDestinationHumanTask);
                            } catch (IOException e) {
                                log.error("Error while copying humantasks deployment directory.", e);
                    } else if ("DSS".equalsIgnoreCase(System.getProperty("server.list"))) {
                        //copying the files before server start. Ex: dss artifacts, etc...
                        String carbonHome = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts"
                                + File.separator + "DSS" + File.separator + "server";
                        String repository = carbonHome + File.separator + "repository";
                        File deploymentSource = new File(repository + File.separator + "deployment");
                        File confSource = new File(carbonHome + File.separator + "conf");
                        File libDirectorySource = new File(carbonHome + File.separator + "lib");
                        File deploymentDestination = new File(
                                this.getCarbonHome() + File.separator + "repository" + File.separator + "deployment");
                        File confDestination = new File(this.getCarbonHome() + File.separator + "conf");
                        File libDestination = new File(this.getCarbonHome() + File.separator + "lib");
                        if (confSource.exists() && confSource.isDirectory()) {
                            try {
                                log.info("Copying " + confSource.getPath() + " to " + confDestination.getPath());
                                FileUtils.copyDirectory(confSource, confDestination, true);
                            } catch (IOException e) {
                                log.error("Error while copying conf directory.", e);
                            }
                        }
                        if (deploymentSource.exists() && deploymentSource.isDirectory()) {
                            try {
                                log.info("Copying " + deploymentSource.getPath() + " to " + deploymentDestination
                                        .getPath());
                                FileUtils.copyDirectory(deploymentSource, deploymentDestination);
                            } catch (IOException e) {
                                log.error("Error while copying deployment directory.", e);
                            }
                        }
                        if (libDirectorySource.exists() && libDirectorySource.isDirectory()) {
                            try {
                                log.info("Copying " + libDirectorySource.getPath() + " to " + libDestination.getPath());
                                FileUtils.copyDirectory(libDirectorySource, libDestination);
                            } catch (IOException e) {
                                log.error("Error while copying lib directory.", e);
                            }
                        }
                    }
                }
            };
            executionEnvironment = getAutomationContext()
                    .getConfigurationValue(ContextXpathConstants.EXECUTION_ENVIRONMENT);
        } catch (XPathExpressionException e) {
            handleException("Error while initiating test environment", e);
        }
    }

    public void onExecutionStart() {
        try {
            if (executionEnvironment.equalsIgnoreCase(ExecutionEnvironment.STANDALONE.name())) {
                String carbonHome = serverManager.startServer();
                System.setProperty(ExtensionConstants.CARBON_HOME, carbonHome);
            }
        } catch (Exception e) {
            handleException("Fail to start carbon server ", e);
        }
    }

    public void onExecutionFinish() {
        try {
            if (executionEnvironment.equalsIgnoreCase(ExecutionEnvironment.STANDALONE.name())) {
                serverManager.stopServer();
            }
        } catch (Exception e) {
            log.error("Fail to stop carbon server ", e);
        }
    }

    private static void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new RuntimeException(msg, e);
    }
}
