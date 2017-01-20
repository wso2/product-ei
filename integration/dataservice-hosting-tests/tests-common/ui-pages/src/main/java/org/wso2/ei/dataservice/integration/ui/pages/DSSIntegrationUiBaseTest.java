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
package org.wso2.ei.dataservice.integration.ui.pages;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.common.HomePageGenerator;

import javax.xml.xpath.XPathExpressionException;

public abstract class DSSIntegrationUiBaseTest {

    protected AutomationContext automationContext;

    protected void init() throws Exception {
        automationContext = new AutomationContext("DSS", "dss001", TestUserMode.SUPER_TENANT_ADMIN);
    }


    protected String getServiceUrl() throws XPathExpressionException {
        return automationContext.getContextUrls().getServiceUrl();
    }

    protected String getLoginURL() throws XPathExpressionException {
        return HomePageGenerator.getProductHomeURL(automationContext);
    }

}
