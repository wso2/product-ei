/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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
package org.wso2.carbon.service.mgt.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingHelper;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisMessage;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOnlyAxisOperation;
import org.apache.axis2.description.ModuleConfiguration;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.PolicyInclude;
import org.apache.axis2.description.TwoChannelAxisOperation;
import org.apache.axis2.description.java2wsdl.TypeTable;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.rpc.receivers.RPCInOnlyMessageReceiver;
import org.apache.axis2.rpc.receivers.RPCMessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyReference;
import org.apache.neethi.PolicyRegistry;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.core.util.SystemFilter;
import org.wso2.carbon.service.mgt.PolicyUtil;
import org.wso2.carbon.utils.ArchiveManipulator;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.FileManipulator;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.carbon.utils.ServerConstants;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class handles the serialization of an AxisServiceGroup into a bundled service archive,
 * i.e. a .aar file. An .aar file thus created can be deployed on a different instance of WSAS or
 * Axis2
 */

public final class ServiceArchiveCreator {

    private static Log log = LogFactory.getLog(ServiceArchiveCreator.class);

    private ServiceArchiveCreator() {
    }

    /**
     * This method will generate an aar based on the information given.
     *
     * @param configurationContext configuration context
     * @param serviceGroupName     service group name
     * @return String : an id. User can access the generated archive using HTTP GET interface and
     *         this Id
     * @throws AxisFault will be thrown.
     */
    public static String createArchive(ConfigurationContext configurationContext,
                                       String serviceGroupName) throws AxisFault {

        String axis2Repo = ServerConfiguration.getInstance().getFirstProperty(
                ServerConfiguration.AXIS2_CONFIG_REPO_LOCATION);
        if (CarbonUtils.isURL(axis2Repo)) {
            String message = "Archive creation is not supported with a URL based Axis2 repository. "
                             + "The repository in use is " + axis2Repo;
            log.error(message);
            throw new AxisFault(message);
        }
        String uuid = String.valueOf(System.currentTimeMillis() + Math.random()) + ".aar";
        AxisServiceGroup axisServiceGroup = configurationContext.getAxisConfiguration()
                .getServiceGroup(serviceGroupName);
        if (axisServiceGroup == null) {
            String error = "Service group " + serviceGroupName + " not found!";
            log.error(error);
            throw new AxisFault(error);
        }

        //TODO until a proper way to find the service type from Axis2, following workaround has
        // TODO been applied to JIRA - 378
        if (serviceGroupName.contains(".jar") || serviceGroupName.contains(".class")) {
            String message = "Archive creation not supported for " + serviceGroupName;
            log.error(message);
            throw new AxisFault(message);
        }
        URL axisServiceGroupURL = null;
        // Filtering axis1 services and data services from creating archives
        //TODO AxisServiceGroup should have a getFileURL method;
        for (Iterator<AxisService> iterator = axisServiceGroup.getServices(); iterator.hasNext(); ) {
            AxisService as = iterator.next();
            for (Parameter parameter : as.getParameters()) {
                String name = parameter.getName();

                Object obj = parameter.getValue();
                String value = "";
                if (obj != null) {
                    value = obj.toString();
                }
                if (name.equals("serviceType")
                    && (value.equals("axis1") ||
                        value.equals("data_service") ||
                        value.equals("jaxws") ||
                        value.equals("proxy") ||
						 value.equals("js_service") ||
                        value.equals("bpel") ||
                        value.equals("bpelmgt"))) {
                    String message = "WSO2 Carbon does not "
                                     + "support creating archive for " + value + " services.";
                    log.warn(message);
                    throw new AxisFault(message);
                }
            }

            if (axisServiceGroupURL == null) {
                if (as.getFileName() == null) {
                    String msg = "Request to create a service archive file for a " +
                                 "service group not found in side repo";
                    log.warn(msg);
                    throw new AxisFault(msg);
                }
                axisServiceGroupURL = as.getFileName();
            }
        }

        if (axisServiceGroupURL == null) {
            String error = ServiceArchiveCreator.class.getName() + " AxisServiceGroup "
                           + serviceGroupName + " location couldn't be found.";
            log.error(error);
            throw new AxisFault(error);
        }

        String workdir = (String) configurationContext.getProperty(ServerConstants.WORK_DIR);
        if (workdir == null) {
            String msg = "Work dir does not exist. Please make sure that the " +
                         ServerConstants.WORK_DIR + " property points to a proper workdir.";
            log.error(msg);
            throw new AxisFault(msg);
        }
        File f = new File(workdir + File.separator + "dump_aar" + File.separator + uuid);
        if (!f.exists() && !f.mkdirs()) {
            log.warn("Could not create " + f.getAbsolutePath());
        }

        try {

            File file = new File(axisServiceGroupURL.getPath());
            ArchiveManipulator am = new ArchiveManipulator();
            if (file.isDirectory()) {
                FileManipulator.copyDir(file, f);
            } else if (file.isFile() && axisServiceGroupURL.getPath().endsWith(".class")) {
                FileManipulator.copyFileToDir(file, f);
            } else {
                am.extract(axisServiceGroupURL.getPath(), f.getAbsolutePath());
            }

            File servicesF =
                    new File(f.getAbsolutePath() + File.separator + "META-INF", "services.xml");

            String servicesXmlPath = servicesF.getAbsolutePath();
            // delete the existing services.xml file
            if (servicesF.exists() && !servicesF.delete()) {
                log.warn("Could not delete the existing services.xml at : " +
                         servicesF.getAbsolutePath());
            }
            // create the new serices.xml file using the created xml infoset
            File newServicesXml = new File(servicesXmlPath);
            OMElement axisServiceGroupXMLInfoset = createServiceGroupXMLInfoset(axisServiceGroup);
            OutputStream os = new FileOutputStream(newServicesXml);
            axisServiceGroupXMLInfoset.serializeAndConsume(os);

            File[] oldWsdls = f.listFiles(new FileFilter() {
                public boolean accept(File fw) {
                    return fw.getName().endsWith(".wsdl");
                }
            });

            if (oldWsdls != null) {
                for (File oldWsdl : oldWsdls) {
                    if (oldWsdl.exists() && !oldWsdl.delete()) {
                        log.warn("Could not delete " + oldWsdl.getAbsolutePath());
                    }
                }
            }
            //Creating wsdls
            for (Iterator<AxisService> iterator = axisServiceGroup.getServices(); iterator.hasNext(); ) {
                AxisService axisService = iterator.next();

                boolean isRpcMessageReceiver = false;
                for (Iterator<AxisOperation> ops = axisService.getOperations(); ops.hasNext(); ) {
                    MessageReceiver receiver =
                            (ops.next()).getMessageReceiver();
                    isRpcMessageReceiver =
                            receiver instanceof RPCMessageReceiver || receiver instanceof RPCInOnlyMessageReceiver;
                }
                // If an RpcMessageReceiver is used, it does not make sense to store the WSDL in the AAR
                if (!isRpcMessageReceiver) {
                    File wsdlF = new File(f.getAbsolutePath() + File.separator + "META-INF",
                                          formatServiceName(axisService.getName()) + ".wsdl");
                    OutputStream wbOut = new FileOutputStream(wsdlF);
                    axisService.printWSDL(wbOut, NetworkUtils.getLocalHostname());
                }
            }
            File fout = new File(workdir + File.separator + "dump_aar_output" + File.separator
                                 + uuid);
            if (!fout.exists() && !fout.mkdirs()) {
                log.warn("Could not create " + fout.getAbsolutePath());
            }
            String outAARFilename = fout.getAbsolutePath() + File.separator +
                                    formatServiceName(serviceGroupName) + ".aar";

            am.archiveDir(outAARFilename, f.getPath());

            // TODO : This is a hack. storing file resources in the super tenant config context
            // this is because, currently File download servlet is only registered under super
            // tenat. all other places like wsdl2code work using the super tenant CC
            ConfigurationContext superTenantConfigContext = MessageContext
                    .getCurrentMessageContext().getConfigurationContext();

            Map fileResourcesMap =
                    (Map) superTenantConfigContext.getProperty(ServerConstants.FILE_RESOURCE_MAP);

            if (fileResourcesMap == null) {
                fileResourcesMap = new Hashtable();
                superTenantConfigContext.setProperty(ServerConstants.FILE_RESOURCE_MAP, fileResourcesMap);
            }

            File[] files = fout.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().endsWith(".aar");
                }
            });

            if ((files != null) && (files[0] != null) && (files[0].getAbsoluteFile() != null)) {
                fileResourcesMap.put(uuid, files[0].getAbsoluteFile().getAbsolutePath());
            }

            return ServerConstants.ContextPaths.DOWNLOAD_PATH + "?id=" + uuid;

        } catch (IOException e) {
            String msg = "IOException occurred while trying to create service archive for service "
                         + "group " + serviceGroupName;
            log.error(msg, e);
            throw new AxisFault(msg, e);
        } catch (XMLStreamException e) {
            String msg = "XMLStreamException occurred while trying to create service archive for service "
                         + "group " + serviceGroupName;
            log.error(msg, e);
            throw new AxisFault(msg, e);
        }
    }

    /**
     * This will create a services.xml with <service name="Foo"> ... </service> This will create an
     * independent service.
     *
     * @param axisService The AxisService which is to be serialized
     * @return OMElement  The serialized format for the service in the services.xml file
     */
    private static OMElement createServicesXMLInfoset(AxisService axisService) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("", "");
        OMElement serviceEle = createOMElement(fac, ns, DeploymentConstants.TAG_SERVICE);

        OMAttribute nameAttr = createOMAttribute(fac, ns, DeploymentConstants.ATTRIBUTE_NAME,
                                                 axisService.getName());
        OMAttribute wsAddAttr = createOMAttribute(fac, ns,
                                                  DeploymentConstants.ATTRIBUTE_WSADDRESSING,
                                                  AddressingHelper.getAddressingRequirementParemeterValue(axisService));
        OMAttribute targetNsAttr = createOMAttribute(fac, ns,
                                                     DeploymentConstants.TARGET_NAME_SPACE, axisService.getTargetNamespace());
        OMAttribute scopeAttr = createOMAttribute(fac, ns, DeploymentConstants.ATTRIBUTE_SCOPE,
                                                  axisService.getScope());
        serviceEle.addAttribute(nameAttr);
        serviceEle.addAttribute(wsAddAttr);
        serviceEle.addAttribute(targetNsAttr);
        serviceEle.addAttribute(scopeAttr);

        OMElement discEle = createOMElement(fac, ns, DeploymentConstants.TAG_DESCRIPTION,
                                            axisService.getDocumentation());

        serviceEle.addChild(discEle);

        OMElement schemaEle = createOMElement(fac, ns, DeploymentConstants.SCHEMA);
        OMAttribute schemaNsAttr = createOMAttribute(fac, ns,
                                                     DeploymentConstants.SCHEMA_NAME_SPACE, axisService.getSchemaTargetNamespace());
        OMAttribute schemaEleQualifiedAttr = createOMAttribute(fac, ns,
                                                               DeploymentConstants.SCHEMA_ELEMENT_QUALIFIED,
                                                               (axisService.isElementFormDefault() ? "true" : "false"));
        schemaEle.addAttribute(schemaNsAttr);
        schemaEle.addAttribute(schemaEleQualifiedAttr);

        serviceEle.addChild(schemaEle);

        Map p2nMap = axisService.getP2nMap();
        if (p2nMap != null) {
            Set entrySet = p2nMap.entrySet();
            for (Object anEntrySet : entrySet) {
                Map.Entry me = (Map.Entry) anEntrySet;
                String packageKey = (String) me.getKey();
                String namesapceValue = (String) me.getValue();
                OMElement mapping = createOMElement(fac, ns, DeploymentConstants.MAPPING);
                OMAttribute packageAttr = createOMAttribute(fac, ns,
                                                            DeploymentConstants.ATTRIBUTE_PACKAGE, packageKey);
                OMAttribute namespaceAttr = createOMAttribute(fac, ns,
                                                              DeploymentConstants.ATTRIBUTE_NAMESPACE, namesapceValue);
                mapping.addAttribute(packageAttr);
                mapping.addAttribute(namespaceAttr);
                schemaEle.addChild(mapping);
            }
        }

        if (!axisService.isEnableAllTransports()) {
            OMElement transportsEle = createOMElement(fac, ns, DeploymentConstants.TAG_TRANSPORTS);
            serviceEle.addChild(transportsEle);
            for (String transport : axisService.getExposedTransports()) {
                OMElement transportEle = createOMElement(fac, ns,
                                                         DeploymentConstants.TAG_TRANSPORT, transport);
                transportsEle.addChild(transportEle);

            }
        }

        //operations
        for (Iterator<AxisOperation> iterator = axisService.getOperations(); iterator.hasNext(); ) {
            AxisOperation operation = iterator.next();

            if (!operation.isControlOperation()) {
                OMElement operationEle = createOMElement(fac, ns, DeploymentConstants.TAG_OPERATION);
                serviceEle.addChild(operationEle);
                OMAttribute opNameAttr = createOMAttribute(fac, ns,
                                                           DeploymentConstants.ATTRIBUTE_NAME, operation.getName().getLocalPart());
                OMAttribute opMEPAttr = createOMAttribute(fac, ns, DeploymentConstants.TAG_MEP,
                                                          operation.getMessageExchangePattern());
                operationEle.addAttribute(opNameAttr);
                operationEle.addAttribute(opMEPAttr);

                OMElement opMREle = createOMElement(fac, ns,
                                                    DeploymentConstants.TAG_MESSAGE_RECEIVER);
                operationEle.addChild(opMREle);

                OMAttribute opMRClassAttr = createOMAttribute(fac, ns,
                                                              DeploymentConstants.TAG_CLASS_NAME, operation.getMessageReceiver()
                        .getClass().getName());
                opMREle.addAttribute(opMRClassAttr);

                List<String> mappingList = operation.getWSAMappingList();
                if (mappingList != null) {
                    for (String aMappingList : mappingList) {
                        OMElement mappingEle = createOMElement(fac, ns,
                                                               Constants.ACTION_MAPPING, aMappingList);
                        operationEle.addChild(mappingEle);

                    }
                }
                String outputAction = operation.getOutputAction();
                if (outputAction != null) {
                    OMElement outputActionMappingEle = createOMElement(fac, ns,
                                                                       org.apache.axis2.Constants.OUTPUT_ACTION_MAPPING, outputAction);
                    operationEle.addChild(outputActionMappingEle);
                }

                String[] faultActions = operation.getFaultActionNames();
                if (faultActions != null) {
                    for (String faultAction : faultActions) {
                        OMElement faultActionEle = createOMElement(fac, ns,
                                                                   Constants.FAULT_ACTION_MAPPING);
                        operationEle.addChild(faultActionEle);
                        OMAttribute faultActionAttr = createOMAttribute(fac, ns,
                                                                        Constants.FAULT_ACTION_NAME, faultAction);
                        faultActionEle.addAttribute(faultActionAttr);

                    }
                }

                List<Parameter> operationParameterList = operation.getParameters();
                serializeParameterList(operationParameterList, operationEle, fac, ns);

                Collection<AxisModule> operationLevelEngagedModulesCollection = operation.getEngagedModules();
                Collection<AxisModule> axisServiceLevelEngagedModuleCollection = axisService
                        .getEngagedModules();
                List<AxisModule> aoOnlyModuleList = new ArrayList<AxisModule>();
                for (AxisModule axisModule : operationLevelEngagedModulesCollection) {
                    if (axisServiceLevelEngagedModuleCollection.contains(axisModule)) {
                        continue;
                    }
                    aoOnlyModuleList.add(axisModule);
                }
                serializeModules(aoOnlyModuleList, operationEle, fac, ns, operation);

                Map<String, AxisMessage> axisMessagesMap = new AxisMessageLookup().lookup(operation);
                Set<Map.Entry<String, AxisMessage>> axisMessagesSet = axisMessagesMap.entrySet();
                for (Map.Entry<String, AxisMessage> me : axisMessagesSet) {
                    String lableKey = me.getKey();
                    AxisMessage axisMessage = me.getValue();
                    OMElement axisMessageEle = createOMElement(fac, ns,
                                                               DeploymentConstants.TAG_MESSAGE);
                    OMAttribute axisMessageLableAttr = createOMAttribute(fac, ns,
                                                                         DeploymentConstants.TAG_LABEL, lableKey);
                    axisMessageEle.addAttribute(axisMessageLableAttr);

                    List<Parameter> axisMessageParameterList = axisMessage.getParameters();
                    serializeParameterList(axisMessageParameterList, axisMessageEle, fac, ns);

                    //TODO replace with operation.getPolicySubject()
                    PolicyInclude policyInclude = operation.getPolicyInclude();
                    PolicyRegistry registry = policyInclude.getPolicyRegistry();
                    //TODO replace with policySubject.getAttachedPolicyComponents()
                    List policyList = policyInclude
                            .getPolicyElements(PolicyInclude.AXIS_MESSAGE_POLICY);
                    if (!policyList.isEmpty()) {
                        serializePolicyIncludes(axisMessageEle, policyList, registry);
                    }

                }

                PolicyInclude policyInclude = operation.getPolicyInclude();
                PolicyRegistry registry = policyInclude.getPolicyRegistry();
                List policyList = policyInclude
                        .getPolicyElements(PolicyInclude.AXIS_OPERATION_POLICY);
                if (!policyList.isEmpty()) {
                    serializePolicyIncludes(operationEle, policyList, registry);
                }
            }

        }

        List<Parameter> serviceParameterList = axisService.getParameters();
        serializeParameterList(serviceParameterList, serviceEle, fac, ns);

        //service level engaged modules.
        Collection<AxisModule> serviceEngagedModuleCollection = axisService.getEngagedModules();
        AxisDescription parent = axisService.getParent();
        AxisServiceGroup asg = (AxisServiceGroup) parent;
        Collection<AxisModule> asgEngagedModulesCollection = asg.getEngagedModules();
        List<AxisModule> asOnlyModuleList = new ArrayList<AxisModule>();
        for (AxisModule axisModule : serviceEngagedModuleCollection) {
            if (asgEngagedModulesCollection.contains(axisModule.getName())) {
                continue;
            }
            asOnlyModuleList.add(axisModule);
        }

        serializeModules(asOnlyModuleList, serviceEle, fac, ns, axisService);

        if (axisService.isCustomWsdl()) {
            OMElement package2QName = createOMElement(fac, ns,
                                                      DeploymentConstants.TAG_PACKAGE2QNAME);
            serviceEle.addChild(package2QName);
            TypeTable typeTable = axisService.getTypeTable();
            if (typeTable != null) {
                Map complexSchemaMap = typeTable.getComplexSchemaMap();
                Set complexSchemaSet = complexSchemaMap.entrySet();
                for (Object aComplexSchemaSet : complexSchemaSet) {
                    Map.Entry me = (Map.Entry) aComplexSchemaSet;
                    String packageKey = (String) me.getKey();
                    QName qName = (QName) me.getValue();
                    OMElement mapping = createOMElement(fac, ns, DeploymentConstants.TAG_MAPPING);
                    OMAttribute packageAttr = createOMAttribute(fac, ns,
                                                                DeploymentConstants.TAG_PACKAGE_NAME, packageKey);
                    OMAttribute qNameAttr = createOMAttribute(fac, ns,
                                                              DeploymentConstants.TAG_QNAME, qName.getNamespaceURI());
                    mapping.addAttribute(packageAttr);
                    mapping.addAttribute(qNameAttr);
                    package2QName.addChild(mapping);
                }
            }
        }

        PolicyInclude policyInclude = axisService.getPolicyInclude();
        PolicyRegistry registry = policyInclude.getPolicyRegistry();

        // services.xml
        List policyList = policyInclude.getPolicyElements(PolicyInclude.AXIS_SERVICE_POLICY);
        if (!policyList.isEmpty()) {
            serializePolicyIncludes(serviceEle, policyList, registry);
        }

        //TODO - Datalocators
        /*OMElement dataLocatorEle = createOMElement(fac,ns, DRConstants.DATA_LOCATOR_ELEMENT);
          serviceEle.addChild(dataLocatorEle);*/

        return serviceEle;
    }

    protected static OMElement createServiceGroupXMLInfoset(AxisServiceGroup axisServiceGroup) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("", "");
        OMElement serviceGroupEle = createOMElement(fac, ns, DeploymentConstants.TAG_SERVICE_GROUP);
        /*OMAttribute serviceGroupName = createOMAttribute(fac, ns,
                                                           DeploymentConstants.ATTRIBUTE_NAME,
                                                           axisServiceGroup.getServiceGroupName());
          serviceGroupEle.addAttribute(serviceGroupName);*/

        List<Parameter> parameterList = axisServiceGroup.getParameters();
        serializeParameterList(parameterList, serviceGroupEle, fac, ns);

        Collection<AxisModule> axisServiceGroupModuleCollection = axisServiceGroup.getEngagedModules();
        serializeModules(axisServiceGroupModuleCollection, serviceGroupEle, fac, ns,
                         axisServiceGroup);

        for (Iterator<AxisService> iterator = axisServiceGroup.getServices(); iterator.hasNext(); ) {
            AxisService axisService = iterator.next();
            OMElement axisServiceEle = createServicesXMLInfoset(axisService);
            serviceGroupEle.addChild(axisServiceEle);
        }
        return serviceGroupEle;
    }

    protected static void serializePolicyIncludes(OMElement parent, List policyList,
                                                  PolicyRegistry policyRegistry) {
        for (Object obj : policyList) {
            if (obj instanceof Policy) {
                Policy policy = (Policy) obj;
                parent.addChild((PolicyUtil.getPolicyAsOMElement(policy)));
            } else if (obj instanceof PolicyReference) {
                PolicyReference policyReference = (PolicyReference) obj;
                Policy policy = policyRegistry.lookup(policyReference.getURI());
                if (policy == null) {
                    log.error(ServiceArchiveCreator.class.getName()
                              + "policy object couldn't be NULL");
                    continue;
                }
                OMElement e = PolicyUtil.getPolicyAsOMElement(policy);
                parent.addChild(e);
            }

        }

    }

    protected static void serializeModules(Collection<AxisModule> moduleCollection,
                                           OMElement parent,
                                           OMFactory fac, OMNamespace ns,
                                           AxisDescription axisDesc) {
        if (moduleCollection != null) {

            for (AxisModule axisModule : moduleCollection) {
                String moduleName = axisModule.getName();
                if (SystemFilter.isFilteredOutModule(moduleName)
                    || axisDesc.getParent().isEngaged(axisModule)) {
                    continue;
                }

                OMElement moduleEle = createOMElement(fac, ns, DeploymentConstants.TAG_MODULE);

                OMAttribute moduleRefAttr = createOMAttribute(fac, ns,
                                                              DeploymentConstants.TAG_REFERENCE, moduleName);
                moduleEle.addAttribute(moduleRefAttr);
                parent.addChild(moduleEle);

                //module configs
                ModuleConfiguration moduleConfig;
                if (axisDesc instanceof AxisService) {
                    moduleConfig = ((AxisService) axisDesc).getModuleConfig(moduleName);
                } else if (axisDesc instanceof AxisOperation) {
                    moduleConfig = ((AxisOperation) axisDesc).getModuleConfig(moduleName);
                } else if (axisDesc instanceof AxisServiceGroup) {
                    moduleConfig = ((AxisServiceGroup) axisDesc).getModuleConfig(moduleName);
                } else {
                    return;
                }

                if (moduleConfig != null) {
                    OMElement moduleConfigEle = createOMElement(fac, ns,
                                                                DeploymentConstants.TAG_MODULE_CONFIG);
                    OMAttribute moduleConfigNameAttr = createOMAttribute(fac, ns,
                                                                         DeploymentConstants.ATTRIBUTE_NAME, moduleConfig.getModuleName());
                    moduleConfigEle.addAttribute(moduleConfigNameAttr);
                    parent.addChild(moduleConfigEle);
                    List<Parameter> paramsList = moduleConfig.getParameters();
                    serializeParameterList(paramsList, moduleConfigEle, fac, ns);
                }
            }
        }

    }

    protected static void serializeParameterList(List<Parameter> parameterList, OMElement parent,
                                                 OMFactory fac, OMNamespace ns) {
        if (parameterList != null) {
            for (Parameter parm : parameterList) {
                //TODO a check to see, whether this param is a control or not
                serializeParameter(parm, parent, fac, ns);
            }
        }

    }

    protected static void serializeParameter(Parameter param, OMElement parent, OMFactory fac,
                                             OMNamespace ns) {
        if (param == null || param.getValue() == null) {
            return;
        }
        int paramType = param.getParameterType();

        if (paramType != Parameter.ANY_PARAMETER) {
            OMElement paramEle = createOMElement(fac, ns, DeploymentConstants.TAG_PARAMETER);
            parent.addChild(paramEle);
            OMAttribute paramNameAttr = createOMAttribute(fac, ns,
                                                          DeploymentConstants.ATTRIBUTE_NAME, param.getName());
            OMAttribute paramLokedAttr = createOMAttribute(fac, ns,
                                                           DeploymentConstants.ATTRIBUTE_LOCKED, param.isLocked() ? "true" : "false");
            paramEle.addAttribute(paramNameAttr);
            paramEle.addAttribute(paramLokedAttr);

            if (paramType == Parameter.OM_PARAMETER) {
                paramEle.addChild((OMElement) param.getValue());
            } else if (paramType == Parameter.TEXT_PARAMETER) {
                paramEle.setText(param.getValue().toString());
            }

        }

    }

    protected static OMElement createOMElement(OMFactory fac, OMNamespace ns, String localName) {
        return fac.createOMElement(localName, ns);
    }

    protected static OMElement createOMElement(OMFactory fac, OMNamespace ns, String localName,
                                               String text) {
        OMElement omElement = fac.createOMElement(localName, ns);
        omElement.setText(text);
        return omElement;
    }

    protected static OMAttribute createOMAttribute(OMFactory fac, OMNamespace ns, String localName,
                                                   String value) {
        return fac.createOMAttribute(localName, ns, value);
    }

    /**
     * If the service is a hierarchical service, there can be '/' charactors in the service name.
     * If that is the case, when using service name as the wsdl file name, we have to use only
     * the last part of the service name.
     * Ex: foo/bar/TestService -> TestService
     *
     * @param name - original service name
     * @return - formatted service name
     */
    private static String formatServiceName(String name) {
        String newName = name;
        int temp = newName.lastIndexOf('/');
        if (temp != -1) {
            newName = newName.substring(temp + 1, newName.length());
        }
        return newName;
    }

    /**
     * TODO Find better way via Axis2. Introduce a new method getMessages to AxisOperation.
     */
    protected static class AxisMessageLookup {
        protected Map<String, AxisMessage> lookup(AxisOperation axisOperation) {
            Map<String, AxisMessage> axisMessageMap = new HashMap<String, AxisMessage>();
            if (axisOperation instanceof InOnlyAxisOperation) {
                axisMessageMap.put(WSDLConstants.MESSAGE_LABEL_IN_VALUE, axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE));
                return axisMessageMap;

            } else if (axisOperation instanceof OutOnlyAxisOperation) {
                axisMessageMap.put(WSDLConstants.MESSAGE_LABEL_OUT_VALUE, axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE));
                return axisMessageMap;

            } else if (axisOperation instanceof TwoChannelAxisOperation) {
                axisMessageMap.put(WSDLConstants.MESSAGE_LABEL_IN_VALUE, axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE));
                axisMessageMap.put(WSDLConstants.MESSAGE_LABEL_OUT_VALUE, axisOperation
                        .getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE));
                return axisMessageMap;

            } else {
                return axisMessageMap;
            }

        }
    }
}
