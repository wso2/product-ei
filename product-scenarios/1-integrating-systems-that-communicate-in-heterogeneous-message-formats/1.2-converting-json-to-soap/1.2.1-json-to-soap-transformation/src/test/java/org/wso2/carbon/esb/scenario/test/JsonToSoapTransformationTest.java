package org.wso2.carbon.esb.scenario.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;

import java.io.File;

public class JsonToSoapTransformationTest extends ScenarioTestBase {

    private static final Log log = LogFactory.getLog(JsonToSoapTransformationTest.class);

    private String proxyName;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        //Deploy artifact
        proxyName = addProxyService(esbUtils.loadResource("artifacts" + File.separator +
                "ESB/synapse-configs/proxy-services/testProxy.xml"));
    }

    @Test(description = "1.3.1.1", enabled = true)
    public void testMessageTransformation() throws Exception {
        log.info("********************************************");
    }

    @AfterClass(description = "Server Cleanup")
    public void cleanup() throws Exception {
        //Cleanup artifacts
        deleteProxyService(proxyName);
    }
}
