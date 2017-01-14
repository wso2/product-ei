/**
 *
 */
package org.wso2.carbon.esb.message.store.test;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.httpserver.RequestInterceptor;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.JMSBrokerController;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpServer;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author wso2
 */
public class JMSEndpointSuspensionViaVFSTest extends ESBIntegrationTest {

    private TestRequestInterceptor interceptor = new TestRequestInterceptor();
    private JMSBrokerController jmsBrokerController;
    private ServerConfigurationManager serverConfigurationManager;

    private final String HAWTBUF = "hawtbuf-1.9.jar";
    private final String ACTIVEMQ_CLIENT = "activemq-client-5.9.1.jar";
    private final String ACTIVEMQ_BROKER = "activemq-broker-5.9.1.jar";
    private final String GERONIMO_J2EE_MANAGEMENT = "geronimo-j2ee-management_1.1_spec-1.0.1.jar";
    private final String GERONIMO_JMS = "geronimo-jms_1.1_spec-1.1.1.jar";
    private final String JAR_LOCATION = "/artifacts/ESB/jar";
    private final int PORT = 9654;
    private SimpleHttpServer httpServer;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        setUpJMSBroker();
        /* Make the port available */
        Utils.shutdownFailsafe(PORT);
        httpServer = new SimpleHttpServer(PORT, new Properties());
        httpServer.start();
        Thread.sleep(5000);

        interceptor = new TestRequestInterceptor();
        httpServer.getRequestHandler().setInterceptor(interceptor);

        super.init();

        serverConfigurationManager = new ServerConfigurationManager(context);
        serverConfigurationManager.copyToComponentLib(new File(getClass().
                getResource(JAR_LOCATION + File.separator + HAWTBUF).toURI()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().
                getResource(JAR_LOCATION + File.separator + ACTIVEMQ_CLIENT).toURI()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().
                getResource(JAR_LOCATION + File.separator + ACTIVEMQ_BROKER).toURI()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().
                getResource(JAR_LOCATION + File.separator + GERONIMO_J2EE_MANAGEMENT).toURI()));
        serverConfigurationManager.copyToComponentLib(new File(getClass().
                getResource(JAR_LOCATION + File.separator + GERONIMO_JMS).toURI()));
        serverConfigurationManager.applyConfiguration(new File(getClass().
                getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "messageStore" + File.separator + "axis2.xml").getPath()));

        super.init();

        File outfolder = new File(getClass().
                getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                            + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath()
                                  + "test" + File.separator + "out" + File.separator);
        File infolder = new File(getClass().
                getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                            + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath()
                                 + "test" + File.separator + "in" + File.separator);
        File originalfolder = new File(getClass().
                getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                            + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath()
                                       + "test" + File.separator + "done" + File.separator);
        File failurelfolder = new File(getClass().
                getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator
                            + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath()
                                       + "test" + File.separator + "failure" + File.separator);
        outfolder.mkdirs();
        infolder.mkdirs();
        originalfolder.mkdirs();
        failurelfolder.mkdirs();
        log.info("Before Class method completed successfully");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Sending a file through VFS Transport to JMS endpoint" +
                                               " and test whether its getting suspended")
    public void testJMSEndpointSuspensionViaVFSTest()
            throws Exception {

        addVFSJMSProxy1();
        File outfile = new File(getClass().getResource(File.separator + "artifacts" + File.separator
                                                       + "ESB" + File.separator + "synapseconfig" + File.separator
                                                       + "messageStore" + File.separator).getPath() + "test"
                                + File.separator + "done" + File.separator + "test.xml");
        if (outfile.exists()) {
            outfile.delete();
        }

        File afile = new File(getClass().getResource(File.separator + "artifacts" + File.separator
                                                     + "ESB" + File.separator + "synapseconfig" + File.separator
                                                     + "messageStore" + File.separator + "test.xml").getPath());
        File bfile = new File(getClass().getResource(File.separator + "artifacts" + File.separator + "ESB"
                                                     + File.separator + "synapseconfig" + File.separator
                                                     + "messageStore" + File.separator).getPath() + "test"
                              + File.separator + "in" + File.separator + "test.xml");

        sendFile(outfile, afile, bfile);

        Assert.assertTrue(interceptor.getPayload().contains("<address>Disney Land</address>"));
//        String vfsOut = FileUtils.readFileToString(outfile);
//        Assert.assertTrue(vfsOut.contains("WSO2 Company"));

        interceptor = new TestRequestInterceptor();
        httpServer.getRequestHandler().setInterceptor(interceptor);
        jmsBrokerController.stop();

        sendFile(outfile, afile, bfile);

        Assert.assertTrue(interceptor.getPayload().contains("Endpoint Down!"));

        deleteProxyService("VFSJMSProxy1");
    }

    private void sendFile(File outfile, File afile, File bfile)
            throws IOException, InterruptedException {
        FileUtils.copyFile(afile, bfile);
        Thread.sleep(2000);

        Assert.assertTrue(outfile.exists());
        bfile.delete();
        outfile.delete();
    }

//    @Test(groups = {"wso2.esb"})
//    public void testSpecialCharacterMediation() throws Exception {
////        serverConfigurationManager.restartGracefully();
////        super.init(5);
//        SimpleHttpClient httpClient = new SimpleHttpClient();
//        String payload = "<test>This payload is üsed to check special character mediation</test>";
//        try {
//
//            HttpResponse response = httpClient.doPost(getProxyServiceURLHttp("InOutProxy"), null, payload, "application/xml");
//        } catch (AxisFault e) {
//            log.error("Response not expected here, Exception can be accepted ");
//        }
//        Thread.sleep(10000);
//        assertTrue(interceptor.getPayload().contains(payload));
//    }

    private void setUpJMSBroker() {
        jmsBrokerController = new JMSBrokerController("localhost", getJMSBrokerConfiguration());
        jmsBrokerController.start();
    }

    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        return JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration();
    }

    private static class TestRequestInterceptor implements RequestInterceptor {

        private String payload;

        public void requestReceived(HttpRequest request) {
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                try {
                    InputStream in = entity.getContent();
                    String inputString = IOUtils.toString(in, "UTF-8");
                    payload = inputString;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getPayload() {
            return payload;
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        try {
            super.cleanup();
        } finally {
            try {
                jmsBrokerController.stop();
            } catch (Exception e) {
                log.warn("Error while shutting down the JMS Broker", e);
            }
            try {
                httpServer.stop();
            } catch (Exception e) {
                log.warn("Error while shutting down the HTTP server", e);
            }
            Thread.sleep(3000);
            serverConfigurationManager.removeFromComponentLib(ACTIVEMQ_CLIENT);
            serverConfigurationManager.removeFromComponentLib(ACTIVEMQ_BROKER);
            serverConfigurationManager.removeFromComponentLib(HAWTBUF);
            serverConfigurationManager.removeFromComponentLib(GERONIMO_J2EE_MANAGEMENT);
            serverConfigurationManager.removeFromComponentLib(GERONIMO_JMS);
            serverConfigurationManager.restoreToLastConfiguration();
        }

        serverConfigurationManager = null;
    }

    private void addVFSJMSProxy1()
            throws Exception {

        addProxyService(AXIOMUtil.stringToOM("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<proxy xmlns=\"http://ws.apache.org/ns/synapse\" name=\"VFSJMSProxy1\" transports=\"vfs\">\n" +
                                             "                <parameter name=\"transport.vfs.FileURI\">file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath() + "test" + File.separator + "in" + File.separator + "</parameter> <!--CHANGE-->\n" +
                                             "                <parameter name=\"transport.vfs.ContentType\">text/xml</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.FileNamePattern\">.*\\.xml</parameter>\n" +
                                             "                <parameter name=\"transport.PollInterval\">1</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterProcess\">MOVE</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.MoveAfterProcess\">file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath() + "test" + File.separator + "done" + File.separator + "</parameter>" +
                                             "                <parameter name=\"transport.vfs.MoveAfterFailure\">file://" + getClass().getResource(File.separator + "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "messageStore" + File.separator).getPath() + "test" + File.separator + "invalid" + File.separator + "</parameter>\n" +
                                             "                <parameter name=\"transport.vfs.ActionAfterFailure\">MOVE</parameter>" +
                                             "                <target>\n" +
                                             "                  <inSequence>\n" +
                                             "                     <property name=\"OUT_ONLY\" value=\"true\" scope=\"default\" type=\"STRING\"/>\n" +
                                             "                     <log level=\"full\"/>\n" +
                                             "                     <send>\n" +
                                             "                          <endpoint>\n" +
                                             "                              <recipientlist>\n" +
                                             "                                  <endpoint>\n" +
                                             "                                      <address uri=\"jms:/Addresses?transport.jms.ConnectionFactoryJNDIName=QueueConnectionFactory&amp;java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory&amp;java.naming.provider.url=tcp://localhost:61616\"/>" +
                                             "                                  </endpoint>" +
                                             "                                  <endpoint>\n" +
                                             "                                      <address uri=\"http://localhost:9654/services/SimpleStockQuoteService\"/>" +
                                             "                                  </endpoint>" +
                                             "                              </recipientlist>\n" +
                                             "                          </endpoint>\n" +
                                             "                      </send>\n" +
                                             "                  </inSequence>\n" +
                                             "                  <faultSequence>\n" +
                                             "                     <log level=\"full\">\n" +
                                             "                        <property name=\"ERROR\" value=\"Endpoint Down!\"/>\n" +
                                             "                     </log>\n" +
                                             "                     <makefault>\n" +
                                             "                         <code value=\"tns:Sender\" xmlns:tns=\"http://www.w3.org/2003/05/soap-envelope\"/>\n" +
                                             "                         <reason value=\"Endpoint Down!\"/>\n" +
                                             "                     </makefault>\n" +
                                             "                     <send>\n" +
                                             "                          <endpoint>\n" +
                                             "                               <address uri=\"http://localhost:9654/services/SimpleStockQuoteService\"/>" +
                                             "                          </endpoint>" +
                                             "                     </send>\n" +
                                             "                  </faultSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }
}
