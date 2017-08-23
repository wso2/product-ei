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
import org.apache.synapse.core.axis2.ProxyService;
import org.apache.synapse.endpoints.AddressEndpoint;
import org.apache.synapse.endpoints.Endpoint;
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
import org.wso2.ei.tools.converter.common.ballerinahelper.Function;
import org.wso2.ei.tools.converter.common.ballerinahelper.HttpClientConnector;
import org.wso2.ei.tools.converter.common.ballerinahelper.JMSClientConnector;
import org.wso2.ei.tools.converter.common.ballerinahelper.Message;
import org.wso2.ei.tools.converter.common.ballerinahelper.Service;
import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.util.Constant;
import org.wso2.ei.tools.converter.common.util.Property;
import org.wso2.ei.tools.synapse2ballerina.util.ArtifactMapper;
import org.wso2.ei.tools.synapse2ballerina.util.JMSPropertyMapper;
import org.wso2.ei.tools.synapse2ballerina.util.ProxyServiceType;
import org.wso2.ei.tools.synapse2ballerina.wrapper.APIWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.MediatorWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.ProxyServiceWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.ResourceWrapper;
import org.wso2.ei.tools.synapse2ballerina.wrapper.SequenceMediatorWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code SynapseConfigVisitor} class visits SynapseConfiguration to populate ballerina model.
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
    private List<SequenceMediator> namedSequenceList = new ArrayList<SequenceMediator>();

    /**
     * Given a synapse configuration object map it's elements to ballerina model to get ballerina file.
     *
     * @param configuration synapse model
     * @return BallerinaFile object
     */
    public BallerinaFile visit(SynapseConfiguration configuration) {
        ballerinaASTModelBuilder = new BallerinaASTModelBuilder();
        this.synapseConfiguration = configuration;

        //Each API will have their own service in ballerina file
        for (API api : configuration.getAPIs()) {
            APIWrapper apiWrapper = new APIWrapper(api);
            apiWrapper.accept(this);
        }

        //Each proxy will be mapped to a service in ballerina
        for (ProxyService proxyService : configuration.getProxyServices()) {
            ProxyServiceWrapper proxyServiceWrapper = new ProxyServiceWrapper(proxyService);
            proxyServiceWrapper.accept(this);
        }

        //Call function: named sequences maps to functions in ballerina
        if (!namedSequenceList.isEmpty()) {
            for (SequenceMediator namedSequence : namedSequenceList) {
                outboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
                Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
                functionParas.put(Property.OUTBOUND_MSG, outboundMsg);
                Function.startFunction(ballerinaASTModelBuilder, functionParas);

                List<Mediator> mediatorList = namedSequence.getList();
                if (mediatorList != null) {
                    for (Mediator mediator : mediatorList) {
                        MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
                        mediatorWrapper.accept(this);
                    }
                }
                functionParas.put(Property.FUNCTION_NAME, namedSequence.getName());
                Function.endFunction(ballerinaASTModelBuilder, functionParas);
            }
        }

        return ballerinaASTModelBuilder.buildBallerinaFile();
    }

    /**
     * Create ballerina http server connector.
     *
     * @param api Synapse API
     */
    public void visit(API api) {
        if (logger.isDebugEnabled()) {
            logger.debug("API");
        }
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);
        Service.startService(ballerinaASTModelBuilder);
        /*Create annotations belong to the service definition*/
        Map<Property, String> serviceAnnotations = new EnumMap<Property, String>(Property.class);
        serviceAnnotations.put(Property.BASEPATH_VALUE, api.getContext());
        Annotation.createServiceAnnotation(ballerinaASTModelBuilder, serviceAnnotations);

        for (Resource resource : api.getResources()) {
            ResourceWrapper resourceWrapper = new ResourceWrapper(resource);
            resourceWrapper.accept(this);
            String resourceName = Constant.BLANG_RESOURCE_NAME + ++resourceCounter;

            Map<Property, Object> resourceParameters = new EnumMap<Property, Object>(Property.class);
            resourceParameters.put(Property.RESOURCE_NAME, resourceName);
            resourceParameters.put(Property.RESOURCE_ANNOTATION_COUNT, Integer.valueOf(resourceAnnotationCount));
            org.wso2.ei.tools.converter.common.ballerinahelper.Resource
                    .endOfResource(ballerinaASTModelBuilder, resourceParameters); //End of resource
            resourceAnnotationCount = 0;
        }
        String serviceName = api.getAPIName();
        Map<Property, String> serviceParameters = new EnumMap<Property, String>(Property.class);
        serviceParameters.put(Property.SERVICE_NAME, serviceName);
        serviceParameters.put(Property.PROTOCOL_PKG_NAME, Constant.BLANG_HTTP);
        Service.endOfService(ballerinaASTModelBuilder, serviceParameters); //End of service
    }

    /**
     * Given a proxy service map it to relevant ballerina service. Currently this supports only http which gets
     * mapped to ballerina client connector.
     *
     * @param proxyService synapse proxy service
     */
    @Override
    public void visit(ProxyService proxyService) {
        if (logger.isDebugEnabled()) {
            logger.debug("Proxy");
        }

        ArrayList<String> transportList = (ArrayList<String>) proxyService.getTransports();
        //Assuming one proxy will only handle one transport type
        String transportType = transportList.get(0);

        ProxyServiceType proxyServiceType = ProxyServiceType.get(transportType);
        switch (proxyServiceType) {
        case HTTP:
        case HTTPS:
            BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);
            String serviceName = proxyService.getName();
            Map<Property, String> serviceParameters = new EnumMap<Property, String>(Property.class);
            serviceParameters.put(Property.SERVICE_NAME, serviceName);
            serviceParameters.put(Property.PROTOCOL_PKG_NAME, Constant.BLANG_HTTP);
            createResourceAndService(proxyService, serviceParameters, true);
            break;
        case JMS:
            break;
        default:
            break;
        }
    }

    /**
     * Start ballerina resource.
     *
     * @param resource synapse resource
     */
    public void visit(Resource resource) {
        if (logger.isDebugEnabled()) {
            logger.debug("Resource");
        }
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startResource(ballerinaASTModelBuilder);
        String[] allowedMethods = { Constant.BLANG_METHOD_GET }; //Default http request method is set to GET
        Map<Property, Object> resourceAnnotations = new EnumMap<Property, Object>(Property.class);
        if (resource.getMethods() != null) {
            resourceAnnotations.put(Property.METHOD_NAME, resource.getMethods());
        } else {
            resourceAnnotations.put(Property.METHOD_NAME, allowedMethods);
        }
        Annotation.createResourceAnnotation(ballerinaASTModelBuilder, resourceAnnotations);
        resourceAnnotationCount++;

        //TODO: Add Path annotation

        //Add inbound message as a resource parameter
        inboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;

        Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
        functionParas.put(Property.INBOUND_MSG, inboundMsg);
        functionParas.put(Property.TYPE, Constant.BLANG_TYPE_MESSAGE);
        BallerinaProgramHelper.addFunctionParameter(ballerinaASTModelBuilder, functionParas);

        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startCallableBody(ballerinaASTModelBuilder);
        //Create empty outbound message
        outboundMsg = Constant.BLANG_VAR_RESPONSE + ++variableCounter;
        BallerinaProgramHelper
                .createVariableWithEmptyMap(ballerinaASTModelBuilder, Constant.BLANG_TYPE_MESSAGE, outboundMsg, true);

        SequenceMediator inSequence = resource.getInSequence();
        SequenceMediatorWrapper inSequenceWrapper = new SequenceMediatorWrapper(inSequence);
        inSequenceWrapper.accept(this);

        SequenceMediator outSequence = resource.getOutSequence();
        SequenceMediatorWrapper outSequenceMediatorWrapper = new SequenceMediatorWrapper(outSequence);
        outSequenceMediatorWrapper.accept(this);
    }

    /**
     * Named sequences will be mapped to a ballerina function else iterate through mediators.
     *
     * @param sequenceMediator synapse SequenceMediator
     */
    @Override
    public void visit(SequenceMediator sequenceMediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("SequenceMediator");
        }
        List<Mediator> mediatorList = null;
        //If key is not null that means this is a named sequence which needs to map to a function in ballerina
        if (sequenceMediator.getKey() != null) {
            SequenceMediator namedSequence = (SequenceMediator) synapseConfiguration.getLocalRegistry()
                    .get(sequenceMediator.getKey().getKeyValue());
            //Keep a list of named sequences, so that the relevant function should be created at the end of services
            namedSequenceList.add(namedSequence);
            //call function
            Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
            functionParas.put(Property.OUTBOUND_MSG, outboundMsg);
            functionParas.put(Property.FUNCTION_NAME, sequenceMediator.getKey().getKeyValue());
            Function.callFunction(ballerinaASTModelBuilder, functionParas);

        } else {
            mediatorList = sequenceMediator.getList();
            if (mediatorList != null) {
                for (Mediator mediator : mediatorList) {
                    MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
                    mediatorWrapper.accept(this);
                }
            }
        }
    }

    /**
     * Get the appropriate internal wrapper and visit each mediator accordingly.
     *
     * @param mediator synapse Mediator
     */
    @Override
    public void visit(Mediator mediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("Mediator >> " + mediator.getType() + " or " + mediator.getMediatorName());
        }
        String mediatorType = (mediator.getType() != null ? mediator.getType() : "");
        String mediatorName = (mediator.getMediatorName() != null ? mediator.getMediatorName() : "");
        //Mediator name needs to be checked, in case of payloadfactory mediator
        if ((artifacts.get(mediatorType) != null) || (artifacts.get(mediatorName) != null)) {
            Class<?> wrapperClass;
            try {
                if (artifacts.get(mediator.getType()) != null) {
                    wrapperClass = Class.forName(artifacts.get(mediatorType));
                } else {
                    wrapperClass = Class.forName(artifacts.get(mediatorName));
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
     * Create ballerina http client connector.
     *
     * @param mediator synapse CallMediator
     */
    @Override
    public void visit(CallMediator mediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("CallMediator");
        }
        this.visit(mediator.getEndpoint());
    }

    /**
     * Based on the endpoint type call relevant visit method.
     *
     * @param endpoint synapse Endpoint
     */
    public void visit(Endpoint endpoint) {
        if (endpoint instanceof IndirectEndpoint) {
            this.visit((IndirectEndpoint) endpoint);
        } else if (endpoint instanceof AddressEndpoint) {
            this.visit((AddressEndpoint) endpoint);
        }
    }

    /**
     * If the address endpoint represent a jms uri map it to a  ballerina jms client connector.
     *
     * @param addressEndpoint
     */
    public void visit(AddressEndpoint addressEndpoint) {
        String address = (addressEndpoint.getDefinition() != null ?
                addressEndpoint.getDefinition().getAddress() :
                null);

        if (address != null) {
            //JMS Sender
            if (address.startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.JMS_PREFIX)) {

                BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_JMS, importTracker);

                //Extract queue name and other jms properties from address
                Map<String, String> parameters = splitJMSAddress(address);
                String queueName = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
                //Create ballerina map with jms properties
                BallerinaProgramHelper.createAndInitializeMap(ballerinaASTModelBuilder, parameters);
                String propertyVar = Constant.BLANG_VAR_PROP_MAP + ++variableCounter;
                ballerinaASTModelBuilder.createVariable(propertyVar, true);

                String jmsMsg = createJMSMessage();

                //Create the JMS connector and invoke it's send method
                String jmsEPVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;
                Map<Property, String> connectorParas = new EnumMap<Property, String>(Property.class);
                connectorParas.put(Property.VARIABLE_NAME, propertyVar);
                connectorParas.put(Property.JMS_EP_VAR_NAME, jmsEPVarName);
                connectorParas.put(Property.JMS_MSG, jmsMsg);
                connectorParas.put(Property.JMS_QUEUE_NAME, queueName);
                JMSClientConnector.createConnector(ballerinaASTModelBuilder, connectorParas);
                JMSClientConnector.callAction(ballerinaASTModelBuilder, connectorParas);
            }
        }
    }

    /*
     * Get HTTPEndpoint from local registry.
     * @param indirectEndpoint synapse IndirectEndpoint
     */
    public void visit(IndirectEndpoint indirectEndpoint) {
        HTTPEndpoint endpoint = (HTTPEndpoint) synapseConfiguration.getLocalRegistry().get(indirectEndpoint.getKey());
        this.visit(endpoint);
    }

    /**
     * HTTPEndpoint maps to a client connector in ballerina.
     *
     * @param endpoint synapse HTTPEndpoint
     */
    public void visit(HTTPEndpoint endpoint) {
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_HTTP, importTracker);
        connectorVarName = Constant.BLANG_VAR_CONNECT + ++variableCounter;

        Map<Property, String> connectorParameters = new EnumMap<Property, String>(Property.class);
        connectorParameters.put(Property.INBOUND_MSG, inboundMsg);
        connectorParameters.put(Property.OUTBOUND_MSG, outboundMsg);
        connectorParameters.put(Property.CONNECTOR_VAR_NAME, connectorVarName);
        connectorParameters.put(Property.URL, endpoint.getDefinition().getAddress());
        connectorParameters.put(Property.PATH, Constant.DIVIDER);

        HttpClientConnector.createConnector(ballerinaASTModelBuilder, connectorParameters);
        HttpClientConnector.callAction(ballerinaASTModelBuilder, connectorParameters);
    }

    /**
     * Ballerina Reply for a resource.
     *
     * @param respondMediator synapse RespondMediator
     */
    @Override
    public void visit(RespondMediator respondMediator) {
        if (logger.isDebugEnabled()) {
            logger.debug("RespondMediator");
        }
        Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
        parameters.put(Property.OUTBOUND_MSG, outboundMsg);
        BallerinaProgramHelper.createReply(ballerinaASTModelBuilder, parameters);
    }

    /**
     * Set the json or xml payload.
     *
     * @param payloadFactoryMediator synapse PayloadFactoryMediator.
     */
    @Override
    public void visit(PayloadFactoryMediator payloadFactoryMediator) {
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        BallerinaProgramHelper.addComment(ballerinaASTModelBuilder,
                "//TODO: If there are arguments, please " + "adjust the logic accordingly");
        String payloadVariableName = "";
        Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
        parameters.put(Property.TYPE, payloadFactoryMediator.getType());
        parameters.put(Property.FORMAT, payloadFactoryMediator.getFormat());
        parameters.put(Property.OUTBOUND_MSG, outboundMsg);

        if (org.wso2.ei.tools.synapse2ballerina.util.Constant.JSON.equals(payloadFactoryMediator.getType())) {
            payloadVariableName = Constant.BLANG_VAR_JSON_PAYLOAD + ++variableCounter;
        } else if (org.wso2.ei.tools.synapse2ballerina.util.Constant.XML.equals(payloadFactoryMediator.getType())) {
            payloadVariableName = Constant.BLANG_VAR_XML_PAYLOAD + ++variableCounter;
        }
        parameters.put(Property.PAYLOAD_VAR_NAME, payloadVariableName);
        Message.setPayload(ballerinaASTModelBuilder, parameters, true);
    }

    /**
     * SwitchMediator maps to ballerina if else clause.
     *
     * @param switchMediator synaspe SwitchMediator
     */
    @Override
    public void visit(SwitchMediator switchMediator) {
        //Identify whether this is header based or content based routing
        String expressionStr = switchMediator.getSource().getExpression();
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        String variableName = "";
        if (expressionStr != null) {
            Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
            parameters.put(Property.INBOUND_MSG, inboundMsg);
            String headerVarName = createHeaderBasedScenario(expressionStr, parameters);
            String pathType = switchMediator.getSource().getPathType();
            String payloadVarName = createContentBasedRouteScenario(pathType, expressionStr, parameters);
            variableName = (payloadVarName != null ? payloadVarName : headerVarName);
        }

        BallerinaProgramHelper.addComment(ballerinaASTModelBuilder,
                "//TODO: Please make sure the conditional " + "expressions are correct.");

        //Inside if
        createIfClause(switchMediator, variableName);

        //Inside else if
        if (switchMediator.getCases().size() > 1) {
            createIfElseClause(switchMediator, variableName);
        }

        //inside else
        createElseClause(switchMediator);
    }

    /**
     * Map synapse proxy service to relevant ballerina server connector and a resource.
     *
     * @param proxyService      synapse ProxyService
     * @param serviceParameters ballerina related server connector info
     * @param replyNeeded       whether reply statement is needed
     */
    private void createResourceAndService(ProxyService proxyService, Map<Property, String> serviceParameters,
            boolean replyNeeded) {

        startServiceAndResource();
        mediationLogicForProxyService(proxyService);
        if (replyNeeded) {
            Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
            parameters.put(Property.OUTBOUND_MSG, outboundMsg);
            BallerinaProgramHelper.createReply(ballerinaASTModelBuilder, parameters);
        }
        endResourceAndService(serviceParameters);
    }

    /**
     * Start ballerina service and resource.
     */
    private void startServiceAndResource() {
        //Start service nad resource
        Service.startService(ballerinaASTModelBuilder);
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startResource(ballerinaASTModelBuilder);

        String[] resourceMethods = new String[2];
        resourceMethods[0] = Constant.BLANG_METHOD_GET;
        resourceMethods[1] = Constant.BLANG_METHOD_POST;

        Map<Property, Object> resourceAnnotations = new EnumMap<Property, Object>(Property.class);
        resourceAnnotations.put(Property.METHOD_NAME, resourceMethods);
        Annotation.createResourceAnnotation(ballerinaASTModelBuilder, resourceAnnotations);
        resourceAnnotationCount++;

        inboundMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++parameterCounter;
        Map<Property, String> functionParas = new EnumMap<Property, String>(Property.class);
        functionParas.put(Property.INBOUND_MSG, inboundMsg);
        functionParas.put(Property.TYPE, Constant.BLANG_TYPE_MESSAGE);
        BallerinaProgramHelper.addFunctionParameter(ballerinaASTModelBuilder, functionParas);
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource.startCallableBody(ballerinaASTModelBuilder);

        //Create empty outbound message
        outboundMsg = Constant.BLANG_VAR_RESPONSE + ++variableCounter;
        BallerinaProgramHelper
                .createVariableWithEmptyMap(ballerinaASTModelBuilder, Constant.BLANG_TYPE_MESSAGE, outboundMsg, true);
    }

    /**
     * End of ballerina resource and service.
     *
     * @param serviceParameters service parameters needed to end a service
     */
    private void endResourceAndService(Map<Property, String> serviceParameters) {
        String resourceName = Constant.BLANG_RESOURCE_NAME + ++resourceCounter;
        Map<Property, Object> resourceParameters = new EnumMap<Property, Object>(Property.class);
        resourceParameters.put(Property.RESOURCE_NAME, resourceName);
        resourceParameters.put(Property.RESOURCE_ANNOTATION_COUNT, Integer.valueOf(resourceAnnotationCount));
        org.wso2.ei.tools.converter.common.ballerinahelper.Resource
                .endOfResource(ballerinaASTModelBuilder, resourceParameters); //End of resource
        resourceAnnotationCount = 0;
        Service.endOfService(ballerinaASTModelBuilder, serviceParameters); //End of service
    }

    /**
     * Mediation logic for synapse proxy service is handled here
     *
     * @param proxyService synapse ProxyService
     */
    private void mediationLogicForProxyService(ProxyService proxyService) {
        SequenceMediator inSequence = proxyService.getTargetInLineInSequence();
        SequenceMediatorWrapper inSequenceMediatorWrapper = new SequenceMediatorWrapper(inSequence);
        inSequenceMediatorWrapper.accept(this);

        if (proxyService.getTargetEndpoint() != null) {
            HTTPEndpoint endpoint = (HTTPEndpoint) synapseConfiguration.getLocalRegistry()
                    .get(proxyService.getTargetEndpoint());
            if (endpoint != null) {
                this.visit(endpoint);
            }
        }

        SequenceMediator outSequence = proxyService.getTargetInLineOutSequence();
        SequenceMediatorWrapper outSequenceMediatorWrapper = new SequenceMediatorWrapper(outSequence);
        outSequenceMediatorWrapper.accept(this);
    }

    /**
     * Split JMS URI and create a map of jms property key pairs.
     *
     * @param address JMS URI
     * @return jms property key value pairs
     */
    private Map<String, String> splitJMSAddress(String address) {
        Map<String, String> jmsProperties = JMSPropertyMapper.getEnumMap();
        String propertyStr = address.substring(address.lastIndexOf("?") + 1); //JMS properties
        String properties[] = propertyStr.split("&");
        Map<String, String> parameters = new HashMap<String, String>();
        for (String property : properties) {
            String keyValuePair[] = property.split("="); //key value pair of a single property
            parameters.put(jmsProperties.get(keyValuePair[0]), keyValuePair[1]);
        }
        return parameters;
    }

    /**
     * Extract payload from inbound message and set it in a new message. (Inbound message cannot be directly passed
     * to jms connector).
     *
     * @return jms message variable name
     */
    private String createJMSMessage() {
        // TODO:Clarify whether it is the outbound message's payload that needs to be sent to jms queue
        // Get the payload from inbound message
        BallerinaProgramHelper.addImport(ballerinaASTModelBuilder, Constant.BLANG_PKG_MESSAGES, importTracker);
        Map<Property, String> payloadParas = new EnumMap<Property, String>(Property.class);
        String variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
        payloadParas.put(Property.TYPE, Constant.BLANG_TYPE_STRING);
        payloadParas.put(Property.VARIABLE_NAME, variableName);
        payloadParas.put(Property.FUNCTION_NAME, Constant.BLANG_GET_STRING_PAYLOAD);
        payloadParas.put(Property.INBOUND_MSG, inboundMsg);
        Message.getPayload(ballerinaASTModelBuilder, payloadParas);

        //Create a message type variable to store queue message
        String jmsMsg = Constant.BLANG_DEFAULT_VAR_MSG + ++variableCounter;
        BallerinaProgramHelper
                .createVariableWithEmptyMap(ballerinaASTModelBuilder, Constant.BLANG_TYPE_MESSAGE, jmsMsg, true);

        //Set the payload of the message to needs to be passed to the queue
        Map<Property, String> payloadSetterParas = new EnumMap<Property, String>(Property.class);
        payloadSetterParas.put(Property.TYPE, Constant.STRING);
        payloadSetterParas.put(Property.OUTBOUND_MSG, jmsMsg);
        payloadSetterParas.put(Property.PAYLOAD_VAR_NAME, variableName);
        Message.setPayload(ballerinaASTModelBuilder, payloadSetterParas, false);
        return jmsMsg;
    }

    /**
     * if expression indicates header based routing get the header values.
     *
     * @param expressionStr expression of switch mediator
     * @param parameters    parameters required for get header values
     * @return variable name that has the header value
     */
    private String createHeaderBasedScenario(String expressionStr, Map<Property, String> parameters) {
        String variableName = "";
        if (expressionStr.startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.HEADER_IDENTIFIER_1)) {
            //header based routing
            String headerName = expressionStr.substring(5);
            variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
            parameters.put(Property.HEADER_NAME, headerName);
            parameters.put(Property.VARIABLE_NAME, variableName);
            Message.getHeaderValues(ballerinaASTModelBuilder, parameters);

        } else if (expressionStr.startsWith(org.wso2.ei.tools.synapse2ballerina.util.Constant.HEADER_IDENTIFIER_2)) {
            //header based routing
            String headerName = expressionStr.substring(25);
            //Remove last bracket
            headerName = headerName.substring(0, headerName.length() - 1);
            variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
            parameters.put(Property.HEADER_NAME, headerName);
            parameters.put(Property.VARIABLE_NAME, variableName);
            Message.getHeaderValues(ballerinaASTModelBuilder, parameters);

        }
        return variableName;
    }

    /**
     * Content based routing scenario is handled here
     *
     * @param pathType      identify whether the payload contains xml or json
     * @param expressionStr expression of switch mediator
     * @param parameters    parameters required to get payload
     * @return variable that contains payload
     */
    private String createContentBasedRouteScenario(String pathType, String expressionStr,
            Map<Property, String> parameters) {
        String variableName = null;
        if (org.wso2.ei.tools.synapse2ballerina.util.Constant.JSON_PATH.equals(pathType)) {
            //content based routing - json
            variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
            parameters.put(Property.TYPE, Constant.BLANG_TYPE_JSON);
            parameters.put(Property.VARIABLE_NAME, variableName);
            parameters.put(Property.FUNCTION_NAME, Constant.BLANG_GET_JSON_PAYLOAD);
            Message.getPayload(ballerinaASTModelBuilder, parameters);

            Map<Property, String> pathParams = new EnumMap<Property, String>(Property.class);
            pathParams.put(Property.TYPE, Constant.BLANG_TYPE_JSON);
            pathParams.put(Property.VARIABLE_NAME, variableName);
            variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
            pathParams.put(Property.VARIABLE_NAME_NEW, variableName);
            pathParams.put(Property.EXPRESSION, expressionStr);
            pathParams.put(Property.PACKAGE_NAME, Constant.BLANG_PKG_JSON);
            BallerinaProgramHelper.getPathValue(ballerinaASTModelBuilder, pathParams, importTracker);

        } else if (org.wso2.ei.tools.synapse2ballerina.util.Constant.XML_PATH.equals(pathType)) {
            //content based routing - xml
            variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
            parameters.put(Property.TYPE, Constant.BLANG_TYPE_XML);
            parameters.put(Property.VARIABLE_NAME, variableName);
            parameters.put(Property.FUNCTION_NAME, Constant.BLANG_GET_XML_PAYLOAD);
            Message.getPayload(ballerinaASTModelBuilder, parameters);

            Map<Property, String> pathParams = new EnumMap<Property, String>(Property.class);
            pathParams.put(Property.TYPE, Constant.BLANG_TYPE_XML);
            pathParams.put(Property.VARIABLE_NAME, variableName);
            variableName = Constant.BLANG_VAR_NAME + ++variableCounter;
            pathParams.put(Property.VARIABLE_NAME_NEW, variableName);
            pathParams.put(Property.EXPRESSION, expressionStr);
            pathParams.put(Property.PACKAGE_NAME, Constant.BLANG_PKG_XML);
            BallerinaProgramHelper.getPathValue(ballerinaASTModelBuilder, pathParams, importTracker);
        }
        return variableName;
    }

    /**
     * Create ballerina if clause.
     *
     * @param switchMediator synapse SwitchMediator
     * @param variableName   variable name of header or payload
     */
    private void createIfClause(SwitchMediator switchMediator, String variableName) {
        BallerinaProgramHelper.enterIfStatement(ballerinaASTModelBuilder);

        Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);
        parameters.put(Property.VARIABLE_NAME, variableName);
        parameters.put(Property.EXPRESSION, switchMediator.getCases().get(0).getRegex().toString());
        BallerinaProgramHelper.createExpression(ballerinaASTModelBuilder, parameters);

        AnonymousListMediator anonymousListMediator = switchMediator.getCases().get(0).getCaseMediator();
        List<Mediator> mediatorList = anonymousListMediator.getList();
        for (Mediator mediator : mediatorList) {
            MediatorWrapper mediatorWrapper = new MediatorWrapper(mediator);
            mediatorWrapper.accept(this);
        }
        BallerinaProgramHelper.exitIfClause(ballerinaASTModelBuilder);
    }

    /**
     * Create ballerina else if clause.
     *
     * @param switchMediator synapse SwitchMediator
     * @param variableName   variable name of header or payload
     */
    private void createIfElseClause(SwitchMediator switchMediator, String variableName) {
        for (int i = 1; i < switchMediator.getCases().size(); i++) {
            BallerinaProgramHelper.enterElseIfClause(ballerinaASTModelBuilder);

            Map<Property, String> elseIfParas = new EnumMap<Property, String>(Property.class);
            elseIfParas.put(Property.VARIABLE_NAME, variableName);
            elseIfParas.put(Property.EXPRESSION, switchMediator.getCases().get(i).getRegex().toString());
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

    /**
     * Create ballerina else clause.
     *
     * @param switchMediator synapse SwitchMediator
     */
    private void createElseClause(SwitchMediator switchMediator) {
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
