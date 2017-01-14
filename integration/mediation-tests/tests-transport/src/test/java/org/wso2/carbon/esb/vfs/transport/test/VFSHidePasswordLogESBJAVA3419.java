package org.wso2.carbon.esb.vfs.transport.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestConstant;
import org.wso2.carbon.utils.CarbonUtils;

/**
 * This test class related to - https://wso2.org/jira/browse/ESBJAVA-3419
 * This class test whether the password is printed in the log while
 * exception happens in vfs.
 */

public class VFSHidePasswordLogESBJAVA3419 extends ESBIntegrationTest {

	private ServerConfigurationManager serverConfigurationManager;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init();

		serverConfigurationManager = new ServerConfigurationManager(
				new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		serverConfigurationManager.applyConfiguration(new File(getClass()
				.getResource(
						File.separator + "artifacts" + File.separator + "ESB"
								+ File.separator + "synapseconfig"
								+ File.separator + "vfsTransport"
								+ File.separator + "axis2.xml").getPath()));
		super.init();
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
	@Test(groups = { "wso2.esb" }, description = "Checking VFSTransportListener not logs the clear password on error")
	public void testVFSListenerHidePasswordInLog() throws Exception {

		addProxyService(AXIOMUtil
				.stringToOM("<proxy xmlns=\"http://ws.apache.org/ns/synapse\"\n"
						+ "       name=\"HidePasswordListenerProxy\"\n"
						+ "       transports=\"vfs\"\n"
						+ "       statistics=\"disable\"\n"
						+ "       trace=\"disable\"\n"
						+ "       startOnLoad=\"true\">\n"
						+ "   <target>\n"
						+ "      <outSequence>\n"
						+ "         <property name=\"transport.vfs.ReplyFileName\"\n"
						+ "                   expression=\"fn:concat(fn:substring-after(get-property('MessageID'), 'urn:uuid:'), '.xml')\"\n"
						+ "                   scope=\"transport\"/>\n"
						+ "         <property name=\"OUT_ONLY\" value=\"true\"/>\n"
						+ "         <send>\n"
						+ "            <endpoint>\n"
						+ "               <address uri=\"vfs:smb://username:ClearPassword@host/test/out\"/>\n"
						+ "            </endpoint>\n"
						+ "         </send>\n"
						+ "      </outSequence>\n"
						+ "      <endpoint>\n"
						+ "         <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"\n"
						+ "                  format=\"soap12\"/>\n"
						+ "      </endpoint>\n"
						+ "   </target>\n"
						+ "   <publishWSDL uri=\"file:repository/samples/resources/proxy/sample_proxy_1.wsdl\"/>\n"
						+ "   <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n"
						+ "   <parameter name=\"transport.PollInterval\">1</parameter>\n"
						+ "   <parameter name=\"transport.vfs.MoveAfterProcess\">vfs:smb://username:ClearPassword@host/test/original</parameter>\n"
						+ "   <parameter name=\"transport.vfs.FileURI\">vfs:smb://username:ClearPassword@host/test/out</parameter>\n"
						+ "   <parameter name=\"transport.vfs.MoveAfterFailure\">vfs:smb://username:ClearPassword@host/test/original</parameter>\n"
						+ "   <parameter name=\"transport.vfs.FileNamePattern\">.*\\.text</parameter>\n"
						+ "   <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n"
						+ "   <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>\n"
						+ "   <parameter name=\"ScenarioID\">scenario1</parameter>\n"
						+ "   <description/>\n" + "</proxy>"));

		Thread.sleep(3000);

		Assert.assertFalse(isClearPassword(),
				" The password is getting printed in the log in the VFSTransportListener.");

	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.STANDALONE })
	@Test(groups = { "wso2.esb" }, description = "Checking VFSTransportSender not logs the clear password on error")
	public void testVFSSenderHidePasswordInLog() throws Exception {

		addProxyService(AXIOMUtil
				.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "    <proxy name=\"HidePasswordSenderProxy\"\n"
						+ "           xmlns=\"http://ws.apache.org/ns/synapse\""
						+ "           transports=\"https http\"\n"
						+ "           startOnLoad=\"true\"\n"
						+ "           trace=\"disable\">\n"
						+ "        <target>\n"
						+ "            <inSequence>\n"
						+ "                <header name=\"To\" value=\"vfs:smb://username:ClearPassword@host/test/out\"/>"
						+ "                <property name=\"OUT_ONLY\" value=\"true\"/>\n"
						+ "                <property name=\"FORCE_SC_ACCEPTED\" value=\"true\" scope=\"axis2\"/>\n"
						+ "                <send>\n"
						+ "                    <endpoint>\n"
						+ "                        <default trace=\"disable\" format=\"pox\">\n"
						+ "                            <timeout>\n"
						+ "                                <duration>1000</duration>\n"
						+ "                                <responseAction>discard</responseAction>\n"
						+ "                            </timeout>\n"
						+ "                            <suspendOnFailure>\n"
						+ "                                <initialDuration>0</initialDuration>\n"
						+ "                                <progressionFactor>1.0</progressionFactor>\n"
						+ "                                <maximumDuration>0</maximumDuration>\n"
						+ "                            </suspendOnFailure>\n"
						+ "                        </default>\n"
						+ "                    </endpoint>\n"
						+ "                </send>\n"
						+ "            </inSequence>\n"
						+ "            <outSequence>\n"
						+ "                <drop/>\n"
						+ "            </outSequence>\n"
						+ "            <faultSequence/>\n"
						+ "        </target>\n" + "    </proxy>"));

		try {
			OMElement response = axis2Client
					.sendSimpleStockQuoteRequest(
							getProxyServiceURLHttp("HidePasswordSenderProxy"),
							getBackEndServiceUrl(ESBTestConstant.SIMPLE_STOCK_QUOTE_SERVICE),
							"WSO2");
		} catch (AxisFault e) {
		}


		Assert.assertFalse(isClearPassword(),
				" The password is getting printed in the log VFSTransportSender.");
	}

	
	/**
	 * The wso2carbon.log is used here, coz the LogViewerClient stack 
	 * is not logs this exception.
	 * @return true if the password printed in the log, else false.
	 */
	private boolean isClearPassword() {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					CarbonUtils.getCarbonHome() 
							+ File.separator + "repository"
							+ File.separator + "logs" + File.separator
							+ "wso2carbon.log"));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.contains("ClearPassword")) {
					return true;
				}
			}

		} catch (IOException e) {
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
			}
		}
		
		return false;
	}
}
