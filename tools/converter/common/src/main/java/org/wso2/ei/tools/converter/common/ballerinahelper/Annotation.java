package org.wso2.ei.tools.converter.common.ballerinahelper;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.converter.common.util.Constant;

import java.util.Map;

/**
 * Represent ballerina annotations
 */
public class Annotation {

    public static void createServiceAnnotation(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {
        ballerinaASTModelBuilder
                .createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_CONFIG);
        ballerinaASTModelBuilder.createAnnotationAttributeValue(Constant.BLANG_BASEPATH,
                (String) parameters.get(Constant.BASEPATH_VALUE));
        ballerinaASTModelBuilder.addAnnotationAttachment(1); //attributesCount is never used
    }

    public static void createResourceAnnotation(BallerinaASTModelBuilder ballerinaASTModelBuilder,
            Map<String, Object> parameters) {
        ballerinaASTModelBuilder
                .createAnnotationAttachment(Constant.BLANG_HTTP, Constant.BLANG_RESOURCE_CONFIG);
       /* ballerinaASTModelBuilder.createAnnotationAttributeValue(Constant.BLANG_METHODS,
                (String) parameters.get(Constant.METHOD_NAME));*/
        ballerinaASTModelBuilder.createAnnotationAttributeArrayType(Constant.BLANG_METHODS, (String[]) parameters.get
                (Constant.METHOD_NAME));
        ballerinaASTModelBuilder.addAnnotationAttachment(1); //attributesCount is never used
    }

}
