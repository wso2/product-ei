package org.wso2.carbon.esb.hl7.transport.test;

import java.io.File;

import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
//import org.wso2.esb.integration.common.clients.feature.mgt.FeatureManagementAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.carbon.esb.hl7.transport.test.sample.HL7Sender;
import org.wso2.carbon.esb.hl7.transport.test.sample.HL7Server;

/**
 * Need to test once the product is released with the actual feature repo link
 * 
 * @author TOSH
 * 
 */
public class HL7TestCase extends ESBIntegrationTest {
	private ServerConfigurationManager serverConfigurationManager;
//	private FeatureManagementAdminServiceClient featureClient;

	private static String featureRepoLocation = "C:\\Downloads\\p2-repo\\p2-repo"; // fix
																				   // this
	private static String featureRepoName = "carbon 4.1.3 repo";
	private static String hl7Vesrion = "4.1.3";
	private static String hl7FeatureID = "org.wso2.carbon.transports.hl7";

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init();
		serverConfigurationManager = new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		serverConfigurationManager.applyConfiguration(new File(getESBResourceLocation()  +File.separator +"hl7Transport" + File.separator +"axis2.xml"));
		super.init();
		/*featureClient = new FeatureManagementAdminServiceClient(getSessionCookie(), contextUrls.getBackEndUrl());
		featureClient.addRepository(featureRepoLocation, featureRepoName);
		featureClient.installFeature(hl7FeatureID, hl7Vesrion);*/
	}

	@AfterClass(alwaysRun = true)
	public void restoreServerConfiguration() throws Exception {
		try {
			super.cleanup();
		} finally {
			Thread.sleep(3000);
			serverConfigurationManager.restoreToLastConfiguration();
			serverConfigurationManager = null;
		}

	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, description = "testing application ack")
	public void testHLProxyApplicationAck() throws Exception {

		addHL7ApplicationAckProxy();

		HL7Server server = new HL7Server(9988);
		server.start();
		Thread.sleep(2000);

		HL7Sender sender = new HL7Sender();
		String response = sender.send("localhost", 9293);

		Assert.assertTrue(response.contains("error msg"));

		removeProxy("HL7ApplicationAckProxy");
	}

	private void addHL7ApplicationAckProxy() throws Exception {

		addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		                                     + "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"HL7ApplicationAckProxy\" transports=\"hl7\">\n"
		                                     + "                 <target>\n"
		                                     + "                       <inSequence> \n "
		                                     + "							<property name=\"HL7_APPLICATION_ACK\" value=\"true\" scope=\"axis2\"/>\n"
		                                     + "									<send> \n"
		                                     + "										<endpoint name=\"endpoint_urn_uuid_9CB8D06C91A1E996796270828144799-1418795938\">\n"
		                                     + "											<address uri=\"hl7://localhost:9988\"/>\n"
		                                     + "										</endpoint> \n"
		                                     + "									</send> \n"
		                                     + "						</inSequence> \n"
		                                     + "                         <outSequence> \n"
		                                     + "								<property name=\"HL7_RESULT_MODE\" value=\"NACK\" scope=\"axis2\"/> \n"
		                                     + "							<property name=\"HL7_NACK_MESSAGE\" value=\"error msg\" scope=\"axis2\"/> \n"
		                                     + "							<send/> \n"
		                                     + "							</outSequence>\n"
		                                     + "							</target> \n"
		                                     + "							<parameter name=\"transport.hl7.AutoAck\">false</parameter> \n"
		                                     + "							<parameter name=\"transport.hl7.ValidateMessage\">true</parameter> \n"
		                                     +

		                                     "        </proxy>"));
	}

	private void removeProxy(String proxyName) throws Exception {
		deleteProxyService(proxyName);
	}
}
