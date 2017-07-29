/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.tools.synapse2ballerina;

import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.config.SynapseConfigurationBuilder;
import org.ballerinalang.model.BallerinaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.generator.BallerinaSourceGenerator;
import org.wso2.ei.tools.synapse2ballerina.visitor.SynapseConfigVisitor;

import java.io.IOException;
import java.util.Properties;

/**
 * {@code SynapseToBalConvertExecutor} is the starting point for synapse2ballerina converter. This class will be
 * refactored.
 */
public class SynapseToBalConvertExecutor {

    private static Logger logger = LoggerFactory.getLogger(SynapseToBalConvertExecutor.class);

    public static void main(String[] args) {

        //For the time being, provide synapse definition for args[0] and destination for args[1]
        SynapseConfiguration synapseConfiguration = SynapseConfigurationBuilder.
                getConfiguration(args[0], new Properties());

        SynapseConfigVisitor configVisitor = new SynapseConfigVisitor();
        BallerinaFile ballerinaFile = configVisitor.visit(synapseConfiguration);
        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        try {
            sourceGenerator.generate(ballerinaFile, args[1]);
        } catch (IOException e) {
            logger.error("Error occured while generating ballerina source", e);
        }
    }
}
