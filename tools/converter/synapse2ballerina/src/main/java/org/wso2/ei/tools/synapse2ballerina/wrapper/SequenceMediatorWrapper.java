package org.wso2.ei.tools.synapse2ballerina.wrapper;

import org.apache.synapse.mediators.base.SequenceMediator;
import org.wso2.ei.tools.synapse2ballerina.visitor.VisitableWrapper;
import org.wso2.ei.tools.synapse2ballerina.visitor.Visitor;

/**
 * {@code SequenceMediatorWrapper} Wrapper class for synapse SequenceMediator
 */
public class SequenceMediatorWrapper implements VisitableWrapper {

    private SequenceMediator sequenceMediator;

    public SequenceMediatorWrapper(SequenceMediator original) {
        this.sequenceMediator = original;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(sequenceMediator);
    }
}
