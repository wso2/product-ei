package org.wso2.ei.tools.synapse2ballerina.wrapper;

import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.builtin.CallMediator;
import org.wso2.ei.tools.synapse2ballerina.visitor.Visitor;

/**
 * {@code CallMediatorWrapper} wrapper for synapse CallMediator
 */
public class CallMediatorWrapper extends MediatorWrapper {

    private CallMediator mediator;

    public CallMediatorWrapper(Mediator original) {
        super(original);
        if (original instanceof CallMediator) {
            this.mediator = (CallMediator) original;
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(mediator);
    }
}
