/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test.common.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;

import javax.xml.stream.XMLStreamException;
import java.util.Map;

/**
 * Util class for XML Utilities
 */
public class XMLUtils {

    private static final Log log = LogFactory.getLog(XMLUtils.class);

    private static String SOAPXPATH = "//soapenv:Envelope/soapenv:Body/";
    private static String SOAP11NamespaceKey = "soapenv";
    private static String SOAP11NamespaceValue = "http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * Returns result of a XPATH evaluation. Use if XPATH has no namespaces.
     *
     * @param input           OMElement input
     * @param xpathExpression XPATH expression as a String. XPATH should begin with "//"
     * @return result OMElement
     * @throws JaxenException in case of an error during evaluation
     */
    public static OMElement evaluateXPATH(OMElement input, String xpathExpression) throws JaxenException {
        AXIOMXPath xpath = new AXIOMXPath(xpathExpression);
        return evaluate(input, xpath);
    }

    /**
     * Returns result of a XPATH evaluation.
     *
     * @param input           OMElement input
     * @param xpathExpression XPATH expression as a String. XPATH should begin with "//"
     * @param namespaces      A map containing definitions of the XPATH <XPATH key, XPATH value>
     * @return result OMElement
     * @throws JaxenException in case of an error during evaluation
     */
    public static OMElement evaluateXPATH(OMElement input, String xpathExpression, Map<String, String> namespaces)
            throws JaxenException {
        AXIOMXPath xpath = new AXIOMXPath(xpathExpression);
        for (Map.Entry<String, String> namespace : namespaces.entrySet()) {
            xpath.addNamespace(namespace.getKey(), namespace.getValue());
        }
        return evaluate(input, xpath);
    }

    /**
     * Returns result of a XPATH evaluation on SOAP 11 message.
     *
     * @param SOAPInput       Input message as OMElement
     * @param xpathExpression XPATH expression as a String. XPATH should give from SOAP BODY Element. Skip any "/" at
     *                        the beginning of the XPATH expression
     * @param namespaces      namespaces for XPATH. No need to give SOAP namespace
     * @return result OMElement
     * @throws JaxenException in case of an error during evaluation
     */
    public static OMElement evaluateXPATHOnSOAP(OMElement SOAPInput, String xpathExpression, Map<String, String> namespaces)
            throws JaxenException {
        xpathExpression = SOAPXPATH + xpathExpression;
        AXIOMXPath xpath = new AXIOMXPath(xpathExpression);

        //Add soapenv namespace
        xpath.addNamespace(SOAP11NamespaceKey, SOAP11NamespaceValue);
        if (namespaces != null) {
            for (Map.Entry<String, String> namespace : namespaces.entrySet()) {
                xpath.addNamespace(namespace.getKey(), namespace.getValue());
            }
        }
        return evaluate(SOAPInput, xpath);
    }

    /**
     * Compares two OMElements. It compares attributes, namespaces, elements with trimmed values.
     *
     * @param elementOne first element to compare
     * @param elementTwo element to compare with the first
     * @return true if compares
     */
    public static boolean compareOMElements(OMElement elementOne, OMElement elementTwo) {
        return XMLComparator.compare(elementOne, elementTwo);
    }

    /**
     * Converts a String input to OM Element
     *
     * @param input String input. This should follow XML conventions
     * @return OMElement converted
     */
    public static OMElement StringASOM(String input) throws XMLStreamException {
        return AXIOMUtil.stringToOM(input);
    }

    private static OMElement evaluate(OMElement input, AXIOMXPath xpath) throws JaxenException {
        OMElement result = (OMElement) xpath.selectSingleNode(input);
        if (result == null) {
            log.info("XPATH [" + xpath + "] executed on input [" + input + "] does not return a result");
        }
        return result;
    }

}
