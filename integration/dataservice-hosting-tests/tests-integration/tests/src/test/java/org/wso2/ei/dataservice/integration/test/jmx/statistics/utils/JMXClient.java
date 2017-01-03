/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.dataservice.integration.test.jmx.statistics.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
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
     * @param serviceName - name of the data service
     * @param hostName    - host name of the data service server
     * @param userName    - user name
     * @param password    - password
     * @throws MalformedObjectNameException - throws if the connection unsuccessful
     */
    public JMXClient(String serviceName, String hostName, String userName, String password)
            throws MalformedObjectNameException {
        String connectionName = "org.wso2.carbon.dataservices.jmx:section=Services,service=" + serviceName;
        this.objectName = new ObjectName(connectionName);
        this.userName = userName;
        this.password = password;
        this.hostName = hostName;
    }

    /**
     * connect to org.wso2.carbon for JMX monitoring
     *
     * @return - return MBeanServerConnection
     * @throws IOException - error in making connection
     * @throws MalformedObjectNameException
     *                             - error in making connection
     */
    public MBeanServerConnection connect()
            throws IOException, MalformedObjectNameException {
        try {
            //need to read rmi ports from environment config
            JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi://localhost:11111/jndi/rmi://" + hostName + ":9999/jmxrmi");

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

    /**
     * @param operationName - operation name to be invoked
     * @param params        - parameters for the operation
     * @return - results of the operation invocation
     * @throws Exception - throws if operation invocation fails
     */
    public Object invoke(String operationName, Object[] params)
            throws Exception {
        try {
            return mbsc.invoke(objectName, operationName, params, new String[]{String.class.getName()});

        } catch (Exception e) {
            log.error("Operation invocation fail " + e);
            throw new Exception("Operation invocation fail " + e);
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

