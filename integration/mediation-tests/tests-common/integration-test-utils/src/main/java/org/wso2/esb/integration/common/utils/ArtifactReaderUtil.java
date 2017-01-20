/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.esb.integration.common.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ArtifactReaderUtil {
    public OMElement getOMElement(String filePath) throws FileNotFoundException,
                                                          XMLStreamException {
        //if file location =null it taking from the test data directory
        OMElement documentElement = null;
        FileInputStream inputStream;
        File file = new File(filePath);
        if (file.exists()) {
            inputStream = new FileInputStream(filePath);
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
            //create the builder
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            //get the root element (in this case the envelope)
            documentElement = builder.getDocumentElement();
        }
        return documentElement;
    }
}