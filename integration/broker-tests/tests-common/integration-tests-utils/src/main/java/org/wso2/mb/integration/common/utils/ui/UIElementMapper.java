/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.common.utils.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read mapper.properties file and load it's uiElements into Properties object.
 */
public class UIElementMapper {
    public static final Properties uiProperties = new Properties();
    private static UIElementMapper instance;

    static {
        try {
            setStream();
            instance = new UIElementMapper();
        } catch (IOException ioe){
            throw new ExceptionInInitializerError("mapper stream not set. Failed to read file");
        }
    }

    private UIElementMapper() {
    }

    public static UIElementMapper getInstance() {
        return instance;
    }

    private static Properties setStream() throws IOException {
        InputStream inputStream = UIElementMapper.class.getResourceAsStream("/mapper.properties");
        if (inputStream.available() > 0) {
            uiProperties.load(inputStream);
            inputStream.close();
            return uiProperties;
        }
        return null;
    }

    public String getElement(String key) {
        return uiProperties.getProperty(key);
    }
}
