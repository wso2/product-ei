/*
 *     Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.wso2.ei.tools.converter.common.generator;

import org.ballerinalang.bre.ConnectorVarLocation;
import org.ballerinalang.bre.ConstantLocation;
import org.ballerinalang.bre.GlobalVarLocation;
import org.ballerinalang.bre.ServiceVarLocation;
import org.ballerinalang.bre.StackVarLocation;
import org.ballerinalang.bre.StructVarLocation;
import org.ballerinalang.bre.WorkerVarLocation;
import org.ballerinalang.model.AnnotationAttachment;
import org.ballerinalang.model.AnnotationAttributeDef;
import org.ballerinalang.model.AnnotationDef;
import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.BTypeMapper;
import org.ballerinalang.model.BallerinaAction;
import org.ballerinalang.model.BallerinaConnectorDef;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.BallerinaFunction;
import org.ballerinalang.model.CompilationUnit;
import org.ballerinalang.model.ConstDef;
import org.ballerinalang.model.GlobalVariableDef;
import org.ballerinalang.model.ImportPackage;
import org.ballerinalang.model.NodeVisitor;
import org.ballerinalang.model.ParameterDef;
import org.ballerinalang.model.Resource;
import org.ballerinalang.model.Service;
import org.ballerinalang.model.StructDef;
import org.ballerinalang.model.VariableDef;
import org.ballerinalang.model.Worker;
import org.ballerinalang.model.expressions.ActionInvocationExpr;
import org.ballerinalang.model.expressions.AddExpression;
import org.ballerinalang.model.expressions.AndExpression;
import org.ballerinalang.model.expressions.ArrayInitExpr;
import org.ballerinalang.model.expressions.ArrayMapAccessExpr;
import org.ballerinalang.model.expressions.BacktickExpr;
import org.ballerinalang.model.expressions.BasicLiteral;
import org.ballerinalang.model.expressions.ConnectorInitExpr;
import org.ballerinalang.model.expressions.DivideExpr;
import org.ballerinalang.model.expressions.EqualExpression;
import org.ballerinalang.model.expressions.Expression;
import org.ballerinalang.model.expressions.FieldAccessExpr;
import org.ballerinalang.model.expressions.FunctionInvocationExpr;
import org.ballerinalang.model.expressions.GreaterEqualExpression;
import org.ballerinalang.model.expressions.GreaterThanExpression;
import org.ballerinalang.model.expressions.InstanceCreationExpr;
import org.ballerinalang.model.expressions.JSONArrayInitExpr;
import org.ballerinalang.model.expressions.JSONFieldAccessExpr;
import org.ballerinalang.model.expressions.JSONInitExpr;
import org.ballerinalang.model.expressions.KeyValueExpr;
import org.ballerinalang.model.expressions.LessEqualExpression;
import org.ballerinalang.model.expressions.LessThanExpression;
import org.ballerinalang.model.expressions.MapInitExpr;
import org.ballerinalang.model.expressions.ModExpression;
import org.ballerinalang.model.expressions.MultExpression;
import org.ballerinalang.model.expressions.NotEqualExpression;
import org.ballerinalang.model.expressions.NullLiteral;
import org.ballerinalang.model.expressions.OrExpression;
import org.ballerinalang.model.expressions.RefTypeInitExpr;
import org.ballerinalang.model.expressions.ResourceInvocationExpr;
import org.ballerinalang.model.expressions.StructInitExpr;
import org.ballerinalang.model.expressions.SubtractExpression;
import org.ballerinalang.model.expressions.TypeCastExpression;
import org.ballerinalang.model.expressions.TypeConversionExpr;
import org.ballerinalang.model.expressions.UnaryExpression;
import org.ballerinalang.model.expressions.VariableRefExpr;
import org.ballerinalang.model.invokers.MainInvoker;
import org.ballerinalang.model.statements.AbortStmt;
import org.ballerinalang.model.statements.ActionInvocationStmt;
import org.ballerinalang.model.statements.AssignStmt;
import org.ballerinalang.model.statements.BlockStmt;
import org.ballerinalang.model.statements.BreakStmt;
import org.ballerinalang.model.statements.CommentStmt;
import org.ballerinalang.model.statements.ForkJoinStmt;
import org.ballerinalang.model.statements.FunctionInvocationStmt;
import org.ballerinalang.model.statements.IfElseStmt;
import org.ballerinalang.model.statements.ReplyStmt;
import org.ballerinalang.model.statements.ReturnStmt;
import org.ballerinalang.model.statements.Statement;
import org.ballerinalang.model.statements.ThrowStmt;
import org.ballerinalang.model.statements.TransactionRollbackStmt;
import org.ballerinalang.model.statements.TransformStmt;
import org.ballerinalang.model.statements.TryCatchStmt;
import org.ballerinalang.model.statements.VariableDefStmt;
import org.ballerinalang.model.statements.WhileStmt;
import org.ballerinalang.model.statements.WorkerInvocationStmt;
import org.ballerinalang.model.statements.WorkerReplyStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @{@link CodeGenVisitor} implements @{@link NodeVisitor} to traverse through Ballerina model of the integration flow
 * and serialize to ballerina source
 */
public class CodeGenVisitor implements NodeVisitor {

    //private static Log log = LogFactory.getLog(CodeGenVisitor.class);
    private static Logger logger = LoggerFactory.getLogger(CodeGenVisitor.class);
    private String ballerinaSourceStr = "";
    private int indentDepth = 0;
    private int previousIndentDepth = 0;
    private String indentStr = "";

    @Override
    public void visit(BLangProgram bLangProgram) {

    }

    @Override
    public void visit(BLangPackage bLangPackage) {

    }

    @Override
    public void visit(BallerinaFile bFile) {
        //get Import packages
        //TODO need to decide to get import packages from BLangProgram or from BallerinaFile
        ImportPackage[] importPackages = bFile.getImportPackages();
        for (ImportPackage importPackage : importPackages) {
            //no need to consider indentation due to imports happens at the beginning of the file
            appendToBalSource(Constants.IMPORT_STR + Constants.SPACE_STR + importPackage.getSymbolName().getName()
                    + Constants.STMTEND_STR + Constants.NEWLINE_STR);
        }

        //TODO : Assume for only one service temporarily
        CompilationUnit[] compilationUnits = bFile.getCompilationUnits();
        for (CompilationUnit compilationUnit : compilationUnits) {
            compilationUnit.accept(this);
        }

    }

    @Override
    public void visit(ImportPackage importPkg) {

    }

    @Override
    public void visit(ConstDef constant) {

    }

    @Override
    public void visit(GlobalVariableDef globalVar) {

    }

    @Override
    public void visit(Service service) {

        //Visit annotationAttachment
        AnnotationAttachment[] annotationAttachments = service.getAnnotations();
        for (AnnotationAttachment annotationAttachment : annotationAttachments) {
            annotationAttachment.accept(this);
        }

        /**
         serviceDefinition : 'service' Identifier serviceBody;
         * */
        appendToBalSource(getIndentationForCurrentLine() + Constants.SERVICE_STR + Constants.SPACE_STR +
                service.getName() + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
        ++indentDepth;

        /**
         * serviceBody: '{' variableDefinitionStatement* resourceDefinition* '}';
         * resourceDefinition: annotationAttachment* 'resource' Identifier '(' parameterList ')' callableUnitBody;
         * annotationAttachment: '@' nameReference '{' annotationAttributeList? '}'
         */
        //TODO:Visit variable definition statements

        //Visit Resource definitions
        Resource[] resources = service.getResources();
        for (Resource resource : resources) {
            resource.accept(this);
        }

        //Service visit completed
        appendToBalSource(Constants.STMTBLOCK_END_STR);
        --indentDepth;

    }

    @Override
    public void visit(BallerinaConnectorDef connector) {

    }

    @Override
    public void visit(Resource resource) {

        /**
         * resourceDefinition : annotationAttachment* 'resource' Identifier '(' parameterList ')' callableUnitBody;
         * parameterList : parameter (',' parameter)*;
         * parameter : annotationAttachment* typeName Identifier;
         */

        //visit annotations
        AnnotationAttachment[] annotationAttachments = resource.getAnnotations();
        for (AnnotationAttachment annotationAttachment : annotationAttachments) {
            annotationAttachment.accept(this);
        }

        appendToBalSource(getIndentationForCurrentLine() + Constants.RESOURCE_STR + Constants.SPACE_STR +
                resource.getSymbolName() + Constants.SPACE_STR + Constants.PARENTHESES_START_STR);

        ParameterDef[] parameterDefs = resource.getParameterDefs();
        for (ParameterDef parameterDef : parameterDefs) {
            parameterDef.accept(this);
            //TODO Handle multiple parameters adding commas
        }

        //end of parameters
        appendToBalSource(Constants.PARENTHESES_END_STR + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR
                + Constants.NEWLINE_STR);
        //process resource block
        /**
         * callableUnitBody : '{' statement* workerDeclaration* '}';
         * statement : variableDefinitionStatement | assignmentStatement | ifElseStatement | iterateStatement |
         *          whileStatement | continueStatement| breakStatement| forkJoinStatement| tryCatchStatement|
         *          throwStatement| returnStatement| replyStatement| workerInteractionStatement| commentStatement|
         *          actionInvocationStatement| functionInvocationStatement| transformStatement| transactionStatement|
         *          abortStatement;
         * variableDefinitionStatement : typeName Identifier ('=' (connectorInitExpression | actionInvocation |
         *                                                                                          expression) )? ';';
         */
        ++indentDepth;
        BlockStmt blockStmt = resource.getCallableUnitBody();
        Statement[] statements = blockStmt.getStatements();
        for (Statement statement : statements) {
            statement.accept(this);
        }
        --indentDepth;
        //end of resource statements block
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(BallerinaFunction function) {

    }

    @Override
    public void visit(BTypeMapper typeMapper) {

    }

    @Override
    public void visit(BallerinaAction action) {

    }

    @Override
    public void visit(Worker worker) {

    }

    @Override
    public void visit(AnnotationAttachment annotation) {
        appendToBalSource(getIndentationForCurrentLine() + annotation.toString() + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ParameterDef parameterDef) {

        //TODO handle annotations for parameter

        appendToBalSource(parameterDef.getTypeName() + Constants.SPACE_STR + parameterDef.getSymbolName());
    }

    @Override
    public void visit(VariableDef variableDef) {

    }

    @Override
    public void visit(StructDef structDef) {

    }

    @Override
    public void visit(AnnotationAttributeDef annotationAttributeDef) {

    }

    @Override
    public void visit(AnnotationDef annotationDef) {

    }

    @Override
    public void visit(VariableDefStmt varDefStmt) {

        /**
         * variableDefinitionStatement : typeName Identifier ('=' (connectorInitExpression | actionInvocation |
         *                                                                                      expression) )? ';';
         */

        VariableDef variableDef = varDefStmt.getVariableDef();
        String varDefStr = variableDef.getTypeName().getName() + Constants.SPACE_STR + variableDef.getSymbolName()
                + Constants.SPACE_STR + Constants.EQUAL_STR + Constants.SPACE_STR;

        Expression rhsExpr = varDefStmt.getRExpr();

        if (rhsExpr instanceof RefTypeInitExpr) {
            RefTypeInitExpr refTypeInitExpr = (RefTypeInitExpr) rhsExpr;
            Expression[] argExpressions = refTypeInitExpr.getArgExprs();

            if (argExpressions.length > 0) {
                //TODO handle args
            } else {
                //TODO : for now assume "{}"
                varDefStr += "{}";
            }
        }

        appendToBalSource(getIndentationForCurrentLine() + varDefStr + Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(AssignStmt assignStmt) {

    }

    @Override
    public void visit(BlockStmt blockStmt) {

    }

    @Override
    public void visit(CommentStmt commentStmt) {

    }

    @Override
    public void visit(IfElseStmt ifElseStmt) {

    }

    @Override
    public void visit(ReplyStmt replyStmt) {
        /**
         * replyStatement : 'reply' expression ';';
         */
        appendToBalSource(getIndentationForCurrentLine() + Constants.REPLY_STR + Constants.SPACE_STR);

        Expression replyExpression = replyStmt.getReplyExpr();
        if (replyExpression instanceof VariableRefExpr) {
            appendToBalSource(((VariableRefExpr) replyExpression).getSymbolName().toString());
        }

        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ReturnStmt returnStmt) {

    }

    @Override
    public void visit(WhileStmt whileStmt) {

    }

    @Override
    public void visit(BreakStmt breakStmt) {

    }

    @Override
    public void visit(TryCatchStmt tryCatchStmt) {

    }

    @Override
    public void visit(ThrowStmt throwStmt) {

    }

    @Override
    public void visit(FunctionInvocationStmt functionInvocationStmt) {

    }

    @Override
    public void visit(ActionInvocationStmt actionInvocationStmt) {

    }

    @Override
    public void visit(WorkerInvocationStmt workerInvocationStmt) {

    }

    @Override
    public void visit(WorkerReplyStmt workerReplyStmt) {

    }

    @Override
    public void visit(ForkJoinStmt forkJoinStmt) {

    }

    @Override
    public void visit(TransformStmt transformStmt) {

    }

    @Override
    public void visit(TransactionRollbackStmt transactionRollbackStmt) {

    }

    @Override
    public void visit(AbortStmt abortStmt) {

    }

    @Override
    public void visit(AddExpression addExpr) {

    }

    @Override
    public void visit(AndExpression andExpression) {

    }

    @Override
    public void visit(BasicLiteral basicLiteral) {

    }

    @Override
    public void visit(DivideExpr divideExpr) {

    }

    @Override
    public void visit(ModExpression modExpression) {

    }

    @Override
    public void visit(EqualExpression equalExpression) {

    }

    @Override
    public void visit(FunctionInvocationExpr functionInvocationExpr) {

    }

    @Override
    public void visit(ActionInvocationExpr actionInvocationExpr) {

    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) {

    }

    @Override
    public void visit(GreaterThanExpression greaterThanExpression) {

    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) {

    }

    @Override
    public void visit(LessThanExpression lessThanExpression) {

    }

    @Override
    public void visit(MultExpression multExpression) {

    }

    @Override
    public void visit(InstanceCreationExpr instanceCreationExpr) {

    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) {

    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(SubtractExpression subtractExpression) {

    }

    @Override
    public void visit(UnaryExpression unaryExpression) {

    }

    @Override
    public void visit(TypeCastExpression typeCastExpression) {

    }

    @Override
    public void visit(TypeConversionExpr typeConversionExpr) {

    }

    @Override
    public void visit(ArrayMapAccessExpr arrayMapAccessExpr) {

    }

    @Override
    public void visit(FieldAccessExpr structAttributeAccessExpr) {

    }

    @Override
    public void visit(JSONFieldAccessExpr jsonPathExpr) {

    }

    @Override
    public void visit(BacktickExpr backtickExpr) {

    }

    @Override
    public void visit(ArrayInitExpr arrayInitExpr) {

    }

    @Override
    public void visit(RefTypeInitExpr refTypeInitExpr) {

    }

    @Override
    public void visit(ConnectorInitExpr connectorInitExpr) {

    }

    @Override
    public void visit(StructInitExpr structInitExpr) {

    }

    @Override
    public void visit(MapInitExpr mapInitExpr) {

    }

    @Override
    public void visit(JSONInitExpr jsonInitExpr) {

    }

    @Override
    public void visit(JSONArrayInitExpr jsonArrayInitExpr) {

    }

    @Override
    public void visit(KeyValueExpr keyValueExpr) {

    }

    @Override
    public void visit(VariableRefExpr variableRefExpr) {

    }

    @Override
    public void visit(NullLiteral nullLiteral) {

    }

    @Override
    public void visit(StackVarLocation stackVarLocation) {

    }

    @Override
    public void visit(ServiceVarLocation serviceVarLocation) {

    }

    @Override
    public void visit(GlobalVarLocation globalVarLocation) {

    }

    @Override
    public void visit(ConnectorVarLocation connectorVarLocation) {

    }

    @Override
    public void visit(ConstantLocation constantLocation) {

    }

    @Override
    public void visit(StructVarLocation structVarLocation) {

    }

    @Override
    public void visit(ResourceInvocationExpr resourceIExpr) {

    }

    @Override
    public void visit(MainInvoker mainInvoker) {

    }

    @Override
    public void visit(WorkerVarLocation workerVarLocation) {

    }

    public String getBallerinaSourceStr() {
        return ballerinaSourceStr;
    }

    private void appendToBalSource(String str) {
        ballerinaSourceStr += str;
    }

    private String getIndentationForCurrentLine() {

        if (previousIndentDepth != indentDepth) {
            String indentation = "";
            for (int i = 0; i < indentDepth; i++) {
                indentation = indentation.concat(Constants.TAB_STR);
            }
            previousIndentDepth = indentDepth;
            indentStr = indentation;
        }
        return indentStr;
    }
}
