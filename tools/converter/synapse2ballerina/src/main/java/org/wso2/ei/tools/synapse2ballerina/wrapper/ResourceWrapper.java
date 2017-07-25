package org.wso2.ei.tools.synapse2ballerina.wrapper;

import org.apache.synapse.rest.Resource;
import org.wso2.ei.tools.synapse2ballerina.visitor.VisitableWrapper;
import org.wso2.ei.tools.synapse2ballerina.visitor.Visitor;

/**
 * {@code ResourceWrapper} Wrapper class for synapse Resource
 */
public class ResourceWrapper implements VisitableWrapper {

    private Resource resource;

    public ResourceWrapper(Resource original) {
        this.resource = original;
    }

    public void accept(Visitor visitor) {
        visitor.visit(resource);
    }
}
