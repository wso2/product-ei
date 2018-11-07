/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.mb.platform.tests.clustering.mqtt.DataProvider;

import org.testng.annotations.DataProvider;
import org.wso2.mb.integration.common.clients.QualityOfService;

/**
 * Data provider to feed Quality Of Service values to test cases.
 */
public class QualityOfServiceDataProvider {

    /**
     * Get the Quality Of Service set to run tests.
     *
     * @return Quality Of Service set to test
     */
    @DataProvider(name = "QualityOfServiceDataProvider")
    public static Object[][] createData() {
        return new Object[][]{
                {QualityOfService.MOST_ONCE},
                {QualityOfService.LEAST_ONCE},
                {QualityOfService.EXACTLY_ONCE}
        };
    }

}


