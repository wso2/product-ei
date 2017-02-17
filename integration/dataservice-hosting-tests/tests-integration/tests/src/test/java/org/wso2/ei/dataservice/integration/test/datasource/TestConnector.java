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

package org.wso2.ei.dataservice.integration.test.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestConnector {
    public static void main(String[] args) {
        String username = "zaqxsw987593";
        String password = "oijoacxe42";
        String driverName = "org.wso2.carbon.dataservices.sql.driver.TDriver";
        String url = "jdbc:wso2:gspread:filePath=https://spreadsheets.google.com/ccc?key=0Av5bU8aVtFjPdElrUVN3VmZlRkoyM1ZzVlE1MzdtbXc;sheetName=CustomerRecords;visibility=private";

        try {
            Class.forName(driverName);
            Connection con =  DriverManager.getConnection(url, username, password);
            PreparedStatement stmt = con.prepareStatement("CREATE SHEET testSheet (Ikkk)");
            stmt.execute();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();  
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
