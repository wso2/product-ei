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
import org.apache.axis2.AxisFault;

import javax.xml.namespace.QName;

public class LBService1 {

    public OMElement setClientName(OMElement cName) {

        cName.build();
        cName.detach();

        cName.setText("Sessions are not supported in this service.");

        return cName;
    }

    public OMElement sampleOperation(OMElement param) {
        param.build();
        param.detach();
        
        String sName = "";
        if (System.getProperty("test_mode") != null) {
            sName = org.apache.axis2.context.MessageContext.getCurrentMessageContext().getTo().getAddress();
        } else {
            sName = System.getProperty("server_name");
        }
        if (sName != null) {
            param.setText("Response from server: " + sName);
        } else {
            param.setText("Response from anonymous server");
        }
        return param;
    }

    public OMElement sleepOperation(OMElement param) throws AxisFault {

        param.build();
        param.detach();

        OMElement timeElement = param.getFirstChildWithName(new QName("load"));
        String time = timeElement.getText();
        try {
            Thread.sleep(Long.parseLong(time));
        } catch (InterruptedException e) {
            throw new AxisFault("Service is interrupted while sleeping.");
        }

        String sName = System.getProperty("server_name");
        if (sName != null) {
            timeElement.setText("Response from server: " + sName);
        } else {
            timeElement.setText("Response from anonymous server");
        }
        return param;
    }

    public OMElement loadOperation(OMElement param) throws AxisFault {

        param.build();
        param.detach();

        OMElement loadElement = param.getFirstChildWithName(new QName("load"));
        String l = loadElement.getText();
        long load = Long.parseLong(l);

        for (long i = 0; i < load; i++) {
            System.out.println("Iteration: " + i);
        }

        String sName = System.getProperty("server_name");
        if (sName != null) {
            loadElement.setText("Response from server: " + sName);
        } else {
            loadElement.setText("Response from anonymous server");
        }
        return param;
    }
}