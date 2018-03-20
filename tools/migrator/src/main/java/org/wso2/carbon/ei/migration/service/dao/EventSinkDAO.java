/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.ei.migration.service.dao;

import org.apache.commons.io.FilenameUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.wso2.carbon.ei.migration.util.Utility;
import org.wso2.carbon.event.sink.EventSink;
import org.wso2.carbon.event.sink.EventSinkException;
import org.wso2.carbon.event.sink.config.EventSinkConfigBuilder;
import org.wso2.carbon.event.sink.config.EventSinkConfigXml;
import org.wso2.carbon.event.sink.config.EventSinkXmlReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access layer for event sink
 */
public class EventSinkDAO {

    private static EventSinkDAO instance = new EventSinkDAO();

    private EventSinkDAO() {

    }

    public static EventSinkDAO getInstance() {
        return instance;
    }

    /**
     * Obtain all the Event Sinks
     *
     * @return EventSinks List
     */
    public List<EventSink> getAllEventSinks() throws EventSinkException {

        List<EventSink> eventSinks = new ArrayList<>();
        String filePath = EventSinkXmlReader.getTenantDeployementDirectoryPath();
        File[] files = new File(filePath).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    EventSink eventSink = getEventSinkFromName(file.getName());
                    eventSinks.add(eventSink);
                }
            }
        }
        return eventSinks;
    }

    /**
     * Get the EventSink object from the configuration file
     *
     * @param name
     * @return EventSink
     * @throws EventSinkException
     */
    public EventSink getEventSinkFromName(String name) throws EventSinkException {
        EventSinkConfigBuilder eventSinkConfigBuilder = new EventSinkConfigBuilder();
        EventSink eventSink = new EventSink();
        String filePath = EventSinkXmlReader.getTenantDeployementDirectoryPath();
        File eventSinkFile = new File(filePath + name);
        if (eventSinkFile.exists()) {
            eventSink.setName(eventSinkFile.getName());
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(eventSinkFile);
                eventSink = eventSinkConfigBuilder.createEventSinkConfig(Utility.toOM(fileInputStream),
                        FilenameUtils.removeExtension(eventSink.getName()));
            } catch (FileNotFoundException e) {
                throw new EventSinkException("File not found. File: " + eventSinkFile.getName() + ", Error : " + e);
            } catch (EventSinkException e) {
                throw new EventSinkException("Error occured in Obtaining Event Sink. With name : " + eventSink.getName()
                        + ", Error: " + e);
            } catch (XMLStreamException e) {
                throw new EventSinkException("Error creating a OMElement from an input stream : " + e);
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException var19) {
                    throw new EventSinkException("Error Occured while closing FileInputStream , Error : " + var19);
                }

            }
        }
        return eventSink;
    }

    /**
     * Writes given Event Sink details to xml file
     *
     * @param eventSink the Event Sink to be write
     */
    public boolean writeEventSink(EventSink eventSink) throws EventSinkException {
        String filePath;
        filePath = EventSinkXmlReader.getTenantDeployementDirectoryPath();
        this.createEventSinkDirectory(filePath);
        EventSinkConfigXml eventSinkConfigXml = new EventSinkConfigXml();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter =
                    new BufferedWriter(new FileWriter(new File(filePath, eventSink.getName() + ".xml")));
            String unFormattedXml = eventSinkConfigXml.buildEventSink(eventSink.getUsername(), eventSink.getPassword(),
                    eventSink.getReceiverUrlSet(), eventSink.getAuthenticationUrlSet()).toString();
            ///formatting xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(unFormattedXml));
            final Document document = db.parse(is);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(100);
            format.setIndenting(true);
            format.setIndent(4);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            bufferedWriter.write(out.toString());

        } catch (FileNotFoundException e) {
            throw new EventSinkException("Failed to open file to write event sink. File: " + filePath + ", " +
                    "ERROR: " + e);
        } catch (IOException e) {
            throw new EventSinkException("Failed to write event sink to file. File: " + filePath + ", ERROR: " + e);
        } catch (ParserConfigurationException e) {
            throw new EventSinkException("Internal error occurred while writing event sink. Failed to format XML. " +
                    "ERROR: " + e);
        } catch (SAXException e) {
            throw new EventSinkException("Internal error occurred while writing event sink. Invalid XML. ERROR: " + e);
        } finally {
            if (bufferedWriter != null)
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    return true;
                } catch (IOException e) {
                    throw new EventSinkException("Failed to close stream, ERROR: " + e);
                }
        }
        return false;
    }

    /**
     * Creates a directory in the specified location
     *
     * @param filePath location the directory should be created
     */
    private boolean createEventSinkDirectory(String filePath) throws EventSinkException {
        File eventSinksDir = new File(filePath);
        if (!eventSinksDir.exists()) {
            try {
                boolean mkdir = eventSinksDir.mkdir();
                if (mkdir) {
                    return true;
                }
            } catch (SecurityException se) {
                throw new EventSinkException("Couldn't create event-Sinks directory in following location" + filePath +
                        " with ERROR : " + se);
            }
        }
        return false;
    }
}
