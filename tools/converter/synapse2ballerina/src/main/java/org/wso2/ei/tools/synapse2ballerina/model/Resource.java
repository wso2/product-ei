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

package org.wso2.ei.tools.synapse2ballerina.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Resource {
    //todo introduce enum later
    private List<String> methodList = new ArrayList<>();

    private String uriTemplate;

    private String urlMapping;

    private List<Sequence> sequenceList = new ArrayList<>();



    public List<String> getMethodList() {
        return methodList;
    }

    @XmlAttribute(name = "methods")
    public void setMethods(String methods) {
        String[] split = methods.split(" ");
        methodList.addAll(Arrays.asList(split));
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    @XmlAttribute(name = "uri-template")
    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public String getUrlMapping() {
        return urlMapping;
    }

    @XmlAttribute(name = "url-mapping")
    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public List<Sequence> getSequenceList() {
        return sequenceList;
    }

    @XmlElements({
                            @XmlElement(name = "inSequence", type = Sequence.class),
                            @XmlElement(name = "outSequence", type = Sequence.class)
                    })
    public void setSequence (Sequence sequence) {
        this.sequenceList.add(sequence);
    }

}
