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

package org.wso2.ei.tools.ds2ballerina.configreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.ds2ballerina.beans.DataService;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * {@code ConfigReader} class reads data service configuration file
 */
public class DataServiceReader {

    private static Logger logger = LoggerFactory.getLogger(DataServiceReader.class);

    public static DataService readDataServiceFile(File dsFile) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataService.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            DataService dataService = (DataService) jaxbUnmarshaller.unmarshal(dsFile);

            return dataService;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
