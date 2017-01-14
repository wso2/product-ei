/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.ui.test.util;

import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkProperties;

public class EndpointGenerator {
    public static String getEndpointServiceUrl(String serviceName) {
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder();
        ProductUrlGeneratorUtil productUrlGeneratorUtil = new ProductUrlGeneratorUtil();
        String backEndServiceUrl;
        FrameworkProperties properties;

        if (environmentBuilder.getFrameworkSettings().getEnvironmentSettings().executionEnvironment().equalsIgnoreCase(ExecutionEnvironment.stratos.name())) {
            properties = FrameworkFactory.getFrameworkProperties(ProductConstant.APP_SERVER_NAME);
            UserInfo info = UserListCsvReader.getUserInfo(ProductConstant.ADMIN_USER_ID);
            backEndServiceUrl = productUrlGeneratorUtil.getHttpServiceURLOfStratos(properties.getWorkerVariables().getHttpPort()
                    , properties.getWorkerVariables().getNhttpPort(), properties.getWorkerVariables().getHostName()
                    , properties, info);
            backEndServiceUrl = backEndServiceUrl + "/" + serviceName;

        } else if (environmentBuilder.getFrameworkSettings().getEnvironmentSettings().is_builderEnabled()) {
            properties = FrameworkFactory.getFrameworkProperties(ProductConstant.AXIS2_SERVER_NAME);
            backEndServiceUrl = productUrlGeneratorUtil.getHttpServiceURLOfProduct(properties.getProductVariables().getHttpPort()
                    , properties.getProductVariables().getNhttpPort()
                    , properties.getProductVariables().getHostName(), properties);
            backEndServiceUrl = backEndServiceUrl + "/" + serviceName;
        } else {
            properties = FrameworkFactory.getFrameworkProperties(ProductConstant.APP_SERVER_NAME);
            if (properties.getEnvironmentSettings().is_runningOnStratos()) {
                backEndServiceUrl = productUrlGeneratorUtil.getHttpServiceURLOfStratos(properties.getProductVariables().getHttpPort()
                        , properties.getProductVariables().getNhttpPort(), properties.getProductVariables().getHostName()
                        , properties, UserListCsvReader.getUserInfo(ProductConstant.ADMIN_USER_ID));
            } else {
                backEndServiceUrl = productUrlGeneratorUtil.getHttpServiceURLOfProduct(properties.getProductVariables().getHttpPort()
                        , properties.getProductVariables().getNhttpPort(), properties.getProductVariables().getHostName()
                        , properties);
            }
            backEndServiceUrl = backEndServiceUrl + "/" + serviceName;
        }

        return backEndServiceUrl;
    }
}
