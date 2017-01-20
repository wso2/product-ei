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

package samples.userguide;

import org.apache.synapse.config.AbstractSynapseObserver;
import org.apache.synapse.config.Entry;
import org.apache.synapse.Mediator;
import org.apache.synapse.Startup;
import org.apache.synapse.eventing.SynapseEventSource;
import org.apache.synapse.core.axis2.ProxyService;
import org.apache.synapse.endpoints.Endpoint;

/**
 * This sample observer implementation simply calls the event handlers defined
 * in the AbstractSynapseObserver super class.
 */
public class SimpleLoggingObserver extends AbstractSynapseObserver {

    public SimpleLoggingObserver() {
        super();
        log.info("Simple logging observer initialized...Capturing Synapse events...");
    }
}
