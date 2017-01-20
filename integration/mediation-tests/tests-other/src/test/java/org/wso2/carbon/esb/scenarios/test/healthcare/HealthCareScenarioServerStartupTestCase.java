package org.wso2.carbon.esb.scenarios.test.healthcare;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.aarservices.stub.ExceptionException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class HealthCareScenarioServerStartupTestCase {
    private SampleAxis2Server axis2Server1 = null;
    private String[] serviceNames = {"geows", "hcfacilitylocator", "hcinformationservice"};

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeTest(alwaysRun = true)
    public void deployServices()
            throws IOException, LoginAuthenticationExceptionException, ExceptionException {

        axis2Server1 = new SampleAxis2Server("test_axis2_server_9009.xml");
        axis2Server1.start();
        axis2Server1.deployService(serviceNames[0]);
        axis2Server1.deployService(serviceNames[1]);
        axis2Server1.deployService(serviceNames[2]);


    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterTest(alwaysRun = true)
    public void unDeployServices()
            throws MalformedURLException, LoginAuthenticationExceptionException, ExceptionException,
                   RemoteException {
        if (axis2Server1 != null && axis2Server1.isStarted()) {
            axis2Server1.stop();
        }

    }
}
