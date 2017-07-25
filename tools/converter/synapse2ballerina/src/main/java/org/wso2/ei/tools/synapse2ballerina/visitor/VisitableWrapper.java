package org.wso2.ei.tools.synapse2ballerina.visitor;

/**
 * {@code VisitableWrapper} wrapper classes that needs to be visited needs to implement this interface
 */
public interface VisitableWrapper {
    void accept(Visitor visitor);
}
