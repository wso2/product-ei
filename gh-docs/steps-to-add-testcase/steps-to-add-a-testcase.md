# Steps to add a Testcase

## Description

The main purpose of this document is to provide a guidance on how to add a new test case into test suite. The main 
feature that was introduced in order to improve the test suite was to move artifact deployment from the run time to the 
initialization of the test suite. With this approach, each test module will contain a separate directory containing all 
the artifacts to be deployed prior to running the whole test suite.

## Test Modules
Following are the main test modules that can be located in product-ie:

1.  Tests-mediator-1 & Tests-mediator-2
    
    Test cases related to mediators should be added into one of these modules. The module on which the mediator is 
    tested can be identified by the the testng.xml file of each module located at <mediator-1(or 2)>/src/test/resources
    /testng.xml
    
2.  Tests-transport

    Tests cases which are written to test the functionality of a particular transport should be included in this module.
    The server should be restarted only if there are configuration changes which could affect other tests. e.g. Changing
    the default value of maximum open connections in passthrough.properties in the test case
    ‘PttMaximumOpenConnections’. If the tests needs to simply enable a transport, the default axis2.xml file located at
    src/test/resources/artifacts/ESB/server/conf/axis2/axis2.xml should be changed to enable that transport if not 
    already done.
    
3.  Tests-patches

    Test cases should NOT be added into this module in public branch(It is only meant for support branches). Even 
    though the 
    test case is supposed to test a fix that was issued for a patch, the test case should be added to where it belongs 
    to, depending on its functionality. (Please note that the test case for the support branch should be added to the
    tests-patches module, unlike what we are talking here, which is the public branch). The tests which already reside 
    in this module should be relocated into respective modules in a future iteration.

    Note: When a Test case is added into the patches in support branch, Please make sure that test fails when 
    the fix is not applied on the distribution. Once the fix is applied to the distribution, It must pass. Test 
    case must be run with the fix and without the fix to verify the test case.

4.  Tests-sample

    Test cases which are testing a sample should be included ONLY in this module. Please note that loading sample 
    configurations should not happen in other test modules.

5.  Tests-service

    Test cases which are related to services provided by WSO2EI such as car deployment, endpoint/local-entry deployment 
    should be included in here.

6.  Tests-platform

    Tests which require integration with other systems e.g. rabbitMQ, wso2mb, etc should be added here.

7.  Tests-other

    Test which cannot be added into any of the above modules should be included in here. E.g. ‘Log4jLoggingTest’ which 
    does not match any of the above descriptions.

## Directory Structure
```
-- src
    -- test
        -- java
            -- LogMediatorAfterSendMediatorTestCase
        -- resources
            -- artifacts
                -- ESB
                    -- server
                        -- conf
                            -- axis2
                                -- Axis2.xml
                            -- lib
                                --sample-class-mediator.jar
                            -- repository.deployment.server.synapse-configs.default
                                -- proxy-services
                                    -- sendMediatorWithLogAfterwardsTestProxy.xml
                                -- api
                                   -- OrderAPI.xml
                                -- sequences
                                    -- fault.xml
                                    -- xsltMediatorTestInSeq.xml
                                -- endponts
                                    -- StockQuoteEP.xml
                                -- local-entries
                                    -- SymbolLE.xml
                                -- message-processors
                                    -- ForwardProcessor.xml
                                -- templates
                                    --publishEventTemplate.xml
```

## Adding a Test Case
1. A new test case should be added only if it has no associated test cases. If there are such test cases, the test 
should
be written as a separate method for that test case. 
2. A test method (and the test case if one is added), should be 
descriptive of the scenario that is being tested. There should not be any test methods named ‘test1,’testMethod’, etc. 
but a descriptive one e.g. ‘logMediatorAfterSendMediatorTest’. 
3. Test class name should end with 
TestCase(CallMediatorBlockingTrueTestCase). Should contain only letters and numbers. 
4. A test driven development should be followed in implementation. In this, the test cases will be written first to 
fail. Then the implementation (could be a feature, or a fix for an issue, etc. ) should be done and verified by the 
success of tests.

    Follow the below suffix convention for synapse artifacts names when artifacts are added.
- API - API (e.g. OrderAPI, ValidateMediatorJsonAPI)
- Proxy Service - TestProxy(StcokQuoteTestProxy, IterateDifferentIdTestProxy)
- Sequences - Seq(ValidateMessageSeq, )
- Endpoints - EP(StockQuoteEP)
- Local Entries - LE(SymbolLE)
- Message Processors - Processor (OrderMsgForwardProcessor)
- Templates - Template(PublishEventTemplate)
- Message Store - Store(OrderMessageStore)
	
A test comprises the following:
- Synapse artifacts to be deployed. E.g. proxy services, sequences, etc.
- Libraries to be added. E.g. activemq-client-5.9.1.jar, SimpleClassMediator-1.0.0.jar, etc.
- Changes in configuration files. E. g. Axis2.xml, log4j.properties file, etc.

All these artifacts should be copied into src/test/resources/artifacts/ESB/server to be in accordance with the directory
structure mentioned in [Directory Structure](#directory-structure).

However if a test is about a deployment of a synapse config or if it cannot be deployed at the startup the the test 
suite for some reason they can be added at runtime but the names used for the sequences, proxy services, apis, etc. 
cannot conflict with others and has to be descriptive of the test for which it is being used for.

All dependency jars should be added to src/test/resources/artifacts/ESB/server/lib or dropins depending on if it is a 
bundle or not.

If a specific transport needs to be enabled for a test case, it can be enabled at the axis2.xml that is placed inside 
src/test/resources/artifacts/ESB/server/conf/axis2/. That way we will not need to restart the server for the execution 
of the test case. However, if there are configurations that should not be used with other test cases, they can be 
separately added.









