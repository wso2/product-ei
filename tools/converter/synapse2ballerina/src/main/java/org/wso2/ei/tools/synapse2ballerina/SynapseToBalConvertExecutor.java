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

import org.ballerinalang.model.BallerinaFile;
import org.wso2.ei.tools.converter.common.generator.BallerinaSourceGenerator;
import org.wso2.ei.tools.synapse2ballerina.builder.ASTBuilder;
import org.wso2.ei.tools.synapse2ballerina.model.API;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class SynapseToBalConvertExecutor {
    public static void main(String[] args) throws Exception {

        String synapseInConfig = args[0];

        JAXBContext jaxbContext = JAXBContext.newInstance(API.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        //todo change the root element to 'definitions'
        API api = (API) jaxbUnmarshaller.unmarshal(new File(synapseInConfig));

        BallerinaFile ballerinaFile = ASTBuilder.build(api);

        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        String balOutFile = args[1];
        sourceGenerator.generate(ballerinaFile, balOutFile);

    }


}
