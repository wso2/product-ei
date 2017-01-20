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
package org.wso2.carbon.esb.hl7.inbound.transport.test;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;

import java.io.IOException;

public class HL7InboundTestSender {

    private static String OULMessage = "MSH|^~\\&|ABCDEF|ABCDEFGH|WSO2HL7TEST|ABCDEFG|20150112100451.0880+0100||OUL^R22^OUL_R22|HL7Abc000BBB12345D22|P|2.5||||||8859/1\n" +
            "PID|1||AAAAAA^^^AA^NNBFH~1111^^^ABC^DI^BKK~00280168^^^LAB^LAB~48P00480513^^^CS^SS~80380000100024916352^^^100^HC||XXXXXX^XXXX||19311126|F|||XXX XXX XXX^^Jambugasmulla^^10250^^H^^078332~XXX XXX XXX^^Nugegoda^^13847^^L^^096074~^^^^^^B^^002155||015 706187^ORN^PH^^^^^^^^^015 706187~LANKA 9988^ATT^LK^^^^^^^^^LANKA 9988|015 706187||||AAAAAA|99A12345678||||002155|||100\n" +
            "PV1|1|E|1640101^^^^^^^^BARRACUDA VALKYRIE||||||||||||||||15100225|||||||||||||||||||||||||20150109\n" +
            "SPM|1|00107616201||GLO^SOTON GOLD|||||||||||||20150112080000";

	public String send(String host, int port) throws HL7Exception, IOException, LLPException {
        HapiContext context = new DefaultHapiContext();
        Connection c = context.newClient(host, port, false);
        Initiator initiator = c.getInitiator();

        ADT_A01 msg = new ADT_A01();
        msg.initQuickstart("ADT", "A01", "T");
        Message resp = initiator.sendAndReceive(msg);
        return resp.encode();
	}

    public String send(String host, int port, Message message) throws HL7Exception, IOException, LLPException {
        HapiContext context = new DefaultHapiContext();
        Connection c = context.newClient(host, port, false);
        Initiator initiator = c.getInitiator();

        Message resp = initiator.sendAndReceive(message);
        return resp.encode();
    }

    public Message getOULMessage() throws HL7Exception {
        PipeParser parser = new PipeParser();
        return parser.parse(OULMessage);
    }

    public static void main (String[] args) {
        HL7InboundTestSender sender = new HL7InboundTestSender();
        try {
            String resp = sender.send("localhost", 20001, sender.getOULMessage());
            System.out.println(resp);
        } catch (HL7Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LLPException e) {
            e.printStackTrace();
        }
    }
}
