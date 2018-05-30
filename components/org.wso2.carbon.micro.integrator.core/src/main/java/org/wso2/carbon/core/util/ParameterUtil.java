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

package org.wso2.carbon.core.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.i18n.Messages;

import javax.xml.namespace.QName;

/**
 * Utility class to manipulate Axis2 parameters
 */
public final class ParameterUtil {

    private ParameterUtil() {
    }

    /**
     * Create an Axis2 parameter using the given OMElement
     *
     * @param parameterElement The OMElement of the parameter
     * @return Axis2 parameter with an the given OMElement
     * @throws AxisFault If the <code>parameterElement</code> is malformed
     */
    public static Parameter createParameter(OMElement parameterElement) throws AxisFault {
        if(parameterElement.getParent() instanceof OMDocument) {
            parameterElement.detach();  //To enable a unified way of accessing the parameter via xpath.
        }
        Parameter parameter = new Parameter();
        //setting parameterElement
        parameter.setParameterElement(parameterElement);

        //setting parameter Name
        OMAttribute paraName = parameterElement.getAttribute(new QName("name"));
        if (paraName == null) {
            throw new AxisFault(Messages.getMessage(DeploymentErrorMsgs.BAD_PARAMETER_ARGUMENT));
        }
        parameter.setName(paraName.getAttributeValue());

        //setting parameter Value (the chiled elemnt of the parameter)
        OMElement paraValue = parameterElement.getFirstElement();
        if (paraValue != null) {
            parameter.setValue(parameterElement);
            parameter.setParameterType(Parameter.OM_PARAMETER);
        } else {
            String paratextValue = parameterElement.getText();
            parameter.setValue(paratextValue);
            parameter.setParameterType(Parameter.TEXT_PARAMETER);
        }

        //setting locking attribute
        OMAttribute paraLocked = parameterElement.getAttribute(new QName("locked"));
        parameter.setParameterElement(parameterElement);

        if (paraLocked != null) {
            String lockedValue = paraLocked.getAttributeValue();
            if ("true".equals(lockedValue)) {

                parameter.setLocked(true);

            } else {
                parameter.setLocked(false);
            }
        }
        return parameter;
    }

    /**
     * Create an Axis2 parameter based on key and value.
     *
     * @param name
     * @param value
     * @return Parameter
     * @throws AxisFault
     */
    public static Parameter createParameter(String name, String value) throws AxisFault {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("", "");
        OMElement paramEle = fac.createOMElement("parameter", ns);

        if (name == null) {
            throw new AxisFault("Parameter name is madatory.");
        }
        paramEle.addAttribute("name", name, ns);
        if (value != null && value.length() != 0) {
            paramEle.setText(value);
        }
        return createParameter(paramEle);
    }

    /**
     * Create an Axis2 parameter based using param name, value and whether it is locked
     *
     * @param name - name of the parameter
     * @param value - value of the parameter
     * @param locked - whether it is a locked param
     * @return Parameter instance
     * @throws AxisFault - error while creating param
     */
    public static Parameter createParameter(String name, String value, boolean locked) throws AxisFault {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("", "");
        OMElement paramEle = fac.createOMElement("parameter", ns);

        if (name == null) {
            throw new AxisFault("Parameter name is madatory.");
        }
        paramEle.addAttribute("name", name, ns);
        if (locked) {
            paramEle.addAttribute("locked", "true", ns);
        }
        if (value != null && value.length() != 0) {
            paramEle.setText(value);
        }
        return createParameter(paramEle);
    }    
}
