package org.wso2.ei.tools.synapse2ballerina.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class EndpointElement  {

Endpoint endpoint;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @XmlElements(
            {@XmlElement(name="http",type = HttpEndpoint.class)
            })
    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }
}
