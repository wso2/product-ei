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

package org.wso2.ei.tools.ds2ballerina.util;

import org.ballerinalang.model.builder.BLangModelBuilder;
import org.ballerinalang.model.types.SimpleTypeName;
import org.wso2.ei.tools.ds2ballerina.beans.Config;
import org.wso2.ei.tools.ds2ballerina.beans.DataService;
import org.wso2.ei.tools.ds2ballerina.beans.Param;
import org.wso2.ei.tools.ds2ballerina.beans.Query;
import org.wso2.ei.tools.ds2ballerina.beans.Resource;
import org.wso2.ei.tools.ds2ballerina.beans.WithParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class to create ballerina model.
 */
public class Util {

    private static Pattern pattern = Pattern.compile("\\{(.*?)\\}");

    public static void createConfigProperties(BLangModelBuilder modelBuilder, DataService dataService) {

        for (Config config : dataService.getConfigs()) {
            modelBuilder.startMapStructLiteral();
            // initialize config properties
            Map<String, String> configPropertiesMap = config.getPropertiesMap();
            // convert config Properties to support Hikari Properties
            configPropertiesMap = convertConfigPropertyMapToSupportHikari(configPropertiesMap);

            for (Map.Entry<String, String> entry : configPropertiesMap.entrySet()) {
                modelBuilder.createStringLiteral(null, null, entry.getKey());
                modelBuilder.createStringLiteral(null, null, entry.getValue());
                modelBuilder.addMapStructKeyValue(null, null);
            }
            modelBuilder.createMapStructLiteral(null, null);
            String builtInRefTypeName1 = "map";
            SimpleTypeName mapSimpleType = new SimpleTypeName(builtInRefTypeName1);
            String mapVariableName = config.getId() + "Properties";
            modelBuilder.addVariableDefinitionStmt(null, null, mapSimpleType, mapVariableName, true);

            // create sql connector
            modelBuilder.startExprList();

            BLangModelBuilder.NameReference mapVariableNameReference = new BLangModelBuilder.NameReference(null,
                    mapVariableName);
            modelBuilder.validateAndSetPackagePath(null, mapVariableNameReference);
            modelBuilder.createVarRefExpr(null, null, mapVariableNameReference);
            modelBuilder.endExprList(1);

            String pkgName6 = "sql";
            String name6 = "ClientConnector";
            BLangModelBuilder.NameReference sqlClientConnectorNameReference;
            sqlClientConnectorNameReference = new BLangModelBuilder.NameReference(pkgName6, name6);
            modelBuilder.validateAndSetPackagePath(null, sqlClientConnectorNameReference);
            SimpleTypeName sqlClientConnectorSimpleTypeName = new SimpleTypeName(
                    sqlClientConnectorNameReference.getName(), sqlClientConnectorNameReference.getPackageName(),
                    sqlClientConnectorNameReference.getPackagePath());
            modelBuilder.validateAndSetPackagePath(null, sqlClientConnectorNameReference);

            modelBuilder.createConnectorInitExpr(null, null, sqlClientConnectorSimpleTypeName, true);

            //with PkgPath
            modelBuilder.addVariableDefinitionStmt(null, null, sqlClientConnectorSimpleTypeName, config.getId(), true);
        }
    }

    public static void createResources(BLangModelBuilder modelBuilder, DataService dataService) {

        for (Resource resource : dataService.getResources()) {
            modelBuilder.startResourceDef();
            modelBuilder.startAnnotationAttachment(null);
            String method = resource.getMethod();

            BLangModelBuilder.NameReference methodNameReference;

            String httpPackage = "http";
            methodNameReference = new BLangModelBuilder.NameReference(httpPackage, method);
            modelBuilder.validateAndSetPackagePath(null, methodNameReference);
            int attributesAvailable = 0;
            modelBuilder.addAnnotationAttachment(null, null, methodNameReference, attributesAvailable);
            modelBuilder.startAnnotationAttachment(null);

            BLangModelBuilder.NameReference pathNameReference = new BLangModelBuilder.NameReference(httpPackage,
                    "Path");
            modelBuilder.validateAndSetPackagePath(null, pathNameReference);

            String resourcePath = resource.getPath();
            modelBuilder.createStringLiteral(null, null, resourcePath);

            modelBuilder.createLiteralTypeAttributeValue(null, null);

            modelBuilder.createAnnotationKeyValue(null, "value");

            modelBuilder.addAnnotationAttachment(null, null, pathNameReference, 1);

            String builtInRefTypeName = "message";
            SimpleTypeName simpleTypeName = new SimpleTypeName(builtInRefTypeName);

            int annotationCount = 0;
            modelBuilder.addParam(null, null, simpleTypeName, "m", annotationCount, false);

            //set pathParams in resource
            if (isPathParamContained(resourcePath)) {
                List<String> pathParams = getPathParams(resourcePath);
                for (String pathParam : pathParams) {
                    modelBuilder.startAnnotationAttachment(null);
                    BLangModelBuilder.NameReference pathParamNameReference = new BLangModelBuilder.NameReference(
                            httpPackage, "PathParam");
                    modelBuilder.validateAndSetPackagePath(null, pathParamNameReference);

                    modelBuilder.createStringLiteral(null, null, pathParam);

                    modelBuilder.createLiteralTypeAttributeValue(null, null);
                    modelBuilder.createAnnotationKeyValue(null, "value");

                    modelBuilder.addAnnotationAttachment(null, null, pathParamNameReference, 1);

                    //set param dataType
                    for (WithParam withParam : resource.getCallQuery().getParamList()) {
                        if (pathParam.equals(withParam.getName()) && "query-param".equals(withParam.getParamType())) {
                            String queryParamName = withParam.getParamValue();
                            Query query = dataService.getQueries().get(resource.getCallQuery().getQueryId());
                            Param param = query.getParamMap().get(queryParamName);
                            simpleTypeName = new SimpleTypeName(param.getSqlType().toLowerCase(Locale.ENGLISH));
                            break;
                        }
                    }

                    modelBuilder.addParam(null, null, simpleTypeName, pathParam, 1, false);
                }
            }

            modelBuilder.startCallableUnitBody(null);

            Util.convertMessageToJson(modelBuilder, "m", "input");

            if (dataService.getQueries().get(resource.getCallQuery().getQueryId()).getParamMap() != null) {
                for (Param param : dataService.getQueries().get(resource.getCallQuery().getQueryId())
                        .getParamMap().values()) {
                    Util.initializeParameter(modelBuilder, param, false, "_" + resource.getMethod() + resourcePath);
                }
            }

            initializeParameterArray(modelBuilder, dataService.getQueries().get(resource.getCallQuery().getQueryId()));

            invokeSQLClientConnector(modelBuilder, dataService.getQueries().get(resource.getCallQuery().getQueryId()),
                    "test");

            convertDatatableToJson(modelBuilder, "test", "payload");

            initializeMessage(modelBuilder, "response");

            setJsonPayloadToMessage(modelBuilder, "response", "payload");
            replyMessage(modelBuilder, "response");

            // response message initialization

            modelBuilder.endCallableUnitBody();
            modelBuilder.addResource(null, null, resource.getCallQuery().getQueryId(), 2);

        }

    }

    private static void initializeParameter(BLangModelBuilder modelBuilder, Param param, boolean isPathParam,
            String path) {

        String paramName = param.getName();
        modelBuilder.startMapStructLiteral();
        BLangModelBuilder.NameReference sqlTypeNameReference = new BLangModelBuilder.NameReference(null, "sqlType");
        modelBuilder.validateAndSetPackagePath(null, sqlTypeNameReference);
        modelBuilder.createVarRefExpr(null, null, sqlTypeNameReference);
        modelBuilder.createStringLiteral(null, null, param.getSqlType());
        modelBuilder.addMapStructKeyValue(null, null);

        BLangModelBuilder.NameReference valueNameReference = new BLangModelBuilder.NameReference(null, "value");
        modelBuilder.validateAndSetPackagePath(null, valueNameReference);
        modelBuilder.createVarRefExpr(null, null, valueNameReference);
        if (!isPathParam) {
            BLangModelBuilder.NameReference wrapperParamNameReference = new BLangModelBuilder.NameReference(null, path);
            modelBuilder.validateAndSetPackagePath(null, wrapperParamNameReference);
            BLangModelBuilder.NameReference variableParamNameReference = new BLangModelBuilder.NameReference(null,
                    paramName);
            modelBuilder.validateAndSetPackagePath(null, variableParamNameReference);

            modelBuilder.createVarRefExpr(null, null, wrapperParamNameReference);
            modelBuilder.createVarRefExpr(null, null, variableParamNameReference);
            modelBuilder.createFieldRefExpr(null, null);

            // Casting variable
            modelBuilder.createTypeCastExpr(null, null, new SimpleTypeName("int"));
        } else {
            BLangModelBuilder.NameReference variableParamNameReference = new BLangModelBuilder.NameReference(null,
                    paramName);
            modelBuilder.validateAndSetPackagePath(null, variableParamNameReference);
            modelBuilder.createVarRefExpr(null, null, variableParamNameReference);

        }
        modelBuilder.addMapStructKeyValue(null, null);

        BLangModelBuilder.NameReference directionNameReference = new BLangModelBuilder.NameReference(null, "direction");
        modelBuilder.validateAndSetPackagePath(null, directionNameReference);
        modelBuilder.createVarRefExpr(null, null, directionNameReference);
        modelBuilder.createIntegerLiteral(null, null, getDirection(param));
        modelBuilder.addMapStructKeyValue(null, null);

        modelBuilder.createMapStructLiteral(null, null);

        BLangModelBuilder.NameReference sqlParameterNameReference = new BLangModelBuilder.NameReference("sql",
                "Parameter");
        modelBuilder.validateAndSetPackagePath(null, sqlParameterNameReference);
        SimpleTypeName sqlParameterSimpleTypeName = new SimpleTypeName(sqlParameterNameReference.getName(),
                sqlParameterNameReference.getPackageName(), sqlParameterNameReference.getPackagePath());

        modelBuilder.addVariableDefinitionStmt(null, null, sqlParameterSimpleTypeName, paramName, true);
    }

    private static void initializeParameterArray(BLangModelBuilder modelBuilder, Query query) {
        if (query.getParamMap() != null) {
            modelBuilder.startExprList();
            for (Param param : query.getParamMap().values()) {
                BLangModelBuilder.NameReference paraNameReference = new BLangModelBuilder.NameReference(null,
                        param.getName());
                modelBuilder.validateAndSetPackagePath(null, paraNameReference);
                modelBuilder.createVarRefExpr(null, null, paraNameReference);
            }
            modelBuilder.endExprList(query.getParamMap().values().size());
            modelBuilder.createArrayInitExpr(null, null, true);
        } else {
            modelBuilder.createArrayInitExpr(null, null, false);

        }

        BLangModelBuilder.NameReference sqlParameterNameReference = new BLangModelBuilder.NameReference("sql",
                "Parameter");
        modelBuilder.validateAndSetPackagePath(null, sqlParameterNameReference);

        SimpleTypeName sqlParameterArraySimpleTypeName = new SimpleTypeName(sqlParameterNameReference.getName(),
                sqlParameterNameReference.getPackageName(), sqlParameterNameReference.getPackagePath());
        sqlParameterArraySimpleTypeName.setArrayType(1);
        //        sqlParameterArraySimpleTypeName.setPkgPath(sqlParameterNameReference.getPackagePath());
        modelBuilder
                .addVariableDefinitionStmt(null, null, sqlParameterArraySimpleTypeName, query.getQueryId() + "Params",
                        true);
    }

    private static void convertMessageToJson(BLangModelBuilder modelBuilder, String inputVariable,
            String outputVariable) {
        modelBuilder.startExprList();
        BLangModelBuilder.NameReference tempMNameReference = new BLangModelBuilder.NameReference(null, inputVariable);
        modelBuilder.validateAndSetPackagePath(null, tempMNameReference);
        modelBuilder.createVarRefExpr(null, null, tempMNameReference);
        modelBuilder.endExprList(1);

        BLangModelBuilder.NameReference getJsonPayloadNameReference = new BLangModelBuilder.NameReference("messages",
                "getJsonPayload");
        modelBuilder.validateAndSetPackagePath(null, getJsonPayloadNameReference);
        modelBuilder.addFunctionInvocationExpr(null, null, getJsonPayloadNameReference, true);
        modelBuilder.addVariableDefinitionStmt(null, null, new SimpleTypeName("json"), outputVariable, true);
    }

    private static List<String> getPathParams(String path) {
        Matcher matcher = pattern.matcher(path);
        List<String> pathParams = new ArrayList<>();
        while (matcher.find()) {
            pathParams.add(removeBracesFromPathParam(matcher.group()));
        }
        return pathParams;
    }

    private static boolean isPathParamContained(String path) {
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

    private static String removeBracesFromPathParam(String pathParam) {
        return pathParam.substring(1, pathParam.length() - 1);
    }

    private static void convertDatatableToJson(BLangModelBuilder modelBuilder, String datatableVariableName,
            String jsonVariableName) {
        modelBuilder.startExprList();
        BLangModelBuilder.NameReference datatableVariableNameReference = new BLangModelBuilder.NameReference(null,
                datatableVariableName);
        modelBuilder.validateAndSetPackagePath(null, datatableVariableNameReference);
        modelBuilder.createVarRefExpr(null, null, datatableVariableNameReference);
        modelBuilder.endExprList(1);
        BLangModelBuilder.NameReference toJsonNameReference = new BLangModelBuilder.NameReference("datatables",
                "toJson");
        modelBuilder.validateAndSetPackagePath(null, toJsonNameReference);
        modelBuilder.addFunctionInvocationExpr(null, null, toJsonNameReference, true);
        modelBuilder.addVariableDefinitionStmt(null, null, new SimpleTypeName("json"), jsonVariableName, true);
    }

/*    private static void convertDatatableToXML(BLangModelBuilder modelBuilder, String datatableVariableName,
            String xmlVariableName) {
        modelBuilder.startExprList();
        BLangModelBuilder.NameReference datatableVariableNameReference = new BLangModelBuilder.NameReference(null,
                datatableVariableName);
        modelBuilder.validateAndSetPackagePath(null, datatableVariableNameReference);
        modelBuilder.createVarRefExpr(null, null, datatableVariableNameReference);
        modelBuilder.endExprList(1);
        BLangModelBuilder.NameReference toJsonNameReference = new BLangModelBuilder.NameReference("datatables",
                "toXml");
        modelBuilder.validateAndSetPackagePath(null, toJsonNameReference);
        modelBuilder.addFunctionInvocationExpr(null, null, toJsonNameReference, true);
        modelBuilder.addVariableDefinitionStmt(null, null, new SimpleTypeName("xml"), xmlVariableName, true);
    }*/

    private static String getDirection(Param param) {
        if ("IN".equals(param.getType())) {
            return "0";
        } else if ("OUT".equals(param.getType())) {
            return "1";
        } else {
            return "2";
        }
    }

    private static void initializeMessage(BLangModelBuilder modelBuilder, String messageVariableName) {
        modelBuilder.startMapStructLiteral();
        modelBuilder.createMapStructLiteral(null, null);
        modelBuilder.addVariableDefinitionStmt(null, null, new SimpleTypeName("message"), messageVariableName, true);
    }

    private static void replyMessage(BLangModelBuilder modelBuilder, String messageVariableName) {
        BLangModelBuilder.NameReference responseNameReference = new BLangModelBuilder.NameReference(null,
                messageVariableName);
        modelBuilder.validateAndSetPackagePath(null, responseNameReference);
        modelBuilder.createVarRefExpr(null, null, responseNameReference);
        modelBuilder.createVarRefExpr(null, null, responseNameReference);
        modelBuilder.createReplyStmt(null, null);
    }

    private static void invokeSQLClientConnector(BLangModelBuilder modelBuilder, Query query,
            String outputDatatableVariableName) {
        modelBuilder.startExprList();
        BLangModelBuilder.NameReference sqlClientConnectorVariableNameReference = new BLangModelBuilder.NameReference(
                null, query.getConfigId());
        modelBuilder.validateAndSetPackagePath(null, sqlClientConnectorVariableNameReference);
        modelBuilder.createVarRefExpr(null, null, sqlClientConnectorVariableNameReference);
        modelBuilder.createStringLiteral(null, null, query.getSqlQuery());
        BLangModelBuilder.NameReference paramsNameReference = new BLangModelBuilder.NameReference(null,
                query.getQueryId() + "Params");
        modelBuilder.validateAndSetPackagePath(null, paramsNameReference);
        modelBuilder.createVarRefExpr(null, null, paramsNameReference);

        modelBuilder.endExprList(3);
        BLangModelBuilder.NameReference sqlClientConnectorNameReference = new BLangModelBuilder.NameReference("sql",
                "ClientConnector");
        modelBuilder.validateAndSetPackagePath(null, sqlClientConnectorNameReference);
        modelBuilder.addActionInvocationExpr(null, null, sqlClientConnectorNameReference, "select", true);
        modelBuilder.addVariableDefinitionStmt(null, null, new SimpleTypeName("datatable"), outputDatatableVariableName,
                true);
    }

    private static void setJsonPayloadToMessage(BLangModelBuilder modelBuilder, String messageVariableName,
            String jsonPayloadVariableName) {
        modelBuilder.startExprList();
        BLangModelBuilder.NameReference messageVariableNameReference = new BLangModelBuilder.NameReference(null,
                messageVariableName);
        modelBuilder.validateAndSetPackagePath(null, messageVariableNameReference);
        modelBuilder.createVarRefExpr(null, null, messageVariableNameReference);
        BLangModelBuilder.NameReference jsonPayloadVariableNameReference = new BLangModelBuilder.NameReference(null,
                jsonPayloadVariableName);
        modelBuilder.validateAndSetPackagePath(null, jsonPayloadVariableNameReference);
        modelBuilder.createVarRefExpr(null, null, jsonPayloadVariableNameReference);
        modelBuilder.endExprList(2);
        BLangModelBuilder.NameReference setJsonPayloadNameReference = new BLangModelBuilder.NameReference("messages",
                "setJsonPayload");
        modelBuilder.validateAndSetPackagePath(null, setJsonPayloadNameReference);
        modelBuilder.createFunctionInvocationStmt(null, null, setJsonPayloadNameReference, true);
    }

    private static Map<String, String> convertConfigPropertyMapToSupportHikari(Map<String, String> configMap) {
        Map<String, String> newConfigMap = new HashMap<>();
        for (Map.Entry<String, String> property : configMap.entrySet()) {
            String key = property.getKey();
            String value = property.getValue();
            if (Constants.TOMCAT_JDBC_TO_HIKARI.containsKey(key)) {
                key = Constants.TOMCAT_JDBC_TO_HIKARI.get(key);
                newConfigMap.put(key, value);
            } else {
                newConfigMap.put(key, value);
            }
        }
        return newConfigMap;
    }


 /*   private static String convertSQLDataTypeToBalDataType(Param param) {

    }*/
}
