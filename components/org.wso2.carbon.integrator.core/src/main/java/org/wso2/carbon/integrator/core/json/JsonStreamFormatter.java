/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.integrator.core.json;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.integrator.core.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

/**
 * JsonStream Formatter class for Enterprise integrator
 */
public class JsonStreamFormatter implements MessageFormatter {

    private Method synapseFormatterGetBytesMethod;
    private Method axis2GsonFormatterGetBytesMethod;
    private Method synapseFormatterWriteToMethod;
    private Method axis2GsonFormatterWriteToMethod;
    private Method synapseFormatterGetContentTypeMethod;
    private Method axis2GsonFormatterGetContentTypeMethod;
    private Method synapseFormatterGetTargetAddressMethod;
    private Method axis2GsonFormatterGetTargetAddressMethod;
    private Method synapseFormatterFormatSOAPActionMethod;
    private Method axis2GsonFormatterFormatSOAPActionMethod;
    private Object synapseFormatter;
    private Object axis2GsonFormatter;

    private static final Log logger = LogFactory.getLog(JsonStreamFormatter.class.getName());

    public JsonStreamFormatter()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException,
                   IOException, XMLStreamException {
        Class<?> synapseFormatterClass =
                JsonStreamFormatter.class.getClassLoader().loadClass(Utils.getPassThroughJsonFormatter());
        this.synapseFormatter = synapseFormatterClass.newInstance();
        this.synapseFormatterGetBytesMethod =
                synapseFormatterClass.getMethod("getBytes", MessageContext.class, OMOutputFormat.class);
        this.synapseFormatterWriteToMethod = synapseFormatterClass
                .getMethod("writeTo", MessageContext.class, OMOutputFormat.class, OutputStream.class, boolean.class);
        this.synapseFormatterGetContentTypeMethod = synapseFormatterClass
                .getMethod("getContentType", MessageContext.class, OMOutputFormat.class, String.class);
        this.synapseFormatterGetTargetAddressMethod = synapseFormatterClass
                .getMethod("getTargetAddress", MessageContext.class, OMOutputFormat.class, URL.class);
        this.synapseFormatterFormatSOAPActionMethod = synapseFormatterClass
                .getMethod("formatSOAPAction", MessageContext.class, OMOutputFormat.class, String.class);

        Class<?> axis2GsonFormatterClass =
                JsonStreamFormatter.class.getClassLoader().loadClass(Utils.getDSSJsonFormatter());
        this.axis2GsonFormatter = axis2GsonFormatterClass.newInstance();
        this.axis2GsonFormatterGetBytesMethod =
                axis2GsonFormatterClass.getMethod("getBytes", MessageContext.class, OMOutputFormat.class);
        this.axis2GsonFormatterWriteToMethod = axis2GsonFormatterClass
                .getMethod("writeTo", MessageContext.class, OMOutputFormat.class, OutputStream.class, boolean.class);
        this.axis2GsonFormatterGetContentTypeMethod = axis2GsonFormatterClass
                .getMethod("getContentType", MessageContext.class, OMOutputFormat.class, String.class);
        this.axis2GsonFormatterGetTargetAddressMethod = axis2GsonFormatterClass
                .getMethod("getTargetAddress", MessageContext.class, OMOutputFormat.class, URL.class);
        this.axis2GsonFormatterFormatSOAPActionMethod = axis2GsonFormatterClass
                .getMethod("formatSOAPAction", MessageContext.class, OMOutputFormat.class, String.class);
    }

    public byte[] getBytes(MessageContext messageContext, OMOutputFormat omOutputFormat) throws AxisFault {
        try {
            if (Utils.isDataService(messageContext)) {
                return (byte[]) axis2GsonFormatterGetBytesMethod
                        .invoke(axis2GsonFormatter, messageContext, omOutputFormat);
            } else {
                return (byte[]) synapseFormatterGetBytesMethod.invoke(synapseFormatter, messageContext, omOutputFormat);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("Error occurred while generating bytes for application/json", e);
            throw new AxisFault(e.getMessage());
        }
    }

    public void writeTo(MessageContext messageContext, OMOutputFormat omOutputFormat, OutputStream outputStream,
                        boolean b) throws AxisFault {
        try {
            if (Utils.isDataService(messageContext)) {
                axis2GsonFormatterWriteToMethod
                        .invoke(axis2GsonFormatter, messageContext, omOutputFormat, outputStream, b);
            } else {
                synapseFormatterWriteToMethod.invoke(synapseFormatter, messageContext, omOutputFormat, outputStream, b);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("Error occurred while writing to application/json", e);
            throw new AxisFault(e.getMessage());
        }
    }

    public String getContentType(MessageContext messageContext, OMOutputFormat omOutputFormat, String s) {
        try {
            if (Utils.isDataService(messageContext)) {
                return (String) axis2GsonFormatterGetContentTypeMethod
                        .invoke(axis2GsonFormatter, messageContext, omOutputFormat, s);
            } else {
                return (String) synapseFormatterGetContentTypeMethod
                        .invoke(synapseFormatter, messageContext, omOutputFormat, s);
            }
        } catch (InvocationTargetException | IllegalAccessException | AxisFault e ) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public URL getTargetAddress(MessageContext messageContext, OMOutputFormat omOutputFormat, URL url)
            throws AxisFault {
        try {
            if (Utils.isDataService(messageContext)) {
                return (URL) axis2GsonFormatterGetTargetAddressMethod
                        .invoke(axis2GsonFormatter, messageContext, omOutputFormat, url);
            } else {
                return (URL) synapseFormatterGetTargetAddressMethod
                        .invoke(synapseFormatter, messageContext, omOutputFormat, url);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("Error occurred while retrieving target address for application/json", e);
            throw new AxisFault(e.getMessage());
        }
    }

    public String formatSOAPAction(MessageContext messageContext, OMOutputFormat omOutputFormat, String s) {
        try {
            if (Utils.isDataService(messageContext)) {
                return (String) axis2GsonFormatterFormatSOAPActionMethod
                        .invoke(axis2GsonFormatter, messageContext, omOutputFormat, s);
            } else {
                return (String) synapseFormatterFormatSOAPActionMethod
                        .invoke(synapseFormatter, messageContext, omOutputFormat, s);
            }
        } catch (InvocationTargetException | IllegalAccessException | AxisFault e ) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
