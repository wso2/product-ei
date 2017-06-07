package org.wso2.ei.tools.synapse2ballerina.model;

import org.wso2.ei.tools.converter.common.builder.BallerinaASTModelBuilder;
import org.wso2.ei.tools.synapse2ballerina.builder.Constants;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static org.wso2.ei.tools.synapse2ballerina.builder.ASTBuilder.addImport;

@XmlRootElement(name = "payloadFactory")
public class PayloadFactoryMediator implements Mediator {

    String payload;

    String mediaType;

    public String getPayload() {
        return payload;
    }

    @XmlAnyElement(value = FormatHandler.class,
                   lax = true)
    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getMediaType() {
        return mediaType;
    }

    @XmlAttribute(name = "media-type")
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public void build(BallerinaASTModelBuilder ballerinaASTModelBuilder, Map<String, Object> parameters) {
        String responseVariableName = (String) parameters.get(Constants.RESPONSE_VAR_NAME);

        ballerinaASTModelBuilder.addTypes(Constants.BLANG_TYPE_MESSAGE);
        ballerinaASTModelBuilder.addMapStructLiteral();
        ballerinaASTModelBuilder.createVariable(responseVariableName, true);

        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createBackTickExpression("`"+getPayload().trim()+"`");
        ballerinaASTModelBuilder.addTypes(getMediaType());
        ballerinaASTModelBuilder.createVariable("payload", true);


        addImport(Constants.BLANG_PKG_MESSAGES);
        ballerinaASTModelBuilder
                .createNameReference(Constants.BLANG_PKG_MESSAGES, Constants.BLANG_PKG_MESSAGES_SET_XML_PAYLOAD);

        ballerinaASTModelBuilder.startExprList();
        ballerinaASTModelBuilder.createNameReference(null, responseVariableName);
        ballerinaASTModelBuilder.createVariableRefExpr();

        ballerinaASTModelBuilder.createNameReference(null, "payload");
        ballerinaASTModelBuilder.createVariableRefExpr();

        ballerinaASTModelBuilder.endExprList(2);
        ballerinaASTModelBuilder.createFunctionInvocation(true);

    }

    private static class FormatHandler implements DomHandler<String, StreamResult> {

        private static final String FORMAT_START_TAG = "<format xmlns=\"http://ws.apache.org/ns/synapse\">";
        private static final String FORMAT_END_TAG = "</format>";

        private StringWriter xmlWriter = new StringWriter();

        @Override
        public StreamResult createUnmarshaller(ValidationEventHandler errorHandler) {
            return new StreamResult(xmlWriter);
        }

        @Override
        public String getElement(StreamResult rt) {
            String xml = rt.getWriter().toString();
            int beginIndex = xml.indexOf(FORMAT_START_TAG) + FORMAT_START_TAG.length();
            int endIndex = xml.indexOf(FORMAT_END_TAG);
            return xml.substring(beginIndex, endIndex);
        }

        @Override
        public Source marshal(String n, ValidationEventHandler errorHandler) {
            try {
                String xml = FORMAT_START_TAG + n.trim() + FORMAT_END_TAG;
                StringReader xmlReader = new StringReader(xml);
                return new StreamSource(xmlReader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}




