/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.mediators;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;

/**
 * This mediator analyze a binary message and convert binary to a given datatype and set is as a message property.
 * User can use the message property for CBR.
 * User has to set the offset : where to start
 *                     length : how many bytes to read
 *                     binaryEncodig : utf-8, utf-16, ASCII, Base64
 *                     VariableName : property name set with the decoded value in the message context
 * These values should set as parameters from the synapse configuration layer.
 */
public class BinaryExtractMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog( BinaryExtractMediator.class);
    private static final String PROP_NAME = "SearchKey";

    private int length=1;
    private int offset=1;
    private int dataType=1;       // Not using this is supporting only char[]/String data types
    private String binaryEncoding="utf-8";
    private String variableName= PROP_NAME;

    public BinaryExtractMediator(){}

    public boolean mediate(MessageContext msgCtx) {
        try {
            log.debug("BinaryExtractMediator Process, with offset: "+offset+" ,length "+length);
            SOAPBody soapBody = msgCtx.getEnvelope().getBody();
            OMElement firstElement = soapBody.getFirstElement();
            log.debug("First Element : "+firstElement.getLocalName());
            log.debug("First Element Text : "+firstElement.getText());
            OMText binaryNode =(OMText) firstElement.getFirstOMChild();
            log.debug("First Element Node Text : "+binaryNode.getText());
            DataHandler dataHandler =(DataHandler) binaryNode.getDataHandler();
            InputStream inputStream = dataHandler.getInputStream();
            byte[] searchByte = new byte[length];
            inputStream.skip(offset - 1);
            int readBytes = inputStream.read(searchByte,0,length);
            String outString = new String(searchByte,binaryEncoding);
            msgCtx.setProperty(variableName,outString);
            log.debug("Set property to MsgCtx, "+variableName+" = "+outString);
            inputStream.close();
        } catch (IOException e) {
            log.error("Excepton on mediation : "+e.getMessage());
        }
        return true;
    }

    public String getType() {
        return null;
    }

    public void setTraceState(int traceState) {
        this.traceState = traceState;
    }

    public int getTraceState() {
        return traceState;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getBinaryEncoding() {
        return binaryEncoding;
    }

    public void setBinaryEncoding(String binaryEncoding) {
        this.binaryEncoding = binaryEncoding;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
}
