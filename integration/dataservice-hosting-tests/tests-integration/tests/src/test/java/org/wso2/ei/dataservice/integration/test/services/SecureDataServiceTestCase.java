/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.ei.dataservice.integration.test.services;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.SecurityAdminServiceClient;
import org.wso2.carbon.integration.common.utils.clients.SecureAxisServiceClient;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.ei.dataservice.integration.common.utils.DSSTestCaseUtils;
import org.wso2.ei.dataservice.integration.test.DSSIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;


public class SecureDataServiceTestCase extends DSSIntegrationTest {

    private static final Log log = LogFactory.getLog(SecureDataServiceTestCase.class);
    private final String serviceName = "SecureDataService";

    @BeforeClass(alwaysRun = true)
    public void serviceDeployment() throws Exception {
        DSSTestCaseUtils dssTestCaseUtils = new DSSTestCaseUtils();
        super.init();
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(new File((getResourceLocation()+ File.separator + "sql" + File.separator
                                 + "MySql" + File.separator + "CreateTables.sql")));
        sqlFileLis.add(new File((getResourceLocation() + File.separator + "sql" + File.separator
                                 + "MySql" + File.separator + "Offices.sql")));

        deployService(serviceName,
                      createArtifact(getResourceLocation() + File.separator + "dbs" + File.separator
                                     + "rdbms" + File.separator + "MySql" + File.separator
                                     + "SecureDataService.dbs", sqlFileLis));


        assertTrue(dssTestCaseUtils.isServiceDeployed(dssContext.getContextUrls().getBackEndUrl(),
                                                      sessionCookie, serviceName));
        log.info(serviceName + " is deployed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        deleteService(serviceName);
        cleanup();
    }


    @Test(groups = {"wso2.dss"}, description = "Provides Authentication. Clients have Username Tokens", enabled = false)
    public void securityPolicy1() throws Exception {
        final int policyId = 1;

        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttps(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("UsernameToken verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy2() throws Exception {
        final int policyId = 2;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("Non-repudiation verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy3() throws Exception {
        final int policyId = 3;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("Integrity verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy4() throws Exception {
        final int policyId = 4;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("Confidentiality verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy5() throws Exception {
        final int policyId = 5;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("Sign and encrypt - X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy6() throws Exception {
        final int policyId = 6;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("Sign and Encrypt - Anonymous clients verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy7() throws Exception {
        final int policyId = 7;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices", getPayload(),
                                                           policyId);
            verifyResponce(response);
        }
        log.info("Encrypt only - Username Token Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy8() throws Exception {
        final int policyId = 8;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("Sign and Encrypt - Username Token Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy9() throws Exception {
        final int policyId = 9;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Sign only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy10() throws Exception {
        final int policyId = 10;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Encrypt only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy11() throws Exception {
        final int policyId = 11;
        this.secureService(policyId);
        Thread.sleep(5000);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - Sign and Encrypt , X509 Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy12() throws Exception {
        final int policyId = 12;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Sign Only - Service as STS - Bootstrap policy - Sign and Encrypt ," +
                 " Anonymous clients verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy13() throws Exception {
        final int policyId = 13;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Anonymous clients verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy14() throws Exception {
        final int policyId = 14;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                                                           getServiceUrlHttp(serviceName), "showAllOffices",
                                                           getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Encrypt Only - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Username Token Authentication verified");
    }

    @Test(groups = {"wso2.dss"}, enabled = false)
    public void securityPolicy15() throws Exception {
        final int policyId = 15;
        this.secureService(policyId);
        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
        OMElement response;
        for (int i = 0; i < 5; i++) {
            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(),
                                                           userInfo.getPassword(), getServiceUrlHttp(serviceName),
                                                           "showAllOffices", getPayload(), policyId);
            verifyResponce(response);
        }
        log.info("SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - " +
                 "Sign and Encrypt , Username Token Authentication verified");
    }

//@Test(dependsOnMethods = {"uploadArtifactTest"})
//    public void securityPolicy16() {
//        this.secureService(16);
//        SecureAxisServiceClient secureAxisServiceClient = new SecureAxisServiceClient();
//        serviceEndPoint = DataServiceUtility.getServiceEndpointHttp(sessionCookie, dssBackEndUrl, serviceName);
//        OMElement response;
//        for (int i = 0; i < 5; i++) {
//            response = secureAxisServiceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(), serviceEndPoint, "showAllOffices", getPayload(), 16);
//            Assert.assertTrue("Expected Result not Found", (response.toString().indexOf("<Office>") > 1));
//            Assert.assertTrue("Expected Result not Found", (response.toString().indexOf("</Office>") > 1));
//        }
//    log.info("Kerberos Authentication - Sign - Sign based on a Kerberos Token verified");
//    }


    private void secureService(int policyId)
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
                   InterruptedException, XPathExpressionException {
        SecurityAdminServiceClient securityAdminServiceClient = new SecurityAdminServiceClient(dssContext.getContextUrls().getBackEndUrl(),sessionCookie);
        if (TestConfigurationProvider.isPlatform()) {
            //todo
            /*securityAdminServiceClient.applySecurity(serviceName, policyId + "", new String[]{"admin"},
                                                     new String[]{userInfo.getDomain().replace('.', '-') + ".jks"},
                                                     userInfo.getDomain().replace('.', '-') + ".jks");*/
        } else {
            securityAdminServiceClient.applySecurity(serviceName, policyId + "", new String[]{"admin"},
                                                     new String[]{"wso2carbon.jks"}, "wso2carbon.jks");
        }
        log.info("Security Scenario " + policyId + " Applied");

        Thread.sleep(1000);

    }

    private void verifyResponce(OMElement response) {
        Assert.assertTrue(response.toString().contains("<Office>"), "Expected Result not Found");
        Assert.assertTrue(response.toString().contains("<officeCode>"), "Expected Result not Found");
        Assert.assertTrue(response.toString().contains("<city>"), "Expected Result not Found");
        Assert.assertTrue(response.toString().contains("<phone>"), "Expected Result not Found");
        Assert.assertTrue(response.toString().contains("</Office>"), "Expected Result not Found");
    }

    private OMElement getPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.wso2.org/dataservice/samples/secure_dataservice", "ns1");
        return fac.createOMElement("showAllOffices", omNs);
    }
}
