package org.wso2.carbon.ei.migration;

public class MigrationClientException extends Exception {
    public MigrationClientException(String message) {
        super(message);
    }

    public MigrationClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
