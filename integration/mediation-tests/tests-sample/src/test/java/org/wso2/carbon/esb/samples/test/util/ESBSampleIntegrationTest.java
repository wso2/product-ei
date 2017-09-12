/*
 * Copyright (c)2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.samples.test.util;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;
import org.wso2.esb.integration.common.clients.endpoint.EndPointAdminClient;
import org.wso2.esb.integration.common.clients.executor.PriorityMediationAdminClient;
import org.wso2.esb.integration.common.clients.localentry.LocalEntriesAdminClient;
import org.wso2.esb.integration.common.clients.mediation.MessageProcessorClient;
import org.wso2.esb.integration.common.clients.mediation.MessageStoreAdminClient;
import org.wso2.esb.integration.common.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.esb.integration.common.clients.rest.api.RestApiAdminClient;
import org.wso2.esb.integration.common.clients.sequences.SequenceAdminServiceClient;
import org.wso2.esb.integration.common.clients.service.mgt.ServiceAdminClient;
import org.wso2.esb.integration.common.clients.tasks.TaskAdminClient;
import org.wso2.esb.integration.common.clients.template.EndpointTemplateAdminServiceClient;
import org.wso2.esb.integration.common.clients.template.SequenceTemplateAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.ESBTestCaseUtils;
import org.wso2.esb.integration.common.utils.ServiceDeploymentUtil;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * The ESBSampleIntegrationTest class provides a way to update synapse with provided a configuration file or a sample.
 */
public class ESBSampleIntegrationTest extends ESBIntegrationTest {

    private static final String PROXY = "proxy";
    private static final String LOCAL_ENTRY = "localEntry";
    private static final String ENDPOINT = "endpoint";
    private static final String SEQUENCE = "sequence";
    private static final String MESSAGE_STORE = "messageStore";
    private static final String MESSAGE_PROCESSOR = "messageProcessor";
    private static final String TEMPLATE = "template";
    private static final String API = "api";
    private static final String PRIORITY_EXECUTOR = "priorityExecutor";
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String TASK = "task";

    public static ESBTestCaseUtils esbUtils = new ESBTestCaseUtils();

    /**
     * Load synapse configuration from OMElement.
     *
     * @param synapseConfig synapse configuration
     * @param backendURL    server backEnd url
     * @param sessionCookie session Cookie
     * @throws java.rmi.RemoteException
     * @throws javax.xml.stream.XMLStreamException
     * @throws javax.servlet.ServletException
     */
    private void updateESBConfiguration(OMElement synapseConfig, String backendURL, String sessionCookie)
            throws Exception {
        ProxyServiceAdminClient proxyAdmin = new ProxyServiceAdminClient(backendURL, sessionCookie);
        EndPointAdminClient endPointAdminClient = new EndPointAdminClient(backendURL, sessionCookie);
        SequenceAdminServiceClient sequenceAdminClient = new SequenceAdminServiceClient(backendURL, sessionCookie);
        LocalEntriesAdminClient localEntryAdminServiceClient = new LocalEntriesAdminClient(backendURL, sessionCookie);
        MessageProcessorClient messageProcessorClient = new MessageProcessorClient(backendURL, sessionCookie);
        MessageStoreAdminClient messageStoreAdminClient = new MessageStoreAdminClient(backendURL, sessionCookie);
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backendURL, sessionCookie);
        EndpointTemplateAdminServiceClient endpointTemplateAdminServiceClient
                = new EndpointTemplateAdminServiceClient(backendURL, sessionCookie);
        SequenceTemplateAdminServiceClient sequenceTemplateAdminServiceClient
                = new SequenceTemplateAdminServiceClient(backendURL, sessionCookie);
        RestApiAdminClient apiAdminClient = new RestApiAdminClient(backendURL, sessionCookie);
        PriorityMediationAdminClient priorityMediationAdminClient
                = new PriorityMediationAdminClient(backendURL, sessionCookie);
        TaskAdminClient taskAdminClient = new TaskAdminClient(backendURL, sessionCookie);
        Iterator<OMElement> localEntries = synapseConfig.getChildrenWithLocalName(LOCAL_ENTRY);
        while (localEntries.hasNext()) {
            OMElement localEntry = localEntries.next();
            String le = localEntry.getAttributeValue(new QName(KEY));
            if (ArrayUtils.contains(localEntryAdminServiceClient.getEntryNames(), le)) {
                Assert.assertTrue(localEntryAdminServiceClient.deleteLocalEntry(le),
                        le + " Local Entry deletion failed");
                Assert.assertTrue(esbUtils.isLocalEntryUnDeployed(backendURL, sessionCookie, le),
                        le + " Local Entry undeployment failed");
            }
            Assert.assertTrue(localEntryAdminServiceClient.addLocalEntry(localEntry), " Local Entry addition failed");
            Assert.assertTrue(esbUtils.isLocalEntryDeployed(backendURL, sessionCookie, le),
                    " Local Entry deployment failed");
            log.info(le + " LocalEntry Uploaded");
        }

        Iterator<OMElement> endpoints = synapseConfig.getChildrenWithLocalName(ENDPOINT);
        while (endpoints.hasNext()) {
            OMElement endpoint = endpoints.next();
            String ep = endpoint.getAttributeValue(new QName(NAME));
            if (ArrayUtils.contains(endPointAdminClient.getEndpointNames(), ep)) {
                Assert.assertTrue(endPointAdminClient.deleteEndpoint(ep), ep + " Endpoint deletion failed");
                Assert.assertTrue(esbUtils.isEndpointUnDeployed(backendURL, sessionCookie, ep),
                        ep + " Endpoint undeployment failed");
            }
            Assert.assertTrue(endPointAdminClient.addEndPoint(endpoint), " Endpoint addition failed");
            Assert.assertTrue(esbUtils.isEndpointDeployed(backendURL, sessionCookie, ep),
                    " Endpoint deployment failed");
            log.info(ep + " Endpoint Uploaded");
        }

        Iterator<OMElement> sequences = synapseConfig.getChildrenWithLocalName(SEQUENCE);
        while (sequences.hasNext()) {
            OMElement sequence = sequences.next();
            String sqn = sequence.getAttributeValue(new QName(NAME));
            boolean isSequenceExist = ArrayUtils.contains(sequenceAdminClient.getSequences(), sqn);
            if (("main".equalsIgnoreCase(sqn) || "fault".equalsIgnoreCase(sqn)) && isSequenceExist) {
                sequenceAdminClient.updateSequence(sequence);
            } else {
                if (isSequenceExist) {
                    sequenceAdminClient.deleteSequence(sqn);
                    Assert.assertTrue(esbUtils.isSequenceUnDeployed(backendURL, sessionCookie, sqn),
                            sqn + " Sequence undeployment failed");
                }
                sequenceAdminClient.addSequence(sequence);
                Assert.assertTrue(esbUtils.isSequenceDeployed(backendURL, sessionCookie, sqn),
                        " Sequence deployment failed");
            }
            log.info(sqn + " Sequence Uploaded");
        }

        Iterator<OMElement> proxies = synapseConfig.getChildrenWithLocalName(PROXY);
        while (proxies.hasNext()) {
            OMElement proxy = proxies.next();
            String proxyName = proxy.getAttributeValue(new QName(NAME));
            if (adminServiceService.isServiceExists(proxyName)) {
                proxyAdmin.deleteProxy(proxyName);
                Assert.assertTrue(esbUtils.isProxyUnDeployed(backendURL, sessionCookie, proxyName),
                        proxyName + " Undeployment failed");
            }
            proxyAdmin.addProxyService(proxy);
            Assert.assertTrue(esbUtils.isProxyDeployed(backendURL, sessionCookie, proxyName),
                    proxyName + " deployment failed");
            log.info(proxyName + " Proxy Uploaded");
        }

        Iterator<OMElement> messageStores = synapseConfig.getChildrenWithLocalName(MESSAGE_STORE);
        while (messageStores.hasNext()) {
            OMElement messageStore = messageStores.next();
            String mStore = messageStore.getAttributeValue(new QName(NAME));
            if (ArrayUtils.contains(messageStoreAdminClient.getMessageStores(), mStore)) {
                messageStoreAdminClient.deleteMessageStore(mStore);
                Assert.assertTrue(esbUtils.isMessageStoreUnDeployed(backendURL, sessionCookie, mStore),
                        mStore + " Message Store undeployment failed");
            }
            messageStoreAdminClient.addMessageStore(messageStore);
            Assert.assertTrue(esbUtils.isMessageStoreDeployed(backendURL, sessionCookie, mStore),
                    " Message Store deployment failed");
            log.info(mStore + " Message Store Uploaded");
        }

        Iterator<OMElement> messageProcessors = synapseConfig.getChildrenWithLocalName(MESSAGE_PROCESSOR);
        while (messageProcessors.hasNext()) {
            OMElement messageProcessor = messageProcessors.next();
            String mProcessor = messageProcessor.getAttributeValue(new QName(NAME));
            if (ArrayUtils.contains(messageProcessorClient.getMessageProcessorNames(), mProcessor)) {
                messageProcessorClient.deleteMessageProcessor(mProcessor);
                Assert.assertTrue(esbUtils.isMessageProcessorUnDeployed(backendURL, sessionCookie, mProcessor),
                        mProcessor + " Message Processor undeployment failed");
            }
            messageProcessorClient.addMessageProcessor(messageProcessor);
            Assert.assertTrue(esbUtils.isMessageProcessorDeployed(backendURL, sessionCookie, mProcessor),
                    " Message Processor  deployment failed");
            log.info(mProcessor + " Message Processor Uploaded");
        }


        Iterator<OMElement> templates = synapseConfig.getChildrenWithLocalName(TEMPLATE);
        while (templates.hasNext()) {
            OMElement template = templates.next();
            String templateName = template.getAttributeValue(new QName(NAME));
            if (template.getFirstChildWithName(new QName(template.getNamespace().getNamespaceURI(), SEQUENCE))
                != null) {
                if (ArrayUtils.contains(sequenceTemplateAdminServiceClient.getSequenceTemplates(), templateName)) {
                    sequenceTemplateAdminServiceClient.deleteTemplate(templateName);
                    Assert.assertTrue(esbUtils.isSequenceTemplateUnDeployed(backendURL, sessionCookie, templateName),
                            templateName + " Sequence Template undeployment failed");
                }
                sequenceTemplateAdminServiceClient.addSequenceTemplate(template);
                Assert.assertTrue(esbUtils.isSequenceTemplateDeployed(backendURL, sessionCookie, templateName),
                        " Sequence  Template  deployment  failed");
            } else {

                if (ArrayUtils.contains(endpointTemplateAdminServiceClient.getEndpointTemplates(), templateName)) {
                    endpointTemplateAdminServiceClient.deleteEndpointTemplate(templateName);
                    Assert.assertTrue(esbUtils.isEndpointTemplateUnDeployed(backendURL, sessionCookie, templateName),
                            templateName + " Endpoint Template undeployment failed");
                }
                endpointTemplateAdminServiceClient.addEndpointTemplate(template);
                Assert.assertTrue(esbUtils.isEndpointTemplateDeployed(backendURL, sessionCookie, templateName),
                        " Endpoint  Template  deployment  failed");
            }
            log.info(templateName + " Template Uploaded");
        }

        Iterator<OMElement> apiElements = synapseConfig.getChildrenWithLocalName(API);
        while (apiElements.hasNext()) {
            OMElement api = apiElements.next();
            String apiName = api.getAttributeValue(new QName(NAME));
            if (ArrayUtils.contains(apiAdminClient.getApiNames(), apiName)) {
                apiAdminClient.deleteApi(apiName);
                Assert.assertTrue(esbUtils.isApiUnDeployed(backendURL, sessionCookie, apiName),
                        apiName + " Api undeployment failed");
            }
            apiAdminClient.add(api);
            Assert.assertTrue(esbUtils.isApiDeployed(backendURL, sessionCookie, apiName), " Api deployment failed");
            log.info(apiName + " API Uploaded");
        }

        Iterator<OMElement> priorityExecutorList = synapseConfig.getChildrenWithLocalName(PRIORITY_EXECUTOR);
        while (priorityExecutorList.hasNext()) {
            OMElement executor = priorityExecutorList.next();
            String executorName = executor.getAttributeValue(new QName(NAME));
            if (ArrayUtils.contains(priorityMediationAdminClient.getExecutorList(), executorName)) {
                priorityMediationAdminClient.remove(executorName);
                Assert.assertTrue(esbUtils.isPriorityExecutorUnDeployed(backendURL, sessionCookie, executorName)
                        , executorName + " Priority Executor undeployment failed");
            }
            priorityMediationAdminClient.addPriorityMediator(executorName, executor);
            Assert.assertTrue(esbUtils.isPriorityExecutorDeployed(backendURL, sessionCookie, executorName),
                    " Priority Executor failed");
            log.info(executorName + " Priority Executor Uploaded");
        }

        Iterator<OMElement> taskList = synapseConfig.getChildrenWithLocalName(TASK);
        while (taskList.hasNext()) {
            OMElement task = taskList.next();
            String taskName = task.getAttributeValue(new QName(NAME));
            if (taskAdminClient.getScheduleTaskList().contains(taskName)) {
                taskAdminClient.updateTask(task);
                continue;
            }
            taskAdminClient.addTask(task);
            Assert.assertTrue(esbUtils.isScheduleTaskDeployed(backendURL, sessionCookie, taskName),
                    " Task deployment failed");
            log.info(taskName + " Task Uploaded");
        }

        Thread.sleep(1000);
        esbUtils.verifySynapseDeployment(synapseConfig, backendURL, sessionCookie);
        log.info("Synapse configuration  Deployed");

    }

    /**
     * Load a synapse configuration given the relative path of the file that contains the synapse configuration.
     *
     * @param relativeFilePath the path of the file relative to the resources directory
     * @throws Exception if an error occurs while updating the synapse configuration.
     */
    protected void loadESBConfigurationFromClasspath(String relativeFilePath) throws Exception {
        relativeFilePath = relativeFilePath.replaceAll("[\\\\/]", Matcher.quoteReplacement(File.separator));

        OMElement synapseConfig = esbUtils.loadResource(relativeFilePath);
        updateESBConfiguration(synapseConfig);

    }

    /**
     * Load a sample synapse configuration when the sample number is provided.
     *
     * @param sampleNo the sample configuration to be loaded
     * @throws Exception if an error occurs while updating the sample synapse configuration
     */
    protected void loadSampleESBConfiguration(int sampleNo) throws Exception {
        OMElement synapseSample = esbUtils.loadESBSampleConfiguration(sampleNo);
        updateESBConfiguration(synapseSample);
    }

    /**
     * Method to update current synapse config provided a new one.
     *
     * @param synapseConfig the new synapse configuration
     * @throws Exception if an error occurs during update
     */
    protected void updateESBConfiguration(OMElement synapseConfig) throws Exception {

        if (synapseConfiguration == null) {
            synapseConfiguration = synapseConfig;
        } else {
            Iterator<OMElement> itr = synapseConfig.cloneOMElement().getChildElements();
            while (itr.hasNext()) {
                synapseConfiguration.addChild(itr.next());
            }
        }
        updateESBConfiguration(setEndpoints(synapseConfig), contextUrls.getBackEndUrl(), sessionCookie);

        if (context.getProductGroup().isClusterEnabled()) {
            long deploymentDelay = Long.parseLong(context.getConfigurationValue("//deploymentDelay"));
            Thread.sleep(deploymentDelay);
            Iterator<OMElement> proxies = synapseConfig.getChildrenWithLocalName("proxy");
            while (proxies.hasNext()) {
                String proxy = proxies.next().getAttributeValue(new QName("name"));

                Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxy), deploymentDelay),
                        "Deployment Synchronizing failed in workers");
            }
        }
    }

    /**
     * Method to find if a wsdl is published.
     *
     * @param serviceUrl         the backend service URL
     * @param synchronizingDelay time to wait for the wsdl to be available since it can take time for the deployment
     * @return true if the wsdl is located
     * @throws Exception if an error occurs during checking availability
     */
    private boolean isProxyWSDlExist(String serviceUrl, long synchronizingDelay)
            throws Exception {
        return new ServiceDeploymentUtil().isServiceWSDlExist(serviceUrl, synchronizingDelay);

    }
}
