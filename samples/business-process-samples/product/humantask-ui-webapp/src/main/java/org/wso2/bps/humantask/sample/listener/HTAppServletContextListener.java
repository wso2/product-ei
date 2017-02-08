/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.bps.humantask.sample.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.bps.humantask.sample.util.HumanTaskSampleConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Properties;

public class HTAppServletContextListener implements ServletContextListener {

    private static Log log = LogFactory.getLog(HTAppServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Properties properties = new Properties();
        // load configuration properties at context initialization
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(
                    "config.properties"));
            //initialize the required parameters
            ServletContext sc = servletContextEvent.getServletContext();
            sc.setInitParameter(HumanTaskSampleConstants.BACKEND_SERVER_URL, properties.get(HumanTaskSampleConstants
                                                                                                    .BACKEND_SERVER_URL).toString());
            sc.setInitParameter(HumanTaskSampleConstants.CLIENT_TRUST_STORE_PATH, properties.getProperty
                    (HumanTaskSampleConstants.CLIENT_TRUST_STORE_PATH));
            sc.setInitParameter(HumanTaskSampleConstants.CLIENT_TRUST_STORE_PASSWORD, properties.getProperty
                    (HumanTaskSampleConstants.CLIENT_TRUST_STORE_PASSWORD));
            sc.setInitParameter(HumanTaskSampleConstants.CLIENT_TRUST_STORE_TYPE, properties.getProperty
                    (HumanTaskSampleConstants.CLIENT_TRUST_STORE_TYPE));

        } catch (IOException e) {
            String errMsg = "Couldn't load properties from config file";
            log.error(errMsg, e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
