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

package org.wso2.carbon.core.transports;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.ParameterInclude;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.transport.TransportSender;
import org.wso2.carbon.base.CarbonBaseUtils;
import org.wso2.carbon.core.transports.util.TransportParameter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods to parse, build and serialize transport configurations
 */
public class TransportBuilderUtils implements DeploymentConstants {

	/**
	 * Builds a TransportInDescription object from an XML configuration. The expected
     * XML configuration should be as follows.
     *
     * <transportReceiver name="myTransport" class="my.transport.listener.Class">
     *     <parameter name="param1">value 1</parameter>
     *     <parameter name="param1">value 1</parameter>
     * </transportReceiver>
	 * 
	 * @param transport An OMElement which contains transport configurations.
     * @param init Whether to initialize the receiver or not
	 * @return a transport in description
	 * @throws DeploymentException on error
	 */
	public static TransportInDescription processTransportReceiver(
            OMElement transport, boolean init) throws DeploymentException {

        if (!TAG_TRANSPORT_RECEIVER.equals(transport.getLocalName())) {
            throw new DeploymentException("Invalid top level element in the transport receiver " +
                    "configuration");
        }

        String name = transport.getAttributeValue(new QName(ATTRIBUTE_NAME));
        if (name == null || "".equals(name)) {
            throw new DeploymentException("Transport name is not specified in the receiver " +
                    "configuration");
        }

        String className = transport.getAttributeValue(new QName(ATTRIBUTE_CLASS));
        if (className == null || "".equals(className)) {
            throw new DeploymentException("Class name is not specified in the receiver " +
                    "configuration");
        }

        TransportInDescription transportIn = new TransportInDescription(name);
        if (init) {
            try {
                Class clazz = org.wso2.carbon.core.transports.TransportBuilderUtils.class.getClassLoader().loadClass(className);
                TransportListener listener = (TransportListener) clazz.newInstance();
                transportIn.setReceiver(listener);
            } catch (Exception e) {
                throw new DeploymentException("Error while initializing transport receiver", e);
            }
        }

        Iterator itr = transport.getChildrenWithName(new QName(TAG_PARAMETER));
        processParameters(itr, transportIn);
        return transportIn;
    }

    /**
	 * Builds a TransportOutDescription object based on the specified XML configuration.
     * The XML should be as follows.
     *
     * <transportSender name="myTransport" class="my.transport.sender.Class">
     *     <parameter name="param1">value 1</parameter>
     *     <parameter name="param1">value 1</parameter>
     * </transportSender>
	 *
	 * @param transport An Iterator of OMElement which contains transport configurations.
     * @param init Whether to initialize the sender or not
	 * @return an array of transport out descriptions
	 * @throws DeploymentException on error
	 */
    public static TransportOutDescription processTransportSender(
            OMElement transport, boolean init) throws DeploymentException {

        if (!TAG_TRANSPORT_SENDER.equals(transport.getLocalName())) {
            throw new DeploymentException("Invalid top level element in the transport sender " +
                    "configuration");
        }

        String name = transport.getAttributeValue(new QName(ATTRIBUTE_NAME));
        if (name == null || "".equals(name)) {
            throw new DeploymentException("Transport name is not specified in the receiver " +
                    "configuration");
        }

        String className = transport.getAttributeValue(new QName(ATTRIBUTE_CLASS));
        if (className == null || "".equals(className)) {
            throw new DeploymentException("Class name is not specified in the receiver " +
                    "configuration");
        }

        TransportOutDescription transportOut = new TransportOutDescription(name);
        if (init) {
            try {
                Class clazz = org.wso2.carbon.core.transports.TransportBuilderUtils.class.getClassLoader().loadClass(className);
                TransportSender sender = (TransportSender) clazz.newInstance();
                transportOut.setSender(sender);
            } catch (Exception e) {
                throw new DeploymentException("Error while initializing transport sender", e);
            }
        }

        Iterator itr = transport.getChildrenWithName(new QName(TAG_PARAMETER));
        processParameters(itr, transportOut);
        return transportOut;
    }

    /**
	 * Processes transport parameters from the given configuration.
	 * 
	 * @param parameters An Iterator of OMElement which contains transport parameters.
	 * @param parameterInclude ParameterInclude
	 * @throws DeploymentException on error
	 */
	private static void processParameters(Iterator parameters,
                                          ParameterInclude parameterInclude) throws DeploymentException {

        if (parameters == null) {
            return;
        }

		while (parameters.hasNext()) {
			// this is to check whether some one has locked the parameter at the
			// top level
			OMElement parameterElement = (OMElement) parameters.next();
			Parameter parameter = new Parameter();
			// setting parameterElement
			parameter.setParameterElement(parameterElement);
			// setting parameter Name
			OMAttribute paramName = parameterElement.getAttribute(new QName(ATTRIBUTE_NAME));
			if (paramName == null) {
				throw new DeploymentException(Messages.getMessage(
						DeploymentErrorMsgs.BAD_PARAMETER_ARGUMENT, parameterElement.toString()));
			}
			parameter.setName(paramName.getAttributeValue());
			// setting parameter Value (the child element of the parameter)
			OMElement paramValue = parameterElement.getFirstElement();
			if (paramValue != null) {
				parameter.setValue(parameterElement);
				parameter.setParameterType(Parameter.OM_PARAMETER);
			} else {
				String paratextValue = parameterElement.getText();
				parameter.setValue(paratextValue);
				parameter.setParameterType(Parameter.TEXT_PARAMETER);
			}
			// setting locking attribute
			OMAttribute paramLocked = parameterElement.getAttribute(new QName(ATTRIBUTE_LOCKED));

			if (paramLocked != null) {
				String lockedValue = paramLocked.getAttributeValue();
				if (BOOLEAN_TRUE.equals(lockedValue)) {
					parameter.setLocked(true);
				} else {
					parameter.setLocked(false);
				}
			}
			try {
				parameterInclude.addParameter(parameter);
			} catch (AxisFault axisFault) {
				throw new DeploymentException(axisFault);
			}
		}
	}

    public static OMElement serializeTransportListener(TransportInDescription transport) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement transportReceiver = fac.createOMElement(new QName(TAG_TRANSPORT_RECEIVER));
        transportReceiver.addAttribute(ATTRIBUTE_NAME, transport.getName(), null);
        transportReceiver.addAttribute(ATTRIBUTE_CLASS, transport.getReceiver().
                getClass().getName(), null);

        serializeParameters(transport, transportReceiver, fac);
        return transportReceiver;
    }

    public static OMElement serializeTransportSender(TransportOutDescription transport) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement transportReceiver = fac.createOMElement(new QName(TAG_TRANSPORT_SENDER));
        transportReceiver.addAttribute(ATTRIBUTE_NAME, transport.getName(), null);
        transportReceiver.addAttribute(ATTRIBUTE_CLASS, transport.getSender().
                getClass().getName(), null);

        serializeParameters(transport, transportReceiver, fac);
        return transportReceiver;
    }

    private static void serializeParameters(ParameterInclude pInclude, OMElement parent,
                                            OMFactory fac) {
        List<Parameter> params = pInclude.getParameters();
        for (Parameter p : params) {
            parent.addChild(serializeParameter(p, fac));   
        }
    }

    public static OMElement serializeParameter(Parameter p, OMFactory fac) {
        if (p.getParameterElement() != null) {
            return p.getParameterElement();
        } else {
            OMElement paramElement = fac.createOMElement(new QName(TAG_PARAMETER));
            paramElement.addAttribute(ATTRIBUTE_NAME, p.getName(), null);
            if (p.getValue() instanceof OMElement) {
                paramElement.addChild((OMElement) p.getValue());
            } else {
                paramElement.setText(p.getValue().toString());
            }
            return paramElement;
        }
    }

    public static Parameter toAxisParameter(TransportParameter parameter) throws XMLStreamException {
        Parameter p = new Parameter();
        p.setName(parameter.getName());

        OMElement paramElement = AXIOMUtil.stringToOM(parameter.getParamElement());
        p.setParameterElement(paramElement);

        if (paramElement.getFirstElement() != null) {
            p.setValue(paramElement);
            p.setParameterType(Parameter.OM_PARAMETER);
        } else {
            p.setValue(paramElement.getText());
            p.setParameterType(Parameter.TEXT_PARAMETER);
        }
        return p;
    }

    public static OMElement parseTransportConfiguration(String transport,
                                                        URL url, boolean listener) throws Exception {

        File configFile = new File(Paths.get(CarbonBaseUtils.getCarbonConfigDirPath(), transport + "-config").toString());

        InputStream configStream;

        if (configFile.exists()) {
            configStream = new FileInputStream(configFile);
        } else {
            configStream = url.openStream();
        }

        if (configStream != null) {
            StAXOMBuilder builder = new StAXOMBuilder(configStream);
            OMElement doc = builder.getDocumentElement();
            if (listener) {
                return doc.getFirstChildWithName(new QName(TAG_TRANSPORT_RECEIVER));
            } else {
                return doc.getFirstChildWithName(new QName(TAG_TRANSPORT_SENDER));
            }
        }

        return null;
    }
}