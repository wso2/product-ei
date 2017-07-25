package org.wso2.ei.tools.synapse2ballerina.wrapper;

import org.apache.synapse.Mediator;
import org.wso2.ei.tools.synapse2ballerina.visitor.VisitableWrapper;
import org.wso2.ei.tools.synapse2ballerina.visitor.Visitor;

/**
 * {@code MediatorWrapper} Wrapper class for synapse Mediator
 */
public class MediatorWrapper implements VisitableWrapper {

    private Mediator mediator;

    public MediatorWrapper(Mediator original) {
        this.mediator = original;
    }

    public void accept(Visitor visitor) {
        visitor.visit(mediator);
    }

}
