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

package samples.mediators.extensions;

import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpringCustomLogger extends AbstractMediator {

    private static final Log log = LogFactory.getLog(SpringCustomLogger.class);
    private String userName;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /*This will log the username email that is initialised from Spring xml*/
    public boolean mediate(MessageContext synCtx) {

        log.info("Starting Spring Meditor");
        log.info("Bean in Initialized with User:["+userName+"]");
        log.info("E-MAIL:["+email+"]");
        log.info("Massage Id:  "+synCtx.getMessageID());
        log.info("Logged....");
        return true;
    }
}
