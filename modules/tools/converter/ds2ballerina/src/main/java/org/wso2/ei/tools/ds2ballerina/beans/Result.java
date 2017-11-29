/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.tools.ds2ballerina.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object for result element.
 */
@XmlRootElement(name = "result") public class Result {

    @XmlElementRef(name = "element") ArrayList<Element> elementList;
    @XmlAttribute(name = "element") private String resultWrapper;
    @XmlAttribute private String rowName;
    @XmlAttribute private boolean useColumnNumbers;
    @XmlAttribute private boolean escapeNonPrintableChar;
    @XmlAttribute private String outputType;
    @XmlAttribute private String defaultNamespace;
    @XmlMixed private List<String> textMapping;
//    private List<Object> attributes;

    public boolean isEscapeNonPrintableChar() {
        return escapeNonPrintableChar;
    }

    public String getOutputType() {
        return outputType;
    }

    public String getNamespace() {
        return defaultNamespace;
    }

    public boolean isUseColumnNumbers() {
        return useColumnNumbers;
    }

    public String getRowName() {
        return rowName;
    }

    public String getResultWrapper() {
        return resultWrapper;
    }

    public ArrayList<Element> getElementList() {
        return elementList;
    }

}
