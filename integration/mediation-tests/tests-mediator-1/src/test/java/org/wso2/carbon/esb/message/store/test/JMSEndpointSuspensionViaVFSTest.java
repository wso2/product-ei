/**
 *
 */
package org.wso2.carbon.esb.message.store.test;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
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
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.extensions.servers.httpserver.RequestInterceptor;
import org.wso2.carbon.automation.extensions.servers.httpserver.SimpleHttpServer;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author wso2
 */
public class JMSEndpointSuspensionViaVFSTest extends ESBIntegrationTest {

    private TestRequestInterceptor interceptorOut = new TestRequestInterceptor();
    private TestRequestInterceptor interceptorFault = new TestRequestInterceptor();
    private final int PORT = 9654;
    private final int PORT_FAULT = 9655;
    private SimpleHttpServer httpServerOut;
    private SimpleHttpServer httpServerFault;
    private BrokerService broker;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        startBroker();
        /* Make the port available */
        Utils.shutdownFailsafe(PORT);
        httpServerOut = new SimpleHttpServer(PORT, new Properties());
        httpServerOut.start();

        Utils.shutdownFailsafe(PORT_FAULT);
        httpServerFault = new SimpleHttpServer(PORT_FAULT, new Properties());
        httpServerFault.start();
        Thread.sleep(5000);

        interceptorOut = new TestRequestInterceptor();
        httpServerOut.getRequestHandler().setInterceptor(interceptorOut);

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

        Assert.assertTrue(interceptorOut.getPayload().contains("<address>Disney Land</address>"));
//        String vfsOut = FileUtils.readFileToString(outfile);
//        Assert.assertTrue(vfsOut.contains("WSO2 Company"));

        interceptorFault = new TestRequestInterceptor();
        httpServerFault.getRequestHandler().setInterceptor(interceptorFault);
        stopBroker();

        sendFile(outfile, afile, bfile);

        Assert.assertTrue(interceptorFault.getPayload().contains("Endpoint Down!"),
                "payload received: " + interceptorFault.getPayload() + ". payload expected: " + "Endpoint Down!");

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
                stopBroker();
            } catch (Exception e) {
                log.warn("Error while shutting down the JMS Broker", e);
            }
            try {
                httpServerOut.stop();
            } catch (Exception e) {
                log.warn("Error while shutting down the HTTP serverOut", e);
            }
            try {
                httpServerFault.stop();
            } catch (Exception e) {
                log.warn("Error while shutting down the HTTP serverFault", e);
            }
        }
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
                                             "                                      <address uri=\"jms:/Addresses?transport.jms.ConnectionFactoryJNDIName=QueueConnectionFactory&amp;java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory&amp;java.naming.provider.url=tcp://localhost:61816\"/>" +
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
                                             "                         <code value=\"tns:Client\" xmlns:tns=\"http://schemas.xmlsoap.org/soap/envelope/\"/>\n" +
                                             "                         <reason value=\"Endpoint Down!\"/>\n" +
                                             "                     </makefault>\n" +
                                             "                     <send>\n" +
                                             "                          <endpoint>\n" +
                                             "                               <address uri=\"http://localhost:9655/services/SimpleStockQuoteService\"/>" +
                                             "                          </endpoint>" +
                                             "                     </send>\n" +
                                             "                  </faultSequence>" +
                                             "                </target>\n" +
                                             "        </proxy>"));
    }


    private List<TransportConnector> getTCPConnectors() {
        //setting the tcp transport configurations
        List<TransportConnector> tcpList = new ArrayList<>();
        TransportConnector tcp = new TransportConnector();
        tcp.setName("tcp");
        try {
            tcp.setUri(new URI("tcp://127.0.0.1:61816"));
        } catch (URISyntaxException e) {
            log.error("Error while setting tcp uri :tcp://127.0.0.1:61816", e);
        }
        tcpList.add(tcp);
        return tcpList;
    }

    private boolean startBroker() {
        try {
            log.info("JMSServerController: Preparing to start JMS Broker: " );
            broker = new BrokerService();
            // configure the broker

            broker.setBrokerName("myBroker1");
            log.info(broker.getBrokerDataDirectory());
            broker.setDataDirectory(System.getProperty(FrameworkConstants.CARBON_HOME) +
                    File.separator + broker.getBrokerDataDirectory());
            broker.setTransportConnectors(getTCPConnectors());
            broker.setPersistent(true);

            broker.start();
            log.info("JMSServerController: Broker is Successfully started. continuing tests");
            return true;
        } catch (Exception e) {
            log.error(
                    "JMSServerController: There was an error starting JMS broker: ", e);
            return false;
        }
    }

    private boolean stopBroker() {
        try {
            log.info(" ************* Stopping **************");
            if (broker.isStarted()) {
                broker.stop();
                for(TransportConnector transportConnector : getTCPConnectors()) {
                    transportConnector.stop();
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error while shutting down the broker", e);
            return false;
        }
    }
}
