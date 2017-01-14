package org.wso2.carbon.esb.jms.transport.test;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;

import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.admin.client.CarbonAppUploaderClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.service.mgt.ServiceAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

/**
 * Related to https://wso2.org/jira/browse/ESBJAVA-3341
 * This class tests whether faulty proxy service undeployment 
 * happens after adding the correct car file.
 *
 */

public class JMSCAppDeploymentWithFaultyProxyTestCase extends
		ESBIntegrationTest {

	private CarbonAppUploaderClient carbonAppUploaderClient;
	private ApplicationAdminClient applicationAdminClient;
	private final int MAX_TIME = 120000;
	private final String carFileName = "cApp_faulty_jms_1.0.0";
	private boolean isCarFileUploaded = false;
	private ServiceAdminClient serviceAdminClient;
	private final String proxyServiceName = "JMSProxyWQConf";

	@BeforeClass(alwaysRun = true)
	protected void uploadCarFileTest() throws Exception {
		
		super.init();

		carbonAppUploaderClient = new CarbonAppUploaderClient(
				contextUrls.getBackEndUrl(),getSessionCookie());

		applicationAdminClient = new ApplicationAdminClient(
				contextUrls.getBackEndUrl(),getSessionCookie());
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanupArtifactsIfExist() throws Exception {
		if (isCarFileUploaded) {
			applicationAdminClient.deleteApplication(carFileName);
			verifyUndeployment();
			super.cleanup();
		}
	}

	@Test(groups = { "wso2.esb" }, description = "faulty proxy service deployment from car file")
	public void proxyServiceDeploymentTest() throws Exception {
		carbonAppUploaderClient.uploadCarbonAppArtifact(
				"cApp_faulty_jms_1.0.0.car", new DataHandler(new URL("file:"
						+ File.separator + File.separator
						+ getESBResourceLocation() + File.separator + "car"
						+ File.separator + "cApp_faulty_jms_1.0.0.car")));

		Assert.assertTrue((isCarFileUploaded = isCarFileDeployed(carFileName)),
				"Car file deployment failed");
		TimeUnit.SECONDS.sleep(5);
		serviceAdminClient = new ServiceAdminClient(contextUrls.getBackEndUrl(),getSessionCookie());
		Assert.assertTrue(serviceAdminClient.isServiceFaulty(proxyServiceName),
				"faulty proxy service deployment failed");

	}

	@Test(groups = { "wso2.esb" }, dependsOnMethods = "proxyServiceDeploymentTest", description = "faulty proxy service invocation")
	public void deleteCarFileAndArtifactUnDeploymentTest() throws Exception {
		applicationAdminClient.deleteApplication(carFileName);
		isCarFileUploaded = false;
		Assert.assertTrue(isCarFileUnDeployed(carFileName));

		TimeUnit.SECONDS.sleep(5);
		// verify whether artifacts are undeployed successfully
		verifyUndeployment();

	}

	/**
	 * @param carFileName - Name of the car file to deploy
	 * @return true if the car file deployed successfully else, false
	 */
	private boolean isCarFileDeployed(String carFileName) throws Exception {

		log.info("waiting " + MAX_TIME + " millis for car deployment "
				+ carFileName);
		boolean isCarFileDeployed = false;
		Calendar startTime = Calendar.getInstance();
		long time;
		while ((time = (Calendar.getInstance().getTimeInMillis() - startTime
				.getTimeInMillis())) < MAX_TIME) {
			String[] applicationList = applicationAdminClient
					.listAllApplications();
			if (applicationList != null) {
				if (ArrayUtils.contains(applicationList, carFileName)) {
					isCarFileDeployed = true;
					log.info("car file deployed in " + time + " mills");
					return isCarFileDeployed;
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}

		}
		return isCarFileDeployed;
	}

	/**
	 * @param carFileName - Name of the car file to undeploy
	 * @return true if the car file undeployed successfully else, false
	 */
	private boolean isCarFileUnDeployed(String carFileName) throws Exception {

		log.info("waiting " + MAX_TIME + " millis for car undeployment "
				+ carFileName);
		boolean isCarFileUnDeployed = false;
		Calendar startTime = Calendar.getInstance();
		long time;
		while ((time = (Calendar.getInstance().getTimeInMillis() - startTime
				.getTimeInMillis())) < MAX_TIME) {
			String[] applicationList = applicationAdminClient
					.listAllApplications();
			if (applicationList != null) {
				if (!ArrayUtils.contains(applicationList, carFileName)) {
					isCarFileUnDeployed = true;
					log.info("car file deployed in " + time + " mills");
					return isCarFileUnDeployed;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			} else {
				isCarFileUnDeployed = true;
				log.info("car file deployed in " + time + " mills");
				return isCarFileUnDeployed;
			}

		}
		return isCarFileUnDeployed;
	}

	/**
	 * to check the faulty proxy get undeployed
	 */
	private void verifyUndeployment() throws Exception {
		serviceAdminClient = new ServiceAdminClient(contextUrls.getBackEndUrl(),getSessionCookie());
		Assert.assertFalse(
				serviceAdminClient.isServiceFaulty(proxyServiceName),
				"faulty proxy service undeployment failed");

	}
}