/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.services;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;

import javax.xml.namespace.QName;

public class LBService2 {

    private ServiceContext serviceContext = null;

    public void init(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public OMElement sleepOperation(OMElement topParam) {

        topParam.build();
        topParam.detach();

        OMElement param = topParam.getFirstChildWithName(new QName("load"));
        String l = param.getText();
        long time = Long.parseLong(l);

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Long c = null;
        Object o = serviceContext.getProperty("count");
        if (o == null) {
            c = new Long(1);
            serviceContext.setProperty("count", c);
        } else {
            c = (Long) o;
            c = new Long(c.longValue() + 1);
            serviceContext.setProperty("count", c);
        }

        String cName = "anonymous";
        Object cn = serviceContext.getProperty("cName");
        if (cn != null) {
            cName = (String) cn;

        }

        String sName = "anonymous";
        Object s = System.getProperty("server_name");
        if (s != null) {
            sName = (String) s;
        }

        String msg = "Server: " + sName + " processed the request " + c.toString() + " from client: " + cName;
        System.out.println(msg);

        param.setText(msg);

        return topParam;
    }

    public OMElement loadOperation(OMElement topParam) {

        topParam.build();
        topParam.detach();

        OMElement param = topParam.getFirstChildWithName(new QName("load"));
        String l = param.getText();
        long load = Long.parseLong(l);

        for (long i = 0; i < load; i++) {
            System.out.println("Iteration: " + i);
        }

        Long c = null;
        Object o = serviceContext.getProperty("count");
        if (o == null) {
            c = new Long(1);
            serviceContext.setProperty("count", c);
        } else {
            c = (Long) o;
            c = new Long(c.longValue() + 1);
            serviceContext.setProperty("count", c);
        }

        String cName = "anonymous";
        Object cn = serviceContext.getProperty("cName");
        if (cn != null) {
            cName = (String) cn;

        }

        String sName = "anonymous";
        Object s = System.getProperty("server_name");
        if (s != null) {
            sName = (String) s;
        }

        String msg = "Server: " + sName + " processed the request " + c.toString() + " from client: " + cName;
        System.out.println(msg);

        param.setText(msg);

        return topParam;
    }

    public OMElement setClientName(OMElement name) {

        name.build();
        name.detach();

        String cName = name.getText();
        serviceContext.setProperty("cName", cName);

        String sName = "anonymous";
        Object s = System.getProperty("server_name");
        if (s != null) {
            sName = (String) s;
        }

        String msg = "Server " + sName + " started a session with client " + cName;
        System.out.println(msg);
        name.setText(msg);

        return name;
    }
}
