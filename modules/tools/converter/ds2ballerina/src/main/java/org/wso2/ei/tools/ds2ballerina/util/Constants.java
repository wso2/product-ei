package org.wso2.ei.tools.ds2ballerina.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the Constants.
 */
public class Constants {

    static final Map<String, String> TOMCAT_JDBC_TO_HIKARI = new HashMap<>();

    static {
        TOMCAT_JDBC_TO_HIKARI.put(RDBMS.URL, HIKARI.JDBC_URL);
    }

    /**
     * Constants related to RDBMS data source, DSS v3.0+.
     */
    public static final class RDBMS {

        private RDBMS() {
            throw new AssertionError();
        }

        public static final String DEFAULT_AUTOCOMMIT = "defaultAutoCommit";
        public static final String DEFAULT_READONLY = "defaultReadOnly";
        public static final String DEFAULT_TX_ISOLATION = "defaultTransactionIsolation";
        public static final String DEFAULT_CATALOG = "defaultCatalog";
        public static final String DRIVER_CLASSNAME = "driverClassName";
        public static final String URL = "url";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String MAX_ACTIVE = "maxActive";
        public static final String MAX_IDLE = "maxIdle";
        public static final String MIN_IDLE = "minIdle";
        public static final String INITIAL_SIZE = "initialSize";
        public static final String MAX_WAIT = "maxWait";
        public static final String TEST_ON_BORROW = "testOnBorrow";
        public static final String TEST_ON_RETURN = "testOnReturn";
        public static final String TEST_WHILE_IDLE = "testWhileIdle";
        public static final String VALIDATION_QUERY = "validationQuery";
        public static final String VALIDATOR_CLASSNAME = "validatorClassName";
        public static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
        public static final String NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";
        public static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
        public static final String REMOVE_ABANDONED = "removeAbandoned";
        public static final String REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
        public static final String LOG_ABANDONED = "logAbandoned";
        public static final String CONNECTION_PROPERTIES = "connectionProperties";
        public static final String INIT_SQL = "initSQL";
        public static final String JDBC_INTERCEPTORS = "jdbcInterceptors";
        public static final String VALIDATION_INTERVAL = "validationInterval";
        public static final String JMX_ENABLED = "jmxEnabled";
        public static final String FAIR_QUEUE = "fairQueue";
        public static final String ABANDON_WHEN_PERCENTAGE_FULL = "abandonWhenPercentageFull";
        public static final String MAX_AGE = "maxAge";
        public static final String USE_EQUALS = "useEquals";
        public static final String SUSPECT_TIMEOUT = "suspectTimeout";
        public static final String VALIDATION_QUERY_TIMEOUT = "validationQueryTimeout";
        public static final String ALTERNATE_USERNAME_ALLOWED = "alternateUsernameAllowed";
        public static final String DATASOURCE_CLASSNAME = "dataSourceClassName";
        public static final String DATASOURCE_PROPS = "dataSourceProps";
        public static final String FORCE_STORED_PROC = "forceStoredProc";
        public static final String FORCE_JDBC_BATCH_REQUESTS = "forceJDBCBatchRequests";
        public static final String QUERY_TIMEOUT = "queryTimeout";
        public static final String AUTO_COMMIT = "autoCommit";
        public static final String FETCH_DIRECTION = "fetchDirection";
        public static final String FETCH_SIZE = "fetchSize";
        public static final String MAX_FIELD_SIZE = "maxFieldSize";
        public static final String MAX_ROWS = "maxRows";
        public static final String DYNAMIC_USER_AUTH_CLASS = "dynamicUserAuthClass";
        public static final String DYNAMIC_USER_AUTH_MAPPING = "dynamicUserAuthMapping";
        public static final String USERNAME_WILDCARD = "*";
        public static final String DSS_TIMERZONE = "dss.timezone";
        public static final String DSS_LEGACY_TIMEZONE_MODE = "dss.legacy.timezone.mode";
        public static final String TIMEZONE_UTC = "UTC";
    }

    /**
     * Constants related to RDBMS data source, DSS v3.0+.
     */
    public static final class HIKARI {

        private HIKARI() {
            throw new AssertionError();
        }

        public static final String DEFAULT_AUTOCOMMIT = "defaultAutoCommit";
        public static final String DEFAULT_READONLY = "defaultReadOnly";
        public static final String DEFAULT_TX_ISOLATION = "defaultTransactionIsolation";
        public static final String DEFAULT_CATALOG = "defaultCatalog";
        public static final String DRIVER_CLASSNAME = "driverClassName";
        public static final String JDBC_URL = "jdbcUrl";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String MAX_ACTIVE = "maxActive";
        public static final String MAX_IDLE = "maxIdle";
        public static final String MIN_IDLE = "minIdle";
        public static final String INITIAL_SIZE = "initialSize";
        public static final String MAX_WAIT = "maxWait";
        public static final String TEST_ON_BORROW = "testOnBorrow";
        public static final String TEST_ON_RETURN = "testOnReturn";
        public static final String TEST_WHILE_IDLE = "testWhileIdle";
        public static final String VALIDATION_QUERY = "validationQuery";
        public static final String VALIDATOR_CLASSNAME = "validatorClassName";
        public static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
        public static final String NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";
        public static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
        public static final String REMOVE_ABANDONED = "removeAbandoned";
        public static final String REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
        public static final String LOG_ABANDONED = "logAbandoned";
        public static final String CONNECTION_PROPERTIES = "connectionProperties";
        public static final String INIT_SQL = "initSQL";
        public static final String JDBC_INTERCEPTORS = "jdbcInterceptors";
        public static final String VALIDATION_INTERVAL = "validationInterval";
        public static final String JMX_ENABLED = "jmxEnabled";
        public static final String FAIR_QUEUE = "fairQueue";
        public static final String ABANDON_WHEN_PERCENTAGE_FULL = "abandonWhenPercentageFull";
        public static final String MAX_AGE = "maxAge";
        public static final String USE_EQUALS = "useEquals";
        public static final String SUSPECT_TIMEOUT = "suspectTimeout";
        public static final String VALIDATION_QUERY_TIMEOUT = "validationQueryTimeout";
        public static final String ALTERNATE_USERNAME_ALLOWED = "alternateUsernameAllowed";
        public static final String DATASOURCE_CLASSNAME = "dataSourceClassName";
        public static final String DATASOURCE_PROPS = "dataSourceProps";
        public static final String FORCE_STORED_PROC = "forceStoredProc";
        public static final String FORCE_JDBC_BATCH_REQUESTS = "forceJDBCBatchRequests";
        public static final String QUERY_TIMEOUT = "queryTimeout";
        public static final String AUTO_COMMIT = "autoCommit";
        public static final String FETCH_DIRECTION = "fetchDirection";
        public static final String FETCH_SIZE = "fetchSize";
        public static final String MAX_FIELD_SIZE = "maxFieldSize";
        public static final String MAX_ROWS = "maxRows";
        public static final String DYNAMIC_USER_AUTH_CLASS = "dynamicUserAuthClass";
        public static final String DYNAMIC_USER_AUTH_MAPPING = "dynamicUserAuthMapping";
        public static final String USERNAME_WILDCARD = "*";
        public static final String DSS_TIMERZONE = "dss.timezone";
        public static final String DSS_LEGACY_TIMEZONE_MODE = "dss.legacy.timezone.mode";
        public static final String TIMEZONE_UTC = "UTC";
    }

}
