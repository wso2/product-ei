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

package org.wso2.mb.integration.tests.server.mgt;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Map;

/**
 * This class contains the carbon server managing function for the automation framework. Can be used to startup or
 * shutdown the server
 */
public class CarbonTestServerManager extends TestServerManager {
    private String carbonHome;

    /**
     * Initiates a carbon server using an {@link AutomationContext}. This does not starts up the server.
     *
     * @param context The automation context
     * @throws XPathExpressionException
     */
    public CarbonTestServerManager(AutomationContext context) throws XPathExpressionException {
        super(context);
    }

    /**
     * Initiates a carbon server given the {@link AutomationContext}, product zip path and arguments. This does not
     * starts up the server.
     *
     * @param context             The automation context
     * @param carbonZip           The path to the product zip file.
     * @param startupParameterMap Arguments for startup.
     * @throws XPathExpressionException
     */
    public CarbonTestServerManager(AutomationContext context, String carbonZip, Map<String, String> startupParameterMap)
            throws XPathExpressionException {
        super(context, carbonZip, startupParameterMap);
    }

    /**
     * Initiates a carbon server using an {@link AutomationContext} and an offset value.
     *
     * @param context    The automation context.
     * @param portOffset The offset value for the carbon server of product ports.
     * @throws XPathExpressionException
     */
    public CarbonTestServerManager(AutomationContext context, int portOffset) throws XPathExpressionException {
        super(context, portOffset);
    }

    /**
     * Starts up the carbon server.
     *
     * @return The absolute path of which the server pack is located.
     * @throws IOException
     * @throws AutomationFrameworkException
     */
    public String startServer() throws IOException, AutomationFrameworkException, XPathExpressionException {
        carbonHome = super.startServer();
        return carbonHome;
    }

    /**
     * Stops the carbon server.
     *
     * @throws AutomationFrameworkException
     */
    public void stopServer() throws AutomationFrameworkException {
        super.stopServer();
    }

    /**
     * Gets the absolute path of which the carbon server pack is located.
     *
     * @return The path.
     */
    public String getCarbonHome() {
        return carbonHome;
    }
}
