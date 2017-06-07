/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.tools.mule2ballerina.configreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.mule2ballerina.elementmapper.AttributeMapper;
import org.wso2.ei.tools.mule2ballerina.elementmapper.ElementMapper;
import org.wso2.ei.tools.mule2ballerina.model.BaseObject;
import org.wso2.ei.tools.mule2ballerina.model.Comment;
import org.wso2.ei.tools.mule2ballerina.model.Flow;
import org.wso2.ei.tools.mule2ballerina.model.GlobalConfiguration;
import org.wso2.ei.tools.mule2ballerina.model.Inbound;
import org.wso2.ei.tools.mule2ballerina.model.Processor;
import org.wso2.ei.tools.mule2ballerina.model.Root;
import org.wso2.ei.tools.mule2ballerina.util.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * {@code ConfigReader} class reads mule configuration file and builds the Mule tree
 */
public class ConfigReader {

    private static Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    private ElementMapper mapperObject;
    private AttributeMapper attributeMapper;
    private Root rootObj;
    private boolean flowStarted = false;
    private List<String> unIdentifiedElements;

    public ConfigReader() {
        mapperObject = new ElementMapper();
        attributeMapper = new AttributeMapper();
        rootObj = new Root();
        unIdentifiedElements = new ArrayList<String>();
    }

    public void readXML(InputStream inputStream) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(inputStream);

            while (eventReader.hasNext()) {
                XMLEvent xmlEvent = eventReader.nextEvent();
                switch (xmlEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = xmlEvent.asStartElement();
                    checkFlowStart(getElementOrAttributeName(startElement.getName()));
                    loadIntermediateMuleObjects(startElement);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = xmlEvent.asEndElement();
                    checkFlowEnd(getElementOrAttributeName(endElement.getName()));
                    break;

                default:
                    break;
                }
            }
        } catch (XMLStreamException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Given a mule configuration file get it's inputstream
     *
     * @param file Mule configuration file
     * @return input stream
     */
    public InputStream getInputStream(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return fileInputStream;
    }

    /**
     * Populate relevant intermediate object that is mapped to mule element
     * If the mule element is not mapped to an object, put it in an unidentified element list and make a comment in
     * ballerina code specifying that feature should be manually handled.
     *
     * @param mElement represents any mule element
     */
    private void loadIntermediateMuleObjects(StartElement mElement) {
        String mElementName = getElementName(mElement);
        String mClassName = mapperObject.getElementToObjMapper().get(mElementName);
        Class<?> intermediateClass = null;
        if (mClassName != null) {
            try {
                intermediateClass = Class.forName(mClassName);
                populateMuleObject(mElement.getAttributes(), intermediateClass);

            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            if (!Constant.MULE_TAG.equals(mElementName)) {
                unIdentifiedElements.add(mElementName);
                Comment comment = new Comment();
                comment.setComment(" //Functionality provided by " + mElementName + " should be handled manually here");
                buildMuleTree(comment);
            }
        }
    }

    private String getElementName(StartElement muleElement) {
        QName qName = (muleElement != null ? muleElement.getName() : null);
        return getElementOrAttributeName(qName);
    }

    private String getAttributeName(Attribute attribute) {
        QName qName = (attribute != null ? attribute.getName() : null);
        return getElementOrAttributeName(qName);
    }

    /**
     * Given a mule element or an attribute get string value of it with the prefix attached to it
     *
     * @param qName
     * @return
     */
    private String getElementOrAttributeName(QName qName) {
        String prefix = (qName != null ? qName.getPrefix() : "");
        String mainElement = (qName != null ? qName.getLocalPart() : "");
        String name = (prefix != null && !prefix.equals("") ? (prefix + ":" + mainElement) : mainElement);
        return name;
    }

    /**
     * Populate intermediate object properties with mule attribute values
     *
     * @param attributes List of attributes associate with a mule element
     * @param mClass     Intermediate class that is mapped to the mule element
     */
    private void populateMuleObject(Iterator<Attribute> attributes, Class<?> mClass) {
        try {
            java.lang.Object object = mClass.newInstance();

            attributes.forEachRemaining(attribute -> {
                try {
                    String property = attributeMapper.getmAttributeMapper().get(getAttributeName(attribute));
                    if (property != null) {
                        Field field = mClass.getDeclaredField(property);
                        field.setAccessible(true);
                        field.set(object, attribute.getValue());

                        /*if the element is a global configuration keep it against it's name as this will
                        * be useful when navigating the processors to identify their global configuration
                        */
                        if ("name".equals(property) && object instanceof GlobalConfiguration) {
                            rootObj.addGlobalConfigurationMap(attribute.getValue(), (GlobalConfiguration) object);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    logger.warn(" Ignoring NoSuchFieldException : There can be attributes in mule xml that is not "
                            + "mapped " + e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            });

            BaseObject muleObj = (BaseObject) object;
            buildMuleTree(muleObj);

        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void checkFlowStart(String startElement) {
        if (Constant.MULE_FLOW.equals(startElement)) {
            flowStarted = true;
        }
    }

    public void checkFlowEnd(String endElement) {
        if (Constant.MULE_FLOW.equals(endElement)) {
            flowStarted = false;
        }
    }

    public Root getRootObj() {
        return rootObj;
    }

    public void setRootObj(Root rootObj) {
        this.rootObj = rootObj;
    }

    public List<String> getUnIdentifiedElements() {
        return unIdentifiedElements;
    }

    /**
     * Build intermediate object tree required for navigation
     *
     * @param muleObj any intermediate object
     */
    public void buildMuleTree(BaseObject muleObj) {

        /* if the intermediate object represents a global configuration in mule, add it to global config list
        * Further if it's an inbound config, keep all the flows that belong to that config in a map, as it is needed
        * to determine the end of service point in ballerina stack
        */
        if (muleObj instanceof GlobalConfiguration) {
            rootObj.addGlobalConfiguration((GlobalConfiguration) muleObj);
            if (muleObj instanceof Inbound) {
                Queue<Flow> flowQueue = null;
                if (rootObj.getServiceMap() != null) {
                    Inbound inboundObj = (Inbound) muleObj;
                    flowQueue = rootObj.getServiceMap().get(inboundObj.getName());
                    if (flowQueue == null) {
                        flowQueue = new LinkedList<Flow>();
                        rootObj.getServiceMap().put(inboundObj.getName(), flowQueue);
                    }
                }
            }
        }

        /* Keep a list of flows separately for tree navigation*/
        if (muleObj instanceof Flow) {
            Flow flow = (Flow) muleObj;
            if (flowStarted) {
                rootObj.addMFlow(flow);
            }
        }

        /* If the intermediate object is a processor within a flow add it to the correct flow
        *  If it's an inbound connector, add the flow which has that connector to the global config map
        */
        if (muleObj instanceof Processor) {
            if (flowStarted) {
                Flow lastAddedFlow = rootObj.getFlowList().peek();
                lastAddedFlow.addProcessor((Processor) muleObj);
                if (muleObj instanceof Inbound) {
                    Queue<Flow> flowQueue = null;
                    if (rootObj.getServiceMap() != null) {
                        Inbound inboundObj = (Inbound) muleObj;
                        flowQueue = rootObj.getServiceMap().get(inboundObj.getName());
                        if (flowQueue == null) {
                            flowQueue = new LinkedList<Flow>();
                            flowQueue.add(lastAddedFlow);
                            rootObj.getServiceMap().put(inboundObj.getName(), flowQueue);
                        } else {
                            flowQueue.add(lastAddedFlow);
                        }
                    }
                }
            }
        }
    }
}
