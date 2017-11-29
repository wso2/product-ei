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

package org.wso2.ei.tools.mule2ballerina.visitor;

import org.wso2.ei.tools.mule2ballerina.model.AsynchronousTask;
import org.wso2.ei.tools.mule2ballerina.model.Comment;
import org.wso2.ei.tools.mule2ballerina.model.Database;
import org.wso2.ei.tools.mule2ballerina.model.DatabaseConfig;
import org.wso2.ei.tools.mule2ballerina.model.Flow;
import org.wso2.ei.tools.mule2ballerina.model.FlowReference;
import org.wso2.ei.tools.mule2ballerina.model.HttpListener;
import org.wso2.ei.tools.mule2ballerina.model.HttpListenerConfig;
import org.wso2.ei.tools.mule2ballerina.model.HttpRequest;
import org.wso2.ei.tools.mule2ballerina.model.HttpRequestConfig;
import org.wso2.ei.tools.mule2ballerina.model.Logger;
import org.wso2.ei.tools.mule2ballerina.model.Payload;
import org.wso2.ei.tools.mule2ballerina.model.PropertyRemover;
import org.wso2.ei.tools.mule2ballerina.model.PropertySetter;
import org.wso2.ei.tools.mule2ballerina.model.Root;
import org.wso2.ei.tools.mule2ballerina.model.VariableRemover;
import org.wso2.ei.tools.mule2ballerina.model.VariableSetter;

/**
 * Visitor interface for TreeVisitor
 */
public interface Visitor {

    void visit(Root root);

    void visit(Payload payload);

    void visit(HttpListenerConfig listenerConfig);

    void visit(HttpListener listener);

    void visit(Flow flow);

    void visit(HttpRequest request);

    void visit(HttpRequestConfig requestConfig);

    void visit(Comment comment);

    void visit(Logger logger);

    void visit(PropertySetter propertySetter);

    void visit(PropertyRemover propertyRemover);

    void visit(VariableSetter variableSetter);

    void visit(VariableRemover variableRemover);

    void visit(FlowReference flowReference);

    void visit(AsynchronousTask asynchronousTask);

    void visit(Database database);

    void visit(DatabaseConfig databaseConfig);

}
