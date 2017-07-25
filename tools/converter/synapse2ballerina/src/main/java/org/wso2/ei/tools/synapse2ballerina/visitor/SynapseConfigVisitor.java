package org.wso2.ei.tools.synapse2ballerina.visitor;

import org.apache.synapse.Mediator;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.endpoints.HTTPEndpoint;
import org.apache.synapse.endpoints.IndirectEndpoint;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.mediators.builtin.CallMediator;
import org.apache.synapse.rest.API;
import org.apache.synapse.rest.Resource;
import org.ballerinalang.model.BallerinaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.util.Constant;
import org.wso2.ei.tools.synapse2ballerina.util.ArtifactMapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.APIWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.MediatorWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.ResourceWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.SequenceMediatorWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code SynapseConfigVisitor} class visits SynapseConfiguration to populate ballerina model
 */
public class SynapseConfigVisitor implements Visitor {

    private static Logger logger = LoggerFactory.getLogger(SynapseConfigVisitor.class);
    private static BallerinaASTModelBuilder ballerinaASTModelBuilder = new BallerinaASTModelBuilder();
    private Map<String, String> artifacts = ArtifactMapper.getEnumMap();
    private Map<String, Boolean> importTracker = new HashMap<String, Boolean>();
    private int resourceAnnotationCount = 0; //Keeps track of annotation count of a resource
    private String inboundMsg; //Holds inbound message variable name
    private int parameterCounter = 0; //For dynamic parameter name creation
    private int variableCounter = 0; //For dynamic variable name creation
    private String outboundMsg; //Holds outbound message variable name
    private int resourceCounter = 0; //For dynamic resource name creation
    private String connectorVarName; //For dynamic connector variable name creation
    private SynapseConfiguration synapseConfiguration;

    public BallerinaFile visit(SynapseConfiguration configuration) {

        ballerinaASTModelBuilder = new BallerinaASTModelBuilder();
        this.synapseConfiguration = configuration;

        for (API api : configuration.getAPIs()) {
            APIWrapper apiWrapper = new APIWrapper(api);
            apiWrapper.accept(this);
        }

        return ballerinaASTModelBuilder.buildBallerinaFile();
    }

    /**
     * Create ballerina http server connector
     *
     * @param api
     */
    public void visit(API api) {
        if (logger.isDebugEnabled()) {
            logger.debug("API");
        }
        addImport(Constant.BLANG_HTTP);
        ballerinaASTModelBuilder.startService();
        /*Create annotations belong to the service definition*/
        ballerinaASTModelBuilder
                .createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_CONFIG, Constant.BLANG_BASEPATH,
                        api.getContext());
        ballerinaASTModelBuilder.addAnnotationAttachment(1); //attributesCount is never used
        for (Resource resource : api.getResources()) {
            ResourceWrapper resourceWrapper = new ResourceWrapper(resource);
            resourceWrapper.accept(this);
            ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
            ballerinaASTModelBuilder.createSimpleVarRefExpr();
            ballerinaASTModelBuilder.createReplyStatement();
            ballerinaASTModelBuilder.endCallableBody();
            String resourceName = Constant.BLANG_RESOURCE_NAME + ++resourceCounter;
            ballerinaASTModelBuilder.endOfResource(resourceName, resourceAnnotationCount); //End of resource
            resourceAnnotationCount = 0;
        }
        String serviceName = api.getAPIName();
        ballerinaASTModelBuilder.endOfService(serviceName, Constant.BLANG_HTTP); //End of service
    }

    /**
     * Start ballerina resource
     *
     * @param resource
     */
    public void visit(Resource resource) {
        if (logger.isDebugEnabled()) {
            logger.debug("Resource");
        }
        ballerinaASTModelBuilder.startResource();
        String allowedMethods = Constant.BLANG_METHOD_GET; //Default http request method is set to GET
        if (resource.getMethods() != null) {
            for (String method : resource.getMethods()) {
                /*Create an annotation without attribute values*/
                ballerinaASTModelBuilder.createAnnotationAttachment(Constant.BLANG_HTTP, method, null, null);
                ballerinaASTModelBuilder.addAnnotationAttachment(0);
                resourceAnnotationCount++;
            }
        } else {
            ballerinaASTModelBuilder.createAnnotationAttachment(Constant.BLANG_HTTP, allowedMethods, null, null);
            ballerinaASTModelBuilder.addAnnotationAttachment(0);
            resourceAnnotationCount++;
        }

        //TODO: Add Path annotation

        //Add inbound message as a resource parameter
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MESSAGE); //type of the parameter
        inboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
        ballerinaASTModelBuilder.addParameter(0, false, inboundMsg);

        ballerinaASTModelBuilder.startCallableBody();
        //Create empty outbound message
        createVariableWithEmptyMap(Constant.BLANG_TYPE_MESSAGE, Constant.BLANG_VAR_RESPONSE + ++variableCounter, true);

        SequenceMediator sequenceMediator = resource.getInSequence();
        SequenceMediatorWrapper sequenceMediatorWrapper = new SequenceMediatorWrapper(sequenceMediator);
        sequenceMediatorWrapper.accept(this);
    }

    @Override
    public void visit(SequenceMediator sequenceMediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("SequenceMediator");
        }
        List<Mediator> mediatorList = sequenceMediator.getList();
        for (Mediator mediator : mediatorList) {
            MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
            mediatorWrapper.accept(this);
        }
    }

    /**
     * Get the appropriate internal wrapper and visit each mediator accordingly
     *
     * @param mediator
     */
    @Override
    public void visit(Mediator mediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("Mediator >> " + mediator.getType());
        }
        if (artifacts.get(mediator.getType()) != null) {
            Class<?> wrapperClass;
            try {
                wrapperClass = Class.forName(artifacts.get(mediator.getType()));
                Constructor constructor = wrapperClass.getConstructor(new Class[] { Mediator.class });
                Object object = constructor.newInstance(mediator);
                ((MediatorWrapper) object).accept(this);
            } catch (ClassNotFoundException e) {
                logger.error("Wrapper class not found for mediator", e);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                    InvocationTargetException e) {
                logger.error("Error when dynamically creating wrapper class", e);
            }
        } else {
            logger.info(mediator.getType() + " is not supported by synapse migration tool!");
        }
    }

    /**
     * Create ballerina http client connector
     *
     * @param mediator
     */
    @Override
    public void visit(CallMediator mediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("CallMediator");
        }
        /* Create reference type variable LHS */
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.createRefereceTypeName();
        /*Create an object out of above created ref type and initialize it with values*/
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();

        //TODO: Refactor this part to support other types of endpoints as well
        IndirectEndpoint indirectEndpoint = (IndirectEndpoint) mediator.getEndpoint();
        HTTPEndpoint endpoint = (HTTPEndpoint) synapseConfiguration.getLocalRegistry().get(indirectEndpoint.getKey());
        ballerinaASTModelBuilder.createStringLiteral(endpoint.getDefinition().getAddress());

        ballerinaASTModelBuilder.endExprList(1); // no of arguments
        ballerinaASTModelBuilder.initializeConnector(true); //arguments available
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;
        ballerinaASTModelBuilder.createVariable(connectorVarName, true);

        //Fill LHS - Assign response to outbound message
        ballerinaASTModelBuilder.createVariableRefList();
        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(1);

        //Fill RHS - Call client connector
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, connectorVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(Constant.DIVIDER);
        ballerinaASTModelBuilder.createNameReference(null, inboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(3);
        //TODO: Support for other http methods as well
        ballerinaASTModelBuilder.createAction(Constant.BLANG_CLIENT_CONNECTOR_GET_ACTION, true);
        ballerinaASTModelBuilder.createAssignmentStatement();
    }

    private void createVariableWithEmptyMap(String typeOfTheParamater, String variableName, boolean exprAvailable) {
        ballerinaASTModelBuilder.addTypes(typeOfTheParamater);
        ballerinaASTModelBuilder.startMapStructLiteral();
        ballerinaASTModelBuilder.createMapStructLiteral();
        ballerinaASTModelBuilder.createVariable(variableName, exprAvailable);
        outboundMsg = variableName;
    }

    /**
     * If ballerina package is not already added to import packages , add it
     *
     * @param packageName
     */
    private void addImport(String packageName) {
        if (importTracker.isEmpty() || importTracker.get(packageName) == null) {
            ballerinaASTModelBuilder
                    .addImportPackage(ballerinaASTModelBuilder.getBallerinaPackageMap().get(packageName), null);
            importTracker.put(packageName, true);
        }
    }
}
