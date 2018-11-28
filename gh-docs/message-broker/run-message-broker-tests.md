# How to run Message Broker Integration Tests

### Integration tests

Integration tests of the Message Broker are disabled by default. You can run the Message Broker test profile using the
following command.

`mvn clean install -Pbroker-tests-profile`

### Cluster Tests

Cluster tests are disabled by default in the Message Broker Test profile. To run cluster tests, setup a Message Broker
cluster and enable the cluster tests module by following these steps.

* Go to `<repo-home>/integration/broker-tests/pom.xml` and enable the `tests-platform` module. You can disable
`tests-integration` and `tests-ui-integration` but make sure to keep the common test modules **enabled** as shown below.

    Example:
    ```
     <modules>
            <module>tests-common/admin-clients</module>
            <module>tests-common/integration-tests-utils</module>
            <module>tests-common/platform-tests-utils</module>
            <!--<module>tests-integration</module>-->
            <!--<module>tests-ui-integration</module>-->
            <module>tests-platform</module>
     </modules>    
    ```
* Run the Message Broker test profile as shown in the previous section.