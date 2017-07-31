/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.tools.synapse2ballerina.visitor;

import org.apache.synapse.Mediator;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.config.xml.AnonymousListMediator;
import org.apache.synapse.config.xml.SwitchCase;
import org.apache.synapse.endpoints.HTTPEndpoint;
import org.apache.synapse.endpoints.IndirectEndpoint;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.mediators.builtin.CallMediator;
import org.apache.synapse.mediators.builtin.RespondMediator;
import org.apache.synapse.mediators.filters.SwitchMediator;
import org.apache.synapse.mediators.transform.PayloadFactoryMediator;
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
            logger.debug("Mediator >> " + mediator.getType() + " or " + mediator.getMediatorName());
        }
        //Mediator name needs to be checked, in case of payloadfactory mediator
        if ((artifacts.get(mediator.getType()) != null) || (artifacts.get(mediator.getMediatorName()) != null)) {
            Class<?> wrapperClass;
            try {
                if (artifacts.get(mediator.getType()) != null) {
                    wrapperClass = Class.forName(artifacts.get(mediator.getType()));
                } else {
                    wrapperClass = Class.forName(artifacts.get(mediator.getMediatorName()));
                }

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
            ballerinaASTModelBuilder
                    .addComment(mediator.getType() + " is not supported by synapse migration tool! " + "yet");
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

    /**
     * Ballerina Reply for a resource
     *
     * @param respondMediator
     */
    @Override
    public void visit(RespondMediator respondMediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("RespondMediator");
        }

        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createReplyStatement();
        ballerinaASTModelBuilder.endCallableBody();
    }

    /**
     * Set the json or xml payload
     *
     * @param payloadFactoryMediator
     */
    @Override
    public void visit(PayloadFactoryMediator payloadFactoryMediator) {
        addImport(Constant.BLANG_PKG_MESSAGES);
        String payloadVariableName = "";
        if (org.wso2.ei.tools.synapse2ballerina.util.Constant.JSON.equals(payloadFactoryMediator.getType())) {
            ballerinaASTModelBuilder.addComment(Constant.BLANG_COMMENT_JSON);
            ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_JSON); //type of the variable
            ballerinaASTModelBuilder.createStringLiteral(payloadFactoryMediator.getFormat());
            payloadVariableName = Constant.BLANG_VAR_JSON_PAYLOAD + ++variableCounter;
            ballerinaASTModelBuilder.createVariable(payloadVariableName, true); //name of the variable
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_JSON_PAYLOAD);

        } else if (org.wso2.ei.tools.synapse2ballerina.util.Constant.XML.equals(payloadFactoryMediator.getType())) {
            ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_XML); //type of the variable
            ballerinaASTModelBuilder.addComment("//IMPORTANT: Change the double quotes to back tick. ");
            ballerinaASTModelBuilder.createXMLLiteral(payloadFactoryMediator.getFormat());
            payloadVariableName = Constant.BLANG_VAR_XML_PAYLOAD + ++variableCounter;
            ballerinaASTModelBuilder.createVariable(payloadVariableName, true); //name of the variable
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_XML_PAYLOAD);
        }

        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createNameReference(null, payloadVariableName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.createFunctionInvocation(true);
    }

    /**
     * SwitchMediator maps to ballerina if else clause
     *
     * @param switchMediator
     */
    @Override
    public void visit(SwitchMediator switchMediator) {

        //Identify whether this is header based or content based routing
        String expressionStr = switchMediator.getSource().getExpression();
        String headerName = "";
        String variableName = "";

        if (expressionStr != null) {
            if (expressionStr.startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.HEADER_IDENTIFIER_1)) {
                //header based routing
                headerName = expressionStr.substring(5);
                addImport(Constant.BLANG_PKG_MESSAGES);
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                getHeaderValues(variableName, headerName);

            } else if (expressionStr
                    .startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.HEADER_IDENTIFIER_2)) {
                //header based routing
                headerName = expressionStr.substring(25);
                //Remove last bracket
                headerName = headerName.substring(0, headerName.length() - 1);
                addImport(Constant.BLANG_PKG_MESSAGES);
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                getHeaderValues(variableName, headerName);

            }
            String pathType = switchMediator.getSource().getPathType();
            if (org.wso2.ei.tools.synapse2ballerina.util.Constant.JSON_PATH.equals(pathType)) {
                //content based routing - json
                addImport(Constant.BLANG_PKG_MESSAGES);
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                getPayload(Constant.BLANG_TYPE_JSON, variableName, Constant.BLANG_GET_JSON_PAYLOAD, expressionStr);
                variableName = getPathValue(Constant.BLANG_TYPE_JSON, variableName, expressionStr,
                        Constant.BLANG_PKG_JSON);

            } else if (org.wso2.ei.tools.synapse2ballerina.util.Constant.XML_PATH.equals(pathType)) {
                //content based routing - xml
                addImport(Constant.BLANG_PKG_MESSAGES);
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                getPayload(Constant.BLANG_TYPE_XML, variableName, Constant.BLANG_GET_XML_PAYLOAD, expressionStr);
                variableName = getPathValue(Constant.BLANG_TYPE_XML, variableName, expressionStr,
                        Constant.BLANG_PKG_XML);
            }
        }

        ballerinaASTModelBuilder.addComment("//IMPORTANT: Please make sure the conditional expressions are correct.");

        //Inside if
        ballerinaASTModelBuilder.enterIfStatement();
        ballerinaASTModelBuilder.createNameReference(null,
                variableName + org.wso2.ei.tools.synapse2ballerina.util.Constant.EQUALS_SIGN +
                        Constant.QUOTE_STR + switchMediator.getCases().get(0).getRegex() + Constant.QUOTE_STR);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        AnonymousListMediator anonymousListMediator = switchMediator.getCases().get(0).getCaseMediator();
        List<Mediator> mediatorList = anonymousListMediator.getList();
        for (Mediator mediator : mediatorList) {
            MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
            mediatorWrapper.accept(this);
        }
        ballerinaASTModelBuilder.exitIfClause();

        //Inside else if
        if (switchMediator.getCases().size() > 1) {
            for (int i = 1; i < switchMediator.getCases().size(); i++) {
                ballerinaASTModelBuilder.enterElseIfClause();
                ballerinaASTModelBuilder.createNameReference(null,
                        variableName + org.wso2.ei.tools.synapse2ballerina.util.Constant.EQUALS_SIGN +
                                Constant.QUOTE_STR + switchMediator.getCases().get(i).getRegex() + Constant.QUOTE_STR);
                ballerinaASTModelBuilder.createSimpleVarRefExpr();
                AnonymousListMediator anonymousList = switchMediator.getCases().get(i).getCaseMediator();
                List<Mediator> caseMediators = anonymousList.getList();
                for (Mediator mediator : caseMediators) {
                    MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
                    mediatorWrapper.accept(this);
                }
                ballerinaASTModelBuilder.exitElseIfClause();
            }
        }

        //inside else
        ballerinaASTModelBuilder.enterElseClause();
        SwitchCase switchCase = switchMediator.getDefaultCase();
        AnonymousListMediator defaultAnonymousMediator = switchCase.getCaseMediator();
        List<Mediator> defaultCaseMediators = defaultAnonymousMediator.getList();
        for (Mediator mediator : defaultCaseMediators) {
            MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
            mediatorWrapper.accept(this);
        }
        ballerinaASTModelBuilder.exitElseClause();

        ballerinaASTModelBuilder.exitIfElseStatement();
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

    /**
     * Get header values
     *
     * @param variableName
     * @param header
     */
    private void getHeaderValues(String variableName, String header) {
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_GET_HEADER);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, inboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(header);
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(variableName, true); //name of the variable
        //  ballerinaASTModelBuilder.addParameter(0,true,inboundMsg);
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Get payload
     *
     * @param type
     * @param variableName
     * @param functionName
     */
    private void getPayload(String type, String variableName, String functionName, String expressionStr) {
        ballerinaASTModelBuilder.addTypes(type); //type of the variable
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, functionName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, inboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(variableName, true); //name of the variable
        ballerinaASTModelBuilder.addTypes(type); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }

    /**
     * Get json or xml path value into a string variable
     */
    private String getPathValue(String type, String variableName, String expressionStr, String packageName) {

        if (Constant.BLANG_TYPE_JSON.equals(type)) {
            addImport(Constant.BLANG_PKG_JSON);
        } else if (Constant.BLANG_TYPE_XML.equals(type)) {
            addImport(Constant.BLANG_PKG_XML);
        }

        String jsonOrXMLVarName = variableName;
        variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.createNameReference(packageName, Constant.BLANG_GET_STRING);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, jsonOrXMLVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(expressionStr);
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(variableName, true); //name of the variable
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
        return variableName;
    }

    private void parseJsonOrXML(String type, String packageName, String nextVariableName, String variableName) {

        if (Constant.BLANG_TYPE_JSON.equals(type)) {
            addImport(Constant.BLANG_PKG_JSON);
        } else if (Constant.BLANG_TYPE_XML.equals(type)) {
            addImport(Constant.BLANG_PKG_XML);
        }

        ballerinaASTModelBuilder.addTypes(type); //type of the variable
        ballerinaASTModelBuilder.createNameReference(packageName, Constant.BLANG_PARSE);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        //   ballerinaASTModelBuilder.createStringLiteral(strJsonOrXMLValue);
        ballerinaASTModelBuilder.createNameReference(null, variableName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1);
        ballerinaASTModelBuilder.addFunctionInvocationExpression(true);
        ballerinaASTModelBuilder.createVariable(nextVariableName, true); //name of the variable
        ballerinaASTModelBuilder.addTypes(type); //type of the variable
        ballerinaASTModelBuilder.addReturnTypes();
    }
}
