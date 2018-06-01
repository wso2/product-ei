/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

package org.wso2.carbon.core.services.echo;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;

import javax.xml.namespace.QName;

/**
 * Echo Service
 */
public class Echo {
    public OMElement echoOMElement(OMElement omEle) {
        return omEle;
    }

    public String echoString(String in) {
        return in;
    }

    public int echoInt(int in) {
        return in;
    }

    public String throwAxisFault() throws AxisFault {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement ele = fac.createOMElement("wso2wsas", fac.createOMNamespace("", ""));
        ele.setText("This is a method which simply throws an AxisFault");

        throw new AxisFault(new QName("wso2wsas_code"),
                            "This is a method which simply throws an AxisFault", "wso2_node",
                            "admin", ele);
    }

    public SimpleBean echoStringArrays(String[] a, String[] b, int c) {
//
        SimpleBean bean = new SimpleBean();
        bean.setA_r(a);
        bean.setB_r(b);
        bean.setC(c);

        return bean;
    }
}
