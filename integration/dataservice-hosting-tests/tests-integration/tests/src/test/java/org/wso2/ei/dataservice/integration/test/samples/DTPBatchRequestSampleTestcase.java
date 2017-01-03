package org.wso2.ei.dataservice.integration.test.samples;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.dataservices.samples.dtp_sample.DTPSampleServiceStub;
import org.wso2.carbon.dataservices.samples.dtp_sample.DataServiceFault;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;
import org.wso2.ws.dataservice.samples.dtp_sample.Entry;

/* 
 * This test case performs a Distributed transaction with a batch request and non batch request.
 * Related to the fix for https://wso2.org/jira/browse/DS-846
 * */
public class DTPBatchRequestSampleTestcase extends DSSIntegrationTest {

    private final OMFactory fac = OMAbstractFactory.getOMFactory();
    private final OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/dtp_sample", "ns1");
	private final String serviceName = "DTPBatchRequestService";
    private static final Log log = LogFactory.getLog(DTPBatchRequestSampleTestcase.class);
    private String serverEpr;

    @Factory(dataProvider = "userModeDataProvider")
    public DTPBatchRequestSampleTestcase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(userMode);
        serverEpr = getServiceUrlHttp(serviceName);
        String resourceFileLocation = getResourceLocation();
        deployService(serviceName,
                      new DataHandler(new URL("file:///" + resourceFileLocation +
                                              File.separator + "samples" + File.separator +
                                              "dbs" + File.separator + "rdbms" + File.separator +
                                              "DTPBatchRequestService.dbs")));
        log.info(serviceName + " uploaded");
    }

    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        deleteService(serviceName);
        cleanup();
    }


    @Test(groups = "wso2.dss", description = "Check whether fault service deployed or not")
    public void testServiceDeployment() throws Exception {
        assertTrue(isServiceDeployed(serviceName));
        log.info(serviceName + " is deployed");
    }

    @Test(groups = {"wso2.dss"}, dependsOnMethods = "testServiceDeployment")
    public void testDTPWithBatchRequests() throws DataServiceFault, RemoteException {
        DTPSampleServiceStub stub = new DTPSampleServiceStub(serverEpr);
        stub._getServiceClient().getOptions().setManageSession(true);
        stub._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);

        //Add two accounts to Bank 1
        Entry[] entry1_1 = stub.addAccountToBank1(1000.00);
        Entry[] entry1_2 = stub.addAccountToBank1(1000.00);
        //Add an account to Bank 2
        Entry[] entry2 = stub.addAccountToBank2(2000.00);

        ServiceClient sender = new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(serverEpr));
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        options.setManageSession(true);
        sender.setOptions(options);
        
        options.setAction("urn:" + "begin_boxcar");
        sender.setOptions(options);
        sender.sendRobust(begin_boxcar());
        

        //Send a batch request to Bank 1 to update two accounts
        OMElement payload = createBatchRequestPayload(entry1_1[0].getID().intValue(), entry1_2[0].getID().intValue());
        options.setAction("urn:" + "addToAccountBalanceInBank1_batch_req");
        sender.setOptions(options);
        sender.sendRobust(payload);
                 

        //this line will cases dss fault due to service input parameter validation
        payload = createAccountUpdatePayload(entry2[0].getID().intValue());
        options.setAction("urn:" + "addToAccountBalanceInBank2");
        sender.setOptions(options);
        sender.sendRobust(payload);

        try {
        	options.setAction("urn:" + "end_boxcar");
            sender.setOptions(options);
        	sender.sendRobust(end_boxcar());
        } catch (AxisFault dssFault) {
            log.error("DSS fault ignored");
        }

        assertEquals(stub.getAccountBalanceFromBank1(entry1_1[0].getID().intValue()), 1000.00);
        assertEquals(stub.getAccountBalanceFromBank1(entry1_2[0].getID().intValue()), 1000.00);
        assertEquals(stub.getAccountBalanceFromBank2(entry2[0].getID().intValue()), 2000.00);
    }

	private OMElement createBatchRequestPayload(int intValue, int intValue2) {
		OMElement payload = fac.createOMElement("addToAccountBalanceInBank1_batch_req", omNs);
		
		OMElement addToAccount1 = fac.createOMElement("addToAccountBalanceInBank1", omNs);
		OMElement accountId1 = fac.createOMElement("accountId", omNs);
		OMElement value1 = fac.createOMElement("value", omNs);
		addToAccount1.addChild(accountId1);
		addToAccount1.addChild(value1);
		accountId1.setText(""+intValue);
		value1.setText("-100");
		
		OMElement addToAccount2 = fac.createOMElement("addToAccountBalanceInBank1", omNs);
		OMElement accountId2 = fac.createOMElement("accountId", omNs);
		OMElement value2 = fac.createOMElement("value", omNs);
		addToAccount2.addChild(accountId2);
		addToAccount2.addChild(value2);
		accountId2.setText(""+intValue2);
		value2.setText("-100");
		
		payload.addChild(addToAccount1);
		payload.addChild(addToAccount2);
		
		return payload;
	}
	
	private OMElement createAccountUpdatePayload(int intValue) {
		OMElement addToAccount = fac.createOMElement("addToAccountBalanceInBank2", omNs);
		OMElement accountId1 = fac.createOMElement("accountId", omNs);
		OMElement value1 = fac.createOMElement("value", omNs);
		addToAccount.addChild(accountId1);
		addToAccount.addChild(value1);
		accountId1.setText(""+intValue);
		value1.setText("h100");
		return addToAccount;
	}
	
	private OMElement begin_boxcar() {
        return fac.createOMElement("begin_boxcar", omNs);

    }

    private OMElement end_boxcar() {
        return fac.createOMElement("end_boxcar", omNs);

    }
}
