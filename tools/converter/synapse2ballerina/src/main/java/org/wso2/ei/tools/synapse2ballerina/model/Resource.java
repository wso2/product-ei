package org.wso2.ei.tools.synapse2ballerina.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Resource {
    //todo introduce enum later
    List<String> methodList = new ArrayList<>();

    String methods;

    String uriTemplate;

    String urlMapping;

    List<Sequence> sequenceList = new ArrayList<>();



    public List<String> getMethodList() {
        return methodList;
    }

    @XmlAttribute(name = "methods")
    public void setMethods(String methods) {
        String[] split = methods.split(" ");
        methodList.addAll(Arrays.asList(split));
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    @XmlAttribute(name = "uri-template")
    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public String getUrlMapping() {
        return urlMapping;
    }

    @XmlAttribute(name = "url-mapping")
    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public List<Sequence> getSequenceList() {
        return sequenceList;
    }

    @XmlElements({
                            @XmlElement(name = "inSequence", type = Sequence.class),
                            @XmlElement(name = "outSequence", type = Sequence.class)
                    })
    public void setSequence (Sequence sequence) {
        this.sequenceList.add(sequence);
    }

}
