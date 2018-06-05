/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.service.mgt;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.neethi.PolicyReference;
import org.apache.neethi.PolicyRegistry;
import org.wso2.carbon.utils.xml.XMLPrettyPrinter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PolicyUtil {

    private static final String EMPTY_POLICY =
            "<wsp:Policy xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\" />";

    private PolicyUtil() {}

    public static OMElement getPolicyAsOMElement(Policy policy) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(baos);
            policy.serialize(writer);
            writer.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            XMLStreamReader xmlStreamReader =
                    XMLInputFactory.newInstance().createXMLStreamReader(bais);
            StAXOMBuilder staxOMBuilder =
                    (StAXOMBuilder) OMXMLBuilderFactory.createStAXOMBuilder(
                            OMAbstractFactory.getOMFactory(), xmlStreamReader);
            return staxOMBuilder.getDocumentElement();

        } catch (Exception ex) {
            throw new RuntimeException("can't convert the policy to an OMElement", ex);
        }
    }

    public static String getPolicyAsString(Policy policy) {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        try {
            XMLStreamWriter writer =
                    XMLOutputFactory.newInstance().createXMLStreamWriter(outBuffer);
            policy.serialize(writer);
            writer.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(outBuffer.toByteArray());
            XMLPrettyPrinter xmlPrettyPrinter = new XMLPrettyPrinter(bais);
            return xmlPrettyPrinter.xmlFormat();

        } catch (XMLStreamException e) {
            throw new RuntimeException("Serialization of Policy object failed " + e);
        }

    }

    public static Policy getPolicyFromOMElement(OMElement policyElement) {
        return PolicyEngine.getPolicy(policyElement);
    }

    public static OMElement getEmptyPolicyAsOMElement() {
        ByteArrayInputStream bais = new ByteArrayInputStream(EMPTY_POLICY.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        return getPolicyAsOMElement(policy);
    }

    public static OMElement getWrapper(String name) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace namespace = fac.createOMNamespace("", name);
        OMElement element = fac.createOMElement("Policy", namespace);
        OMAttribute attribute = fac.createOMAttribute("name", namespace, name);
        element.addAttribute(attribute);

        return element;
    }

    public static String[] processPolicyElements(Iterator policyIterator, PolicyRegistry registry) {
        List<String> policyList = new ArrayList<String>();
        while (policyIterator.hasNext()) {
            Object value = policyIterator.next();

            if (value instanceof Policy) {
                Policy policy = (Policy) value;
                policyList.add(PolicyUtil.getPolicyAsString(policy));
            } else if (value instanceof PolicyReference) {

                PolicyReference policyReference = (PolicyReference) value;
                Policy policy = registry.lookup(policyReference.getURI());

                if (policy == null) {
                    throw new RuntimeException(policyReference.getURI() +
                                               " cannot be resolved");
                }
                policyList.add(PolicyUtil.getPolicyAsString(policy));
            }
        }

        return policyList.toArray(new String[policyList.size()]);

    }

}
