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
package org.wso2.ei.dataservice.integration.test.server.mgt;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeSuite;
import org.wso2.carbon.integration.common.tests.ServerStartupBaseTest;

import javax.xml.xpath.XPathExpressionException;

public class DSSServerStartupTestCase extends ServerStartupBaseTest {

    private static final Log log = LogFactory.getLog(DSSServerStartupTestCase.class);

    @BeforeSuite
    @Override
    public void initialize() throws XPathExpressionException, AxisFault {
        log.info("Starting DSSServerStartupTestCase ...");
        super.initialize();
    }

}
