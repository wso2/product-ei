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
import org.wso2.ei.tools.converter.common.ballerinahelper.Annotation;
import org.wso2.ei.tools.converter.common.ballerinahelper.BallerinaProgramHelper;
import org.wso2.ei.tools.converter.common.ballerinahelper.HttpClientConnector;
import org.wso2.ei.tools.converter.common.ballerinahelper.Message;
import org.wso2.ei.tools.converter.common.ballerinahelper.Service;
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
    private BallerinaASTModelBuilder ballerinaASTModelBuilder = new BallerinaASTModelBuilder();
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
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);
        Service.startService(ballerinaASTModelBuilder);
        /*Create annotations belong to the service definition*/
        Map<String, Object> serviceAnnotations = new HashMap<String, Object>();
        serviceAnnotations.put(Constant.BASEPATH_VALUE, api.getContext());
        Annotation.createServiceAnnotation(ballerinaASTModelBuilder, serviceAnnotations);

        for (Resource resource : api.getResources()) {
            ResourceWrapper resourceWrapper = new ResourceWrapper(resource);
            resourceWrapper.accept(this);
            String resourceName = Constant.BLANG_RESOURCE_NAME + ++resourceCounter;

            Map<String, Object> resourceParameters = new HashMap<String, Object>();
            resourceParameters.put(Constant.RESOURCE_NAME, resourceName);
            resourceParameters.put(Constant.RESOURCE_ANNOTATION_COUNT, Integer.valueOf(resourceAnnotationCount));
            org.wso2.ei.tools.converter.common.ballerinahelper.Resource
                    .endOfService(ballerinaASTModelBuilder, resourceParameters); //End of resource
            resourceAnnotationCount = 0;
        }
        String serviceName = api.getAPIName();
        Map<String, Object> serviceParameters = new HashMap<String, Object>();
        serviceParameters.put(Constant.SERVICE_NAME, serviceName);
        serviceParameters.put(Constant.PROTOCOL_PKG_NAME, Constant.BLANG_HTTP);
        Service.endOfService(ballerinaASTModelBuilder, serviceParameters); //End of service
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
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startResource(ballerinaASTModelBuilder);
        String allowedMethods = Constant.BLANG_METHOD_GET; //Default http request method is set to GET
        if (resource.getMethods() != null) {
            for (String method : resource.getMethods()) {
                Map<String, Object> resourceAnnotations = new HashMap<String, Object>();
                resourceAnnotations.put(Constant.METHOD_NAME, method);
                Annotation.createResourceAnnotation(ballerinaASTModelBuilder, resourceAnnotations);
                resourceAnnotationCount++;
            }
        } else {
            Map<String, Object> resourceAnnotations = new HashMap<String, Object>();
            resourceAnnotations.put(Constant.METHOD_NAME, allowedMethods);
            Annotation.createResourceAnnotation(ballerinaASTModelBuilder, resourceAnnotations);
            resourceAnnotationCount++;
        }

        //TODO: Add Path annotation

        //Add inbound message as a resource parameter
        inboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;

        Map<String, Object> functionParas = new HashMap<String, Object>();
        functionParas.put(Constant.INBOUND_MSG, inboundMsg);
        functionParas.put(Constant.TYPE, Constant.BLANG_TYPE_MESSAGE);
        BallerinaProgramHelper.addFunctionParameter(ballerinaASTModelBuilder, functionParas);

        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startCallableBody(ballerinaASTModelBuilder);
        //Create empty outbound message
        outboundMsg = BallerinaProgramHelper
                .createVariableWithEmptyMap(ballerinaASTModelBuilder, Constant.BLANG_TYPE_MESSAGE,
                        Constant.BLANG_VAR_RESPONSE + ++variableCounter, true);

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
            BallerinaProgramHelper.addComment(ballerinaASTModelBuilder,
                    mediator.getType() + " is not supported by  " + "synapse migration tool yet! ");
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
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);
        //TODO: Refactor this part to support other types of endpoints as well
        IndirectEndpoint indirectEndpoint = (IndirectEndpoint) mediator.getEndpoint();
        HTTPEndpoint endpoint = (HTTPEndpoint) synapseConfiguration.getLocalRegistry().get(indirectEndpoint.getKey());
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;

        Map<String, Object> connectorParameters = new HashMap<String, Object>();
        connectorParameters.put(Constant.INBOUND_MSG, inboundMsg);
        connectorParameters.put(Constant.OUTBOUND_MSG, outboundMsg);
        connectorParameters.put(Constant.CONNECTOR_VAR_NAME, connectorVarName);
        connectorParameters.put(Constant.URL, endpoint.getDefinition().getAddress());

        HttpClientConnector.createConnector(ballerinaASTModelBuilder, connectorParameters);
        HttpClientConnector.callAction(ballerinaASTModelBuilder, connectorParameters);
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
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Constant.OUTBOUND_MSG, outboundMsg);
        BallerinaProgramHelper.createReply(ballerinaASTModelBuilder, parameters);
    }

    /**
     * Set the json or xml payload
     *
     * @param payloadFactoryMediator
     */
    @Override
    public void visit(PayloadFactoryMediator payloadFactoryMediator) {
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        BallerinaProgramHelper.addComment(ballerinaASTModelBuilder,
                "//IMPORTANT: If there are arguments, please " + "adjust the logic accordingly");
        String payloadVariableName = "";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Constant.TYPE, payloadFactoryMediator.getType());
        parameters.put(Constant.FORMAT, payloadFactoryMediator.getFormat());
        parameters.put(Constant.OUTBOUND_MSG, outboundMsg);

        if (org.wso2.ei.tools.synapse2ballerina.util.Constant.JSON.equals(payloadFactoryMediator.getType())) {
            payloadVariableName = Constant.BLANG_VAR_JSON_PAYLOAD + ++variableCounter;
            parameters.put(Constant.PAYLOAD_VAR_NAME, payloadVariableName);
        } else if (org.wso2.ei.tools.synapse2ballerina.util.Constant.XML.equals(payloadFactoryMediator.getType())) {
            payloadVariableName = Constant.BLANG_VAR_XML_PAYLOAD + ++variableCounter;
            parameters.put(Constant.PAYLOAD_VAR_NAME, payloadVariableName);
        }
        Message.setPayload(ballerinaASTModelBuilder, parameters);
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
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);

        if (expressionStr != null) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(Constant.INBOUND_MSG, inboundMsg);

            if (expressionStr.startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.HEADER_IDENTIFIER_1)) {
                //header based routing
                headerName = expressionStr.substring(5);
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                parameters.put(Constant.HEADER_NAME, headerName);
                parameters.put(Constant.VARIABLE_NAME, variableName);
                Message.getHeaderValues(ballerinaASTModelBuilder, parameters);

            } else if (expressionStr
                    .startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.HEADER_IDENTIFIER_2)) {
                //header based routing
                headerName = expressionStr.substring(25);
                //Remove last bracket
                headerName = headerName.substring(0, headerName.length() - 1);
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                parameters.put(Constant.HEADER_NAME, headerName);
                parameters.put(Constant.VARIABLE_NAME, variableName);
                Message.getHeaderValues(ballerinaASTModelBuilder, parameters);

            }
            String pathType = switchMediator.getSource().getPathType();
            if (org.wso2.ei.tools.synapse2ballerina.util.Constant.JSON_PATH.equals(pathType)) {
                //content based routing - json
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                parameters.put(Constant.TYPE, Constant.BLANG_TYPE_JSON);
                parameters.put(Constant.VARIABLE_NAME, variableName);
                parameters.put(Constant.FUNCTION_NAME, Constant.BLANG_GET_JSON_PAYLOAD);
                Message.getPayload(ballerinaASTModelBuilder, parameters);

                String newVariableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                Map<String, Object> pathParams = new HashMap<String, Object>();
                pathParams.put(Constant.TYPE, Constant.BLANG_TYPE_JSON);
                pathParams.put(Constant.VARIABLE_NAME, variableName);
                pathParams.put(Constant.VARIABLE_NAME_NEW, newVariableName);
                pathParams.put(Constant.EXPRESSION, expressionStr);
                pathParams.put(Constant.PACKAGE_NAME, Constant.BLANG_PKG_JSON);
                variableName = BallerinaProgramHelper.getPathValue(ballerinaASTModelBuilder, pathParams, importTracker);

            } else if (org.wso2.ei.tools.synapse2ballerina.util.Constant.XML_PATH.equals(pathType)) {
                //content based routing - xml
                variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                parameters.put(Constant.TYPE, Constant.BLANG_TYPE_XML);
                parameters.put(Constant.VARIABLE_NAME, variableName);
                parameters.put(Constant.FUNCTION_NAME, Constant.BLANG_GET_XML_PAYLOAD);
                Message.getPayload(ballerinaASTModelBuilder, parameters);

                String newVariableName = Constant.BLANG_VAR_NAME + ++variableCounter;
                Map<String, Object> pathParams = new HashMap<String, Object>();
                pathParams.put(Constant.TYPE, Constant.BLANG_TYPE_XML);
                pathParams.put(Constant.VARIABLE_NAME, variableName);
                pathParams.put(Constant.VARIABLE_NAME_NEW, newVariableName);
                pathParams.put(Constant.EXPRESSION, expressionStr);
                pathParams.put(Constant.PACKAGE_NAME, Constant.BLANG_PKG_XML);
                variableName = BallerinaProgramHelper.getPathValue(ballerinaASTModelBuilder, pathParams, importTracker);
            }
        }

        BallerinaProgramHelper.addComment(ballerinaASTModelBuilder,
                "//IMPORTANT: Please make sure the conditional " + "expressions are correct.");

        //Inside if
        BallerinaProgramHelper.enterIfStatement(ballerinaASTModelBuilder);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Constant.VARIABLE_NAME, variableName);
        parameters.put(Constant.EXPRESSION, switchMediator.getCases().get(0).getRegex().toString());
        BallerinaProgramHelper.createExpression(ballerinaASTModelBuilder, parameters);

        AnonymousListMediator anonymousListMediator = switchMediator.getCases().get(0).getCaseMediator();
        List<Mediator> mediatorList = anonymousListMediator.getList();
        for (Mediator mediator : mediatorList) {
            MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
            mediatorWrapper.accept(this);
        }
        BallerinaProgramHelper.exitIfClause(ballerinaASTModelBuilder);

        //Inside else if
        if (switchMediator.getCases().size() > 1) {
            for (int i = 1; i < switchMediator.getCases().size(); i++) {
                BallerinaProgramHelper.enterElseIfClause(ballerinaASTModelBuilder);

                Map<String, Object> elseIfParas = new HashMap<String, Object>();
                elseIfParas.put(Constant.VARIABLE_NAME, variableName);
                elseIfParas.put(Constant.EXPRESSION, switchMediator.getCases().get(i).getRegex().toString());
                BallerinaProgramHelper.createExpression(ballerinaASTModelBuilder, elseIfParas);

                AnonymousListMediator anonymousList = switchMediator.getCases().get(i).getCaseMediator();
                List<Mediator> caseMediators = anonymousList.getList();
                for (Mediator mediator : caseMediators) {
                    MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
                    mediatorWrapper.accept(this);
                }
                BallerinaProgramHelper.exitElseIfClause(ballerinaASTModelBuilder);
            }
        }

        //inside else
        BallerinaProgramHelper.enterElseClause(ballerinaASTModelBuilder);
        SwitchCase switchCase = switchMediator.getDefaultCase();
        AnonymousListMediator defaultAnonymousMediator = switchCase.getCaseMediator();
        List<Mediator> defaultCaseMediators = defaultAnonymousMediator.getList();
        for (Mediator mediator : defaultCaseMediators) {
            MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
            mediatorWrapper.accept(this);
        }
        BallerinaProgramHelper.exitElseClause(ballerinaASTModelBuilder);

        BallerinaProgramHelper.exitIfElseStatement(ballerinaASTModelBuilder);
    }


   /* private void parseJsonOrXML(String type, String packageName, String nextVariableName, String variableName) {
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
    }*/
}
