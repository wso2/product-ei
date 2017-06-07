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

        String synapseInConfig = "/home/senduran/projects/bsenduran/product-ei/tools/converter/synapse2ballerina/src/main/resources/sample-synapse-configs/payloadSample.xml";

        JAXBContext jaxbContext = JAXBContext.newInstance(API.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        //todo change the root element to 'definitions'
        API api = (API) jaxbUnmarshaller.unmarshal(new File(synapseInConfig));

        BallerinaFile ballerinaFile = ASTBuilder.build(api);

        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        String balOutFile = "/home/senduran/projects/bsenduran/product-ei/tools/converter/synapse2ballerina/src/main/resources/sample-synapse-configs/payloadSample.bal";
        sourceGenerator.generate(ballerinaFile, balOutFile);

    }


}
