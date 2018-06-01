/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.core.util;

import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.wso2.carbon.CarbonConstants;

/**
 * This class will filter admin services and modules. WSAS specific services are hard
 * coded and rest is plugable via services.xml.
 */
public class SystemFilter {

    public static boolean isFilteredOutService(AxisServiceGroup axisServiceGroup) {
        String adminParamValue =
                (String) axisServiceGroup.getParameterValue(CarbonConstants.ADMIN_SERVICE_PARAM_NAME);
        String hiddenParamValue =
                (String) axisServiceGroup.getParameterValue(CarbonConstants.HIDDEN_SERVICE_PARAM_NAME);
        String dynamicParamValue =
                (String) axisServiceGroup.getParameterValue(CarbonConstants.DYNAMIC_SERVICE_PARAM_NAME);
        if (adminParamValue != null && adminParamValue.length() != 0) {
            if (Boolean.parseBoolean(adminParamValue.trim())) {
                return true;
            }
        } else if (hiddenParamValue != null && hiddenParamValue.length() != 0) {
            if (Boolean.parseBoolean(hiddenParamValue.trim())) {
                return true;
            }
        } else if (dynamicParamValue != null && dynamicParamValue.length() != 0){
            if(Boolean.parseBoolean(dynamicParamValue.trim())) {
                return true;
            }
        }
        return false;
        
    }

    public static boolean isGhostServiceGroup(AxisServiceGroup axisServiceGroup) {
        if (axisServiceGroup == null) {
            return false;
        }
        String ghostParamValue =
                (String) axisServiceGroup.getParameterValue(CarbonConstants.GHOST_SERVICE_PARAM);
        if (ghostParamValue != null && ghostParamValue.length() != 0) {
            if (Boolean.parseBoolean(ghostParamValue.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFilteredOutService(AxisService service) {
        String adminParamValue =
                (String) service.getParameterValue(CarbonConstants.ADMIN_SERVICE_PARAM_NAME);
        String hiddenParamValue =
                (String) service.getParameterValue(CarbonConstants.HIDDEN_SERVICE_PARAM_NAME);
        String dynamicParamValue =
                (String) service.getParameterValue(CarbonConstants.DYNAMIC_SERVICE_PARAM_NAME);
        if (adminParamValue != null && adminParamValue.length() != 0) {
            if (Boolean.parseBoolean(adminParamValue.trim())) {
                return true;
            }
        } else if (hiddenParamValue != null && hiddenParamValue.length() != 0) {
            if (Boolean.parseBoolean(hiddenParamValue.trim())) {
                return true;
            }
        } else if (dynamicParamValue != null && dynamicParamValue.length() != 0){
            if(Boolean.parseBoolean(dynamicParamValue.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdminService(AxisService service) {
        String adminParamValue =
                (String) service.getParameterValue(CarbonConstants.ADMIN_SERVICE_PARAM_NAME);
        if (adminParamValue != null && adminParamValue.length() != 0) {
            if (Boolean.parseBoolean(adminParamValue.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHiddenService(AxisService service) {
        String hiddenParamValue =
                (String) service.getParameterValue(CarbonConstants.HIDDEN_SERVICE_PARAM_NAME);
        if (hiddenParamValue != null && hiddenParamValue.length() != 0) {
            if (Boolean.parseBoolean(hiddenParamValue.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFilteredOutModule(AxisModule module) {
        Parameter adminParam = module.getParameter(CarbonConstants.ADMIN_MODULE_PARAM_NAME);
        if (adminParam != null ) {
            String adminParamValue = (String)adminParam.getValue();
            if (adminParamValue != null && Boolean.parseBoolean(adminParamValue.trim())) {
              return true;
            }
        }
        return false;
    }

    //TODO this doen't give filtered modules now, but used by service archive create 
    public static boolean isFilteredOutModule(String moduleName) {
        return false;
    }

    public static boolean isManagedModule(AxisModule module) {
        Parameter managedParam = module.getParameter(CarbonConstants.MANAGED_MODULE_PARAM_NAME);
        if (managedParam != null ) {
            String managedParamValue = (String)managedParam.getValue();
            if (managedParamValue != null && Boolean.parseBoolean(managedParamValue.trim())) {
              return true;
            }
        }
        return false;
    }

}
