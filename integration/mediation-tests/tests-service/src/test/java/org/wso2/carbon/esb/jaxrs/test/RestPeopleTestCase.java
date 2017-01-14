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

package org.wso2.carbon.esb.jaxrs.test;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerManager;
import org.wso2.carbon.automation.extensions.servers.tomcatserver.TomcatServerType;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.services.jaxrs.peoplesample.AppConfig;

import java.io.File;


public class RestPeopleTestCase extends ESBIntegrationTest {
    TomcatServerManager tomcatServerManager;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB"+ File.separator + "jaxrs" + File.separator + "putpeopleproxy.xml");
        String basedirLocation = System.getProperty("basedir")+ File.separator+"target";
        tomcatServerManager = new TomcatServerManager(AppConfig.class.getName(), TomcatServerType.jaxrs.name(), 8080);
    }

    @Test
    public void addPeople() throws Exception {

        tomcatServerManager.startServer();
        Thread.sleep(50000);
        //String text = new Scanner( new File("poem.txt"), "UTF-8" ).useDelimiter("\\A").next();
        //OMElement putProxyService = AXIOMUtil.stringToOM(new Scanner(new File(getESBResourceLocation() + File.separator + "jaxrs" + File.separator + "putpeopleproxy.xml"), "UTF-8").useDelimiter("\\A").next());
       // addProxyService(putProxyService);

        if (tomcatServerManager.isRunning()) {
            System.out.println("Tomcat started");
        } else {
            System.out.println("---------------------------");
        }

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement putRequest = AXIOMUtil.stringToOM("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soapenv:Header/>\n" +
                "         <person>\n" +
                "         \t<email>dkasunw@gmail.com</email>\n" +
                "\t\t<firstname>dharshana</firstname>\n" +
                "\t\t<lastname>Warusavitharana</lastname>\n" +
                "         </person>\n" +
                "   <soapenv:Body/>\n" +
                "</soapenv:Envelope>");
        axisServiceClient.fireAndForget(putRequest,getProxyServiceURLHttp("peoplePutProxy"),"urn:mediate");


        tomcatServerManager.stop();
        if (tomcatServerManager.isRunning()) {
            System.out.println("Tomcat is running");
        } else {
            System.out.println("---------------------------");
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        super.cleanup();
    }

}
