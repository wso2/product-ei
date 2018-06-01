/*
 *  Copyright (c) 2008, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.service.mgt;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisBinding;
import org.apache.axis2.description.AxisBindingOperation;
import org.apache.axis2.description.AxisEndpoint;
import org.apache.axis2.description.AxisMessage;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.PolicySubject;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisEvent;
import org.apache.axis2.util.PolicyLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyComponent;
import org.apache.neethi.PolicyEngine;
import org.apache.neethi.PolicyReference;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.application.deployer.AppDeployerUtils;
import org.wso2.carbon.application.deployer.CarbonApplication;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.core.util.ParameterUtil;
import org.wso2.carbon.core.util.SystemFilter;
import org.wso2.carbon.micro.integrator.core.internal.CarbonCoreDataHolder;
import org.wso2.carbon.service.mgt.util.Utils;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.DataPaginator;
import org.wso2.carbon.utils.FileManipulator;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.ServerException;
import org.wso2.carbon.utils.deployment.GhostDeployerUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ServiceAdmin extends AbstractAdmin implements ServiceAdminMBean {

    private static final Log log = LogFactory.getLog(ServiceAdmin.class);

    private static final String SERVICE_MUST_CONTAIN_AT_LEAST_ONE_TRANSPORT =
            "Cannot remove transport binding. " +
            "<br/>A service must contain at least one transport binding!";

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    public static final String DISABLE_TRY_IT_PARAM = "disableTryIt";
    public static final String DISABLE_DELETION_PARAM = "disableDeletion";
    private static final String AXIS2_SERVICE_TYPE = "axis2";
    private static final String PROXY_SERVICE_TYPE = "proxy";
    private static final String DATA_SERVICE_TYPE = "data_service";


    public ServiceAdmin() throws Exception {
        super();
//        pf = PersistenceFactory.getInstance(getAxisConfig());
//        spm = pf.getServicePM();
    }

    public ServiceAdmin(AxisConfiguration axisConfig) throws Exception {
        super(axisConfig);
//        pf = PersistenceFactory.getInstance(getAxisConfig());
//        spm = pf.getServicePM();
    }

    public void setConfigurationContext(ConfigurationContext configurationContext) {
        super.setConfigurationContext(configurationContext);
    }

    /**
     * This method add Policy to service at the Registry. Does not add the
     * policy to Axis2. To all Bindings available
     * <p/>
     * todo find from where addPoliciesToService is invoked and make sure the returned modulePaths conform to new persistence logic - kasung
     * Make it look Strings like name-version
     *
     * @param serviceName
     * @param policy
     * @param policyType
     * @throws Exception
     */
    public void addPoliciesToService(String serviceName, Policy policy, int policyType,
                                     String[] modulePaths) throws Exception {

        AxisService axisService = this.getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
  /*      ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();

        // persist
        OMElement policyWrapperElement = omFactory.createOMElement(Resources.POLICY, null);
        policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_TYPE, "" + policyType, null);
        //we don't need the version?
//        policyWrapperElement.addAttribute(Resources.VERSION, version, null);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
            //todo this MUST be a element instead of an attribute. That's how the core is designed.
            policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_UUID, "" + policy.getId(), null);
            OMElement policyElement = PersistenceUtils.createPolicyElement(policy);
            policyWrapperElement.addChild(policyElement);
        } else {
            OMElement policyElement = PersistenceUtils.createPolicyElement(policy);
            policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_UUID, "" + policy.getId(), null);
            policyWrapperElement.addChild(policyElement);
        }

        String serviceXPath = PersistenceUtils.getResourcePath(axisService);
        String policyResourcePath = serviceXPath + "/" + Resources.POLICIES +
                                    "/" + Resources.POLICY + PersistenceUtils.
                getXPathAttrPredicate(Resources.ServiceProperties.POLICY_UUID, policy.getId());

        boolean transactionStarted1 = sfpm.isTransactionStarted(serviceGroupId);
        if (!transactionStarted1) {
            sfpm.beginTransaction(serviceGroupId);
        }
        if (sfpm.elementExists(serviceGroupId, policyResourcePath)) {
            sfpm.get(serviceGroupId, policyResourcePath).detach();
        }
        sfpm.put(serviceGroupId, policyWrapperElement, serviceXPath + "/" + Resources.POLICIES);
        if (!transactionStarted1) {
            sfpm.commitTransaction(serviceGroupId);
        }*/

        // at axis2
        Map endPointMap = axisService.getEndpoints();
        for (Object o : endPointMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            AxisEndpoint point = (AxisEndpoint) entry.getValue();
            AxisBinding binding = point.getBinding();
            binding.applyPolicy(policy);
        }

/*        // handle each module required
        try {
*//*            boolean transactionStarted = sfpm.isTransactionStarted(serviceGroupId);
            if (!transactionStarted) {
                sfpm.beginTransaction(serviceGroupId);
            }*//*
            for (String path : modulePaths) {
                String[] values = path.split("/");
                String moduleName = values[0]; //todo make sure this is correct
                String moduleVersion = values[1];

                OMElement modAssoc = PersistenceUtils.createModule(moduleName, moduleVersion, Resources.Associations.REQUIRED_MODULES);
                //OMElement assoc = PersistenceUtils.createAssociation(path, Resources.Associations.REQUIRED_MODULES);

                sfpm.put(serviceGroupId, modAssoc, policyResourcePath); //xpath double checked
                this.engageModuleToService(serviceName, moduleName, moduleVersion);
            }
            if (!transactionStarted) {
                sfpm.commitTransaction(serviceGroupId);
            }
        } catch (Exception e) {
            sfpm.rollbackTransaction(serviceGroupId);
            throw AxisFault.makeFault(e);
        }*/
    }

    //  todo add this to jira as this is now complete - kasung
    public void removeServicePoliciesByNamespace(String serviceName, String namesapce)
            throws Exception {
        try {
            AxisService service = getAxisConfig().getServiceForActivation(serviceName);

            // persist
  /*          String serviceGroupId = service.getAxisServiceGroup().getServiceGroupName();
            OMFactory omFactory = OMAbstractFactory.getOMFactory();
            ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();
            String serviceXPath = PersistenceUtils.getResourcePath(service);
            String policyResourcePath = serviceXPath + "/" + Resources.POLICIES + "/" + Resources.POLICY;

            List<String> removedModuleAssociations = new ArrayList<String>();
            boolean isTransactionStarted = sfpm.isTransactionStarted(serviceGroupId);
            if (!isTransactionStarted) {
                sfpm.beginTransaction(serviceGroupId);
            }
            if (sfpm.elementExists(serviceGroupId, policyResourcePath)) {
                List policyWrappers = sfpm.getAll(serviceGroupId, policyResourcePath);
                for (Object obj : policyWrappers) {
                    OMElement policyWrapper = (OMElement) obj;
                    OMElement element = policyWrapper.getFirstChildWithName(
                            new QName(Resources.WS_POLICY_NAMESPACE, "Policy")); //note that P is capital
                    Policy policy = PolicyEngine.getPolicy(element);
                    removeAssertionsByNamespace(policy, namesapce);
                    int iValue = policy.getAssertions().size();
                    if (iValue == 1) { // TODO :: Check here
                        List values = sfpm.getAll(
                                serviceGroupId,
                                policyResourcePath +
                                PersistenceUtils.getXPathAttrPredicate(Resources.ServiceProperties.POLICY_UUID,
                                                                       policyWrapper.getAttributeValue(new QName(
                                                                               Resources.ServiceProperties.POLICY_UUID))) +
                                "/" + Resources.ModuleProperties.MODULE_XML_TAG +
                                PersistenceUtils.getXPathAttrPredicate(Resources.ModuleProperties.TYPE,
                                                                       Resources.Associations.REQUIRED_MODULES));
                        for (Object val : values) {
                            OMElement moduleAssoc = (OMElement) val;
                            removedModuleAssociations.add(
                                    moduleAssoc.getAttributeValue(new QName(Resources.NAME)) +
                                    "/" + moduleAssoc.getAttributeValue(new QName(Resources.VERSION))); //ex. rampart/1.6.3-SNAPSHOT
                        }
                    }
                    policyWrapper.detach();
                }
            }
            if (!isTransactionStarted) {
                sfpm.commitTransaction(serviceGroupId);
            }*/

            // at axis2
            Map endPointMap = service.getEndpoints();
            for (Object o : endPointMap.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                AxisEndpoint point = (AxisEndpoint) entry.getValue();
                AxisBinding binding = point.getBinding();
                Policy policy = binding.getEffectivePolicy();
                removeAssertionsByNamespace(policy, namesapce);
            }

            // handle module removals
//            disengageUnusedModuleFromAxisService(serviceName, removedModuleAssociations);

        } catch (Exception e) {
            throw new Exception("errorRemovingServicePolicies", e);
        }
    }

//    private void disengageUnusedModuleFromAxisService(String serviceName,
//                                                      List<String> checkList) throws Exception {
//        AxisService axisService = this.getAxisService(serviceName);
//        // persist
///*        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        OMFactory omFactory = OMAbstractFactory.getOMFactory();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();
//        String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//        String policyResourcePath = serviceXPath + "/" + Resources.POLICIES + "/" + Resources.POLICY;
//
//
////        String serviceResourcePath = Resources.SERVICE_GROUPS
////                                     + axisService.getAxisServiceGroup().getServiceGroupName()
////                                     + Resources.SERVICES + axisService.getName();
////        String policyResourcePath = serviceResourcePath + Resources.POLICIES;
//        boolean isTransactionStarted = sfpm.isTransactionStarted(serviceGroupId);
//        if (!isTransactionStarted) {
//            sfpm.beginTransaction(serviceGroupId);
//        }
//
//        boolean doDisengage = true;
//        for (String moduleInfo : checkList) {
//            // check required by other policies
////            if(sfpm.elementExists(serviceGroupId, policyResourcePath)) {
//            List policyWrappers = sfpm.getAll(serviceGroupId, policyResourcePath);
////                for (Object obj : policyWrappers) {
//
//            for (Object obj : policyWrappers) {
//                OMElement policyWrapper = (OMElement) obj;
////                OMElement element = policyWrapper.getFirstChildWithName(
////                        new QName(Resources.WS_POLICY_NAMESPACE, "Policy")); //note that P is capital
////                Policy policy = PolicyEngine.getPolicy(element);
//                List values = sfpm.getAll(
//                        serviceGroupId,
//                        policyResourcePath +
//                        PersistenceUtils.getXPathAttrPredicate(Resources.ServiceProperties.POLICY_UUID,
//                                                               policyWrapper.getAttributeValue(new QName(
//                                                                       Resources.ServiceProperties.POLICY_UUID))) +
//                        "/" + Resources.ModuleProperties.MODULE_XML_TAG +
//                        PersistenceUtils.getXPathAttrPredicate(Resources.ModuleProperties.TYPE,
//                                                               Resources.Associations.REQUIRED_MODULES));
//                for (Object val : values) {
//                    OMElement moduleAss = (OMElement) val;
//                    String moduleInfoEl = moduleAss.getAttribute(new QName(Resources.NAME)) +
//                                          "/" + moduleAss.getAttribute(new QName(Resources.VERSION));
//                    if (moduleInfoEl.equals(moduleInfo)) {
//                        doDisengage = false;
//                        break;
//                    }
//                }
//
//                if (!doDisengage) {
//                    break;
//                }
//            }*/
//
//   /*         // check required by service level engaged modules
//            if (doDisengage) {
//                List serviceAss = sfpm.getAll(
//                        serviceGroupId,
//                        serviceXPath +
//                        "/" + Resources.ModuleProperties.MODULE_XML_TAG +
//                        PersistenceUtils.getXPathAttrPredicate(Resources.ModuleProperties.TYPE,
//                                                               Resources.Associations.ENGAGED_MODULES));
//                for (Object obj : serviceAss) {
//                    OMElement moduleAss = (OMElement) obj;
//                    String moduleInfoEl = moduleAss.getAttribute(new QName(Resources.NAME)) +
//                                          "/" + moduleAss.getAttribute(new QName(Resources.VERSION));
//                    if (moduleInfoEl.equals(moduleInfo)) {
//                        doDisengage = false;
//                        break;
//                    }
//                }
//            }*/
//
//            if (doDisengage) {
//
//                String[] values = moduleInfo.split("/");
//                String moduleName = values[values.length - 2];       //length should be 2 always
//                String moduleVersion = values[values.length - 1];
//                // disengage at Axis
//                axisService.disengageModule(axisService.getAxisConfiguration().getModule(
//                        moduleName, moduleVersion));
//            }
//        }
//
//  /*      if (!isTransactionStarted) {
//            sfpm.commitTransaction(serviceGroupId);
//        }*/
//
//    }

    public void engageModuleToService(String serviceName, String moduleName, String version)
            throws Exception {
        AxisService axisService = this.getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();
//
//        // engage at persistence
////        String moduleXPath = Resources.ModuleProperties.VERSION_XPATH+PersistenceUtils.
////                getXPathAttrPredicate(Resources.ModuleProperties.VERSION_ID, version);
//        OMElement modElement = PersistenceUtils.createModule(moduleName, version,
//                                                             Resources.Associations.ENGAGED_MODULES);
//        if (!sfpm.elementExists(serviceGroupId,
//                                serviceXPath + "/" + Resources.ModuleProperties.MODULE_XML_TAG +
//                                PersistenceUtils.getXPathAttrPredicate(Resources.NAME, moduleName) +
//                                PersistenceUtils.getXPathAttrPredicate(Resources.VERSION, version))) {
//            pf.getServiceGroupFilePM().put(serviceGroupId, modElement, serviceXPath);
//        }

        // engage at axis2
        AxisModule module = axisService.getAxisConfiguration().getModule(moduleName);
        axisService.disengageModule(module);
        axisService.engageModule(module);
    }

    private void removeAssertionsByNamespace(Policy policy, String namespace) {
        List lst = policy.getAssertions();
        Iterator itePolices = lst.iterator();
        while (itePolices.hasNext()) {
            PolicyComponent comp = (PolicyComponent) itePolices.next();
            if (comp instanceof Assertion) {
                Assertion assertion = (Assertion) comp;
                if (assertion.getName().getNamespaceURI().equals(namespace)) {
                    itePolices.remove();
                }
            }
        }
    }

    /**
     * Return service group details for a given service group
     *
     * @param serviceGroupName
     * @return ServiceGroupMetaData
     * @throws org.apache.axis2.AxisFault TODO: Do we need this in ServiceAdmin?
     */
    public ServiceGroupMetaData listServiceGroup(String serviceGroupName) throws AxisFault {
        ServiceGroupMetaData sgmd = new ServiceGroupMetaData();

        AxisServiceGroup serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);
        sgmd.setServiceGroupName(serviceGroup.getServiceGroupName());

        Parameter parameter = serviceGroup.getParameter(Constants.Configuration.ENABLE_MTOM);
        if (parameter != null) {
            sgmd.setMtomStatus((String) parameter.getValue());
        }

        return sgmd;
    }

    /**
     * List all the available services
     *
     * @param serviceTypeFilter   Service type of services to be returned
     * @param serviceSearchString Service name or part of a service name
     * @param pageNumber          The number of the page to be retrieved
     * @return The service metadata
     * @throws org.apache.axis2.AxisFault If an error occurs while retrieving services
     */
    public ServiceMetaDataWrapper listServices(String serviceTypeFilter,
                                               String serviceSearchString,
                                               int pageNumber) throws AxisFault {
        if (serviceTypeFilter == null) {
            serviceTypeFilter = "ALL";
        }
        if (pageNumber < 0 || pageNumber == Integer.MAX_VALUE) {
            pageNumber = 0;
        }
        List<ServiceMetaData> serviceList = new ArrayList<ServiceMetaData>();
        TreeSet<String> serviceTypes = new TreeSet<String>();
        serviceTypes.add("axis2");

        HashMap<String, AxisService> axisServices = getAxisConfig().getServices();
        Set<String> axisFaultServices = (getAxisConfig().getFaultyServices()).keySet();
        List<AxisService> axisServicesList = new ArrayList<AxisService>();

        // we have to check services in transit ghost state as well..
        Map<String, AxisService> originalTransitGhosts = GhostDeployerUtils
                .getTransitGhostServicesMap(getAxisConfig());
        Map<String, AxisService> clonedTransitGhosts = new HashMap<String, AxisService>();
        clonedTransitGhosts.putAll(originalTransitGhosts);

        for (Map.Entry<String, AxisService> entry : axisServices.entrySet()) {
            AxisService axisService = entry.getValue();
            // Filtering the admin services
            if (SystemFilter.isAdminService(axisService) || SystemFilter.isHiddenService(axisService)) {
                continue;  // No advancement of currentIndex
            }
            String serviceType = "axis2";
            Parameter serviceTypeParam;
            serviceTypeParam = axisService.getParameter(ServerConstants.SERVICE_TYPE);
            if (serviceTypeParam != null) {
                serviceType = (String) serviceTypeParam.getValue();
                serviceTypes.add(serviceType);
            }
            // Filter out client side services
            if (axisService.isClientSide()) {
                continue;
            }
            // Filter out services based on service type
            if (!serviceTypeFilter.equals("ALL") && !serviceTypeFilter.equals(serviceType)) {
                continue;
            }
            // Filter out services based on serviceSearchString
            if (serviceSearchString != null &&
                serviceSearchString.trim().length() > 0 &&
                !isServiceSatisfySearchString(serviceSearchString, axisService.getName())) {
                continue;
            }
            axisServicesList.add(axisService);
            if (clonedTransitGhosts.containsKey(axisService.getName())) {
                clonedTransitGhosts.remove(axisService.getName());
            }
        }

        Collection<AxisService> transitGhosts = clonedTransitGhosts.values();
        for (AxisService transitGhost : transitGhosts) {
            axisServicesList.add(transitGhost);
        }

        if (axisServicesList.size() > 0) {
            Collections.sort(axisServicesList, new Comparator<AxisService>() {
                public int compare(AxisService arg0, AxisService arg1) {
                    return arg0.getName().compareToIgnoreCase(arg1.getName());
                }
            });
        }

//        String itemsPerPage = ServerConfiguration.getInstance().getFirstProperty("ItemsPerPage");
//        int itemsPerPageInt = 10; // the default number of item per page
//        if (itemsPerPage != null) {
//            itemsPerPageInt = Integer.parseInt(itemsPerPage);
//        }
//        int startIndex = pageNumber * itemsPerPageInt;
//        int endIndex = (pageNumber + 1) * itemsPerPageInt;

        //Get only required services for page.
        List<AxisService> axisServicesRequiredForPage = new ArrayList<AxisService>();
//        for (int i = startIndex; i < endIndex && i < axisServicesList.size(); i++) {
//            axisServicesRequiredForPage.add(axisServicesList.get(i));
//        }
        for (AxisService anAxisServicesList : axisServicesList) {
            axisServicesRequiredForPage.add(anAxisServicesList);
        }

        for (AxisService axisService : axisServicesRequiredForPage) {
            String serviceType = "axis2";
            Parameter serviceTypeParam;
            serviceTypeParam = axisService.getParameter(ServerConstants.SERVICE_TYPE);
            if (serviceTypeParam != null) {
                serviceType = (String) serviceTypeParam.getValue();
                serviceTypes.add(serviceType);
            }

            ServiceMetaData service = new ServiceMetaData();
            String serviceName = axisService.getName();
            service.setName(serviceName);
            service.setCAppArtifact(isAxisServiceCApp(axisService));

            // extract service type
            serviceTypeParam = axisService.getParameter(ServerConstants.SERVICE_TYPE);
            if (serviceTypeParam != null) {
                serviceType = (String) serviceTypeParam.getValue();
            }
            service.setServiceType(serviceType);
            AxisConfiguration axisConfiguration = getAxisConfig();
            service.setWsdlURLs(Utils.getWsdlInformation(serviceName, axisConfiguration));
            service.setTryitURL(Utils.getTryitURL(serviceName, getConfigContext()));
            service.setActive(axisService.isActive());
            Parameter parameter = axisService.getParameter(ServiceAdmin.DISABLE_TRY_IT_PARAM);
            if (parameter != null && Boolean.TRUE.toString().equalsIgnoreCase((String) parameter.getValue())) {
                service.setDisableTryit(true);
            }
            parameter = axisService.getParameter(ServiceAdmin.DISABLE_DELETION_PARAM);
            if (parameter != null && Boolean.TRUE.toString().equalsIgnoreCase((String) parameter.getValue())) {
                service.setDisableDeletion(true);
            }
            service.setServiceGroupName(axisService.getAxisServiceGroup().getServiceGroupName());

            // find the current security scenario id
            if (GhostDeployerUtils.isGhostService(axisService)) {
                Parameter secParam = axisService.getParameter(CarbonConstants
                                                                      .GHOST_ATTR_SECURITY_SCENARIO);
                if (secParam != null) {
                    service.setSecurityScenarioId((String) secParam.getValue());
                }
            } else {
//                SecurityScenarioData securityScenario = getSecurityScenario(serviceName);
//                if (securityScenario != null) {
//                    service.setSecurityScenarioId(securityScenario.getScenarioId());
//                }
            }
            if(!axisFaultServices.contains(axisService.getName())){
            	serviceList.add(service);
            }
        }
        ServiceMetaDataWrapper wrapper;
        wrapper = new ServiceMetaDataWrapper();
        wrapper.setNumberOfCorrectServiceGroups(getNumberOfServiceGroups());
        wrapper.setNumberOfFaultyServiceGroups(getNumberOfFaultyServices());
        wrapper.setServiceTypes(serviceTypes.toArray(new String[serviceTypes.size()]));
        wrapper.setNumberOfActiveServices(getNumberOfActiveServices());
        //  DataPaginator.doPaging(pageNumber, axisServicesList, serviceList, wrapper);
        DataPaginator.doPaging(pageNumber, serviceList, wrapper);
        return wrapper;
    }

    private boolean isServiceSatisfySearchString(String serviceSearchString,
                                                 String axisServiceName) {
        if (serviceSearchString != null) {
            String regex = serviceSearchString.toLowerCase().
                    replace("..?", ".?").replace("..*", ".*").
                    replaceAll("\\?", ".?").replaceAll("\\*", ".*?");

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(axisServiceName.toLowerCase());

            return regex.trim().length() == 0 || matcher.find();
        }
        return false;
    }

    public int getNumberOfServiceGroups() throws AxisFault {
        List<String> sgList = new ArrayList<String>();
        for (Iterator<AxisServiceGroup> serviceGroups = getAxisConfig().getServiceGroups();
             serviceGroups.hasNext(); ) {
            AxisServiceGroup serviceGroup = serviceGroups.next();
            if (!SystemFilter.isFilteredOutService(serviceGroup)) {
                if (!serviceGroup.getServices().hasNext() ||
                    serviceGroup.getServices().next().isClientSide()) {
                    continue; // No advancement of currentIndex
                }
                sgList.add(serviceGroup.getServiceGroupName());
            }
        }
        // check service groups in transit ghost state as well
        Map<String, AxisService> transitGhostServicesMap = GhostDeployerUtils
                .getTransitGhostServicesMap(getAxisConfig());
        Collection<AxisService> transitServices = transitGhostServicesMap.values();
        for (AxisService transitGhost : transitServices) {
            AxisServiceGroup sg = transitGhost.getAxisServiceGroup();
            if (!sgList.contains(sg.getServiceGroupName())) {
                sgList.add(sg.getServiceGroupName());
            }
        }
        Set<String> faultServices = (getAxisConfig().getFaultyServices()).keySet();
        sgList.removeAll(faultServices);
        return sgList.size();
    }

    public int getNumberOfActiveServices() throws AxisFault {
        List<String> activeList = new ArrayList<String>();
        Map<String, AxisService> services = getAxisConfig().getServices();
        Set<String> faultServices = (getAxisConfig().getFaultyServices()).keySet();
        for (AxisService service : services.values()) {
            if (!SystemFilter.isFilteredOutService((AxisServiceGroup) service.getParent()) &&
                !service. isClientSide() && service.isActive() &&
                !faultServices.contains(service.getName())) {
                activeList.add(service.getName());
            }
        }
        // check services in transit ghost state as well
        Map<String, AxisService> transitGhostServicesMap = GhostDeployerUtils
                .getTransitGhostServicesMap(getAxisConfig());
        Collection<AxisService> transitServices = transitGhostServicesMap.values();
        for (AxisService transitGhost : transitServices) {
            if (transitGhost.isActive() && !activeList.contains(transitGhost.getName())) {
                activeList.add(transitGhost.getName());
            }
        }
        return activeList.size();
    }


    public int getNumberOfInactiveServices() throws Exception {
        int inactiveServices = 0;
        Map<String, AxisService> services = getAxisConfig().getServices();
        for (AxisService service : services.values()) {
            if (!SystemFilter.isFilteredOutService((AxisServiceGroup) service.getParent()) &&
                !service.isActive()) {
                inactiveServices++;
            }
        }
        return inactiveServices;
    }

    public int getNumberOfFaultyServices() {
        return getAxisConfig().getFaultyServices().size();
    }

    public FaultyServicesWrapper getFaultyServiceArchives(int pageNumber) throws AxisFault {
        AxisConfiguration ac = getAxisConfig();
        String repository = ac.getRepository().getPath();
        Hashtable<String, String> faultyServices = ac.getFaultyServices();
        List<FaultyService> fsList = new ArrayList<FaultyService>();
        for (Map.Entry<String, String> entry : faultyServices.entrySet()) {
            String artifactPath = entry.getKey();
            String serviceName = artifactPath;
            String fault = entry.getValue();
            FaultyService fs = new FaultyService();

            if (File.separatorChar == '\\') {
                serviceName = serviceName.replace('\\', '/');
                repository = repository.replace('\\', '/');
                if (repository.startsWith("/")) {
                    repository = repository.substring(1);
                }
            }
            if (serviceName.endsWith("/")) {
                serviceName = serviceName.substring(0, serviceName.length() - 1);
            }
            if (repository.endsWith("/")) {
                repository = repository.substring(0, repository.length() - 1);
            }

            if (serviceName.startsWith(repository)) {
                serviceName = serviceName.substring(repository.length() + 1);
                serviceName = serviceName.substring(serviceName.indexOf('/') + 1);
            }

            int slashIndex = serviceName.lastIndexOf('/');
            int dotIndex = serviceName.lastIndexOf('.');
            if (dotIndex != -1 && (dotIndex > slashIndex)) {
                serviceName = serviceName.substring(0, dotIndex);
            }

            //Retrieving faulty services registered via CarbonConfigurationContext
            AxisService axisService = CarbonUtils.getFaultyService(artifactPath,
                                                                   this.getConfigContext());
            fs.setServiceName(serviceName);
            fs.setFault(fault);

            if (File.separatorChar == '\\') {
                artifactPath = artifactPath.replace('\\', '/');
            }

            if (axisService != null) {
                fs.setArtifact(artifactPath.replace(repository, ""));
                fs.setServiceType(getServiceType(axisService));
                fsList.add(fs);
                continue;
            }

            // sometimes we get the real serviceName. Ex: proxy services. In that case, there
            // might be an AxisService object..
            axisService = ac.getService(serviceName);

            if (artifactPath.startsWith(repository)) {
                artifactPath = artifactPath.replace(repository, "");
                fs.setArtifact(artifactPath);
            }

            fs.setServiceName(serviceName);
            fs.setFault(fault);

            if (axisService != null) {
                fs.setServiceType(getServiceType(axisService));
            }

            fsList.add(fs);
        }
        FaultyServicesWrapper wrapper = null;
        if (fsList.size() > 0) {
            Collections.sort(fsList, new Comparator<FaultyService>() {
                public int compare(FaultyService arg0, FaultyService arg1) {
                    return arg0.getServiceName().compareToIgnoreCase(arg1.getServiceName());
                }
            });

            // Pagination
            wrapper = new FaultyServicesWrapper();
            wrapper.setNumberOfFaultyServiceGroups(getNumberOfFaultyServices());
            DataPaginator.doPaging(pageNumber, fsList, wrapper);
        }
        return wrapper;
    }

    public boolean deleteFaultyServiceGroup(String archiveName) throws AxisFault {

        String repository = getAxisConfig().getRepository().getPath();
        String originalName = archiveName;
        if (repository.endsWith("/")) {
            repository = repository.substring(0, repository.length() - 1);
        }
        if (File.separatorChar == '\\') {
            archiveName = archiveName.replace('/', '\\');
            if (repository.startsWith("/")) {
                repository = repository.substring(1);
            }
            repository = repository.replace('/', '\\');
        }

        if (archiveName.indexOf(repository) != 0) {
        	originalName = repository + archiveName;
        }

        if (log.isDebugEnabled()) {
            log.debug("Deleting faulty service archive " + archiveName);
        }
        boolean isDeleted = false;
        if (archiveName.trim().length() != 0) {
            File file = new File(originalName);
            if (file.exists()) {
                if (!((file.isDirectory() && FileManipulator.deleteDir(file)) || file
                        .delete())) {
                    throw new AxisFault("Faulty service archive deletion failed. "
                                        + "Due to a JVM issue on MS-Windows, "
                                        + "service archive files cannot be deleted. "
                                        + "Please stop the server and manually delete this file.");
                } else {
                    isDeleted = true;
                    getAxisConfig().getFaultyServices().remove(originalName);
                }
            } else {
                isDeleted = true;
                getAxisConfig().getFaultyServices().remove(archiveName);
                deleteServiceGroup(archiveName);
            }
        } else {
	        	isDeleted = true;
	        	getAxisConfig().getFaultyServices().remove(archiveName);
	        	deleteServiceGroup(archiveName);
        	}

        return isDeleted;
    }

    public void deleteAllNonAdminServiceGroups() throws AxisFault {
        for (Iterator<AxisServiceGroup> iter = getAxisConfig().getServiceGroups(); iter.hasNext(); ) {
            AxisServiceGroup asGroup = iter.next();
            if (!SystemFilter.isFilteredOutService(asGroup)) {
                deleteServiceGroup(asGroup.getServiceGroupName());
            }
        }
    }

    public void deleteAllFaultyServiceGroups() throws AxisFault {
        for (String fileName : getAxisConfig().getFaultyServices().values()) {
            deleteFaultyServiceGroup(fileName);
        }
    }

    /**
     * Check the service group list for service groups which has more than one service
     *
     * @param serviceGroupsList - list of service groups
     * @return true if found
     * @throws AxisFault on error
     */
    public boolean checkForGroupedServices(String[] serviceGroupsList) throws AxisFault {
        AxisConfiguration axisConfig = getAxisConfig();
        for (String serviceGroup : serviceGroupsList) {
            AxisServiceGroup asGroup = axisConfig.getServiceGroup(serviceGroup);
            int count = 0;
            for (Iterator serviceIter = asGroup.getServices(); serviceIter.hasNext(); ) {
                serviceIter.next();
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteServiceGroups(String[] serviceGroups) throws AxisFault {
        //remove duplicates
        Set<String> serviceGroupsSet = new HashSet<String>(Arrays.asList(serviceGroups));
        String[] serviceGroupsArray = new String[serviceGroupsSet.size()];
        serviceGroupsSet.toArray(serviceGroupsArray);
        for (String serviceGroup : serviceGroupsArray) {
            deleteServiceGroup(serviceGroup);
        }
    }

    public void deleteFaultyServiceGroups(String[] fileNames) throws AxisFault {
        for (String fileName : fileNames) {
            deleteFaultyServiceGroup(fileName);
        }
    }

    private void deleteServiceGroup(String serviceGroupName) throws AxisFault {
        AxisConfiguration axisConfig = getAxisConfig();
        AxisServiceGroup asGroup = axisConfig.getServiceGroup(serviceGroupName);

        if (asGroup == null) {
            throw new AxisFault("Invalid service group name " + serviceGroupName);
        }

        if (SystemFilter.isFilteredOutService(asGroup)) {
            String msg = "Cannot delete admin service group " + serviceGroupName;
            log.error(msg);
            throw new AxisFault(msg);
        }

        String fileName = null;
        for (Iterator<AxisService> serviceIter = asGroup.getServices(); serviceIter.hasNext(); ) {
            PrivilegedCarbonContext privilegedCarbonContext =
                    PrivilegedCarbonContext.getThreadLocalCarbonContext();
//            PrivilegedCarbonContext.startTenantFlow();
            AxisService axisService = serviceIter.next();
            URL fn = axisService.getFileName();
            if (fn != null) {
                fileName = fn.getPath();
            }
            privilegedCarbonContext.setApplicationName(axisService.getName());

            /*
            WSAS-933 - We should not remove service from axisConfig, let Deployer to undeploy.
            Only delete the file is enough.

            TODO - For the moment this fix work for all service types expect proxy services defined on synspase.xml
             */
           if("proxy".equalsIgnoreCase(getServiceType(axisService))){
               //originator param will read from ProxyObserver to identify if service is removed from
               //Service listing UI
               axisService.addParameter("originator","ServiceAdmin");
               // removing the service from axis configuration
               axisConfig.removeService(axisService.getName());
               //adding log per service in order to notify user about the service's state when viewing logs.
               log.info("Undeploying Axis2 Service: " + axisService.getName());

//               Registry registry = getConfigSystemRegistry();
//               String servicePath = RegistryResources.ROOT + "axis2" +
//                       RegistryConstants.PATH_SEPARATOR + "service-groups" +
//                       RegistryConstants.PATH_SEPARATOR +
//                       axisService.getAxisServiceGroup().getServiceGroupName() +
//                       RegistryConstants.PATH_SEPARATOR + "services" +
//                       RegistryConstants.PATH_SEPARATOR + axisService.getName();
//               try {
//                   registry.delete(servicePath);
//               } catch (RegistryException e) {
//                   log.warn("Unable to delete registry collection conf:" + servicePath,e);
//               }

           }

//            PrivilegedCarbonContext.endTenantFlow();

            // If service was deployed by CApp we need to manualy undeploy the service when deleting service group
            // since the artifact is not inside hot deployment directory
            if (fileName != null && fileName.contains("carbonapps")) {
                // if its a proxy deployed using CApp it can be removed form above check
                if (axisConfig.getService(axisService.getName()) != null){
                    axisConfig.removeService(axisService.getName());
                    //adding log per service in order to notify user about the service's state when viewing logs.
                    log.info("Undeploying Axis2 Service: " + axisService.getName());
                }
            }
        }
        /*
            WSAS-933 - We should not remove service from axisConfig, let Deployer to undeploy.
            Only delete the file is enough.
             */
        // remove the service group from axis config and config context
        // AxisServiceGroup serviceGroup = axisConfig.removeServiceGroup(asGroup.getServiceGroupName());
        // if (serviceGroup != null) {
        //    getConfigContext().removeServiceGroupContext(serviceGroup);
        //    log.info(Messages.getMessage(DeploymentErrorMsgs.SERVICE_REMOVED,
        //                                 fileName != null ? fileName : serviceGroupName));
        // } else {
        //    axisConfig.removeFaultyService(fileName);
        //  }

        /*
        TODO This code does not work
        Object bundleIdObject = asGroup.getParameterValue(BUNDLE_ID);
        if (bundleIdObject != null) {
            Bundle bundle = (Bundle) bundleIdObject;
            try {
                bundle.stop();
                bundle.uninstall();
                return true;
            } catch (BundleException e) {
                throw new AxisFault("Bundle cannot be stoped and uninstall", e);
            }
        }*/

        // We cannot delete items from a URL repo
        // TODO: Do we need to throw an exception if we try to remove a service in a URL repo
        /*String axis2Repo = ServerConfiguration.getInstance().getFirstProperty(
                ServerConfiguration.AXIS2_CONFIG_REPO_LOCATION);
        if (CarbonUtils.isURL(axis2Repo)) {
            throw new AxisFault("You are not permitted to remove the " + serviceGroupName
                    + " service group from the URL repository " + axis2Repo);
        }*/

        if ((fileName != null) && (fileName.trim().length() != 0)) {
            if (log.isDebugEnabled()) {
                log.debug("Deleting service file " + fileName);
            }
            File file = new File(fileName);
            if (file.exists()) {
                if (!((file.isDirectory() && FileManipulator.deleteDir(file)) || file
                        .delete())) {
                    log.error("Service file/directory deletion failed : " + fileName);
                }
            }
            // now check whether this is a hierarchical service. if so, there should be a "/"
            // in service group name
            if (serviceGroupName.lastIndexOf(File.separator) != -1) {
                // if this is a hierarchical service, we have to remove the empty dirs on path
                String firstHierarchicalDir = serviceGroupName.substring(0,
                                                                         serviceGroupName.indexOf(File.separator));
                Utils.deleteEmptyDirsOnPath(fileName, firstHierarchicalDir);
            }
        }
        // Remove the corresponding ghost file
        File ghostFile = GhostDeployerUtils.getGhostFile(fileName, axisConfig);
        if (ghostFile != null && ghostFile.exists() && !ghostFile.delete()) {
            log.error("Error while deleting ghost service file : " + ghostFile.getAbsolutePath());
        }
    }

    public ServiceMetaData getServiceData(String serviceName) throws Exception {
        AxisService service = getAxisConfig().getServiceForActivation(serviceName);

        if (service == null) {
            // check services in transit ghost state as well
            Map<String, AxisService> transitGhostServicesMap = GhostDeployerUtils
                    .getTransitGhostServicesMap(getAxisConfig());
            service = transitGhostServicesMap.get(serviceName);
        }

        if (service == null) {
            String msg = "Invalid service name, service not found : " + serviceName;
            log.error(msg);
            throw new AxisFault(msg);
        }

        // if the existing service is a ghost service, deploy the actual one
        if (GhostDeployerUtils.isGhostService(service)) {
            service = GhostDeployerUtils.deployActualService(getAxisConfig(), service);
        }

        String serviceType = getServiceType(service);
        List<String> ops = new ArrayList<String>();
        for (Iterator<AxisOperation> opIter = service.getOperations(); opIter.hasNext(); ) {
            AxisOperation axisOperation = opIter.next();

            if (axisOperation.getName() != null) {
                ops.add(axisOperation.getName().getLocalPart());
            }
        }

        ServiceMetaData serviceMetaData = new ServiceMetaData();
        serviceMetaData.setOperations(ops.toArray(new String[ops.size()]));
        serviceMetaData.setName(serviceName);
        serviceMetaData.setServiceId(serviceName);
        serviceMetaData.setServiceVersion("");
        serviceMetaData.setActive(service.isActive());
        String[] eprs = getServiceEPRs(serviceName);
        serviceMetaData.setEprs(eprs);
        serviceMetaData.setServiceType(serviceType);
        AxisConfiguration axisConfiguration = getAxisConfig();
        serviceMetaData.setWsdlURLs(Utils.getWsdlInformation(serviceName, axisConfiguration));
        serviceMetaData.setTryitURL(Utils.getTryitURL(serviceName, getConfigContext()));
        AxisServiceGroup serviceGroup = (AxisServiceGroup) service.getParent();
        serviceMetaData.setFoundWebResources(serviceGroup.isFoundWebResources());
        serviceMetaData.setScope(service.getScope());
        serviceMetaData.setWsdlPorts(service.getEndpoints());
        serviceMetaData.setCAppArtifact(isAxisServiceCApp(service));

        Parameter deploymentTime =
                service.getParameter(CarbonConstants.SERVICE_DEPLOYMENT_TIME_PARAM);
        if (deploymentTime != null) {
            serviceMetaData.setServiceDeployedTime((Long) deploymentTime.getValue());
        }
        serviceMetaData.setServiceGroupName(serviceGroup.getServiceGroupName());

        //TODO need to fix security
/*        SecurityScenarioData securityScenario = getSecurityScenario(serviceName);
        if (securityScenario != null) {
            serviceMetaData.setSecurityScenarioId(securityScenario.getScenarioId());
        }*/

        if (service.getDocumentation() != null) {
            serviceMetaData.setDescription(service.getDocumentation());
        } else {
            serviceMetaData.setDescription("No service description found");
        }

        Parameter parameter = service.getParameter(Constants.Configuration.ENABLE_MTOM);
        if (parameter != null) {
            serviceMetaData.setMtomStatus((String) parameter.getValue());
        } else {
            serviceMetaData.setMtomStatus("false");
        }

        parameter = service.getParameter(DISABLE_TRY_IT_PARAM);
        if (parameter != null && Boolean.TRUE.toString().equalsIgnoreCase((String) parameter.getValue())) {
            serviceMetaData.setDisableTryit(true);
        }

        return serviceMetaData;
    }

//    private SecurityScenarioData getSecurityScenario(String serviceName) throws AxisFault {
//        try {
//            return new SecurityConfigAdmin(getUserRealm(),
//                                           getConfigSystemRegistry(),
//                                           getAxisConfig()).getCurrentScenario(serviceName);
//        } catch (SecurityConfigException e) {
//            String msg = "Cannot retrieve security scenario for service "+serviceName;
//            log.error(msg, e);
//            throw new AxisFault(msg, e);
//        }
//    }

    private String getServiceType(AxisService service) {
        Parameter serviceTypeParam = service.getParameter(ServerConstants.SERVICE_TYPE);
        String serviceType;
        if (serviceTypeParam != null) {
            serviceType = (String) serviceTypeParam.getValue();
        } else {
            serviceType = AXIS2_SERVICE_TYPE;
        }
        return serviceType;
    }

    private String[] getServiceEPRs(String serviceName) throws AxisFault {
        getAxisService(serviceName).setEPRs(null);
        try{
            return getAxisService(serviceName).getEPRs();
        }catch(NullPointerException ignored){
            //TODO: Hack to get rid of https://wso2.org/jira/browse/ESBJAVA-1545
            //If any transport except HTTP/S added there will be NPE
            return new String[0];
        }
    }

    public void changeServiceState(String serviceName, boolean isActive) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Changing service Active state to " + isActive + " for service " + serviceName);
        }

        AxisService service = getAxisService(serviceName);
        if (service == null) {
            String msg = "Service " + serviceName + " is not available";
            log.error(msg);
            throw new AxisFault(msg);
        }

        if (isActive) {
            getAxisConfig().startService(serviceName);
//            sendClusterSyncMessage(ServiceConstants.ServiceOperationType.ACTIVATE, serviceName);
        } else {
            getAxisConfig().stopService(serviceName);
//            sendClusterSyncMessage(ServiceConstants.ServiceOperationType.DEACTIVATE, serviceName);
        }


//        // Persist the service state
//        try {
//            spm.setServiceProperty(service,
//                                   Resources.ServiceProperties.ACTIVE, String.valueOf(isActive));
//        } catch (Exception e) {
//            String msg = "Cannot persist ACTIVE service parameter";
//            log.error(msg, e);
//            throw new AxisFault(msg, e);
//        }
    }

    /**
     * set the service parameter enableMTOM to manipulate MTOM flag
     * true/false/optional
     *
     * @param flag
     * @param serviceName
     */
    public void configureMTOM(String flag, String serviceName) throws AxisFault {

        AxisService service = getAxisConfig().getServiceForActivation(serviceName);
        if (service == null) {
            throw new AxisFault("AxisService " + serviceName + " cannot be found.");
        }
        String serviceGroupId = service.getAxisServiceGroup().getServiceGroupName();

        if (log.isDebugEnabled()) {
            log.debug("Setting the MTOM status to " + flag + " for service " + serviceName);
        }

        Parameter parameter = ParameterUtil.createParameter(Constants.Configuration.ENABLE_MTOM,
                                                            flag.trim());
        service.addParameter(parameter);

        for (Iterator<AxisOperation> iterator1 = service.getOperations(); iterator1.hasNext(); ) {
            AxisOperation axisOperation = iterator1.next();
            axisOperation.
                    addParameter(ParameterUtil.createParameter(Constants.Configuration.ENABLE_MTOM,
                                                               (String) parameter.getValue()));
        }
//
//        try {
//            spm.updateServiceParameter(service, parameter);  // a transaction is started inside
//        } catch (Exception e) {
//            String msg = "Cannot persist MTOM service parameter in the registry";
//            log.error(msg, e);
//            throw new AxisFault(msg, e);
//        }
    }

    public void startService(String serviceName) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Activating service " + serviceName);
        }

        AxisService axisService = getAxisConfig().getServiceForActivation(serviceName);

        if (axisService == null) {
            throw new Exception("Invalid service name " + serviceName);
        }
        try {
            getAxisConfig().startService(serviceName);
        } catch (AxisFault e) {
            String msg = "Cannot start service " + serviceName;
            log.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    public void stopService(String serviceName) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Deactivating service " + serviceName);
        }

        AxisService axisService = getAxisConfig().getServiceForActivation(serviceName);
        if (axisService == null) {
            throw new Exception("Invalid service name " + serviceName);
        }

        try {
            getAxisConfig().stopService(serviceName);
        } catch (AxisFault e) {
            String msg = "Cannot stop service " + serviceName;
            log.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    public String[] getExposedTransports(String serviceId) throws AxisFault {
        AxisService axisService = getAxisConfig().getServiceForActivation(serviceId);
        if (!axisService.isEnableAllTransports()) {
            List<String> exposedTransports = axisService.getExposedTransports();
            return exposedTransports.toArray(new String[exposedTransports.size()]);
        } else {
            Map transportsIn = getConfigContext().getAxisConfiguration()
                    .getTransportsIn();
            String[] transports = new String[transportsIn.size()];
            int i = 0;

            for (Object o : transportsIn.values()) {
                TransportInDescription tiDesc = (TransportInDescription) o;
                transports[i++] = tiDesc.getName();
            }

            return transports;
        }
    }

    public String addTransportBinding(String serviceId, String transportProtocol) throws Exception {
        if (transportProtocol == null || transportProtocol.trim().length() == 0) {
            return "Invalid transport " + transportProtocol;
        }
        AxisService axisService = getAxisConfig().getServiceForActivation(serviceId);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();
//
//        Registry registry = getConfigSystemRegistry();

        if (axisService.isExposedTransport(transportProtocol)) {
            return "Service [" + serviceId + "] already contains the " + transportProtocol
                   + " transport binding!";
        }

//        OMElement serviceElement = spm.getService(axisService);
//        // TODO In the current implementation of Carbon-core this
//        // serviceElement can be null
//        // TODO Therefore we need to add it to the registry if it is null
//        if (serviceElement == null) {
//            spm.handleNewServiceAddition(axisService);
//            serviceElement = spm.getService(axisService);
//        }

        // TODO This should be added, but for the moment it is commented
//        if (serviceElement.getAttribute(new QName(Resources.ServiceProperties.IS_UT_ENABLED)) !=
//            null) {
//            if (!transportProtocol
//                    .equalsIgnoreCase(ServerConstants.HTTPS_TRANSPORT)) {
//                throw new AxisFault(
//                        "Cannot add non-HTTPS transport binding for Service ["
//                        + serviceId
//                        + "] since a security scenario which requires the "
//                        + "service to contain only the HTTPS transport binding"
//                        + " has been applied to this service.");
//            }
//        }
//         if (serviceDO.getIsUTAuthEnabled()) {
//             if (!transportProtocol
//             .equalsIgnoreCase(ServerConstants.HTTPS_TRANSPORT)) {
//             throw new AxisFault(
//             "Cannot add non-HTTPS transport binding for Service ["
//             + serviceId
//             + "] since a security scenario which requires the "
//             + "service to contain only the HTTPS transport binding"
//             + " has been applied to this service.");
//             }
//         }

        if (!axisService.isEnableAllTransports()) {
            axisService.addExposedTransport(transportProtocol);
            org.apache.axis2.deployment.util.Utils.addEndpointsToService(axisService, axisConfig);
        } else {
            return "Service [" + serviceId + "] already contains the " + transportProtocol
                   + " transport binding!";
        }

//        Resource transportResource =
//                new TransportPersistenceManager(getAxisConfig()).getTransportResource(transportProtocol);
//        if (transportResource != null) {
//            try {
//                boolean regTransactionStarted = Transaction.isStarted();
////                boolean fileTransactionStarted = sfpm.isTransactionStarted(serviceGroupId);
//                if (!regTransactionStarted) {
//                    getConfigSystemRegistry().beginTransaction();
//                }
//                if (!fileTransactionStarted) {
//                    sfpm.beginTransaction(serviceGroupId);
//                }
//                serviceElement.addAttribute(Resources.ServiceProperties.EXPOSED_ON_ALL_TANSPORTS, String
//                        .valueOf(false), null);
//                sfpm.put(serviceGroupId,
//                         PersistenceUtils.createAssociation(
//                                 transportResource.getPath(), Resources.Associations.EXPOSED_TRANSPORTS),
//                         PersistenceUtils.getResourcePath(axisService));
//
//                // registry.addAssociation(transportResource.getPath(),
//                // serviceResource.getPath(),
//                // Resources.Associations.EXPOSED_TRANSPORTS);
//                sfpm.put(serviceGroupId, serviceElement,
//                         PersistenceUtils.getResourcePath(axisService));
//                registry.put(transportResource.getPath(), transportResource);
//                if (!regTransactionStarted) {
//                    getConfigSystemRegistry().commitTransaction();
//                }
//                if (!fileTransactionStarted) {
//                    sfpm.commitTransaction(serviceGroupId);
//                }
//            } catch (Exception e) {
//                String msg = "Service with name " + serviceId + " not found.";
//                log.error(msg);
//                sfpm.rollbackTransaction(serviceGroupId);
//                try {
//                    getConfigSystemRegistry().rollbackTransaction();
//                } catch (RegistryException e1) {
//                    throw AxisFault.makeFault(e);
//                }
//                throw new AxisFault(msg, e);
//            }
//        }

        axisConfig.notifyObservers(
                new AxisEvent(CarbonConstants.AxisEvent.TRANSPORT_BINDING_ADDED, axisService),
                axisService);
        return "Successfully added " + transportProtocol + " transport binding to service "
               + serviceId;
    }

    public String removeTransportBinding(String serviceId, String transportProtocol)
            throws Exception {

        AxisService axisService = getAxisConfig().getServiceForActivation(serviceId);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();
//
//        Registry registry = getConfigSystemRegistry();
//
//        OMElement serviceElement = spm.getService(axisService);
//        // TODO In the current implementation of Carbon-core this
//        // serviceResource can be null
//        // TODO Therefore we need to add it to the registry if it is null
//        if (serviceElement == null) {
//            spm.handleNewServiceAddition(axisService);
//            serviceElement = spm.getService(axisService);
//        }

//        // TODO This should be added, but for the moment it is commented
//        if (serviceElement.getAttribute(new QName(Resources.ServiceProperties.IS_UT_ENABLED)) !=
//            null) {
//            if (transportProtocol
//                    .equalsIgnoreCase(ServerConstants.HTTPS_TRANSPORT)) {
//                throw new AxisFault(
//                        "Cannot add non-HTTPS transport binding for Service ["
//                        + serviceId
//                        + "] since a security scenario which requires the "
//                        + "service to contain only the HTTPS transport binding"
//                        + " has been applied to this service.");
//            }
//        }
        // if (serviceDO.getIsUTAuthEnabled()) {
        // if (transportProtocol
        // .equalsIgnoreCase(ServerConstants.HTTPS_TRANSPORT)) {
        // throw new AxisFault(
        // "HTTPS transport binding for Service ["
        // + serviceId
        // + "] cannot be removed since a security scenario which requires"
        // + " HTTPS has been applied to this service.");
        // }
        // }
//        try {
//            boolean regTransactionStarted = Transaction.isStarted();
//            boolean fileTransactionStarted = sfpm.isTransactionStarted(serviceGroupId);
//            if (!regTransactionStarted) {
//                getConfigSystemRegistry().beginTransaction();
//            }
//            if (!fileTransactionStarted) {
//                sfpm.beginTransaction(serviceGroupId);
//            }
//            Resource transportResource;
//            if (!axisService.isEnableAllTransports()) {
//                transportResource =
//                        new TransportPersistenceManager(getAxisConfig()).
//                                getTransportResource(transportProtocol);
//                if (axisService.getExposedTransports().size() == 1) {
//                    return SERVICE_MUST_CONTAIN_AT_LEAST_ONE_TRANSPORT;
//                } else {
//                    sfpm.delete(serviceGroupId,
//                                PersistenceUtils.getResourcePath(axisService) + "/" +
//                                Resources.Associations.ASSOCIATION_XML_TAG +
//                                PersistenceUtils.getXPathAttrPredicate(
//                                        Resources.Associations.DESTINATION_PATH, transportResource.getPath()) +
//                                PersistenceUtils.getXPathAttrPredicate(
//                                        "type", Resources.Associations.EXPOSED_TRANSPORTS));
//                    // registry.removeAssociation(transportResource.getPath(),
//                    // serviceResource.getPath(),
//                    // Resources.Associations.EXPOSED_TRANSPORTS);
//                    axisService.removeExposedTransport(transportProtocol);
//                    registry.put(transportResource.getPath(), transportResource);
//                }
//            } /*else {
//                //TODO this should come from Transport component UI
//                //todo if uncommented, change it to not use registry for service metadata - kasung
//                ServiceTracker transportServiceTracker = new ServiceTracker(bundleContext,
//                                                                            TransportAdmin.class.getName(),
//                                                                            null);
//                try {
//                    transportServiceTracker.open();
//                    TransportAdmin transportAdmin =
//                            (TransportAdmin) transportServiceTracker.getService();
//                    if (transportAdmin != null) {
//                        TransportSummary[] transports = transportAdmin.listTransports();
//                        if (transports.length == 1) {
//                            return SERVICE_MUST_CONTAIN_AT_LEAST_ONE_TRANSPORT;
//
//                        } else {
//                            for (TransportSummary transport : transports) {
//                                String protocol = transport.getProtocol();
//
//                                if (!protocol.equals(transportProtocol)) {
//                                    axisService.addExposedTransport(protocol);
//                                    transportResource = spm.getTransport(protocol);
//                                    registry.addAssociation(serviceResource.getPath(),
//                                                            transportResource.getPath(),
//                                                            Resources.Associations.EXPOSED_TRANSPORTS);
//                                    // registry.addAssociation(transportResource.getPath(),
//                                    // serviceResource.getPath(),
//                                    // Resources.Associations.EXPOSED_TRANSPORTS);
//                                    registry.put(transportResource.getPath(), transportResource);
//                                }
//                            }
//                        }
//                    } else {
//                        log.error("TransportAdmin OSGi Service is not available");
//                    }
//                } catch (RegistryException e) {
//                } catch (Exception e) {
//                    String msg = "Could not get transports list form TransportAdmin service";
//                    log.error(msg, e);
//                    throw new AxisFault(msg, e);
//                } finally {
//                    transportServiceTracker.close();
//                }
//            }*/
//
//            serviceElement.addAttribute(Resources.ServiceProperties.EXPOSED_ON_ALL_TANSPORTS,
//                                        String.valueOf(false), null);
//            sfpm.put(serviceGroupId, serviceElement,
//                     PersistenceUtils.getResourcePath(axisService));
//
//            if (!regTransactionStarted) {
//                getConfigSystemRegistry().commitTransaction();
//            }
//            if (!fileTransactionStarted) {
//                sfpm.commitTransaction(serviceGroupId);
//            }
//        } catch (Exception e) {
//            sfpm.rollbackTransaction(serviceGroupId);
//            try {
//                getConfigSystemRegistry().rollbackTransaction();
//            } catch (RegistryException e1) {
//                throw AxisFault.makeFault(e);
//            }
//            throw AxisFault.makeFault(e);
//        }

        axisConfig.notifyObservers(
                new AxisEvent(CarbonConstants.AxisEvent.TRANSPORT_BINDING_REMOVED, axisService),
                axisService);
        return "Removed " + transportProtocol + " transport binding for " + serviceId + " service";
    }

    /**
     * Removes policy given the key
     *
     * @param serviceName Name of the service
     * @param policyKey   Key of the policy to be removed
     * @param moduleNames Array of module names
     * @throws ServerException on error
     */
    public void removeBindingPolicy(String serviceName, String policyKey, String[] moduleNames)
            throws ServerException {
        try {
            AxisConfiguration axisConfig = getAxisConfig();
//            Registry registry = getConfigSystemRegistry();
            AxisService service = axisConfig.getServiceForActivation(serviceName);
            String serviceGroupId = service.getAxisServiceGroup().getServiceGroupName();
//            ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();

//            String serviceXPath = PersistenceUtils.getResourcePath(service);

//            String policyPath = serviceXPath +
//                                "/" + Resources.POLICIES +
//                                "/" + Resources.POLICY +
//                                PersistenceUtils.getXPathTextPredicate(Resources.ServiceProperties.POLICY_UUID, policyKey);
//            // "/policies/policy[policyUUID/text()=\"SecPolicy\"]"
//            sfpm.delete(serviceGroupId, policyPath);

//            for (String moduleName : moduleNames) {
////                String modPath = Resources.MODULES + moduleName;
//                sfpm.delete(serviceGroupId, serviceXPath + "/" + Resources.ModuleProperties.MODULE_XML_TAG +
//                                            PersistenceUtils.getXPathAttrPredicate(Resources.NAME, moduleName) +
//                                            PersistenceUtils.getXPathAttrPredicate(Resources.ModuleProperties.TYPE,
//                                                                                   Resources.Associations.ENGAGED_MODULES));
//            }

            // at axis2
            Map<String, AxisEndpoint> endPointMap = service.getEndpoints();
            for (Map.Entry<String, AxisEndpoint> o : endPointMap.entrySet()) {
                AxisEndpoint point = o.getValue();
                AxisBinding binding = point.getBinding();
                PolicySubject subject = binding.getPolicySubject();
                subject.detachPolicyComponent(policyKey);
            }
        } catch (Exception e) {
            String msg = "Cannot remove service policy";
            log.error(msg, e);
            throw new ServerException(msg, e);
        }
    }

    public void setServiceParameters(String serviceName,
                                     String[] parameters) throws AxisFault {
        for (String parameter : parameters) {
            setServiceParameter(serviceName, parameter);
        }
    }

    private void setServiceParameter(String serviceName,
                                     String parameterStr) throws AxisFault {
        AxisService axisService = getAxisConfig().getServiceForActivation(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name service not found : " + serviceName);
        }

        OMElement paramEle;
        try {
            XMLStreamReader xmlSR =
                    StAXUtils.createXMLStreamReader(
                            new ByteArrayInputStream(parameterStr.getBytes()));
            paramEle = new StAXOMBuilder(xmlSR).getDocumentElement();
        } catch (XMLStreamException e) {
            String msg = "Cannot create OMElement from parameter: " + parameterStr;
            log.error(msg, e);
            throw new AxisFault(msg, e);
        }

        Parameter parameter = ParameterUtil.createParameter(paramEle);
        if (axisService.getParameter(parameter.getName()) != null) {
            if (!axisService.getParameter(parameter.getName()).isLocked()) {
                axisService.addParameter(parameter);
            }
        } else {
            axisService.addParameter(parameter);
        }
//
//        try {
//            spm.updateServiceParameter(axisService, parameter);
//        } catch (Exception e) {
//            String msg = "Cannot persist service parameter change for service " + serviceName;
//            log.error(msg, e);
//            throw new AxisFault(msg, e);
//        }
    }

    public void removeServiceParameter(String serviceName,
                                       String parameterName) throws AxisFault {
        AxisService axisService = getAxisConfig().getServiceForActivation(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name service not found : " +
                                serviceName);
        }

        Parameter parameter = ParameterUtil.createParameter(parameterName, null);
        axisService.removeParameter(parameter);
//        try {
//            spm.removeServiceParameter(axisService, parameter);
//        } catch (Exception e) {
//            String msg = "Cannot persist service parameter removal. Service " + serviceName;
//            log.error(msg, e);
//            throw new AxisFault(msg, e);
//        }
    }

    public String[] getServiceParameters(String serviceName) throws ServerException {
        try {
            AxisService service = getAxisService(serviceName);
            ArrayList<Parameter> parameters = service.getParameters();

            List<String> params = new ArrayList<String>();
            for (Parameter param : parameters) {
                OMElement paramEle = param.getParameterElement();
                if (paramEle != null) {
                    params.add(paramEle.toString());
                } else if (param.getParameterType() == Parameter.TEXT_PARAMETER) {
                    Parameter paramElement = ParameterUtil.createParameter(param.getName().trim(),
                            (String) param.getValue(), param.isLocked());
                    params.add(paramElement.getParameterElement().toString());
                }
            }

            return params.toArray(new String[params.size()]);
        } catch (Exception e) {
            String msg = "Error occured while getting parameters of service : " + serviceName;
            log.error(msg, e);
            throw new ServerException(msg, e);
        }
    }

    /**
     * Returns the Service Policy for a given service.
     *
     * @param serviceName The name of the service
     * @return A string representing the Policy XML
     * @throws AxisFault on error
     */
    public String getPolicy(String serviceName) throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        PolicySubject servicePolicySubject = axisService.getPolicySubject();
        List<PolicyComponent> policyList =
                new ArrayList<PolicyComponent>(servicePolicySubject.getAttachedPolicyComponents());
        Policy servicePolicy = org.apache.axis2.util.PolicyUtil.getMergedPolicy(policyList,
                                                                                axisService);

        if (servicePolicy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(servicePolicy).toString();
    }

    /**
     * Retrieves the merged policy from a given modiule
     *
     * @param moduleName    Name of the module
     * @param moduleVersion Version string of the string
     * @return A string representaiton of the policy
     * @throws AxisFault on error
     */
    public String getModulePolicy(String moduleName, String moduleVersion) throws AxisFault {
        AxisConfiguration axisConfig = MessageContext.getCurrentMessageContext()
                .getConfigurationContext().getAxisConfiguration();
        AxisModule axisModule = axisConfig.getModule(moduleName, moduleVersion);

        if (axisModule == null) {
            throw new AxisFault("invalid service name");
        }

        PolicySubject modulePolicySubject = axisModule.getPolicySubject();
        List<PolicyComponent> policyList =
                new ArrayList<PolicyComponent>(modulePolicySubject.getAttachedPolicyComponents());

        // Get the merged module policy
        Policy policy = null;
        for (PolicyComponent policyElement : policyList) {
            if (policyElement instanceof Policy) {
                policy = (policy == null) ? (Policy) policyElement
                                          : policy.merge((Policy) policyElement);
            } else {
                PolicyReference policyReference = (PolicyReference) policyElement;

                String key = policyReference.getURI();
                int pos = key.indexOf('#');
                if (pos == 0) {
                    key = key.substring(1);
                } else if (pos > 0) {
                    key = key.substring(0, pos);
                }

                PolicyComponent attachedPolicyComponent = modulePolicySubject
                        .getAttachedPolicyComponent(key);

                if (attachedPolicyComponent instanceof Policy) {
                    policy = (Policy) attachedPolicyComponent;
                }
            }
        }

        if (policy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(policy).toString();
    }

    public void setModulePolicy(String moduleName, String moduleVersion, String policyString)
            throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }

        // At axis2
        AxisConfiguration axisConfig = MessageContext.getCurrentMessageContext()
                .getConfigurationContext().getAxisConfiguration();
        AxisModule axisModule = axisConfig.getModule(moduleName, moduleVersion);
        axisModule.getPolicySubject().clear();
        axisModule.getPolicySubject().attachPolicy(policy);

//        // persistence
//        ModuleFilePersistenceManager mfpm = pf.getModuleFilePM();
//        try {
//            pf.getModulePM().persistModulePolicy(moduleName, moduleVersion,
//                    policy, policy.getId(), "" + PolicyInclude.AXIS_MODULE_POLICY,
//                    PersistenceUtils.getResourcePath(axisModule));
//        } catch (Exception e) {
//            String msg = "Cannot persist module policy addition. Module " + moduleName + moduleVersion;
//            log.error(msg, e);
//            mfpm.rollbackTransaction(moduleName);
//            throw new AxisFault(msg, e);
//
//        }
    }

    /**
     * Returns the policy for a given service and it's operation
     *
     * @param serviceName   The name of the service
     * @param operationName The operation
     * @return a String representing the Policy XML
     * @throws AxisFault on error
     */
    public String getOperationPolicy(String serviceName, String operationName) throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        AxisOperation axisOperation = axisService.getOperation(new QName(operationName));

        PolicySubject operationPolicySubject = axisOperation.getPolicySubject();
        List<PolicyComponent> policyList =
                new ArrayList<PolicyComponent>(operationPolicySubject.getAttachedPolicyComponents());
        Policy operationPolicy = org.apache.axis2.util.PolicyUtil.getMergedPolicy(policyList,
                                                                                  axisService);

        if (operationPolicy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(operationPolicy).toString();
    }

    /**
     * Returns the policy for a given message
     *
     * @param serviceName   The name of the service
     * @param operationName The name of the operation
     * @param messageType   The type of message
     * @return A string representation of the Policy XML
     * @throws AxisFault on error
     */
    public String getOperationMessagePolicy(String serviceName, String operationName,
                                            String messageType)
            throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        AxisMessage axisMessage =
                axisService.getOperation(new QName(operationName)).getMessage(messageType);

        PolicySubject messagePolicySubject = axisMessage.getPolicySubject();
        List<PolicyComponent> policyList =
                new ArrayList<PolicyComponent>(messagePolicySubject.getAttachedPolicyComponents());
        Policy messagePolicy = org.apache.axis2.util.PolicyUtil.getMergedPolicy(policyList,
                                                                                axisService);

        if (messagePolicy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(messagePolicy).toString();
    }

    public PolicyMetaData[] getPolicies(String serviceName) throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        ArrayList<PolicyMetaData> policyDataArray = new ArrayList<PolicyMetaData>();

        PolicySubject servicePolicySubject = axisService.getPolicySubject();
        List<PolicyComponent> policyList;

        // services.xml
        policyList = new ArrayList<PolicyComponent>(servicePolicySubject.getAttachedPolicyComponents());

        if (!policyList.isEmpty()) {
            PolicyMetaData policyData = new PolicyMetaData();
            policyData.setWrapper("Policies that are applicable for " + axisService.getName()
                                  + " service");
            policyData.setPolycies(PolicyUtil.processPolicyElements(policyList.iterator(),
                                                                    new PolicyLocator(
                                                                            axisService)));
            policyDataArray.add(policyData);
        }

        for (AxisEndpoint axisEndpoint : axisService.getEndpoints().values()) {
            policyList = new ArrayList<PolicyComponent>(axisEndpoint.getPolicySubject()
                                                                .getAttachedPolicyComponents());

            if (!policyList.isEmpty()) {
                PolicyMetaData policyData = new PolicyMetaData();
                policyData.setWrapper("Policies that are applicable for " + axisEndpoint.getName()
                                      + " endpoint");
                policyData.setPolycies(PolicyUtil.processPolicyElements(policyList.iterator(),
                                                                        new PolicyLocator(
                                                                                axisService)));
                policyDataArray.add(policyData);
            }
        }

        return (policyDataArray.toArray(new PolicyMetaData[policyDataArray.size()]));
    }

    /**
     * Returns the policy for a given service binding
     *
     * @param serviceName The name of the service
     * @param bindingName The name of the binding in question
     * @return A String representing the Policy XML
     * @throws AxisFault on error
     */
    public String getBindingPolicy(String serviceName, String bindingName) throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        AxisBinding axisBinding = null;

        // at axis2
        Map endPointMap = axisService.getEndpoints();
        for (Object o : endPointMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            AxisEndpoint point = (AxisEndpoint) entry.getValue();
            if (point.getBinding().getName().getLocalPart().equals(bindingName)) {
                axisBinding = point.getBinding();
                break;
            }
        }

        if (axisBinding == null) {
            throw new AxisFault("invalid binding name");
        }

        PolicySubject bindingPolicy = axisBinding.getPolicySubject();
        List<PolicyComponent> policyList =
                new ArrayList<PolicyComponent>(bindingPolicy.getAttachedPolicyComponents());
        Policy servicePolicy = org.apache.axis2.util.PolicyUtil.getMergedPolicy(policyList,
                                                                                axisService);

        if (servicePolicy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(servicePolicy).toString();
    }

    /**
     * Returns a policy for an operation of a given service binding
     *
     * @param serviceName   Name of the service
     * @param bindingName   Name of the service binding
     * @param operationName Name of the service operation
     * @return A policy
     * @throws AxisFault on error
     */
    public String getBindingOperationPolicy(String serviceName, String bindingName,
                                            String operationName) throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        AxisBinding axisBinding = null;

        // at axis2
        Map<String, AxisEndpoint> endPointMap = axisService.getEndpoints();
        for (Map.Entry<String, AxisEndpoint> o : endPointMap.entrySet()) {
            AxisEndpoint point = o.getValue();
            if (point.getBinding().getName().getLocalPart().equals(bindingName)) {
                axisBinding = point.getBinding();
                break;
            }
        }

        if (axisBinding == null) {
            throw new AxisFault("invalid binding name");
        }

        Policy bindingOperationPolicy = null;
        Iterator operations = axisBinding.getChildren();
        while (operations.hasNext()) {
            AxisBindingOperation currentOperation = (AxisBindingOperation) operations.next();
            if (currentOperation.getName().toString().equals(operationName)) {
                PolicySubject bindingOperationPolicySubject = currentOperation.getPolicySubject();
                List<PolicyComponent> policyList =
                        new ArrayList<PolicyComponent>(bindingOperationPolicySubject.getAttachedPolicyComponents());
                bindingOperationPolicy =
                        org.apache.axis2.util.PolicyUtil.getMergedPolicy(policyList, axisService);
                break;
            }
        }

        if (bindingOperationPolicy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(bindingOperationPolicy).toString();
    }

    /**
     * Returns the policy of a given message for a given service binding's operation
     *
     * @param serviceName   Service name
     * @param bindingName   Service binding name
     * @param operationName Service operation name
     * @param messageType   Message type
     * @return Policy key
     * @throws AxisFault on error
     */
    public String getBindingOperationMessagePolicy(String serviceName, String bindingName,
                                                   String operationName, String messageType)
            throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService == null) {
            throw new AxisFault("invalid service name");
        }

        AxisBinding axisBinding = null;

        // at axis2
        Map endPointMap = axisService.getEndpoints();
        for (Object o : endPointMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            AxisEndpoint point = (AxisEndpoint) entry.getValue();
            if (point.getBinding().getName().getLocalPart().equals(bindingName)) {
                axisBinding = point.getBinding();
                break;
            }
        }

        if (axisBinding == null) {
            throw new AxisFault("invalid binding name");
        }

        Policy bindingOperationMessagePolicy = null;
        Iterator<AxisBindingOperation> operations = axisBinding.getChildren();
        while (operations.hasNext()) {
            AxisBindingOperation currentOperation = operations.next();
            if (currentOperation.getName().toString().equals(operationName)) {
                PolicySubject bindingOperationMessagePolicySubject =
                        currentOperation.getChild(messageType).getPolicySubject();
                List<PolicyComponent> policyList =
                        new ArrayList<PolicyComponent>(
                                bindingOperationMessagePolicySubject.getAttachedPolicyComponents());
                bindingOperationMessagePolicy =
                        org.apache.axis2.util.PolicyUtil.getMergedPolicy(policyList, axisService);
                break;
            }
        }

        if (bindingOperationMessagePolicy == null) {
            return PolicyUtil.getEmptyPolicyAsOMElement().toString();
        }

        return PolicyUtil.getPolicyAsOMElement(bindingOperationMessagePolicy).toString();
    }


    public void setPolicy(String serviceName, String policyString) throws Exception {
        setServicePolicy(serviceName, policyString);
    }

    public void setServicePolicy(String serviceName, String policyString) throws Exception {

        AxisService axisService = getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();

        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }

//        // persistence
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();
//        try {
//           String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//           spm.persistServicePolicy(serviceGroupId, policy, policy.getId(),
//                    "" + PolicyInclude.AXIS_SERVICE_POLICY, serviceXPath, serviceXPath);
//        } catch (Exception e) {
//            String msg = "Cannot persist service policy addition. Service " + serviceGroupId;
//            log.error(msg, e);
//            sfpm.rollbackTransaction(serviceGroupId);
//            throw new AxisFault(msg, e);
//
//        }

        // at axis2
        axisService.getPolicySubject().clear();
        axisService.getPolicySubject().attachPolicy(policy);
        axisService.getAxisConfiguration().notifyObservers(new AxisEvent(CarbonConstants.POLICY_ADDED
                , axisService), axisService);
    }

    public void setServiceOperationPolicy(String serviceName, String operationName,
                                          String policyString) throws Exception {

        AxisService axisService = getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();

        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }

        // Persist the new policy to the file system
//        try {
//            String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//            String operationXPath = PersistenceUtils.getResourcePath(
//                    axisService.getOperation(new QName(operationName)));
//            spm.persistServicePolicy(serviceGroupId, policy, policy.getId(),
//                    "" + PolicyInclude.AXIS_OPERATION_POLICY, serviceXPath,
//                    operationXPath);
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            sfpm.rollbackTransaction(serviceGroupId);
//            throw AxisFault.makeFault(e);
//        }

        // at axis2
        AxisOperation axisOperation = axisService.getOperation(new QName(operationName));
        axisOperation.getPolicySubject().clear();
        axisOperation.getPolicySubject().attachPolicy(policy);
        axisService.getAxisConfiguration().notifyObservers(new AxisEvent(CarbonConstants.POLICY_ADDED
                , axisOperation), axisService);
    }

    public void setServiceOperationMessagePolicy(String serviceName, String operationName,
                                                 String messageType, String policyString)
            throws Exception {

        AxisService axisService = getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();

        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }
//
//        try {
//            String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//            boolean transactionStarted = sfpm.isTransactionStarted(serviceGroupId);
//            if (!transactionStarted) {
//                sfpm.beginTransaction(serviceGroupId);
//            }
//
//            // Persist the new policy to the file system
//            OMElement policyElement = PersistenceUtils.createPolicyElement(policy);
//            OMFactory omFactory = OMAbstractFactory.getOMFactory();
//            OMElement policyWrapperElement = omFactory.createOMElement(Resources.POLICY, null);
//
//            OMElement idElement = omFactory.createOMElement(Resources.ServiceProperties.POLICY_UUID, null);
//            idElement.setText("" + policy.getId());
//            policyWrapperElement.addChild(idElement);
////            policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_UUID, policy.getId(), null);
//
//            policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_TYPE,
//                                              "" + PolicyInclude.AXIS_MESSAGE_POLICY, null);
//            policyWrapperElement.addChild(policyElement);
//
//            // Update the service operation resource to point to this merged policy
//            if (messageType.equals(WSDLConstants.MESSAGE_LABEL_IN_VALUE)) {
//                OMElement messageInIdElement = omFactory.createOMElement(
//                        Resources.ServiceProperties.MESSAGE_IN_POLICY_UUID, null);
//                idElement.setText("" + policy.getId());
//
//                sfpm.put(serviceGroupId, messageInIdElement, PersistenceUtils.getResourcePath(axisService) + "/" +
//                        Resources.OPERATION + PersistenceUtils.getXPathAttrPredicate(Resources.NAME, operationName));
//                        //add it to operation element
//            } else if (messageType.equals(WSDLConstants.MESSAGE_LABEL_OUT_VALUE)) {
//                OMElement messageOutIdElement = omFactory.createOMElement(
//                        Resources.ServiceProperties.MESSAGE_OUT_POLICY_UUID, null);
//                idElement.setText("" + policy.getId());
//
//                sfpm.put(serviceGroupId, messageOutIdElement, PersistenceUtils.getResourcePath(axisService) + "/" +
//                        Resources.OPERATION + PersistenceUtils.getXPathAttrPredicate(Resources.NAME, operationName));
//                        //add it to operation element
//            }
//
//            String policiesPath = PersistenceUtils.
//                    getResourcePath(axisService) + "/" + Resources.POLICIES;
//            if (!spm.getServiceGroupFilePM().elementExists(serviceGroupId, policiesPath)) {
//                OMElement policiesEl = omFactory.createOMElement(Resources.POLICIES, null);
//                spm.getServiceGroupFilePM().put(serviceGroupId, policiesEl, PersistenceUtils.getResourcePath(axisService));
//            } else {
//                //you must manually delete the existing policy before adding new one.
//                String pathToPolicy = policiesPath +
//                        "/" + Resources.POLICY +
//                        PersistenceUtils.getXPathTextPredicate(
//                                Resources.ServiceProperties.POLICY_UUID, policy.getId());
//                if (spm.getServiceGroupFilePM().elementExists(serviceGroupId, pathToPolicy)) {
//                    spm.getServiceGroupFilePM().delete(serviceGroupId, pathToPolicy);
//                }
//            }
//
//            spm.getServiceGroupFilePM().put(serviceGroupId, policyWrapperElement, policiesPath);
//            if (!transactionStarted) {
//                sfpm.commitTransaction(serviceGroupId);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            sfpm.rollbackTransaction(serviceGroupId);
//            throw AxisFault.makeFault(e);
//        }

        // at axis2
        axisService.getOperation(new QName(operationName)).getMessage(messageType)
                .getPolicySubject().clear();
        axisService.getOperation(new QName(operationName)).getMessage(messageType)
                .getPolicySubject().attachPolicy(policy);

    }

    public void setBindingPolicy(String serviceName, String bindingName, String policyString)
            throws Exception {

        AxisService axisService = getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();

        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }

//        try {
//            String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//            String bindingXPath = PersistenceUtils.getResourcePath(axisService) +
//                    "/" + Resources.ServiceProperties.BINDINGS +
//                    "/" + Resources.ServiceProperties.BINDING_XML_TAG +
//                    PersistenceUtils.getXPathAttrPredicate(Resources.NAME, bindingName);
//            spm.persistServicePolicy(serviceGroupId, policy, policy.getId(),
//                    "" + PolicyInclude.BINDING_POLICY, serviceXPath, bindingXPath);
//
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            sfpm.rollbackTransaction(serviceGroupId);
//            throw AxisFault.makeFault(e);
//        }

        // at axis2
        Map<String, AxisEndpoint> endPointMap = axisService.getEndpoints();
        for (Map.Entry<String, AxisEndpoint> o : endPointMap.entrySet()) {
            AxisEndpoint point = o.getValue();
            AxisBinding binding = point.getBinding();

            if (binding.getName().getLocalPart().equals(bindingName)) {
                binding.getPolicySubject().clear();
                binding.getPolicySubject().attachPolicy(policy);
            }
        }
    }

    public void setBindingOperationPolicy(String serviceName, String bindingName,
                                          String operationName, String policyString)
            throws Exception {

        AxisService axisService = getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();


        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }

//        try {
//            String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//            String bindingOperationXPath = PersistenceUtils.getResourcePath(axisService) +
//                    "/" + Resources.ServiceProperties.BINDINGS +
//                    "/" + Resources.ServiceProperties.BINDING_XML_TAG +
//                    PersistenceUtils.getXPathAttrPredicate(Resources.NAME, bindingName) +
//                    "/" + Resources.OPERATION +
//                    PersistenceUtils.getXPathAttrPredicate(Resources.NAME, operationName);
//            spm.persistServicePolicy(serviceGroupId, policy, policy.getId(),
//                    "" + PolicyInclude.BINDING_OPERATION_POLICY, serviceXPath,
//                    bindingOperationXPath);
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            sfpm.rollbackTransaction(serviceGroupId);
//            throw AxisFault.makeFault(e);
//        }

        // at axis2
        Map<String, AxisEndpoint> endPointMap = axisService.getEndpoints();
        for (Map.Entry<String, AxisEndpoint> o : endPointMap.entrySet()) {
            AxisEndpoint point = o.getValue();
            AxisBinding binding = point.getBinding();

            if (binding.getName().getLocalPart().equals(bindingName)) {
                Iterator operations = binding.getChildren();
                while (operations.hasNext()) {
                    AxisBindingOperation currentOperation =
                            (AxisBindingOperation) operations.next();
                    if (currentOperation.getName().toString().equals(operationName)) {
                        currentOperation.getPolicySubject().clear();
                        currentOperation.getPolicySubject().attachPolicy(policy);
                        break;
                    }
                }
            }
        }
    }

    public void setBindingOperationMessagePolicy(String serviceName, String bindingName,
                                                 String operationName, String messageType,
                                                 String policyString)
            throws Exception {

        AxisService axisService = getAxisService(serviceName);
        String serviceGroupId = axisService.getAxisServiceGroup().getServiceGroupName();
//        ServiceGroupFilePersistenceManager sfpm = pf.getServiceGroupFilePM();

        ByteArrayInputStream bais = new ByteArrayInputStream(policyString.getBytes());
        Policy policy = PolicyEngine.getPolicy(bais);

        if (policy.getId() == null) {
            // Generate an ID
            policy.setId(UIDGenerator.generateUID());
        }

//        String servicePath = Resources.SERVICE_GROUPS
//                             + axisService.getAxisServiceGroup().getServiceGroupName()
//                             + Resources.SERVICES + axisService.getName();

//        Registry registry = getConfigSystemRegistry();

//        String policyType;
//        if (messageType.equalsIgnoreCase(WSDLConstants.MESSAGE_LABEL_IN_VALUE)) {
//            policyType = "" + PolicyInclude.BINDING_INPUT_POLICY;
//        } else {
//            policyType = "" + PolicyInclude.BINDING_OUTPUT_POLICY;
//        }

//        try {
//            String serviceXPath = PersistenceUtils.getResourcePath(axisService);
//            boolean transactionStarted = sfpm.isTransactionStarted(serviceGroupId);
//            if (!transactionStarted) {
//                sfpm.beginTransaction(serviceGroupId);
//            }
//
//            // Persist the new policy to the file system
//            OMElement policyElement = PersistenceUtils.createPolicyElement(policy);
//            OMFactory omFactory = OMAbstractFactory.getOMFactory();
//            OMElement policyWrapperElement = omFactory.createOMElement(Resources.POLICY, null);
//
//            OMElement idElement = omFactory.createOMElement(Resources.ServiceProperties.POLICY_UUID, null);
//            idElement.setText("" + policy.getId());
//            policyWrapperElement.addChild(idElement);
////            policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_UUID, policy.getId(), null);
//
//            policyWrapperElement.addAttribute(Resources.ServiceProperties.POLICY_TYPE,
//                                              policyType, null);
//            policyWrapperElement.addChild(policyElement);
//
//            // Update the service operation resource to point to this merged policy
//            String bindingOppath = PersistenceUtils.getResourcePath(axisService) +
//                                   "/" + Resources.ServiceProperties.BINDINGS +
//                                   "/" + Resources.ServiceProperties.BINDING_XML_TAG +
//                                   PersistenceUtils.getXPathAttrPredicate(Resources.NAME, bindingName) +
//                                   "/" + Resources.OPERATION +
//                                   PersistenceUtils.getXPathAttrPredicate(Resources.NAME, operationName);
//            if (messageType.equals(WSDLConstants.MESSAGE_LABEL_IN_VALUE)) {
//                OMElement messageInIdElement = omFactory.createOMElement(
//                        Resources.ServiceProperties.MESSAGE_IN_POLICY_UUID, null);
//                idElement.setText("" + policy.getId());
//
//                sfpm.put(serviceGroupId, messageInIdElement, bindingOppath);   //add it to operation element
//            } else if (messageType.equals(WSDLConstants.MESSAGE_LABEL_OUT_VALUE)) {
//                OMElement messageOutIdElement = omFactory.createOMElement(
//                        Resources.ServiceProperties.MESSAGE_OUT_POLICY_UUID, null);
//                idElement.setText("" + policy.getId());
//
//                sfpm.put(serviceGroupId, messageOutIdElement, bindingOppath);   //add it to operation element
//            }
//
//            String policiesPath = PersistenceUtils.
//                    getResourcePath(axisService) + "/" + Resources.POLICIES;
//            if (!spm.getServiceGroupFilePM().elementExists(serviceGroupId, policiesPath)) {
//                OMElement policiesEl = omFactory.createOMElement(Resources.POLICIES, null);
//                spm.getServiceGroupFilePM().put(serviceGroupId, policiesEl, PersistenceUtils.getResourcePath(axisService));
//            }
//
//            spm.getServiceGroupFilePM().put(serviceGroupId, policyWrapperElement, policiesPath);
//            if (!transactionStarted) {
//                sfpm.commitTransaction(serviceGroupId);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            sfpm.rollbackTransaction(serviceGroupId);
//            throw AxisFault.makeFault(e);
//        }

        // at axis2
        Map<String, AxisEndpoint> endPointMap = axisService.getEndpoints();
        for (Map.Entry<String, AxisEndpoint> o : endPointMap.entrySet()) {
            AxisEndpoint point = o.getValue();
            AxisBinding binding = point.getBinding();

            if (binding.getName().getLocalPart().equals(bindingName)) {
                Iterator<AxisBindingOperation> operations = binding.getChildren();
                while (operations.hasNext()) {
                    AxisBindingOperation currentOperation =
                            operations.next();
                    if (currentOperation.getName().toString().equals(operationName)) {
                        currentOperation.getChild(messageType).getPolicySubject().clear();
                        currentOperation.getChild(messageType).getPolicySubject().attachPolicy(policy);
                        break;
                    }
                }
            }
        }
    }

    public OMElement getWSDL(String serviceName) throws AxisFault {
        AxisService axisService = getAxisService(serviceName);

        if (axisService != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String url = MessageContext.getCurrentMessageContext().getTo().getAddress();
            int ipindex = url.indexOf("//");
            String ip = null;

            if (ipindex >= 0) {
                ip = url.substring(ipindex + 2, url.length());

                int seperatorIndex = ip.indexOf(':');

                if (seperatorIndex > 0) {
                    ip = ip.substring(0, seperatorIndex);
                }
            }

            axisService.printWSDL(out, ip);

            try {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(
                        new ByteArrayInputStream(out.toByteArray()));

                OMFactory fac = OMAbstractFactory.getOMFactory();
                OMNamespace namespace = fac.createOMNamespace("http://org.apache.axis2/xsd", "ns1");
                OMElement wsdlWrapper = fac.createOMElement("getWSDLResponse", namespace);
                OMElement retvalue = fac.createOMElement("return", null);
                wsdlWrapper.addChild(retvalue);

                StAXOMBuilder staxOMBuilder = new StAXOMBuilder(fac, xmlReader);
                retvalue.addChild(staxOMBuilder.getDocumentElement());

                return wsdlWrapper;
            } catch (XMLStreamException e) {
                throw AxisFault.makeFault(e);
            }
        }

        return null;
    }

    public String[] getServiceBindings(String serviceName)
            throws Exception {

        AxisService axisService = getAxisService(serviceName);

        ServiceMetaData serviceMetaData = new ServiceMetaData();
        serviceMetaData.setServiceId(serviceName);

        // Get all bindings
        ArrayList<String> bindingsList = new ArrayList<String>();
        Map endPointMap = axisService.getEndpoints();
        for (Object o : endPointMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            AxisEndpoint point = (AxisEndpoint) entry.getValue();

            String currentBinding = point.getBinding().getName().getLocalPart();
            if ((!currentBinding.contains("HttpBinding")) &&
                (!bindingsList.contains(currentBinding))) {
                bindingsList.add(currentBinding);
            }
        }
        String[] bindings = new String[bindingsList.size()];
        bindingsList.toArray(bindings);

        return bindings;
    }

    private AxisService getAxisService(String serviceName) throws AxisFault {
        return getAxisConfig().getServiceForActivation(serviceName);
    }

    /**
     * Downloads service archive files
     *
     * @param serviceGroupName name of the service group needs to be downloaded
     * @return the corresponding data handler and the name of the service archive / file that's downloaded
     */
    public ServiceDownloadData downloadServiceArchive(String serviceGroupName) {
        AxisConfiguration axisConfig = getAxisConfig();
        AxisServiceGroup asGroup = axisConfig.getServiceGroup(serviceGroupName);
        String fileName = null;
        for (Iterator<AxisService> serviceIter = asGroup.getServices(); serviceIter.hasNext(); ) {
            AxisService axisService = serviceIter.next();
            URL fn = axisService.getFileName();
            if (fn != null) {
                fileName = fn.getPath();
            }
        }
        DataHandler handler;
        if (fileName != null) {
            File file = new File(fileName);
            FileDataSource datasource = new FileDataSource(file);
            handler = new DataHandler(datasource);

            ServiceDownloadData data = new ServiceDownloadData();
            data.setFileName(file.getName());
            data.setServiceFileData(handler);
            return data;
        } else {
            return null;
        }

    }

/*
    private void sendClusterSyncMessage(ServiceConstants.ServiceOperationType applicationOpType, String serviceName) {
        // For sending clustering messages we need to use the super-tenant's AxisConfig (Main Server
        // AxisConfiguration) because we are using the clustering facility offered by the ST in the
        // tenants
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();

        ClusteringAgent clusteringAgent =
                DataHolder.getServerConfigContext().getAxisConfiguration().getClusteringAgent();
        if (clusteringAgent != null) {
            int numberOfRetries = 0;
            UUID messageId = UUID.randomUUID();
            ServiceSynchronizeRequest request =
                    new ServiceSynchronizeRequest(tenantId, tenantDomain, messageId,
                            applicationOpType, serviceName);
            while (numberOfRetries < ServiceConstants.MAX_RETRY_COUNT) {
                try {
                    clusteringAgent.sendMessage(request, true);
                    log.info("Sent [" + request + "]");
                    break;
                } catch (ClusteringFault e) {
                    numberOfRetries++;
                    if (numberOfRetries < ServiceConstants.MAX_RETRY_COUNT) {
                        log.warn("Could not send SynchronizeRepositoryRequest for tenant " +
                                tenantId + ". Retry will be attempted in 2s. Request: " + request, e);
                    } else {
                        log.error("Could not send SynchronizeRepositoryRequest for tenant " +
                                tenantId + ". Several retries failed. Request:" + request, e);
                    }
                    try {
                        Thread.sleep(ServiceConstants.RETRY_INTERVAL);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }
*/

    private boolean isAxisServiceCApp(AxisService axisService) {
        //Check if Service is deployed from a CApp
        if (AXIS2_SERVICE_TYPE.equals(getServiceType(axisService)) || PROXY_SERVICE_TYPE
                .equals(getServiceType(axisService)) || DATA_SERVICE_TYPE.equals(getServiceType(axisService))) {
            URL fileName = axisService.getFileName();
            if (fileName != null) {
                try {
                    Path axis2ServiceAppPath = Paths.get(fileName.toURI());
                    if (axis2ServiceAppPath != null) {
                        String tenantId = AppDeployerUtils.getTenantIdString();
                        // Check whether there is an application in the system from the given name
                        ArrayList<CarbonApplication> appList = CarbonCoreDataHolder.getInstance().getApplicationManager().getCarbonApps(tenantId);
                        for (CarbonApplication application : appList) {
                            Path cappPath = Paths.get(application.getExtractedPath());
                            if (axis2ServiceAppPath.startsWith(cappPath)) {
                                return true;
                            }
                        }
                    }
                } catch (URISyntaxException e) {
                    log.error("Unable to retrieve CApp file path ", e);
                }
            }
        }
        return false;
    }
}
