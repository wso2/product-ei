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
import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
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
import org.wso2.ei.tools.mule2ballerina.util.Constant;
import org.wso2.ei.tools.mule2ballerina.util.LogLevel;
import org.wso2.ei.tools.mule2ballerina.util.MimeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code TreeVisitor} visits intermediate object stack and populate Ballerina AST
 */
public class TreeVisitor implements Visitor {

    private static Logger logger = LoggerFactory.getLogger(TreeVisitor.class);

    private BallerinaASTModelBuilder ballerinaASTAPI;
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
        ballerinaASTAPI = new BallerinaASTModelBuilder();
        this.mRoot = mRoot;
    }

    /**
     * Visit Root. Main flows and private flows are visited separately as they serve two different purposes.
     *
     * @param root
     */
    @Override
    public void visit(Root root) {
        logger.debug("-SRoot");
        //Visit each main flow to create resources
        for (Flow flow : root.getFlowList()) {
            flow.accept(this);
        }
        //Visit each private flow to create functions. (Private flows are treated as functions.)
        for (Flow privateFlow : root.getPrivateFlowList()) {
            ballerinaASTAPI.startFunction();
            ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_MESSAGE); //type of the parameter
           /*Set the new message variable to 'outboundMsg' since all the other elements inside function will use this
            as their message reference */
            outboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
            ballerinaASTAPI.addParameter(0, false, outboundMsg);
            ballerinaASTAPI.startCallableBody();
            for (Processor processor : privateFlow.getFlowProcessors()) {
                processor.accept(this);
            }
            ballerinaASTAPI.endCallableBody();
            ballerinaASTAPI.endOfFunction(privateFlow.getName()); //Function name will be the same as private flow name
        }
        logger.debug("-ERoot");
        ballerinaFile = ballerinaASTAPI.buildBallerinaFile();
    }

    /**
     * Navigate flow processors. Flow is equivalent of a resource in Ballerina
     *
     * @param flow
     */
    @Override
    public void visit(Flow flow) {
        logger.debug("--SFlow");
        int i = 0;
        int flowSize = flow.getFlowProcessors().size();
        for (Processor processor : flow.getFlowProcessors()) {
            processor.accept(this);
            i++;
            //If end of flow
            if (flowSize == i) {
                ballerinaASTAPI.createNameReference(null, outboundMsg);
                ballerinaASTAPI.createSimpleVarRefExpr();
                ballerinaASTAPI.createReplyStatement();
                ballerinaASTAPI.endCallableBody();

                //Workers should be declared after reply statement but before end of resource
                if (mRoot.getAsyncTaskList() != null) {
                    for (AsynchronousTask asynchronousTask : mRoot.getAsyncTaskList()) {
                        ballerinaASTAPI.enterWorkerDeclaration();
                        ballerinaASTAPI.createWorkerDefinition(asynchronousTask.getName());
                        ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_MESSAGE);
                        ballerinaASTAPI.createVariable(Constant.BLANG_VAR_WORKER_MSG, false);
                        ballerinaASTAPI.startExprList();
                        ballerinaASTAPI.createNameReference(null, Constant.BLANG_VAR_WORKER_MSG);
                        ballerinaASTAPI.createSimpleVarRefExpr();
                        ballerinaASTAPI.endExprList(1);
                        ballerinaASTAPI.exitWorkerReply(Constant.BLANG_VAR_DEFAULT_WORKER);
                        outboundMsg = Constant.BLANG_VAR_WORKER_MSG;
                        for (Processor asyncProcessor : asynchronousTask.getAsyncProcessors()) {
                            asyncProcessor.accept(this);
                        }
                        ballerinaASTAPI.exitWorkerDeclaration(asynchronousTask.getName());
                    }
                    mRoot.getAsyncTaskList().clear();
                }

                String resourceName = Constant.BLANG_RESOURCE_NAME + ++resourceCounter;
                ballerinaASTAPI.endOfResource(resourceName, resourceAnnotationCount); //End of resource
                resourceAnnotationCount = 0;
                logger.debug("--EFlow");

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
                            ballerinaASTAPI.endOfService(serviceName, protocolPkgName); //End of service
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
     * @param payload
     */
    @Override
    public void visit(Payload payload) {
        if (importTracker.isEmpty() || importTracker.get(Constant.BLANG_PKG_MESSAGES) == null) {
            ballerinaASTAPI
                    .addImportPackage(ballerinaASTAPI.getBallerinaPackageMap().get(Constant.BLANG_PKG_MESSAGES), null);
            importTracker.put(Constant.BLANG_PKG_MESSAGES, true);
        }
        logger.debug("----Payload");

        String payloadVariableName = "";
        //If the string is wrapped around quotes remove them first.
        String payloadValue = ((payload.getValue() != null && Constant.QUOTE_STR
                .equals(String.valueOf(payload.getValue().charAt(0)))) ?
                (payload.getValue().substring(1, payload.getValue().length() - 1)) :
                payload.getValue());

        if (payload.getMimeType() != null) {
            MimeType mimeType = MimeType.get(payload.getMimeType());

            switch (mimeType) {
            case XML:
                ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_XML); //type of the variable
                /*Backtick expression is not longer supported in Ballerina. Improvements may come in a future release*/
                // ballerinaASTAPI.createBackTickExpression(Constant.BACKTICK + payload.getValue() + Constant.BACKTICK);
                ballerinaASTAPI.addComment("//TODO: set xml payload ");
                ballerinaASTAPI.createStringLiteral(payloadValue);
                payloadVariableName = Constant.BLANG_VAR_XML_PAYLOAD + ++variableCounter;
                ballerinaASTAPI.createVariable(payloadVariableName, true); //name of the variable
                ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_XML_PAYLOAD);
                break;

            /*
            IMPORTANT: For Json variables, you have to manually remove the quotation surrounding the json value
             */
            case JSON:
                ballerinaASTAPI.addComment(Constant.BLANG_COMMENT_JSON);
                ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_JSON); //type of the variable
                ballerinaASTAPI.createStringLiteral(payloadValue);
                payloadVariableName = Constant.BLANG_VAR_JSON_PAYLOAD + ++variableCounter;
                ballerinaASTAPI.createVariable(payloadVariableName, true); //name of the variable
                ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_JSON_PAYLOAD);
                break;

            default:
                payloadVariableName = createVariableOfTypeString(payloadValue, Constant.BLANG_VAR_STRING_PAYLOAD, true,
                        true);
                ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);
                break;
            }
        } else {
            payloadVariableName = createVariableOfTypeString(payloadValue, Constant.BLANG_VAR_STRING_PAYLOAD, true,
                    true);
            ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_SET_STRING_PAYLOAD);
        }

        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, outboundMsg);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.createNameReference(null, payloadVariableName);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endExprList(2);
        ballerinaASTAPI.createFunctionInvocation(true);
    }

    /**
     * HTTP listener is equivalent of http server connector in Ballerina
     *
     * @param listenerConfig
     */
    @Override
    public void visit(HttpListenerConfig listenerConfig) {

        /*If ballerina http package is not already added to import packages , add it*/
        if (importTracker.isEmpty() || importTracker.get(Constant.BLANG_HTTP) == null) {
            ballerinaASTAPI.addImportPackage(ballerinaASTAPI.getBallerinaPackageMap().get(Constant.BLANG_HTTP), null);
            importTracker.put(Constant.BLANG_HTTP, true);
        }
        logger.debug("--HttpListenerConfig");

        /*If the service is not yet created, start creating service definition*/
        if (serviceTrack.get(listenerConfig.getName()) == null) {
            ballerinaASTAPI.startService();

            /*Create annotations belong to the service definition*/
            ballerinaASTAPI
                    .createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_CONFIG, Constant.BLANG_BASEPATH,
                            listenerConfig.getBasePath());
            ballerinaASTAPI.addAnnotationAttachment(1); //attributesCount is never used
            serviceTrack.put(listenerConfig.getName(), true);
            inboundName = listenerConfig.getName();
            protocolPkgName = Constant.BLANG_HTTP;
        }
    }

    /**
     * When the inbound connector is encountered, first visit it's global configuration to start the service and then
     * start the resource definition.
     *
     * @param listener
     */
    @Override
    public void visit(HttpListener listener) {
        logger.debug("----HttpListener");
        GlobalConfiguration globalConfiguration = mRoot.getConfigMap().get(listener.getConfigName());
        globalConfiguration.accept(this);

        /*Inbound connectors need to start the resource definition. Resource is not created at the start of a flow
        , because for the creation of a resource, a service definition has to be started, which only happens once the
        first processor's config is visited */
        ballerinaASTAPI.startResource();
        String allowedMethods = Constant.BLANG_METHOD_GET; //Default http request method is set to GET
        if (listener.getAllowedMethods() != null) {
            allowedMethods = listener.getAllowedMethods();
            String[] methodBits = allowedMethods.split(",");
            for (String method : methodBits) {
                /*Create an annotation without attribute values*/
                ballerinaASTAPI.createAnnotationAttachment(Constant.BLANG_HTTP, method, null, null);
                ballerinaASTAPI.addAnnotationAttachment(0);
                resourceAnnotationCount++;
            }
        } else {
            ballerinaASTAPI.createAnnotationAttachment(Constant.BLANG_HTTP, allowedMethods, null, null);
            ballerinaASTAPI.addAnnotationAttachment(0);
            resourceAnnotationCount++;
        }

        //Add the Path annotation
        if (listener.getPath() != null) {
            ballerinaASTAPI.createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_PATH, Constant.BLANG_VALUE,
                    listener.getPath());
            ballerinaASTAPI.addAnnotationAttachment(1);
            resourceAnnotationCount++;
        }

        //Add inbound message as a resource parameter
        ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_MESSAGE); //type of the parameter
        inboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
        ballerinaASTAPI.addParameter(0, false, inboundMsg);

        //Then add path parameters
        if (listener.getPath() != null) {
            //check whether any path params have been used
            String[] pathParams = listener.getPath().split("/");
            for (String path : pathParams) {
                Pattern pattern = Pattern.compile("\\{(.*)\\}");   // the pattern to search for
                Matcher matcher = pattern.matcher(path);
                // now try to find at least one match
                if (matcher.find()) {
                    //add it to path param
                    ballerinaASTAPI.createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_PATHPARAM,
                            Constant.BLANG_VALUE, path);
                    ballerinaASTAPI.addAnnotationAttachment(1);
                    ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_STRING); //type of the parameter
                    //TODO: 'funcParaName' might cause problems when accessing different path params in the logic
                    funcParaName = Constant.BLANG_VAR_CONNECT_PATHPARAM + ++parameterCounter;
                    ballerinaASTAPI.addParameter(1, false, funcParaName);
                }
            }
        }

        ballerinaASTAPI.startCallableBody();
        //Create empty outbound message
        createVariableWithEmptyMap(Constant.BLANG_TYPE_MESSAGE, Constant.BLANG_VAR_RESPONSE + ++variableCounter, true);
    }

    /**
     * HttpRequest represents a http client connector in Ballerina. First visit this element's global config element to
     * populate required values necessary for client connector creation.
     *
     * @param request
     */
    @Override
    public void visit(HttpRequest request) {
        logger.debug("----HttpRequest");
        GlobalConfiguration globalConfiguration = mRoot.getConfigMap().get(request.getConfigName());
        globalConfiguration.accept(this);

        ballerinaASTAPI.createVariableRefList();
        ballerinaASTAPI.createNameReference(null, outboundMsg);
        // ballerinaASTAPI.createVariableRefExpr();
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endVariableRefList(1);
        ballerinaASTAPI.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, connectorVarName);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.createStringLiteral(request.getPath());
        ballerinaASTAPI.createNameReference(null, inboundMsg);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endVariableRefList(3);
        ballerinaASTAPI.createAction(Constant.BLANG_CLIENT_CONNECTOR_GET_ACTION, true);
        ballerinaASTAPI.createAssignmentStatement();
    }

    /**
     * HttpRequestConfig contains attributes required to create http client connector in Ballerina.
     *
     * @param requestConfig
     */
    @Override
    public void visit(HttpRequestConfig requestConfig) {
        logger.debug("----HttpRequestConfig");
        /*Create reference type variable LHS*/
        ballerinaASTAPI.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTAPI.createRefereceTypeName();
        /*Create an object out of above created ref type and initialize it with values*/
        ballerinaASTAPI.createNameReference(Constant.BLANG_HTTP, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTAPI.startExprList();

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

        ballerinaASTAPI.createStringLiteral(strUrl);
        ballerinaASTAPI.endExprList(1); // no of arguments
        ballerinaASTAPI.initializeConnector(true); //arguments available
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;
        ballerinaASTAPI.createVariable(connectorVarName, true);
    }

    /**
     * Add a comment in Ballerina code.
     *
     * @param comment
     */
    @Override
    public void visit(Comment comment) {
        ballerinaASTAPI.addComment(comment.getComment());
    }

    /**
     * Prints the logger message in correct log level. In mule, if the message property of logger is not set with any
     * value it print out the whole message property details. In Ballerina, since this is not directly available that
     * is not provided here.
     *
     * @param log
     */
    @Override
    public void visit(org.wso2.ei.tools.mule2ballerina.model.Logger log) {
        /*If ballerina system package is not already added to import packages , add it*/
        if (importTracker.isEmpty() || importTracker.get(Constant.BLANG_PKG_LOGGER) == null) {
            ballerinaASTAPI
                    .addImportPackage(ballerinaASTAPI.getBallerinaPackageMap().get(Constant.BLANG_PKG_LOGGER), null);
            importTracker.put(Constant.BLANG_PKG_LOGGER, true);
        }
        // ballerinaASTAPI.createNameReference(Constant.BLANG_SYSTEM, Constant.BLANG_LOG);

        LogLevel logLevel = LogLevel.get(log.getLevel());

        switch (logLevel) {
        case LOG_TRACE:
            // ballerinaASTAPI.createIntegerLiteral(LogLevel.LOG_TRACE.getValue());
            ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_TRACE);
            break;
        case LOG_DEBUG:
            ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_DEBUG);
            break;
        case LOG_WARN:
            ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_WARN);
            break;
        case LOG_ERROR:
            ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_ERROR);
            break;
        case LOG_INFO:
        default:
            ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_LOGGER, Constant.BLANG_INFO);
            break;
        }

        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createStringLiteral(log.getMessage());
        ballerinaASTAPI.endExprList(1);
        ballerinaASTAPI.addFunctionInvocationStatement(true);
    }

    /**
     * Add a header to outbound message
     *
     * @param propertySetter
     */
    @Override
    public void visit(PropertySetter propertySetter) {

        if (importTracker.isEmpty() || importTracker.get(Constant.BLANG_PKG_MESSAGES) == null) {
            ballerinaASTAPI
                    .addImportPackage(ballerinaASTAPI.getBallerinaPackageMap().get(Constant.BLANG_PKG_MESSAGES), null);
            importTracker.put(Constant.BLANG_PKG_MESSAGES, true);
        }

        ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_ADD_HEADER);
        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, outboundMsg);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.createStringLiteral(propertySetter.getPropertyName());
        ballerinaASTAPI.createStringLiteral(propertySetter.getValue());
        ballerinaASTAPI.endExprList(3);
        ballerinaASTAPI.createFunctionInvocation(true);
    }

    /**
     * Remove header from outbound message
     *
     * @param propertyRemover
     */
    @Override
    public void visit(PropertyRemover propertyRemover) {

        if (importTracker.isEmpty() || importTracker.get(Constant.BLANG_PKG_MESSAGES) == null) {
            ballerinaASTAPI
                    .addImportPackage(ballerinaASTAPI.getBallerinaPackageMap().get(Constant.BLANG_PKG_MESSAGES), null);
            importTracker.put(Constant.BLANG_PKG_MESSAGES, true);
        }

        ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_MESSAGES, Constant.BLANG_REMOVE_HEADER);
        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, outboundMsg);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.createStringLiteral(propertyRemover.getPropertyName());
        ballerinaASTAPI.endExprList(2);
        ballerinaASTAPI.createFunctionInvocation(true);
    }

    /**
     * Create a variable of type string in Ballerina with the mule variable value
     *
     * @param variableSetter
     */
    @Override
    public void visit(VariableSetter variableSetter) {
        createVariableOfTypeString(variableSetter.getValue(), variableSetter.getVariableName(), true, false);
    }

    /**
     * Set the variable value in Ballerina to null
     *
     * @param variableRemover
     */
    @Override
    public void visit(VariableRemover variableRemover) {
        // createVariableOfTypeString("", variableRemover.getVariableName(), true,false);
      /*  ballerinaASTAPI.createStringLiteral("");
        ballerinaASTAPI.createVariable(variableRemover.getVariableName(), false);*/
        ballerinaASTAPI.createNameReference(null, variableRemover.getVariableName());
        ballerinaASTAPI.createSimpleVarRefExpr();
    }

    /**
     * When a flow reference is called, it might either refer to a sub flow or a private flow. In case of a sub flow,
     * add the processors that's been referred in it, in the calling resource. But if it's a private flow call the
     * respective function.
     *
     * @param flowReference
     */
    @Override
    public void visit(FlowReference flowReference) {
        //Add the sub flow processors also into the calling resource
        if (mRoot.getSubFlowMap() != null && !mRoot.getSubFlowMap().isEmpty()) {
            SubFlow subFlow = mRoot.getSubFlowMap().get(flowReference.getName());
            if (subFlow != null) {
                ballerinaASTAPI.addComment("//------Consider wrapping the following logic in a function-----");
                for (Processor processor : subFlow.getFlowProcessors()) {
                    processor.accept(this);
                }
                ballerinaASTAPI.addComment("//-------------------------------------------------------------");
            }
        }
        if (mRoot.getPrivateFlowMap() != null && !mRoot.getPrivateFlowMap().isEmpty()) {
            Flow privateFlow = mRoot.getPrivateFlowMap().get(flowReference.getName());
            if (privateFlow != null) {
                /*ballerinaASTAPI.addComment(
                        "//Calling the function that has the processors belong to " + flowReference.getName());*/
                ballerinaASTAPI.createNameReference(null, flowReference.getName());
                ballerinaASTAPI.startExprList();
                ballerinaASTAPI.createNameReference(null, outboundMsg);
                ballerinaASTAPI.createSimpleVarRefExpr();
                ballerinaASTAPI.endExprList(1);
                ballerinaASTAPI.createFunctionInvocation(true);
            }
        }
    }

    /**
     * AsynchronousTask maps to Workers in Ballerina
     *
     * @param asynchronousTask
     */
    @Override
    public void visit(AsynchronousTask asynchronousTask) {
        ballerinaASTAPI.addComment("//Call Worker!");
        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, inboundMsg);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endExprList(1);
        String workerName = Constant.BLANG_WORKER_NAME + ++workerCounter;
        ballerinaASTAPI.createWorkerInvocationStmt(workerName);

        asynchronousTask.setName(workerName);
        mRoot.addAsynchronousTask(asynchronousTask); //This needs to be visited only after reply statement in resource
    }

    /**
     * This method will be refactored in future
     * @param database
     */
    @Override
    public void visit(Database database) {
        logger.debug("----Database");
        GlobalConfiguration globalConfiguration = mRoot.getConfigMap().get(database.getConfigName());
        globalConfiguration.accept(this);

        String query = createVariableOfTypeString(database.getQuery(), Constant.BLANG_VAR_QUERY, true, true);

        ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_SQL_PARAMETER);

        ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_DATATABLE);
        ballerinaASTAPI.createStringLiteral("");
        String variableName = Constant.BLANG_VAR_DATATABLE + ++variableCounter;
        ballerinaASTAPI.createVariable(variableName, true);

        ballerinaASTAPI.createVariableRefList();
        ballerinaASTAPI.createNameReference(null, variableName);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endVariableRefList(1);
        ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, connectorVarName);
        ballerinaASTAPI.createSimpleVarRefExpr();
        //  ballerinaASTAPI.createStringLiteral(database.getQuery());
        ballerinaASTAPI.createNameReference(null, query);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.createNameReference(null, "parameters");
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endVariableRefList(3);
        ballerinaASTAPI.createAction(Constant.BLANG_CLIENT_CONNECTOR_SELECT_ACTION, true);
        ballerinaASTAPI.createAssignmentStatement();
    }

    /**
     * This method will be refactored in future.
     * @param databaseConfig
     */
    @Override
    public void visit(DatabaseConfig databaseConfig) {

        if (importTracker.isEmpty() || importTracker.get(Constant.BLANG_PKG_SQL) == null) {
            ballerinaASTAPI
                    .addImportPackage(ballerinaASTAPI.getBallerinaPackageMap().get(Constant.BLANG_PKG_SQL), null);
            importTracker.put(Constant.BLANG_PKG_SQL, true);
        }

        ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_MAP);
        ballerinaASTAPI.startMapStructLiteral();
        ballerinaASTAPI.createStringLiteral("maximumPoolSize");
        ballerinaASTAPI.createStringLiteral("1");
        ballerinaASTAPI.createStringLiteral("password");
        ballerinaASTAPI.createStringLiteral(databaseConfig.getPassword());
        ballerinaASTAPI.createStringLiteral("username");
        ballerinaASTAPI.createStringLiteral(databaseConfig.getUser());
        ballerinaASTAPI.createStringLiteral("jdbcUrl");
        ballerinaASTAPI.createStringLiteral("jdbc:mysql://172.17.0.2:3306/library");
        //Create four key value pairs for the above properties
        for (int i = 0; i < 4; i++) {
            ballerinaASTAPI.addMapStructKeyValue();
        }
        ballerinaASTAPI.createMapStructLiteral();
        String propertyVar = Constant.BLANG_VAR_PROP_MAP + ++variableCounter;
        ballerinaASTAPI.createVariable(propertyVar, true);

        ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTAPI.createRefereceTypeName();
        /*Create an object out of above created ref type and initialize it with values*/
        ballerinaASTAPI.createNameReference(Constant.BLANG_PKG_SQL, Constant.BLANG_CLIENT_CONNECTOR);
        ballerinaASTAPI.startExprList();
        ballerinaASTAPI.createNameReference(null, propertyVar);
        ballerinaASTAPI.createSimpleVarRefExpr();
        ballerinaASTAPI.endExprList(1); // no of arguments
        ballerinaASTAPI.initializeConnector(true); //arguments available
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;
        ballerinaASTAPI.createVariable(connectorVarName, true);
    }

    public BallerinaFile getBallerinaFile() {
        return ballerinaFile;
    }

    private void createVariableWithEmptyMap(String typeOfTheParamater, String variableName, boolean exprAvailable) {
        ballerinaASTAPI.addTypes(typeOfTheParamater);
        ballerinaASTAPI.startMapStructLiteral();
        ballerinaASTAPI.createMapStructLiteral();
        ballerinaASTAPI.createVariable(variableName, exprAvailable);
        outboundMsg = variableName;
    }

    /**
     * Create a variable of type string.
     *
     * @param value
     * @param varName
     * @param exprAvailable
     * @param isCounterUsed determines whether the variable name will be different
     * @return
     */
    private String createVariableOfTypeString(String value, String varName, boolean exprAvailable,
            boolean isCounterUsed) {
        ballerinaASTAPI.addTypes(Constant.BLANG_TYPE_STRING); //type of the variable
        ballerinaASTAPI.createStringLiteral(value);
        String variableName = (isCounterUsed ? varName + ++variableCounter : varName);
        ballerinaASTAPI.createVariable(variableName, exprAvailable); //name of the variable
        return variableName;
    }
}
