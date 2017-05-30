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
import org.wso2.ei.tools.mule2ballerina.model.Flow;
import org.wso2.ei.tools.mule2ballerina.model.GlobalConfiguration;
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
import java.util.List;
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
    private Root mRoot;
    private boolean flowStarted = false;
    private List<String> unIdentifiedElements;

    public ConfigReader() {
        mapperObject = new ElementMapper();
        attributeMapper = new AttributeMapper();
        mRoot = new Root();
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
                    loadIntermediateMuleObjects(startElement);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = xmlEvent.asEndElement();
                    checkFlowEnd(endElement);
                    break;

                default:
                    break;
                }
            }

        } catch (XMLStreamException e) {
            logger.error(e.toString());
        }
    }

    public InputStream getInputStream(String fileName) {
        File file = new File(fileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("Error");
        }
        //ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //InputStream inputStream = classloader.getResourceAsStream(fileName);
        return fis;
    }

    private void loadIntermediateMuleObjects(StartElement mElement) {
        String mElementName = getElementName(mElement);
        String mClassName = mapperObject.getElementToObjMapper().get(mElementName);

        if (mClassName != null) {
            Class<?> mClass = null;
            try {
                mClass = Class.forName(mClassName);
                populateMuleObject(mElement.getAttributes(), mClass);

            } catch (ClassNotFoundException e) {
                logger.error(e.toString());
            }
        } else {
            if (!Constant.MULE_TAG.equals(mElementName)) {
                unIdentifiedElements.add(mElementName);
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

    private String getElementOrAttributeName(QName qName) {
        String prefix = (qName != null ? qName.getPrefix() : "");
        String mainElement = (qName != null ? qName.getLocalPart() : "");
        String name = (prefix != null && !prefix.equals("") ? (prefix + ":" + mainElement) : mainElement);
        return name;
    }

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
                        if ("name".equals(property) && object instanceof GlobalConfiguration) {
                            mRoot.addGlobalConfigurationMap(attribute.getValue(), (GlobalConfiguration) object);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    logger.error(
                        " Ignoring NoSuchFieldException : There can be attributes in mule xml that we don't support "
                            + "in our objects" + e.getMessage());
                } catch (IllegalAccessException e) {
                    logger.error(e.toString());
                }
            });

            BaseObject muleObj = (BaseObject) object;

            buildMTree(muleObj);

        } catch (IllegalAccessException e) {
            logger.error(e.toString());
        } catch (InstantiationException e) {
            logger.error(e.toString());
        }
    }

    public void checkFlowEnd(EndElement endElement) {
        if (Constant.MULE_FLOW.equals(endElement.getName().toString())) {
            flowStarted = false;
        }
    }

    public Root getmRoot() {
        return mRoot;
    }

    public void setMuleRoot(Root muleRoot) {
        this.mRoot = muleRoot;
    }

    public List<String> getUnIdentifiedElements() {
        return unIdentifiedElements;
    }

    public void buildMTree(BaseObject muleObj) {
        if (muleObj instanceof GlobalConfiguration) {
            mRoot.addGlobalConfiguration((GlobalConfiguration) muleObj);
        }

        if (muleObj instanceof Flow) {
            Flow flow = (Flow) muleObj;
            mRoot.addMFlow(flow);
            flowStarted = true;
        }

        if (muleObj instanceof Processor) {
            if (flowStarted) {
                Flow lastAddedFlow = mRoot.getFlowList().peek();
                lastAddedFlow.addProcessor((Processor) muleObj);
            }
        }
    }
}
