///*
//*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//*
//*WSO2 Inc. licenses this file to you under the Apache License,
//*Version 2.0 (the "License"); you may not use this file except
//*in compliance with the License.
//*You may obtain a copy of the License at
//*
//*http://www.apache.org/licenses/LICENSE-2.0
//*
//*Unless required by applicable law or agreed to in writing,
//*software distributed under the License is distributed on an
//*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//*KIND, either express or implied.  See the License for the
//*specific language governing permissions and limitations
//*under the License.
//*/
//
//package org.wso2.carbon.esb.mediator.test.clone;
//
//import org.apache.axiom.om.OMElement;
//import org.apache.axiom.om.util.AXIOMUtil;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
//
///**
// * https://wso2.org/jira/browse/ESBJAVA-823
// */
//public class SecuredCloneMediatorTestCase extends ESBIntegrationTest {
//
//
//    @BeforeClass(alwaysRun = true)
//    public void init() throws Exception {
//        super.init();
//        addProxy();
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void close() throws Exception {
//        super.cleanup();
//    }
//
//    //Related to Patch Automation https://wso2.org/jira/browse/ESBJAVA-823
//    @Test(groups = {"wso2.esb"}, description = "Using clone mediator to send back a Response when SAML authentication is enabled")
//    public void testSecuredCloneMediator() throws Exception {
//
//        applySecurity("Secured_Clone", 1, getUserRole(userInfo.getUserId()));
//
//        SecureServiceClient secureAxisServiceClient = new SecureServiceClient();
//        OMElement response = secureAxisServiceClient.sendSecuredStockQuoteRequest(userInfo, getProxyServiceURLHttps("Secured_Clone"), 1, "WSO2");
//        Assert.assertNotNull(response);
//        Assert.assertTrue(response.toString().contains("WSO2 Company"));
//        //Please add a log mediator read assertion for further make sure this works fine.
//    }
//
//    private void addProxy() throws Exception {
//        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"Secured_Clone\"\n" +
//                                             "          transports=\"https\"\n" +
//                                             "          startOnLoad=\"true\"\n" +
//                                             "          trace=\"disable\">\n" +
//                                             "      <description/>\n" +
//                                             "      <target>\n" +
//                                             "         <inSequence>\n" +
//                                             "            <log level=\"full\"/>\n" +
//                                             "            <header xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"\n" +
//                                             "                    name=\"wsse:Security\"\n" +
//                                             "                    action=\"remove\"/>\n" +
//                                             "            <log level=\"full\"/>\n" +
//                                             "            <send>\n" +
//                                             "               <endpoint>\n" +
//                                             "                  <address uri=\"http://localhost:9000/services/SimpleStockQuoteService\"/>\n" +
//                                             "               </endpoint>\n" +
//                                             "            </send>\n" +
//                                             "         </inSequence>\n" +
//                                             "         <outSequence>\n" +
//                                             "            <log level=\"full\"/>\n" +
//                                             "            <clone>\n" +
//                                             "               <target>\n" +
//                                             "                  <sequence>\n" +
//                                             "                     <send/>\n" +
//                                             "                  </sequence>\n" +
//                                             "               </target>\n" +
//                                             "               <target>\n" +
//                                             "                  <sequence>\n" +
//                                             "                     <log separator=\",\">\n" +
//                                             "                        <property name=\"-------------- TEST ------------------\" value=\"TRUE\"/>\n" +
//                                             "                     </log>\n" +
//                                             "                     <drop/>\n" +
//                                             "                  </sequence>\n" +
//                                             "               </target>\n" +
//                                             "            </clone>\n" +
//                                             "         </outSequence>\n" +
//                                             "      </target>\n" +
//                                             "   </proxy>"));
//    }
//
//
//
//}
//
