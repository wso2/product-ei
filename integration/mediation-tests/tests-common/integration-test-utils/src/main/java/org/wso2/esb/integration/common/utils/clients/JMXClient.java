/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.esb.integration.common.utils.clients;

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

/**
 * JMX Client which can be used to connect and invoke operations on local JMX Services
 */
public class JMXClient {

    private MBeanServerConnection mbsc = null;

    private String userName;
    private String password;
    private ObjectName objectName;
    private String hostName;
    private String rmiServerPort;
    private String rmiRegistryPort;

    private JMXConnector jmxc;
    private static final Log log = LogFactory.getLog(JMXClient.class);

    /**
     * @param connectionName - full name of the service with the connection
     * @param hostName       - host name of the data service server
     * @param userName       - user name
     * @param password       - password
     * @throws MalformedObjectNameException - throws if the connection unsuccessful
     */
    public JMXClient(String connectionName, String hostName, String rmiServerPort, String rmiRegistryPort,
            String userName, String password) throws MalformedObjectNameException {
        this.objectName = new ObjectName(connectionName);
        this.userName = userName;
        this.password = password;
        this.hostName = hostName;
        this.rmiServerPort = rmiServerPort;
        this.rmiRegistryPort = rmiRegistryPort;
    }

    /**
     * connect to org.wso2.carbon for JMX monitoring
     *
     * @return - return MBeanServerConnection
     * @throws java.io.IOException                           - error in making connection
     * @throws javax.management.MalformedObjectNameException - error in making connection
     */
    public MBeanServerConnection connect() throws IOException, MalformedObjectNameException {
        try {
            JMXServiceURL url = new JMXServiceURL(
                    "service:jmx:rmi://localhost:" + rmiServerPort + "/jndi/rmi://" + hostName + ":" + rmiRegistryPort
                            + "/jmxrmi");

            Hashtable<String, String[]> hashT = new Hashtable<String, String[]>();
            String[] credentials = new String[] { userName, password };
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

    /**
     * Disconnect from JMX endpoint
     *
     * @throws ListenerNotFoundException
     * @throws InstanceNotFoundException
     * @throws IOException
     */
    public void disconnect() throws ListenerNotFoundException, InstanceNotFoundException, IOException {

        if (jmxc != null) {
            log.info("Closing jmx client connection...............");
            jmxc.close();
        }
        if (mbsc != null) {
            mbsc = null;
        }
    }

    /**
     * @param operationName   - operation name to be invoked
     * @param params          - parameters for the operation
     * @param signatureTypes- array of Strings which contains each parameters' type
     * @return - results of the operation invocation
     * @throws Exception - throws if operation invocation fails
     */
    public Object invoke(String operationName, Object[] params, String[] signatureTypes) throws Exception {
        try {
            return mbsc.invoke(objectName, operationName, params, signatureTypes);

        } catch (Exception e) {
            log.error("Operation invocation fail " + e);
            throw new Exception("Operation invocation fail " + e);
        }
    }

    /**
     * Get attribute for the given name
     *
     * @param attribute - String name of the attribute
     * @return - Object returned as attribute for the given name
     * @throws Exception - Throws exception if attribute retrieval failed
     */
    public Object getAttribute(String attribute) throws Exception {
        try {
            return mbsc.getAttribute(objectName, attribute);
        } catch (Exception e) {
            log.error("Unable to get attribute " + e);
            throw new Exception("Unable to get attribute  " + e);
        }
    }
}