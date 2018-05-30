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

package org.wso2.carbon.core.transports.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.util.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class RequestProcessorUtil {
    private static Log log = LogFactory.getLog(org.wso2.carbon.core.transports.util.RequestProcessorUtil.class);
    private static final XMLInputFactory xmlInputFactory;

    static {
        xmlInputFactory = XMLInputFactory.newInstance();
        try {
            xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        } catch (IllegalArgumentException e) {
            log.error("Failed to load XML Processor Feature XMLInputFactory.IS_NAMESPACE_AWARE", e);
        }

        try {
            xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        } catch (IllegalArgumentException e) {
            log.error("Failed to load XML Processor Feature XMLInputFactory.SUPPORT_DTD", e);
        }

        try {
            xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        } catch (IllegalArgumentException e) {
            log.error("Failed to load XML Processor Feature XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES", e);
        }
    }

    /**
     * @param byteArrayOutStream
     * @param out
     * @param annotatedXsl
     * @param contextRoot
     * @param annotation         : If annotation is false PI would not be attached.
     */
    public static void writeDocument(ByteArrayOutputStream byteArrayOutStream,
                                     OutputStream out,
                                     String annotatedXsl,
                                     String contextRoot, boolean annotation) {
        XMLStreamWriter writer;
        ByteArrayInputStream bais = null;
        XMLStreamReader reader = null;
        try {
            bais =
                    new ByteArrayInputStream(byteArrayOutStream.toByteArray());
            reader = xmlInputFactory.createXMLStreamReader(bais);
            StAXOMBuilder builder = new StAXOMBuilder(reader);
            OMElement docElem = builder.getDocumentElement();
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            if (annotatedXsl != null && annotation) {
                writer.writeProcessingInstruction("xml-stylesheet",
                                                  "  type=\"text/xsl\" href=\"" +
                                                  (contextRoot.equals("/") ? "" : contextRoot) +
                                                  "/styles/" +
                                                  annotatedXsl + "\"");
            }
            docElem.serialize(writer);
            writer.flush();
        } catch (XMLStreamException e) {
            log.error("Error occurred while trying to write processing instruction for attaching " +
                      "annotated style sheet", e);
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                log.error(e.getMessage(), e);
            }

        }
    }

    /**
     * Checks whether exposing the WSDL & WSDL elements such as schema & policy have been allowed
     *
     * @param service The AxisService which needs to be verified
     * @return true - if service metadata can be exposed, false - otherwise
     * @throws IOException If exposing WSDL & WSDL elements has been restricted.
     */
    public static boolean canExposeServiceMetadata(AxisService service) throws IOException {
        Parameter exposeServiceMetadata = service.getParameter("exposeServiceMetadata");
        if (exposeServiceMetadata != null &&
            JavaUtils.isFalseExplicitly(exposeServiceMetadata.getValue())) {
            return false;
        }
        return true;
    }

    public static String getServiceContextPath(ConfigurationContext configCtx) {
        String serviceContextPath = configCtx.getServiceContextPath();
        if (!configCtx.getContextRoot().equals("/")
            && !serviceContextPath.startsWith("/")) {
            serviceContextPath = "/" + serviceContextPath;
        }
        return serviceContextPath;
    }
}
