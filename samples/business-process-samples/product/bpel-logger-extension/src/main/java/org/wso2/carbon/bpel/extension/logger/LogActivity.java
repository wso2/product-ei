/**
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
package org.wso2.carbon.bpel.extension.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.common.FaultException;
import org.apache.ode.bpel.elang.xpath20.o.OXPath20ExpressionBPEL20;
import org.apache.ode.bpel.explang.ConfigurationException;
import org.apache.ode.bpel.explang.EvaluationException;
import org.apache.ode.bpel.o.OExpressionLanguage;
import org.apache.ode.bpel.runtime.ExprEvaluationContextImpl;
import org.apache.ode.bpel.runtime.ExtensionContextImpl;
import org.apache.ode.bpel.runtime.ScopeFrame;
import org.apache.ode.bpel.runtime.extension.ExtensionContext;
import org.apache.ode.utils.DOMUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class LogActivity {

    private ExtensionContext extensionContext;
    private Element element;
    private String logLevel;
    public final static Log log = LogFactory.getLog(LoggerExtensionOperation.class.getName());

    public LogActivity(ExtensionContext extensionContext, Element element) {
        this.extensionContext = extensionContext;
        this.element = element;
    }

    /**
     * @throws FaultException
     */

    public void runLogActivity() throws FaultException {

        String variableName = "", part = "", i_id = "";
        int fromTagCount = 0;

        logLevel = getLogLevel();
        i_id = String.valueOf(get_iid());

        while (element.getElementsByTagNameNS(LoggerConstants.LOGGER_NS, LoggerConstants.ATTRIBUTE_FROM).item(fromTagCount) != null) {

            Node fromNode = element.getElementsByTagNameNS(LoggerConstants.LOGGER_NS, LoggerConstants.ATTRIBUTE_FROM).item(fromTagCount);
            variableName = getVaribleName(fromNode);
            part = getPartName(fromNode);

            if (getLiteral(fromNode) != null) {

                log("[" + i_id + "] " + getLiteral(fromNode).replace("\n", "").replace("\r", ""), logLevel);
            }
            if (getXpath(fromNode) != null) {
                evaluateXpath(getXpath(fromNode));
            }


            if (variableName != null && part != null) {
                int partCount = 0, variablePartIndex = 0;
                boolean hasPart = false;

                try {
                    while (extensionContext.readVariable(variableName).getChildNodes().item(partCount) != null) {
                        try {
                            if (part.equalsIgnoreCase(extensionContext.readVariable(variableName).getChildNodes().item(partCount).getLocalName())) {
                                hasPart = true;
                                variablePartIndex = partCount;
                            }
                        } catch (FaultException e) {
                            e.printStackTrace();
                        }
                        partCount++;
                    }
                } catch (FaultException e) {
                    e.printStackTrace();
                }

                if (hasPart) {
                    String logtext = null;
                    try {
                        logtext = DOMUtils.domToString(extensionContext.readVariable(variableName).getChildNodes().item(variablePartIndex));
                        logtext = logtext.replace("\n", "").replace("\r", "");
                    } catch (FaultException e) {
                        e.printStackTrace();
                    }

                    log("[" + i_id + "] " + logtext, logLevel);
                } else {
                    log.error("part specified in the varialbe " + variableName + " does not exist");
                    throw new FaultException(new QName(LoggerConstants.LOGGER_NS),
                            "part specified in the varialbe " + variableName + " does not exist");
                }
            } else if (variableName != null) {
                String logtext = null;
                logtext = extensionContext.readVariable(variableName).getTextContent();
                logtext = logtext.replace("\n", "").replace("\r", "");
                log("[" + i_id + "] " + logtext, logLevel);

            }
            fromTagCount++;
        }

    }

    /**
     * @return "name" attribute declared inside a "from" tag
     */

    private String getVaribleName(Node fromNode) {
        if (fromNode.getAttributes().getNamedItem(LoggerConstants.ATTRIBUTE_VARIABLE) != null) {
            return fromNode.getAttributes().getNamedItem(LoggerConstants.ATTRIBUTE_VARIABLE).getTextContent();
        } else {
            return null;
        }


    }

    /**
     * @param fromNode node of "from" tag
     * @return "part" attribute declared inside a "from" tag
     */

    private String getPartName(Node fromNode) {
        if (fromNode.getAttributes().getNamedItem(LoggerConstants.ATTRIBUTE_PART) != null) {
            return fromNode.getAttributes().getNamedItem(LoggerConstants.ATTRIBUTE_PART).getTextContent();
        } else {
            return null;
        }

    }

    /**
     * @param extensionContext
     * @param variable         value of the "variable" attribute inside "from" tag
     * @param itemIndex
     * @return
     */

    private String getTextValue(ExtensionContext extensionContext, String variable, int itemIndex) {

        try {
            return extensionContext.readVariable(variable).getFirstChild().getFirstChild().getChildNodes().item(itemIndex).getTextContent();
        } catch (FaultException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }


    }

    /**
     * @param fromNode index of the "from" tag
     * @return if  the "from" element contains a literal element, the string content of the literal element
     */
    private String getLiteral(Node fromNode) {

        String literal = fromNode.getTextContent();
        if (literal != null && !literal.equalsIgnoreCase("") && getXpath(fromNode) == null)
            literal = DOMUtils.domToString(fromNode.getChildNodes().item(1));
        if (literal.contains("$") || "".equalsIgnoreCase(literal)) {
            return null;
        } else {
            return literal;
        }

    }

    /**
     * @param fromNode index of the "from" tag
     * @return if  the "from" element contains an Xpath, the Xpath will be returned,
     */

    private String getXpath(Node fromNode) {
        if (fromNode.getAttributes().getNamedItem("xpath") != null) {
            return fromNode.getAttributes().getNamedItem("xpath").getTextContent();
        } else {
            return null;
        }

    }

    /**
     * @return the log status declared in the deploy.xml file
     */
/*    public boolean isLogEnabled() {
        DeploymentUnitDir du = new DeploymentUnitDir(new File(extensionContext.getDUDir()));
        boolean logEnabled = false;


        List<TDeployment.Process> processArray = du.getDeploymentDescriptor().getDeploy().getProcessList();
        for (TDeployment.Process process : processArray) {
            if ((process.getName().toString()).equalsIgnoreCase(extensionContext.getInternalInstance().getProcessQName().toString())) {
                if (process.getEnableLog()) {
                    logEnabled = true;
                    break;
                }
            }
        }
        return logEnabled;
    }*/

    /**
     * @return the name of the log activity in bpel
     */

    private String getLogActivityName() {
        return element.getAttribute(LoggerConstants.ATTRIBUTE_NAME);
    }

    /**
     * the level of log of the current activity
     *
     * @return
     */

    private String getLogLevel() {
        return element.getAttribute(LoggerConstants.ATTRIBUTE_LEVEL);
    }

    /**
     * @return the current instance id
     */
    private Long get_iid() {
        return extensionContext.getInternalInstance().getPid();
    }

    /**
     * @param logText  text to be logged
     * @param logLevel log level
     */
    private void log(String logText, String logLevel) {
        if ("info".equalsIgnoreCase(logLevel)) {
            log.info(logText);
        } else if ("debug".equalsIgnoreCase(logLevel)) {
            log.debug(logText);
        } else if ("warn".equalsIgnoreCase(logLevel)) {
            log.warn(logText);
        } else if ("fatal".equalsIgnoreCase(logLevel)) {
            log.fatal(logText);

        }
    }


    public void evaluateXpath(String xpath) {
        QName _qnFnGetVariableData = new QName(LoggerConstants.BPEL11_NS, "getVariableData");
        QName _qnFnGetVariableProperty = new QName(LoggerConstants.BPEL11_NS, "getVariableProperty");
        QName _qnFnGetLinkStatus = new QName(LoggerConstants.BPEL11_NS, "getLinkStatus");
        QName _qnFngetDoXslTransform = new QName(LoggerConstants.BPEL11_NS, "getDoXslTransform");


        OXPath20ExpressionBPEL20 oexp = new OXPath20ExpressionBPEL20(extensionContext.getInternalInstance().getProcessModel().getOwner(), _qnFnGetVariableData, _qnFnGetVariableProperty, _qnFnGetLinkStatus, _qnFngetDoXslTransform, false);
        OExpressionLanguage oExpressionLanguage = new OExpressionLanguage(extensionContext.getProcessModel().getOwner(), null);
        oExpressionLanguage.expressionLanguageUri = "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0";
        oexp.expressionLanguage = oExpressionLanguage;

        oExpressionLanguage.properties.put("runtime-class", "org.apache.ode.bpel.elang.xpath20.runtime.XPath20ExpressionRuntime");
        try {
            extensionContext.getInternalInstance().getExpLangRuntime().registerRuntime(oExpressionLanguage);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        oexp.insertMissingData = true;
        ScopeFrame scopeFrame = ((ExtensionContextImpl) extensionContext).getScopeFrame();


        ExprEvaluationContextImpl exprEvaluationContext = new ExprEvaluationContextImpl(scopeFrame, extensionContext.getInternalInstance());


        try {
            oexp.vars = (HashMap) extensionContext.getVisibleVariables();
        } catch (FaultException e) {
            e.printStackTrace();
        }


        oexp.namespaceCtx = extensionContext.getProcessModel().namespaceContext;
        try {
            oexp.xpath = xpath;
            List result = extensionContext.getInternalInstance().getExpLangRuntime().evaluate(oexp, exprEvaluationContext);

            if (result != null) {
                ListIterator itorator = result.listIterator();

                while (itorator.hasNext()) {

                    log("[" + String.valueOf(get_iid()) + "] " + xpath + " value :" + itorator.next().toString(), logLevel);
                }
            }

        } catch (FaultException e) {

            e.printStackTrace();
        } catch (EvaluationException e) {

            e.printStackTrace();
        }

    }
}
