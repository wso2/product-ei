package org.wso2.carbon.ei.migration;

public interface MigrationClient {

    void execute() throws MigrationClientException;
}