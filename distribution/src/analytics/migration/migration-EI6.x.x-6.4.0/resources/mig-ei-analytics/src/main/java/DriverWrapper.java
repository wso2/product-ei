/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Dynamically Load Jar in runtime
 */
class DriverWrapper implements Driver {

    private Driver driver;

    DriverWrapper(Driver d) {

        this.driver = d;
    }

    public boolean acceptsURL(String u) throws SQLException {

        return this.driver.acceptsURL(u);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {

        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {

        return 0;
    }

    @Override
    public int getMinorVersion() {

        return 0;
    }

    @Override
    public boolean jdbcCompliant() {

        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {

        return null;
    }

    public Connection connect(String u, Properties p) throws SQLException {

        return this.driver.connect(u, p);
    }
}
