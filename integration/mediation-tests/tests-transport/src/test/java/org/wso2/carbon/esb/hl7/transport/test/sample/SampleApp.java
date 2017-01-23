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
package org.wso2.carbon.esb.hl7.transport.test.sample;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.app.DefaultApplication;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.PipeParser;

import java.io.IOException;

public class SampleApp implements Application {

    public boolean canProcess(Message theIn) {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public Message processMessage(Message theIn) throws ApplicationException, HL7Exception {

        String encodedMessage = new PipeParser().encode(theIn);
        System.out.println("Received message:\n" + encodedMessage + "\n\n");

        // Now we need to generate a message to return. This will generally be an ACK message.
        Segment msh = (Segment) theIn.get("MSH");
        Message retVal;
        try {
            // This method takes in the MSH segment of an incoming message, and generates an
            // appropriate ACK
            retVal = DefaultApplication.makeACK(msh);
        } catch (IOException e) {
            throw new HL7Exception(e);
        }

        return retVal;
    }

}
