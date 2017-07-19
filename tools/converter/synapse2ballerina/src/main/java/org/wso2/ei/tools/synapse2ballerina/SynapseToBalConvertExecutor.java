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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.Utils;
import org.wso2.ei.tools.synapse2ballerina.model.Artifact;
import org.wso2.ei.tools.synapse2ballerina.util.ArtifactType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * Main class to start the converter
 */
public class SynapseToBalConvertExecutor {

    private static Logger logger = LoggerFactory.getLogger(SynapseToBalConvertExecutor.class);

    public static void main(String[] args) throws Exception {

        /*String synapseInConfig = args[0];

        JAXBContext jaxbContext = JAXBContext.newInstance(API.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        //todo change the root element to 'definitions'
        API api = (API) jaxbUnmarshaller.unmarshal(new File(synapseInConfig));

        BallerinaFile ballerinaFile = ASTBuilder.build(api);

        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        String balOutFile = args[1];
        sourceGenerator.generate(ballerinaFile, balOutFile);*/

        if (args == null || args.length == 0) {
            logger.error("Please provide source file/folder");
            System.exit(-1);
        }

        String destinationFolder = Utils.extractFiles(args[0], args[1], true);
        logger.debug("Extracted files are in: " + destinationFolder);

        File source = new File(args[0]);
        Path sourcePath = source.toPath().toAbsolutePath();

        //  Root rootObj = new Root();

        if (Files.isDirectory(sourcePath)) {
            //Loop through every extracted folder to generate bal source
            File[] files = new File(destinationFolder).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        logger.debug("Outer Directory: " + file.getName());
                        File[] innerFiles = file.listFiles();
                        if (innerFiles != null) {
                            for (File innerFile : innerFiles) {
                                if (innerFile.isDirectory()) {
                                    logger.debug("Inner Directory: " + innerFile.getName());
                                    JAXBContext jaxbContext = JAXBContext.newInstance(Artifact.class);
                                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                                    Artifact artifact = (Artifact) jaxbUnmarshaller
                                            .unmarshal(new File(innerFile.getAbsolutePath() + "/artifact.xml"));
                                    logger.debug("Actual File >> " + artifact.getFile());
                                    ArtifactType artifactType = ArtifactType.get(artifact.getType());
                                    switch (artifactType) {
                                    case API:
                                        break;
                                    case ENDPOINT:
                                        break;
                                    default:
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }/* else {
            Only the mentioned archive needs to be considered when generating bal source
        }*/

    }

}
