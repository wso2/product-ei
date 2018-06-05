/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.core.transports;

import org.apache.axis2.context.ConfigurationContext;

/**
 * This interface is used for plugging in different implementations for special processing of some
 * HTTP GET requests.
 * <p/>
 * e.g. ?wsdl, ?wsdl2, ?rss, ?atom etc.
 * <p/>
 * If you need to handle a special HTTP GET request, you have to write an implementation of this
 * interface, and plug it in through the carbon.xml file.
 */
public interface HttpGetRequestProcessor {

    /**
     * Process the HTTP GET request
     *
     * @param request              The CarbonHttpRequest
     * @param response             The CarbonHttpResponse
     * @param configurationContext The system ConfigurationContext
     * @throws Exception If some failure occurs during processing
     */
    void process(CarbonHttpRequest request,
                 CarbonHttpResponse response,
                 ConfigurationContext configurationContext) throws Exception;
}
