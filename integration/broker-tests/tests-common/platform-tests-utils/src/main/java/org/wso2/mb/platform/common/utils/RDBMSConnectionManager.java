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

package org.wso2.mb.platform.common.utils;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.platform.common.utils.exceptions.DataAccessUtilException;

import javax.xml.xpath.XPathExpressionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is used for creating RDBMS Connection
 */
public class RDBMSConnectionManager {

    /**
     * Get database connection.
     * @return database connection
     * @throws XPathExpressionException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws XPathExpressionException, ClassNotFoundException, SQLException {

        AutomationContext automationContext = new AutomationContext("MB_Cluster", TestUserMode.SUPER_TENANT_ADMIN);

        String url = automationContext.getConfigurationValue("//datasources/datasource[@name=\'mbCluster\']/url");
        String username = automationContext.getConfigurationValue
                ("//datasources/datasource[@name=\'mbCluster\']/username");
        String password = automationContext.getConfigurationValue
                ("//datasources/datasource[@name=\'mbCluster\']/password");
        String driverName = automationContext.getConfigurationValue
                ("//datasources/datasource[@name=\'mbCluster\']/driverClassName");

        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
