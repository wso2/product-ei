package org.wso2.carbon.esb.jms.transport.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

public class JMXClient {
    private MBeanServerConnection mbsc = null;
    private String userName;
    private String password;
    private ObjectName objectName;
    private String hostName;

    private JMXConnector jmxc;
    private static final Log log = LogFactory.getLog(JMXClient.class);

    /**
     * @param serviceName - name of the service
     * @param hostName    - host name of the server
     * @param userName    - user name
     * @param password    - password
     * @throws javax.management.MalformedObjectNameException - throws if the connection unsuccessful
     */
    public JMXClient(String type, String serviceName, String hostName, String userName, String password)
            throws MalformedObjectNameException {
        String connectionName = "org.apache.synapse:Type="+type+",Name=" + serviceName;
        this.objectName = new ObjectName(connectionName);
        this.userName = userName;
        this.password = password;
        this.hostName = hostName;
    }

    /**
     * connect to org.wso2.carbon for JMX monitoring
     *
     * @return - return MBeanServerConnection
     * @throws java.io.IOException - error in making connection
     * @throws javax.management.MalformedObjectNameException
     *                             - error in making connection
     */
    public MBeanServerConnection connect()
            throws IOException, MalformedObjectNameException {
        try {
            //need to read rmi ports from environment config
            //default ports (11111 & 9999) have been offset by 200 due to the overall port offset
            JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi://localhost:11311/jndi/rmi://" + hostName + ":10199/jmxrmi");

            Hashtable<String, String[]> hashT = new Hashtable<String, String[]>();
            String[] credentials = new String[]{userName, password};
            hashT.put("jmx.remote.credentials", credentials);

            jmxc = JMXConnectorFactory.connect(url, hashT);
            mbsc = jmxc.getMBeanServerConnection();

            if (mbsc != null) {
                return mbsc;
            }

        } catch (MalformedURLException e) {
            log.error("Error while creating Jmx connection ", e);
            throw new MalformedURLException();
        } catch (IOException e) {
            log.error("Error while creating Jmx connection ", e);
            throw new IOException("Error while creating Jmx connection " + e);
        }
        return null;
    }


    public void disconnect()
            throws ListenerNotFoundException, InstanceNotFoundException, IOException {

        if (jmxc != null) {
            log.info("Closing jmx client connection...............");
            jmxc.close();

        }
        if (mbsc != null) {
            mbsc = null;
        }
    }

    public Object getAttribute(String attribute) throws Exception {
        try {
            return mbsc.getAttribute(objectName, attribute);
        } catch (Exception e) {
            log.error("Unable to get attribute " + e);
            throw new Exception("Unable to get attribute  " + e);
        }
    }
}