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
package org.wso2.carbon.service.mgt;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.core.util.ParameterUtil;
import org.wso2.carbon.core.util.SystemFilter;
import org.wso2.carbon.service.mgt.util.ServiceArchiveCreator;
import org.wso2.carbon.service.mgt.util.Utils;
import org.wso2.carbon.utils.DataPaginator;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.ServerException;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Admin service to manage service groups
 */
@SuppressWarnings("unused")
public class ServiceGroupAdmin extends AbstractAdmin {
    private static Log log = LogFactory.getLog(ServiceGroupAdmin.class);


    public ServiceGroupAdmin(ConfigurationContext configCtx) throws Exception {//TO CHECK
        super(configCtx.getAxisConfiguration());
        setConfigurationContext(configCtx);
    }

    /**
     * List all the available service groups
     *
     * @param pageNumber The number of the page to be retrieved
     * @return The service group metadata
     * @throws org.apache.axis2.AxisFault If an error occurs while retrieving service groups
     */
    public ServiceGroupMetaDataWrapper listServiceGroups(String serviceTypeFilter,
                                                         String serviceGroupSearchString,
                                                         int pageNumber) throws AxisFault {
        if (serviceTypeFilter == null) {
            serviceTypeFilter = "ALL";
        }
        if (pageNumber < 0 || pageNumber == Integer.MAX_VALUE) {
            pageNumber = 0;
        }
        List<ServiceGroupMetaData> sgList = new ArrayList<ServiceGroupMetaData>();
        TreeSet<String> serviceTypes = new TreeSet<String>();
        serviceTypes.add("axis2");

        List<AxisServiceGroup> axisServiceGroupList = new ArrayList<AxisServiceGroup>();

        for (Iterator sgs = getAxisConfig().getServiceGroups(); sgs.hasNext(); ) {
            AxisServiceGroup serviceGroup = (AxisServiceGroup) sgs.next();
            // Filtering the admin services
            if (SystemFilter.isFilteredOutService(serviceGroup)) {
                continue;  // No advancement of currentIndex
            }
            String serviceType = "axis2";
            Parameter serviceTypeParam;
            if (serviceGroup.getServices().hasNext()) {
                serviceTypeParam = (serviceGroup.getServices().next())
                        .getParameter(ServerConstants.SERVICE_TYPE);
                if (serviceTypeParam != null) {
                    serviceType = (String) serviceTypeParam.getValue();
                    serviceTypes.add(serviceType);
                }
            }
            if (!serviceTypeFilter.equals("ALL")) {
                if (!serviceTypeFilter.equals(serviceType)) {
                    continue;
                }
            }
            if (serviceGroupSearchString != null &&
                serviceGroupSearchString.trim().length() > 0 &&
                !serviceGroup.getServiceGroupName().toLowerCase().contains(serviceGroupSearchString.toLowerCase())) {
                continue;
            }
            boolean isClientSide = false;
            int noOfServices = 0;
            for (Iterator serviceIter = serviceGroup.getServices(); serviceIter.hasNext(); ) {
                AxisService axisService = (AxisService) serviceIter.next();
                if (axisService.isClientSide()) {
                    isClientSide = true;
                    break;
                }
                noOfServices++;
            }
            if (noOfServices == 0 || isClientSide) {
                continue; // No advancement of currentIndex
            }
            axisServiceGroupList.add(serviceGroup);
        }

        if (axisServiceGroupList.size() > 0) {
            Collections.sort(axisServiceGroupList, new Comparator<AxisServiceGroup>() {
                public int compare(AxisServiceGroup arg0, AxisServiceGroup arg1) {
                    return arg0.getServiceGroupName()
                            .compareToIgnoreCase(arg1.getServiceGroupName());
                }
            });
        }
//
//        String itemsPerPage = ServerConfiguration.getInstance().getFirstProperty("ItemsPerPage");
//        int itemsPerPageInt = 10; // the default number of item per page
//        if (itemsPerPage != null) {
//            itemsPerPageInt = Integer.parseInt(itemsPerPage);
//        }
//        int startIndex = pageNumber * itemsPerPageInt;
//        int endIndex = (pageNumber + 1) * itemsPerPageInt;

        List<AxisServiceGroup> axisServiceGroupsRequiredForPage = new ArrayList<AxisServiceGroup>();
//        for (int i = startIndex; i < endIndex && i < axisServiceGroupList.size(); i++) {
//            axisServiceGroupsRequiredForPage.add(axisServiceGroupList.get(i));
//        }
        for (AxisServiceGroup anAxisServiceGroupList : axisServiceGroupList) {
            axisServiceGroupsRequiredForPage.add(anAxisServiceGroupList);
        }
        for (AxisServiceGroup serviceGroup : axisServiceGroupsRequiredForPage) {
            String serviceType = "axis2";
            Parameter serviceTypeParam;
            if (serviceGroup.getServices().hasNext()) {
                serviceTypeParam = (serviceGroup.getServices().next())
                        .getParameter(ServerConstants.SERVICE_TYPE);
                if (serviceTypeParam != null) {
                    serviceType = (String) serviceTypeParam.getValue();
                    serviceTypes.add(serviceType);
                }
            }

            ServiceGroupMetaData sgMetaData = new ServiceGroupMetaData();

            List<ServiceMetaData> services = new ArrayList<ServiceMetaData>();
            for (Iterator serviceIter = serviceGroup.getServices(); serviceIter.hasNext(); ) {
                AxisService axisService = (AxisService) serviceIter.next();

                ServiceMetaData service = new ServiceMetaData();
                String serviceName = axisService.getName();
                service.setName(serviceName);

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
                    sgMetaData.setDisableDeletion(true);
                }
                services.add(service);
            }

            String sgName = serviceGroup.getServiceGroupName();
            sgMetaData.setServices(services.toArray(new ServiceMetaData[services.size()]));
            sgMetaData.setServiceGroupName(sgName);
            sgMetaData.setServiceContextPath(getConfigContext().getServiceContextPath());
            Parameter parameter =
                    serviceGroup.getParameter(Constants.Configuration.ENABLE_MTOM);
            if (parameter != null) {
                sgMetaData.setMtomStatus((String) parameter.getValue());
            } else {
                sgMetaData.setMtomStatus("false");
            }
            sgList.add(sgMetaData);
        }

        ServiceGroupMetaDataWrapper wrapper;
        wrapper = new ServiceGroupMetaDataWrapper();
        wrapper.setNumberOfCorrectServiceGroups(sgList.size());
        wrapper.setNumberOfFaultyServiceGroups(getAxisConfig().getFaultyServices().size());
        wrapper.setServiceTypes(serviceTypes.toArray(new String[serviceTypes.size()]));
        try {
            wrapper.setNumberOfActiveServices(new ServiceAdmin(getAxisConfig()).
                    getNumberOfActiveServices());
        } catch (Exception e) {
            throw new AxisFault("Cannot get active services from ServiceAdmin", e);
        }
        //   DataPaginator.doPaging(pageNumber, axisServiceGroupList, sgList, wrapper);
        DataPaginator.doPaging(pageNumber, sgList, wrapper);
        return wrapper;
    }

    /**
     * Return service group details for a given service group
     *
     * @param serviceGroupName axis service group name
     * @return ServiceGroupMetaData
     * @throws AxisFault
     */
    public ServiceGroupMetaData listServiceGroup(String serviceGroupName) throws Exception {
        ServiceGroupMetaData sgmd = new ServiceGroupMetaData();
        Collection engagedModules;
        String[] engagedModuleNames = null;
        AxisServiceGroup serviceGroup;
        Parameter parameter;

        serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);
        if (serviceGroup == null) {
            String msg = "Service group " + serviceGroupName + " not found";
            log.error(msg);
            throw new Exception(msg);
        }
        sgmd.setServiceGroupName(serviceGroup.getServiceGroupName());

        parameter = serviceGroup.getParameter(Constants.Configuration.ENABLE_MTOM);
        if (parameter != null) {
            sgmd.setMtomStatus((String) parameter.getValue());
        }

        engagedModules = serviceGroup.getEngagedModules();

        if (engagedModules != null && engagedModules.size() > 0) {
            int i = 0;
            engagedModuleNames = new String[engagedModules.size()];
            for (Object engagedModule : engagedModules) {
                AxisModule module = (AxisModule) engagedModule;
                engagedModuleNames[i++] = module.getName();
            }
        }
        sgmd.setEngagedModules(engagedModuleNames);

        List<ServiceMetaData> services = new ArrayList<ServiceMetaData>();
        for (Iterator servicesIter = serviceGroup.getServices(); servicesIter.hasNext(); ) {
            AxisService service = (AxisService) servicesIter.next();
            String serviceName = service.getName();
            ServiceMetaData serviceMetaData = new ServiceMetaData();
            serviceMetaData.setName(service.getName());

            service.setName(serviceName);

            // extract service type
            String serviceType = "axis2";
            Parameter serviceTypeParam = service.getParameter(ServerConstants.SERVICE_TYPE);
            if (serviceTypeParam != null) {
                serviceType = (String) serviceTypeParam.getValue();
            }
            serviceMetaData.setServiceType(serviceType);
            AxisConfiguration axisConfiguration = getAxisConfig();
            serviceMetaData.setWsdlURLs(Utils.getWsdlInformation(serviceName, axisConfiguration));
            serviceMetaData.setTryitURL(Utils.getTryitURL(serviceName, getConfigContext()));
            serviceMetaData.setActive(service.isActive());

            parameter = service.getParameter(ServiceAdmin.DISABLE_TRY_IT_PARAM);
            if (parameter != null && Boolean.TRUE.toString().equalsIgnoreCase((String) parameter.getValue())) {
                serviceMetaData.setDisableTryit(true);
            }
            services.add(serviceMetaData);
        }
        sgmd.setServices(services.toArray(new ServiceMetaData[services.size()]));

        return sgmd;
    }

    /**
     * set the service group parameter enableMTOM to manipulate MTOM flag
     * true/false/optional
     *
     * @param flag
     * @return ServiceGroupMetaData
     */
    public ServiceGroupMetaData configureServiceGroupMTOM(String flag, String serviceGroupName)
            throws Exception {

        AxisServiceGroup serviceGroup = null;
        ArrayList parameters = null;
        boolean found = false;

        serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);

        if (serviceGroup == null) {
            throw new AxisFault("Service group " + serviceGroupName + "cannnot be found!");
        }
        // get the declared parameters
        parameters = serviceGroup.getParameters();

        for (Object parameter1 : parameters) {
            Parameter parameter = (Parameter) parameter1;
            if (parameter.getParameterType() == Parameter.TEXT_PARAMETER
                && parameter.getValue().toString().equals(Constants.Configuration.ENABLE_MTOM)) {
                parameter.setValue(flag.trim());
                found = true;
                break;
            }

        }
        if (!found) {
            Parameter parameter = ParameterUtil.createParameter(
                    Constants.Configuration.ENABLE_MTOM, flag.trim());
            serviceGroup.addParameter(parameter);
        }

        Parameter parameter = serviceGroup.getParameter(Constants.Configuration.ENABLE_MTOM);

        return listServiceGroup(serviceGroupName);
    }

    /**
     * @param serviceGroupName
     * @param params
     * @throws ServerException
     */
    public void updateServiceGroupParamters(String serviceGroupName, ParameterMetaData[] params)
            throws ServerException {

        AxisServiceGroup serviceGroup = null;

        try {
            serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);
            Parameter parameter;

            for (ParameterMetaData paramMetaData : params) {
                parameter = serviceGroup.getParameter(paramMetaData.getName());
                if (parameter == null) {
                    parameter = new Parameter(paramMetaData.getName(), null);
                }
                parameter.setParameterType(paramMetaData.getType());
                if (paramMetaData.getType() == Parameter.OM_PARAMETER) {
                    OMElement elem = AXIOMUtil.stringToOM(paramMetaData.getValue());
                    parameter.setParameterElement(elem);
                } else {
                    parameter.setValue(paramMetaData.getValue());
                }
            }
        } catch (Exception e) {
            String msg = "Error occured while updating parameters of service group: "
                         + serviceGroupName;
            log.error(msg, e);
            throw new ServerException("updateServiceParameters", e);
        }
    }

    /**
     * @param serviceGroupName
     * @param paramMetaData
     * @throws ServerException
     */
    public void updateServiceGroupParameter(String serviceGroupName,
                                            ParameterMetaData paramMetaData)
            throws ServerException {

        AxisServiceGroup serviceGroup = null;

        try {
            serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);
            Parameter parameter = serviceGroup.getParameter(paramMetaData.getName());
            if (parameter == null) {
                parameter = new Parameter(paramMetaData.getName(), null);
            }
            parameter.setParameterType(paramMetaData.getType());
            if (paramMetaData.getType() == Parameter.OM_PARAMETER) {
                OMElement elem = AXIOMUtil.stringToOM(paramMetaData.getValue());
                parameter.setParameterElement(elem);
            } else {
                parameter.setValue(paramMetaData.getValue());
            }
        } catch (Exception e) {
            String msg = "Error occured while updating parameters of service group: "
                         + serviceGroupName;
            log.error(msg, e);
            throw new ServerException("updateServiceParameters", e);
        }

    }

    /**
     * @param serviceGroupName
     * @return
     * @throws ServerException
     */
    public String[] getServiceGroupParameters(String serviceGroupName) throws ServerException {

        AxisServiceGroup serviceGroup = null;
        String[] params = new String[0];

        try {
            serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);
            ArrayList<Parameter> parameters = serviceGroup.getParameters();

            if (parameters == null || parameters.size() == 0) {
                return params;
            }

            params = new String[parameters.size()];
            int i = 0;
            for (Parameter param : parameters) {
                if (param.getParameterElement() != null) {
                    params[i++] = param.getParameterElement().toString();
                }
            }

            return params;

        } catch (Exception e) {
            String msg = "Error occured while getting parameters of service group : "
                         + serviceGroupName;
            log.error(msg, e);
            throw new ServerException("getServiceParameters", e);
        }
    }

    /**
     * @param serviceGroupName
     * @param paramName
     * @return
     * @throws ServerException
     */
    public ParameterMetaData getServiceGroupParameter(String serviceGroupName, String paramName)
            throws ServerException {

        AxisServiceGroup serviceGroup = null;

        try {
            serviceGroup = getAxisConfig().getServiceGroup(serviceGroupName);
            Parameter parameter = serviceGroup.getParameter(paramName);

            if (parameter == null) {
                return null;
            }

            ParameterMetaData paramMetaData = new ParameterMetaData();
            paramMetaData.setName(parameter.getName());
            paramMetaData.setType(parameter.getParameterType());
            if (parameter.getParameterType() == Parameter.OM_PARAMETER) {
                paramMetaData.setValue(parameter.getParameterElement().toString());
            } else if (parameter.getParameterType() == Parameter.TEXT_PARAMETER) {
                paramMetaData.setValue((String) parameter.getValue());
            } else {
                paramMetaData.setValue(parameter.getValue().toString());
            }

            return paramMetaData;

        } catch (Exception e) {
            String msg = "Error occured while gettig parameter " + paramName
                         + " of service group : " + serviceGroupName;
            log.error(msg, e);
            throw new ServerException("getServiceParameter", e);
        }
    }

    /**
     * @param serviceGroupId
     * @param parameterElement
     * @throws AxisFault
     */
    public void setServiceGroupParameters(String serviceGroupId, String[] parameterElement)
            throws AxisFault {
        for (String aParameterElement : parameterElement) {
            setServiceGroupParameter(serviceGroupId, aParameterElement);
        }
    }

    /**
     * @param serviceGroupId
     * @param parameterElement
     * @throws AxisFault
     */
    public void setServiceGroupParameter(String serviceGroupId, String parameterElement)
            throws AxisFault {

        AxisServiceGroup axisServiceGroup = getAxisConfig().getServiceGroup(serviceGroupId);

        OMElement param = null;
        try {
            param = AXIOMUtil.stringToOM(parameterElement);
        } catch (XMLStreamException e) {
            String msg = "Cannot create OMElement from parameter: " + parameterElement;
            log.error(msg, e);
            throw new AxisFault(msg, e);
        }

        Parameter parameter = ParameterUtil.createParameter(param);
        if (axisServiceGroup.getParameter(parameter.getName()) != null) {
            if (!axisServiceGroup.getParameter(parameter.getName()).isLocked()) {
                axisServiceGroup.addParameter(parameter);
            }
        } else {
            axisServiceGroup.addParameter(parameter);
        }

    }

    /**
     * @param serviceGroupId
     * @param parameterName
     * @throws AxisFault
     */
    public void removeServiceGroupParameter(String serviceGroupId, String parameterName)
            throws AxisFault {
        AxisServiceGroup axisServiceGroup = getAxisConfig().getServiceGroup(serviceGroupId);

        if (axisServiceGroup == null) {
            throw new AxisFault("invalid service group name service group not found" +
                                serviceGroupId);
        }

        axisServiceGroup.removeParameter(ParameterUtil.createParameter(parameterName, null));
    }

    /**
     * Using the information from AxisServiceGroup, a service archive will be
     * created. A String will be returned with ID, that can be used to access
     * the AAR and dump it anywhere user wishes.
     *
     * @param serviceGroupName
     * @return id of service archive
     * @throws AxisFault
     */
    public String dumpAAR(String serviceGroupName) throws AxisFault {
        try {
            return ServiceArchiveCreator.createArchive(getConfigContext(), serviceGroupName);
        } catch (Exception ex) {
            throw new AxisFault(ex.getMessage(), ex);
        }

    }
}
