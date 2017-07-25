package org.wso2.ei.tools.synapse2ballerina.wrapper;

import org.apache.synapse.rest.API;
import org.wso2.ei.tools.synapse2ballerina.visitor.VisitableWrapper;
import org.wso2.ei.tools.synapse2ballerina.visitor.Visitor;

/**
 * {@code APIWrapper} Wrapper class for synapse API
 */
public class APIWrapper implements VisitableWrapper {

    private API api;

    public APIWrapper(API original) {
        this.api = original;
    }

    public void accept(Visitor visitor) {
        visitor.visit(api);
    }

}
