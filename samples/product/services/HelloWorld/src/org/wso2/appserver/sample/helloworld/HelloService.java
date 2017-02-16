/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

package org.wso2.appserver.sample.helloworld;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.clustering.state.Replicator;
import org.apache.axis2.clustering.ClusteringFault;

import java.util.Iterator;
import java.util.Properties;

/**
 * Helloworld service implementation
 */
public class HelloService {
    private static final String HELLO_SERVICE_NAME = "HelloService.Name";

    public String greet(String name) {
        ServiceContext serviceContext =
                MessageContext.getCurrentMessageContext().getServiceContext();
        serviceContext.setProperty(HELLO_SERVICE_NAME, name);
        try {
            Replicator.replicate(serviceContext, new String[]{HELLO_SERVICE_NAME});
        } catch (ClusteringFault clusteringFault) {
            clusteringFault.printStackTrace();
        }

        if (name != null) {
            return "Hello World, " + name + " !!!";
        } else {
            return "Hello World !!!";
        }
    }
}
