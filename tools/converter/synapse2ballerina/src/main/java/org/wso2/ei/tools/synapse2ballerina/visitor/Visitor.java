package org.wso2.ei.tools.synapse2ballerina.visitor;

import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.mediators.builtin.CallMediator;
import org.apache.synapse.rest.API;
import org.apache.synapse.rest.Resource;

/**
 * {@code Visitor} interface for SynapseConfigVisitor
 */
public interface Visitor {

     void visit(API api);

     void visit(Resource api);

     void visit(SequenceMediator sequenceMediator);

     void visit(Mediator mediator);

     void visit(CallMediator callMediator);
}
