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

package org.wso2.ei.tools.mule2ballerina.visitor;

import org.ballerinalang.model.BallerinaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.ballerinahelper.Annotation;
import org.wso2.ei.tools.converter.common.ballerinahelper.BallerinaProgramHelper;
import org.wso2.ei.tools.converter.common.ballerinahelper.Function;
import org.wso2.ei.tools.converter.common.ballerinahelper.HttpClientConnector;
import org.wso2.ei.tools.converter.common.ballerinahelper.Message;
import org.wso2.ei.tools.converter.common.ballerinahelper.Service;
import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.util.Constant;
import org.wso2.ei.tools.converter.common.util.Property;
import org.wso2.ei.tools.mule2ballerina.model.AsynchronousTask;
import org.wso2.ei.tools.mule2ballerina.model.Comment;
import org.wso2.ei.tools.mule2ballerina.model.Database;
import org.wso2.ei.tools.mule2ballerina.model.DatabaseConfig;
import org.wso2.ei.tools.mule2ballerina.model.Flow;
import org.wso2.ei.tools.mule2ballerina.model.FlowReference;
import org.wso2.ei.tools.mule2ballerina.model.GlobalConfiguration;
import org.wso2.ei.tools.mule2ballerina.model.HttpListener;
import org.wso2.ei.tools.mule2ballerina.model.HttpListenerConfig;
import org.wso2.ei.tools.mule2ballerina.model.HttpRequest;
import org.wso2.ei.tools.mule2ballerina.model.HttpRequestConfig;
import org.wso2.ei.tools.mule2ballerina.model.Payload;
import org.wso2.ei.tools.mule2ballerina.model.Processor;
import org.wso2.ei.tools.mule2ballerina.model.PropertyRemover;
import org.wso2.ei.tools.mule2ballerina.model.PropertySetter;
import org.wso2.ei.tools.mule2ballerina.model.Root;
import org.wso2.ei.tools.mule2ballerina.model.SubFlow;
import org.wso2.ei.tools.mule2ballerina.model.VariableRemover;
import org.wso2.ei.tools.mule2ballerina.model.VariableSetter;
import org.wso2.ei.tools.mule2ballerina.util.LogLevel;
import org.wso2.ei.tools.mule2ballerina.util.MimeType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * {@code TreeVisitor} visits intermediate object stack and populate Ballerina AST. This class needs to be refactored
 * to use common ballerina helper methods. Therefore the commented out code will not be removed until they are
 * reviewed.
 */
public class TreeVisitor implements Visitor {

    private static Logger logger = LoggerFactory.getLogger(TreeVisitor.class);

    private BallerinaASTModelBuilder ballerinaASTModelBuilder;
    private Root mRoot;
    private BallerinaFile ballerinaFile;
    private Map<String, Boolean> serviceTrack = new HashMap<String, Boolean>();
    private Map<String, Boolean> importTracker = new HashMap<String, Boolean>();
    private String inboundName;
    private String protocolPkgName;
    private int serviceCounter = 0; //For dynamic service name creation
    private int resourceCounter = 0; //For dynamic resource name creation
    private int parameterCounter = 0; //For dynamic parameter name creation
    private int variableCounter = 0; //For dynamic variable name creation
    private String connectorVarName; //For dynamic connector variable name creation
    private String outboundMsg; //Holds outbound message variable name
    private String inboundMsg; //Holds inbound message variable name
    private String funcParaName;
    private int resourceAnnotationCount = 0; //Keeps track of annotation count of a resource
    private int workerCounter = 0;

    public TreeVisitor(Root mRoot) {
        ballerinaASTModelBuilder = new BallerinaASTModelBuilder();
        this.mRoot = mRoot;
    }

    /**
     * Visit Root. Main flows and private flows are visited separately as they serve two different purposes.
     *
     * @param root Intermediate Root object
     */
    @Override
    public void visit(Root root) {
        logger.info("-SRoot");
        //Visit each main flow to create resources
        for (Flow flow : root.getFlowList()) {
            flow.accept(this);
        }
      /*  //Visit each private flow to create functions. (Private flows are treated as functions.)
        for (Flow privateFlow : root.getPrivateFlowList()) {
            ballerinaASTModelBuilder.startFunction();
            ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MESSAGE); //type of the parameter
           *//*Set the new message variable to 'outboundMsg' since all the other elements inside function will use this
            as their message reference *//*
            outboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
            ballerinaASTModelBuilder.addParameter(0, false, outboundMsg);
            ballerinaASTModelBuilder.startCallableBody();
            for (Processor processor : privateFlow.getFlowProcessors()) {
                processor.accept(this);
            }
            ballerinaASTModelBuilder.endCallableBody();
            ballerinaASTModelBuilder
                    .endOfFunction(privateFlow.getName()); //Function name will be the same as private flow name
        }*/

        for (Flow privateFlow : root.getPrivateFlowList()) {
            outboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
            Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
            functionParas.put(Property.OUTBOUND_MSG, outboundMsg);
            Function.startFunction(ballerinaASTModelBuilder, functionParas);
            for (Processor processor : privateFlow.getFlowProcessors()) {
                processor.accept(this);
            }
            functionParas.put(Property.FUNCTION_NAME, privateFlow.getName());
            Function.endFunction(ballerinaASTModelBuilder, functionParas);
        }

        logger.info("-ERoot");
        ballerinaFile = ballerinaASTModelBuilder.buildBallerinaFile();
    }

    /**
     * Navigate flow processors. Flow is equivalent of a resource in Ballerina.
     *
     * @param flow Flow object
     */
    @Override
    public void visit(Flow flow) {
        logger.info("--SFlow");
        int i = 0;
        int flowSize = flow.getFlowProcessors().size();
        for (Processor processor : flow.getFlowProcessors()) {
            processor.accept(this);
            i++;
            //If end of flow
            if (flowSize == i) {
              /*  ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
                ballerinaASTModelBuilder.createSimpleVarRefExpr();
                ballerinaASTModelBuilder.createReplyStatement();
                ballerinaASTModelBuilder.endCallableBody();*/

                Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
                parameters.put(Property.OUTBOUND_MSG, outboundMsg);
                BallerinaProgramHelper.createReply(ballerinaASTModelBuilder, parameters);

                //Workers should be declared after reply statement but before end of resource
                if (mRoot.getAsyncTaskList() != null) {
                    for (AsynchronousTask asynchronousTask : mRoot.getAsyncTaskList()) {
                        ballerinaASTModelBuilder.enterWorkerDeclaration();
                        ballerinaASTModelBuilder.createWorkerDefinition(asynchronousTask.getName());
                        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MESSAGE);
                        ballerinaASTModelBuilder.createVariable(Constant.BLANG_VAR_WORKER_MSG, false);
                        ballerinaASTModelBuilder.startExprList();
                        ballerinaASTModelBuilder.createNameReference(null, Constant.BLANG_VAR_WORKER_MSG);
                        ballerinaASTModelBuilder.createSimpleVarRefExpr();
                        ballerinaASTModelBuilder.endExprList(1);
                        ballerinaASTModelBuilder.exitWorkerReply(Constant.BLANG_VAR_DEFAULT_WORKER);
                        outboundMsg = Constant.BLANG_VAR_WORKER_MSG;
                        for (Processor asyncProcessor : asynchronousTask.getAsyncProcessors()) {
                            asyncProcessor.accept(this);
                        }
                        ballerinaASTModelBuilder.exitWorkerDeclaration(asynchronousTask.getName());
                    }
                    mRoot.getAsyncTaskList().clear();
                }

                String resourceName = Constant.BLANG_RESOURCE_NAME + ++resourceCounter;
                //  ballerinaASTModelBuilder.endOfResource(resourceName, resourceAnnotationCount); //End of resource
                Map<Property, Object> resourceParameters = new EnumMap<Property, Object>(Property.class);
                resourceParameters.put(Property.RESOURCE_NAME, resourceName);
                resourceParameters.put(Property.RESOURCE_ANNOTATION_COUNT, Integer.valueOf(resourceAnnotationCount));
                org.wso2.ei.tools.converter.common.ballerinahelper.Resource
                        .endOfResource(ballerinaASTModelBuilder, resourceParameters); //End of resource
                resourceAnnotationCount = 0;
                logger.info("--EFlow");

                /* At the end of each flow get the flow queue associate with its config and
                 * remove this flow from the queue, so that when there are no flows (resources) associate with a config
                 * (service) we can close the service
                 */
                if (mRoot.getServiceMap() != null) {
                    Queue<Flow> flows = mRoot.getServiceMap().get(inboundName);
                    if (flows != null) {
                        flows.remove();
                        if (flows.size() == 0) { //If no more resources
                            String serviceName = Constant.BLANG_SERVICE_NAME + ++serviceCounter;
                            //  ballerinaASTModelBuilder.endOfService(serviceName, protocolPkgName); //End of service
                            Map<Property, String> serviceParameters = new EnumMap<Property, String>(Property.class);
                            serviceParameters.put(Property.SERVICE_NAME, serviceName);
                            serviceParameters.put(Property.PROTOCOL_PKG_NAME, Constant.BLANG_HTTP);
                            Service.endOfService(ballerinaASTModelBuilder, serviceParameters); //End of service
                        }
                    }
                }
            }
        }
    }

    /**
     * Set the payload of the outbound message. Currently only String,JSON and XML types are supported. All the
     * other types are treated as Strings. First a variable
     * will be created to hold the value and then set the payload with that value.
     *
     * @param payload Payload object
     */
    @Override
    public void visit(Payload payload) {
        logger.info("----Payload");
        // addImport(Constant.BLANG_PKG_MESSAGES);
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        String payloadVariableName = "";
        //If the string is wrapped around quotes remove them first.
        String payloadValue = ((payload.getValue() != null && Constant.QUOTE_STR
                .equals(String.valueOf(payload.getValue().charAt(0)))) ?
                (payload.getValue().substring(1, payload.getValue().length() - 1)) :
                payload.getValue());

        /*if (payload.getMimeType() != null) {
            MimeType mimeType = MimeType.get(payload.getMimeType());

            switch (mimeType) {
            case XML:
                ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_XML); //type of the variable
                *//*Backtick expression is not longer supported in Ballerina.  Improvements may come in a future
                * release*//*
                // ballerinaASTAPI.createBackTickExpression(Constant.BACKTICK + payload.getValue() + Constant.BACKTICK);
                ballerinaASTModelBuilder.addComment("//TODO: Change the double quotes to back tick. ");
                ballerinaASTModelBuilder.createXMLLiteral(payloadValue);
                payloadVariableName = Constant.BLANG_VAR_XML_PAYLOAD + ++variableCounter;
                ballerinaASTModelBuilder.createVariable(payloadVariableName, true); //name of the variable
                ballerinaASTModelBuilder
                        .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_XML_PAYLOAD);
                break;

            *//*
            IMPORTANT: For Json variables, you have to manually remove the quotation surrounding the json value
             *//*
            case JSON:
                ballerinaASTModelBuilder.addComment(Constant.BLANG_COMMENT_JSON);
                ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_JSON); //type of the variable
                ballerinaASTModelBuilder.createStringLiteral(payloadValue);
                payloadVariableName = Constant.BLANG_VAR_JSON_PAYLOAD + ++variableCounter;
                ballerinaASTModelBuilder.createVariable(payloadVariableName, true); //name of the variable
                ballerinaASTModelBuilder
                        .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_JSON_PAYLOAD);
                break;

            default:
                payloadVariableName = createVariableOfTypeString(payloadValue, Constant.BLANG_VAR_STRING_PAYLOAD, true,
                        true);
                ballerinaASTModelBuilder
                        .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);
                break;
            }
        } else {
            payloadVariableName = createVariableOfTypeString(payloadValue, Constant.BLANG_VAR_STRING_PAYLOAD, true,
                    true);
            ballerinaASTModelBuilder
                    .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);
        }

        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createNameReference(null, payloadVariableName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.createFunctionInvocation(true);*/

        Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
        if (payload.getMimeType() != null) {
            MimeType mimeType = MimeType.get(payload.getMimeType());
            switch (mimeType) {
            case XML:
                parameters.put(Property.TYPE, Constant.XML);
                parameters.put(Property.FORMAT, payloadValue);
                parameters.put(Property.OUTBOUND_MSG, outboundMsg);
                payloadVariableName = Constant.BLANG_VAR_XML_PAYLOAD + ++variableCounter;
                parameters.put(Property.PAYLOAD_VAR_NAME, payloadVariableName);
                break;

            case JSON:
                parameters.put(Property.TYPE, Constant.JSON);
                parameters.put(Property.FORMAT, payloadValue);
                parameters.put(Property.OUTBOUND_MSG, outboundMsg);
                payloadVariableName = Constant.BLANG_VAR_JSON_PAYLOAD + ++variableCounter;
                parameters.put(Property.PAYLOAD_VAR_NAME, payloadVariableName);
                break;

            default:
               /* payloadVariableName = createVariableOfTypeString(payloadValue, Constant.BLANG_VAR_STRING_PAYLOAD,
               true,
                        true);
                ballerinaASTModelBuilder
                        .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);*/
                break;
            }
        } /*else {
            payloadVariableName = createVariableOfTypeString(payloadValue, Constant.BLANG_VAR_STRING_PAYLOAD, true,
                    true);
            ballerinaASTModelBuilder
                    .createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);
        }*/

        Message.setPayload(ballerinaASTModelBuilder, parameters, true);

    }

    /**
     * HTTP listener is equivalent of http server connector in Ballerina.
     *
     * @param listenerConfig http listener configuration details
     */
    @Override
    public void visit(HttpListenerConfig listenerConfig) {
        logger.info("--HttpListenerConfig");
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);
        /*If the service is not yet created, start creating service definition*/
        if (serviceTrack.get(listenerConfig.getName()) == null) {
            Service.startService(ballerinaASTModelBuilder);

            /*Create annotations belong to the service definition*/
           /* ballerinaASTModelBuilder
                    .createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_CONFIG, Constant.BLANG_BASEPATH,
                            listenerConfig.getBasePath());
            ballerinaASTModelBuilder.addAnnotationAttachment(1); //attributesCount is never used
            serviceTrack.put(listenerConfig.getName(), true);*/

            Map<Property, String> serviceAnnotations = new EnumMap<Property, String>(Property.class);
            serviceAnnotations.put(Property.BASEPATH_VALUE, listenerConfig.getBasePath());
            Annotation.createServiceAnnotation(ballerinaASTModelBuilder, serviceAnnotations);
            serviceTrack.put(listenerConfig.getName(), true);

            inboundName = listenerConfig.getName();
            protocolPkgName = Constant.BLANG_HTTP;
        }
    }

    /**
     * When the inbound connector is encountered, first visit it's global configuration to start the service and then
     * start the resource definition.
     *
     * @param listener HttpListener object
     */
    @Override
    public void visit(HttpListener listener) {
        logger.info("----HttpListener");
        GlobalConfiguration globalConfiguration = mRoot.getConfigMap().get(listener.getConfigName());
        globalConfiguration.accept(this);

        /*Inbound connectors need to start the resource definition. Resource is not created at the start of a flow
        , because for the creation of a resource, a service definition has to be started, which only happens once the
        first processor's config is visited */
        // ballerinaASTModelBuilder.startResource();
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startResource(ballerinaASTModelBuilder);
        String allowedMethods = Constant.BLANG_METHOD_GET; //Default http request method is set to GET
        if (listener.getAllowedMethods() != null) {
            allowedMethods = listener.getAllowedMethods();
            String[] methodBits = allowedMethods.split(",");
            for (String method : methodBits) {
                /*Create an annotation without attribute values*/
                /*ballerinaASTModelBuilder.createAnnotationAttachment(Constant.BLANG_HTTP, method, null, null);
                ballerinaASTModelBuilder.addAnnotationAttachment(0);*/
                Map<Property, Object> resourceAnnotations = new EnumMap<Property, Object>(Property.class);
                resourceAnnotations.put(Property.METHOD_NAME, method);
                Annotation.createResourceAnnotation(ballerinaASTModelBuilder, resourceAnnotations);
                resourceAnnotationCount++;
            }
        } else {
          /*  ballerinaASTModelBuilder.createAnnotationAttachment(Constant.BLANG_HTTP, allowedMethods, null, null);
            ballerinaASTModelBuilder.addAnnotationAttachment(0);*/
            Map<Property, Object> resourceAnnotations = new EnumMap<Property, Object>(Property.class);
            resourceAnnotations.put(Property.METHOD_NAME, allowedMethods);
            Annotation.createResourceAnnotation(ballerinaASTModelBuilder, resourceAnnotations);
            resourceAnnotationCount++;
        }

        //TODO:Add the Path annotation
       /* if (listener.getPath() != null) {
            ballerinaASTModelBuilder
                    .createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_PATH, Constant.BLANG_VALUE,
                            listener.getPath());
            ballerinaASTModelBuilder.addAnnotationAttachment(1);
            resourceAnnotationCount++;
        }*/

        //Add inbound message as a resource parameter
        //  ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MESSAGE); //type of the parameter
        inboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
        //  ballerinaASTModelBuilder.addParameter(0, false, inboundMsg);
        Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
        functionParas.put(Property.INBOUND_MSG, inboundMsg);
        functionParas.put(Property.TYPE, Constant.BLANG_TYPE_MESSAGE);
        BallerinaProgramHelper.addFunctionParameter(ballerinaASTModelBuilder, functionParas);

        //TODO:Then add path parameters
      /*  if (listener.getPath() != null) {
            //check whether any path params have been used
            String[] pathParams = listener.getPath().split("/");
            for (String path : pathParams) {
                Pattern pattern = Pattern.compile("\\{(.*)\\}");   // the pattern to search for
                Matcher matcher = pattern.matcher(path);
                // now try to find at least one match
                if (matcher.find()) {
                    //add it to path param
                    ballerinaASTModelBuilder.createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_PATHPARAM,
                            Constant.BLANG_VALUE, path);
                    ballerinaASTModelBuilder.addAnnotationAttachment(1);
                    ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the parameter
                    //TODO: 'funcParaName' might cause problems when accessing different path params in the logic
                    funcParaName = Constant.BLANG_VAR_CONNECT_PATHPARAM + ++parameterCounter;
                    ballerinaASTModelBuilder.addParameter(1, false, funcParaName);
                }
            }
        }*/

       /* ballerinaASTModelBuilder.startCallableBody();*/
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startCallableBody(ballerinaASTModelBuilder);
        //Create empty outbound message
        /*createVariableWithEmptyMap(Constant.BLANG_TYPE_MESSAGE, Constant.BLANG_VAR_RESPONSE  + ++variableCounter,
        true);*/
        outboundMsg = Constant.BLANG_VAR_RESPONSE + ++variableCounter;
        BallerinaProgramHelper
                .createVariableWithEmptyMap(ballerinaASTModelBuilder, Constant.BLANG_TYPE_MESSAGE, outboundMsg, true);
    }

    /**
     * HttpRequest represents a http client connector in Ballerina. First visit this element's global config element to
     * populate required values necessary for client connector creation.
     *
     * @param request HttpRequest object
     */
    @Override
    public void visit(HttpRequest request) {
        logger.info("----HttpRequest");
        GlobalConfiguration globalConfiguration = mRoot.getConfigMap().get(request.getConfigName());
        globalConfiguration.accept(this);

      /*  ballerinaASTModelBuilder.createVariableRefList();
        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        // ballerinaASTAPI.createVariableRefExpr();
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(1);
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, connectorVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(request.getPath());
        ballerinaASTModelBuilder.createNameReference(null, inboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(3);
        ballerinaASTModelBuilder.createAction(Constant.BLANG_CLIENT_CONNECTOR_GET_ACTION, true);
        ballerinaASTModelBuilder.createAssignmentStatement();*/

        Map<Property, String> connectorParameters = new EnumMap<Property, String>(Property.class);
        connectorParameters.put(Property.INBOUND_MSG, inboundMsg);
        connectorParameters.put(Property.OUTBOUND_MSG, outboundMsg);
        connectorParameters.put(Property.CONNECTOR_VAR_NAME, connectorVarName);
        connectorParameters.put(Property.PATH, request.getPath());
        HttpClientConnector.callAction(ballerinaASTModelBuilder, connectorParameters);
    }

    /**
     * HttpRequestConfig contains attributes required to create http client connector in Ballerina.
     *
     * @param requestConfig http request configuration details
     */
    @Override
    public void visit(HttpRequestConfig requestConfig) {
        logger.info("----HttpRequestConfig");
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);

       /* *//*Create reference type variable LHS*//*
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.createRefereceTypeName();
        *//*Create an object out of above created ref type and initialize it with values*//*
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();

        String protocol = (Constant.HTTPS.equalsIgnoreCase(requestConfig.getProtocol()) ?
                Constant.HTTPS_PROTOCOL :
                Constant.HTTP_PROTOCOL);
        String strUrl = "";
        if (!Constant.DEFAULT_PORT.equals(requestConfig.getPort())) {
            strUrl = protocol + requestConfig.getHost() + ":" + requestConfig.getPort() +
                    requestConfig.getBasePath();
        } else {
            strUrl = protocol + requestConfig.getHost() + requestConfig.getBasePath();
        }

        ballerinaASTModelBuilder.createStringLiteral(strUrl);
        ballerinaASTModelBuilder.endExprList(1); // no of arguments
        ballerinaASTModelBuilder.initializeConnector(true); //arguments available
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;
        ballerinaASTModelBuilder.createVariable(connectorVarName, true);*/

        String protocol = (Constant.HTTPS.equalsIgnoreCase(requestConfig.getProtocol()) ?
                Constant.HTTPS_PROTOCOL :
                Constant.HTTP_PROTOCOL);
        String strUrl = "";
        if (!Constant.DEFAULT_PORT.equals(requestConfig.getPort())) {
            strUrl = protocol + requestConfig.getHost() + ":" + requestConfig.getPort() +
                    requestConfig.getBasePath();
        } else {
            strUrl = protocol + requestConfig.getHost() + requestConfig.getBasePath();
        }

        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;

        Map<Property, String> connectorParameters = new EnumMap<Property, String>(Property.class);
        connectorParameters.put(Property.INBOUND_MSG, inboundMsg);
        connectorParameters.put(Property.OUTBOUND_MSG, outboundMsg);
        connectorParameters.put(Property.CONNECTOR_VAR_NAME, connectorVarName);
        connectorParameters.put(Property.URL, strUrl);

        HttpClientConnector.createConnector(ballerinaASTModelBuilder, connectorParameters);

    }

    /**
     * Add a comment in Ballerina code.
     *
     * @param comment Represent a comment
     */
    @Override
    public void visit(Comment comment) {
        logger.info("----Comment" + comment.getComment());
        //  ballerinaASTModelBuilder.addComment(comment.getComment());
        BallerinaProgramHelper.addComment(ballerinaASTModelBuilder, comment.getComment());
    }

    /**
     * Prints the logger message in correct log level. In mule, if the message property of logger is not set with any
     * value it print out the whole message property details. In Ballerina, since this is not directly available that
     * is not provided here.
     *
     * @param log Logger object
     */
    @Override
    public void visit(org.wso2.ei.tools.mule2ballerina.model.Logger log) {
        logger.info("----Logger");
        //addImport(Constant.BLANG_PKG_LOGGER);
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_LOGGER, importTracker);
        LogLevel logLevel = LogLevel.get(log.getLevel());
        switch (logLevel) {
        case LOG_TRACE:
            // ballerinaASTAPI.createIntegerLiteral(LogLevel.LOG_TRACE.getValue());
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_TRACE);
            break;
        case LOG_DEBUG:
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_DEBUG);
            break;
        case LOG_WARN:
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_WARN);
            break;
        case LOG_ERROR:
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_ERROR);
            break;
        case LOG_INFO:
        default:
            ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_INFO);
            break;
        }

        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createStringLiteral(log.getMessage());
        ballerinaASTModelBuilder.endExprList(1);
        ballerinaASTModelBuilder.addFunctionInvocationStatement(true);
    }

    /**
     * Add a header to outbound message.
     *
     * @param propertySetter Property setter
     */
    @Override
    public void visit(PropertySetter propertySetter) {
        logger.info("----PropertySetter");
        // addImport(Constant.BLANG_PKG_MESSAGES);
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_ADD_HEADER);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(propertySetter.getPropertyName());
        ballerinaASTModelBuilder.createStringLiteral(propertySetter.getValue());
        ballerinaASTModelBuilder.endExprList(3);
        ballerinaASTModelBuilder.createFunctionInvocation(true);
    }

    /**
     * Remove header from outbound message.
     *
     * @param propertyRemover Property remover
     */
    @Override
    public void visit(PropertyRemover propertyRemover) {
        logger.info("----PropertyRemover");
        // addImport(Constant.BLANG_PKG_MESSAGES);
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_REMOVE_HEADER);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createStringLiteral(propertyRemover.getPropertyName());
        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.createFunctionInvocation(true);
    }

    /**
     * Create a variable of type string in Ballerina with the mule variable value.
     *
     * @param variableSetter Variable setter
     */
    @Override
    public void visit(VariableSetter variableSetter) {
        logger.info("----VariableSetter");
        createVariableOfTypeString(variableSetter.getValue(), variableSetter.getVariableName(), true, false);
    }

    /**
     * Set the variable value in Ballerina to null.
     *
     * @param variableRemover Variable remover
     */
    @Override
    public void visit(VariableRemover variableRemover) {
        logger.info("----VariableRemover");
        ballerinaASTModelBuilder.createNameReference(null, variableRemover.getVariableName());
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
    }

    /**
     * When a flow reference is called, it might either refer to a sub flow or a private flow. In case of a sub flow,
     * add the processors that's been referred in it, in the calling resource. But if it's a private flow call the
     * respective function.
     *
     * @param flowReference FlowReference details
     */
    @Override
    public void visit(FlowReference flowReference) {
        logger.info("----FlowReference");
        //Add the sub flow processors also into the calling resource
        if (mRoot.getSubFlowMap() != null && !mRoot.getSubFlowMap().isEmpty()) {
            SubFlow subFlow = mRoot.getSubFlowMap().get(flowReference.getName());
            if (subFlow != null) {
                ballerinaASTModelBuilder.addComment("//------Consider wrapping the following logic in a function-----");
                for (Processor processor : subFlow.getFlowProcessors()) {
                    processor.accept(this);
                }
                ballerinaASTModelBuilder.addComment("//-------------------------------------------------------------");
            }
        }
        if (mRoot.getPrivateFlowMap() != null && !mRoot.getPrivateFlowMap().isEmpty()) {
            Flow privateFlow = mRoot.getPrivateFlowMap().get(flowReference.getName());
            if (privateFlow != null) {
                /*ballerinaASTAPI.addComment(
                        "//Calling the function that has the processors belong to " + flowReference.getName());*/
               /* ballerinaASTModelBuilder.createNameReference(null, flowReference.getName());
                ballerinaASTModelBuilder.createSimpleVarRefExpr();
                ballerinaASTModelBuilder.startExprList();
                ballerinaASTModelBuilder.createNameReference(null, outboundMsg);
                ballerinaASTModelBuilder.createSimpleVarRefExpr();
                ballerinaASTModelBuilder.endExprList(1);
                ballerinaASTModelBuilder.createFunctionInvocation(true);*/

                Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
                functionParas.put(Property.OUTBOUND_MSG, outboundMsg);
                functionParas.put(Property.FUNCTION_NAME, flowReference.getName());
                Function.callFunction(ballerinaASTModelBuilder, functionParas);
            }
        }
    }

    /**
     * AsynchronousTask maps to Workers in Ballerina.
     *
     * @param asynchronousTask Asynchronous task
     */
    @Override
    public void visit(AsynchronousTask asynchronousTask) {
        logger.info("----AsynchronousTask");
        ballerinaASTModelBuilder.addComment("//Call Worker!");
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, inboundMsg);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1);
        String workerName = Constant.BLANG_WORKER_NAME + ++workerCounter;
        ballerinaASTModelBuilder.createWorkerInvocationStmt(workerName);

        asynchronousTask.setName(workerName);
        mRoot.addAsynchronousTask(asynchronousTask); //This needs to be visited only after reply statement in resource
    }

    /**
     * This method will be refactored in future.
     *
     * @param database Database details
     */
    @Override
    public void visit(Database database) {
        logger.info("----Database");
        GlobalConfiguration globalConfiguration = mRoot.getConfigMap().get(database.getConfigName());
        globalConfiguration.accept(this);

        String query = createVariableOfTypeString(database.getQuery(), Constant.BLANG_VAR_QUERY, true, true);

        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_SQL_PARAMETER);

        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_DATATABLE);
        ballerinaASTModelBuilder.createStringLiteral("");
        String variableName = Constant.BLANG_VAR_DATATABLE + ++variableCounter;
        ballerinaASTModelBuilder.createVariable(variableName, true);

        ballerinaASTModelBuilder.createVariableRefList();
        ballerinaASTModelBuilder.createNameReference(null, variableName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(1);
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, connectorVarName);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        //  ballerinaASTAPI.createStringLiteral(database.getQuery());
        ballerinaASTModelBuilder.createNameReference(null, query);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.createNameReference(null, "parameters");
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endVariableRefList(3);
        ballerinaASTModelBuilder.createAction(Constant.BLANG_CLIENT_CONNECTOR_SELECT_ACTION, true);
        ballerinaASTModelBuilder.createAssignmentStatement();
    }

    /**
     * This method will be refactored in future.
     *
     * @param databaseConfig database configuration details
     */
    @Override
    public void visit(DatabaseConfig databaseConfig) {
        logger.info("----DatabaseConfig");
        // addImport(Constant.BLANG_PKG_SQL);
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_SQL, importTracker);

        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_MAP);
        ballerinaASTModelBuilder.startMapStructLiteral();
        ballerinaASTModelBuilder.createStringLiteral("maximumPoolSize");
        ballerinaASTModelBuilder.createStringLiteral("1");
        ballerinaASTModelBuilder.createStringLiteral("password");
        ballerinaASTModelBuilder.createStringLiteral(databaseConfig.getPassword());
        ballerinaASTModelBuilder.createStringLiteral("username");
        ballerinaASTModelBuilder.createStringLiteral(databaseConfig.getUser());
        ballerinaASTModelBuilder.createStringLiteral("jdbcUrl");
        ballerinaASTModelBuilder.createStringLiteral("jdbc:mysql://172.17.0.2:3306/library");
        //Create four key value pairs for the above properties
        for (int i = 0; i < 4; i++) {
            ballerinaASTModelBuilder.addMapStructKeyValue();
        }
        ballerinaASTModelBuilder.createMapStructLiteral();
        String propertyVar = Constant.BLANG_VAR_PROP_MAP + ++variableCounter;
        ballerinaASTModelBuilder.createVariable(propertyVar, true);

        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.createRefereceTypeName();
        /*Create an object out of above created ref type and initialize it with values*/
        ballerinaASTModelBuilder.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, propertyVar);
        ballerinaASTModelBuilder.createSimpleVarRefExpr();
        ballerinaASTModelBuilder.endExprList(1); // no of arguments
        ballerinaASTModelBuilder.initializeConnector(true); //arguments available
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;
        ballerinaASTModelBuilder.createVariable(connectorVarName, true);
    }

    /**
     * Get ballerina file object.
     *
     * @return BallerinaFile object
     */
    public BallerinaFile getBallerinaFile() {
        return ballerinaFile;
    }

   /* private void createVariableWithEmptyMap(String typeOfTheParamater, String variableName, boolean exprAvailable) {
        ballerinaASTModelBuilder.addTypes(typeOfTheParamater);
        ballerinaASTModelBuilder.startMapStructLiteral();
        ballerinaASTModelBuilder.createMapStructLiteral();
        ballerinaASTModelBuilder.createVariable(variableName, exprAvailable);
        outboundMsg = variableName;
    }*/

    /**
     * Create a variable of type string.
     *
     * @param value         valuee for the String variable
     * @param varName       variable name
     * @param exprAvailable expression availability
     * @param isCounterUsed determines whether the variable name will be different
     * @return newly created variable name
     */
    private String createVariableOfTypeString(String value, String varName, boolean exprAvailable,
            boolean isCounterUsed) {
        ballerinaASTModelBuilder.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTModelBuilder.createStringLiteral(value);
        String variableName = (isCounterUsed ? varName + ++variableCounter : varName);
        ballerinaASTModelBuilder.createVariable(variableName, exprAvailable); //name of the variable
        return variableName;
    }

    /**
     * If ballerina package is not already added to import packages , add it.
     *
     * @param packageName
     */
   /* private void addImport(String packageName) {
        if (importTracker.isEmpty() || importTracker.get(packageName) == null) {
            ballerinaASTModelBuilder
                    .addImportPackage(ballerinaASTModelBuilder.getBallerinaPackageMap().get(packageName), null);
            importTracker.put(packageName, true);
        }
    }*/
}
