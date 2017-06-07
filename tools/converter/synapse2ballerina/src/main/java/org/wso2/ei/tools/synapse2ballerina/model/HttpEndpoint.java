package org.wso2.ei.tools.synapse2ballerina.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "http")
public class HttpEndpoint implements Endpoint {

    String uriTemplate;


    public String getUriTemplate() {
        return uriTemplate;
    }

    @XmlAttribute(name = "uri-template")
    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

}
