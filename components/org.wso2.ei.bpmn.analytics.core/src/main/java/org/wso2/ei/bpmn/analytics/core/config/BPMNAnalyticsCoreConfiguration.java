/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */
package org.wso2.ei.bpmn.analytics.core.config;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.wso2.ei.bpmn.common.analytics.config.*;
import org.wso2.ei.bpmn.analytics.core.BPMNAnalyticsCoreConstants;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The memory model of the BPS analytics configuration - bps-analytics.xml.
 */
public class BPMNAnalyticsCoreConfiguration {
    private static final Log log = LogFactory.getLog(BPMNAnalyticsCoreConfiguration.class);
    private BPSAnalyticsDocument bpsAnalyticsDocument;

    /* Configurations related to BPMN Analytics Core*/
    //private boolean analyticsEnabled;
    private String analyticsServerURL;
    private String analyticsServerUsername;
    private String analyticsServerPassword;

    /* Configurations related to bpmn analytics */
    private boolean analyticsDashboardEnabled;

    /**
     * Create BPS Analytics Configuration from a configuration file. If error occurred while parsing configuration
     * file, default configuration will be created.
     *
     * @param BPSAnalyticsConfig XMLBeans object of BPS Analytics configuration file
     */
    public BPMNAnalyticsCoreConfiguration(File BPSAnalyticsConfig) {
        bpsAnalyticsDocument = readConfigurationFromFile(BPSAnalyticsConfig);

        if (bpsAnalyticsDocument == null) {
            return;
        }
        initConfigurationFromFile(BPSAnalyticsConfig);
    }

    // Getters retrieve BPS analytics configuration elements
    public static Log getLog() {
        return log;
    }

    /**
     * Parse BPS analytics configuration file - bps-analytics.xml and read configurations
     *
     * @param BPSAnalyticsConfigurationFile
     * @return
     */
    private BPSAnalyticsDocument readConfigurationFromFile(File BPSAnalyticsConfigurationFile) {
        try {
            return BPSAnalyticsDocument.Factory.parse(new FileInputStream(BPSAnalyticsConfigurationFile));
        } catch (XmlException e) {
            log.error("Error parsing BPS analytics configuration.", e);
        } catch (FileNotFoundException e) {
            log.info("Cannot find the BPS analytics configuration in specified location "
                     + BPSAnalyticsConfigurationFile.getPath() + " . Loads the default configuration.");
        } catch (IOException e) {
            log.error("Error reading BPS analytics configuration file" + BPSAnalyticsConfigurationFile.getPath());
        }
        return null;
    }

    /**
     * Initialize the configuration object from the properties in the BPS Analytics config xml file.
     */
    private void initConfigurationFromFile(File BPMNAnalyticsCoreConfigurationFile) {
        SecretResolver secretResolver = null;
        try (InputStream in = new FileInputStream(BPMNAnalyticsCoreConfigurationFile)) {
            StAXOMBuilder builder = new StAXOMBuilder(in);
            secretResolver = SecretResolverFactory.create(builder.getDocumentElement(), true);
        } catch (Exception e) {
            log.warn("Error occurred while retrieving secured BPS Analytics configuration.", e);
        }
        TBPSAnalytics tBPSAnalytics = bpsAnalyticsDocument.getBPSAnalytics();
        if (tBPSAnalytics == null) {
            return;
        }

        if (tBPSAnalytics.getBPMN() != null) {
            initBPMNAnalytics(tBPSAnalytics.getBPMN());
        }

        if (tBPSAnalytics.getAnalyticServer() != null) {
            initAnalytics(secretResolver, tBPSAnalytics.getAnalyticServer());
        }

//        if (tBPSAnalytics.getAnalytics() != null) {
//            initAnalytics(secretResolver, tBPSAnalytics.getAnalytics());
//        }
    }

    /**
     * Initialize analytics common configurations
     *
     * @param secretResolver
     * @param tAnalyticServer
     */
    private void initAnalytics(SecretResolver secretResolver, TAnalyticServer tAnalyticServer) {
        // Get Enabled
        //this.analyticsEnabled = tAnalyticServer.getEnabled();
        if (this.isAnalyticsDashboardEnabled()) {
            // Get URL
            if (tAnalyticServer.getDASServerUrl().endsWith("/")) {
                this.analyticsServerURL = tAnalyticServer.getDASServerUrl().substring(0, tAnalyticServer.getDASServerUrl()
                                                                                              .length() - 1);
            } else {
                this.analyticsServerURL = tAnalyticServer.getDASServerUrl();
            }
            // Get Username
            this.analyticsServerUsername = tAnalyticServer.getUsername();
            // Get Password
            if (secretResolver != null && secretResolver.isInitialized()
                && secretResolver.isTokenProtected(BPMNAnalyticsCoreConstants.ANALYTICS_SERVER_PASSWORD_SECRET_ALIAS)) {
                this.analyticsServerPassword = secretResolver.resolve(BPMNAnalyticsCoreConstants
                                                                              .ANALYTICS_SERVER_PASSWORD_SECRET_ALIAS);
                if (log.isDebugEnabled()) {
                    log.debug("Loaded  analytics  password from secure vault");
                }
            } else {
                if (tAnalyticServer.getPassword() != null) {
                    this.analyticsServerPassword = tAnalyticServer.getPassword();
                }
            }
        }
    }

    /**
     * Initialize bpmn analytics configurations
     *
     * @param tbpmn
     */
    private void initBPMNAnalytics(TBPMN tbpmn) {
        // Get Enabled configurations
        this.analyticsDashboardEnabled = tbpmn.getDashboardAnalyticsEnabled();
    }

//    public boolean isAnalyticsEnabled() {
//        return analyticsEnabled;
//    }

    public String getAnalyticsServerURL() {
        return analyticsServerURL;
    }

    public String getAnalyticsServerUsername() {
        return analyticsServerUsername;
    }

    public String getAnalyticsServerPassword() {
        return analyticsServerPassword;
    }

    public boolean isAnalyticsDashboardEnabled() {
        return analyticsDashboardEnabled;
    }

}
