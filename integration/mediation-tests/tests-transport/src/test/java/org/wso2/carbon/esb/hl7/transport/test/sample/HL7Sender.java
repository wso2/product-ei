/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.hl7.transport.test.sample;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v21.message.ADT_A01;
import ca.uhn.hl7v2.parser.PipeParser;

import java.io.IOException;

public class HL7Sender {

	public String send(String host, int port) throws HL7Exception {
		System.out.println("[ Executing HL7Sender : HOST:" + host + "  ;port :" + port + " ]");
		// The connection hub connects to listening servers
		ConnectionHub connectionHub = ConnectionHub.getInstance();
		// A connection object represents a socket attached to an HL7 server
		Connection connection =
		                        connectionHub.attach(host, port, new PipeParser(),
		                                             MinLowerLayerProtocol.class);

		// The initiator is used to transmit unsolicited messages
		Initiator initiator = connection.getInitiator();
		ADT_A01 adt = new ADT_A01();
		try {
	        adt.initQuickstart("ADT", "A01", "T");
        } catch (IOException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
	
		String responseString = null;

		// send
		Message response = null;
		try {
			response = initiator.sendAndReceive(adt);
			PipeParser parser = new PipeParser();
			responseString = parser.encode(response);
			System.out.println("Received response:\n" + responseString);
		} catch (LLPException e) {
			System.out.println("Error : " + e);
		} catch (IOException e) {
			System.out.println("Error : " + e);
		}

		// Close the connection and server
		connectionHub.discard(connection);

		return responseString;
	}

}
