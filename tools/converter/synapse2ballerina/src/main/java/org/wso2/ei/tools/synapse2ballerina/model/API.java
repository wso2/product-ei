package org.wso2.ei.tools.synapse2ballerina.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://ws.apache.org/ns/synapse")
public class API {

    String name;
    String context;
    List<Resource> resourceList = new ArrayList<>();

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    @XmlAttribute
    public void setContext(String context) {
        this.context = context;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    @XmlElement(name="resource", type = Resource.class)
    public void setResource(Resource resource) {
        this.resourceList.add(resource);
    }

}
