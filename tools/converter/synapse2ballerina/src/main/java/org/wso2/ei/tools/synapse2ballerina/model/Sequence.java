package org.wso2.ei.tools.synapse2ballerina.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Sequence {
    String name;
    String type;
    List<Mediator> mediatorList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Mediator> getMediatorList() {
        return mediatorList;
    }


    @XmlElements({@XmlElement(name="call", type = CallMediator.class),
                  @XmlElement(name="respond", type = RespondMediator.class),
                  @XmlElement(name="payloadFactory", type = PayloadFactoryMediator.class)
                 })
    public void setMediator(Mediator mediator) {
        this.mediatorList.add(mediator);
    }

}
