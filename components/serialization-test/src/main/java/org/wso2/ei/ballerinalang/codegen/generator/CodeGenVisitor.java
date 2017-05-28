/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.ballerinalang.codegen.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import java.util.Stack;

public class CodeGenVisitor implements NodeVisitor{

    private static Log log = LogFactory.getLog(CodeGenVisitor.class);
    private String ballerinaSourceStr = "";

    //Stack to hold code block ends
    //private Stack<String> codeBlockStack = new Stack<>();
    
    private void logExecMethod () {
        StackTraceElement STElements[] = Thread.currentThread().getStackTrace();
        System.out.println(STElements[3].getMethodName());
    }

    @Override
    public void visit(BLangProgram bLangProgram) {
        logExecMethod ();
    }

    @Override
    public void visit(BLangPackage bLangPackage) {
		logExecMethod ();
    }

    @Override
    public void visit(BallerinaFile bFile) {
		logExecMethod ();
    }

    @Override
    public void visit(ImportPackage importPkg) {
		logExecMethod ();
    }

    @Override
    public void visit(ConstDef constant) {
		logExecMethod ();
    }

    @Override
    public void visit(GlobalVariableDef globalVar) {
		logExecMethod ();
    }

    @Override
    public void visit(Service service) {
        logExecMethod ();

        //Visit annotationAttachment
        AnnotationAttachment[] annotationAttachments = service.getAnnotations();
        for (AnnotationAttachment annotationAttachment : annotationAttachments) {
            ballerinaSourceStr += annotationAttachment.toString() + Constants.NEWLINE_STR;
        }

        /**
            serviceDefinition : 'service' Identifier serviceBody;
        * */

        ballerinaSourceStr += Constants.SERVICE_STR + " " + service.getName() + " " + Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR;

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
        ballerinaSourceStr += Constants.STMTBLOCK_END_STR;
        System.out.println("===================================");
        System.out.println(ballerinaSourceStr);
        System.out.println("===================================");

    }

    @Override
    public void visit(BallerinaConnectorDef connector) {
		logExecMethod ();
    }

    @Override
    public void visit(Resource resource) {
		logExecMethod ();

        /**
         * resourceDefinition : annotationAttachment* 'resource' Identifier '(' parameterList ')' callableUnitBody;
         * parameterList : parameter (',' parameter)*;
         * parameter : annotationAttachment* typeName Identifier;
         */

        //visit annotations
        AnnotationAttachment[] annotationAttachments = resource.getAnnotations();
        for (AnnotationAttachment annotationAttachment : annotationAttachments) {
            ballerinaSourceStr += annotationAttachment.toString() + Constants.NEWLINE_STR;
        }

        ballerinaSourceStr += Constants.RESOURCE_STR + Constants.SPACE_STR + resource.getSymbolName() +
                                                                Constants.SPACE_STR + Constants.PARENTHESES_START_STR;

        ParameterDef[] parameterDefs = resource.getParameterDefs();
        for (ParameterDef parameterDef : parameterDefs) {
            parameterDef.accept(this);
            //TODO Handle multiple parameters adding commas
        }

        //end of parameters
        ballerinaSourceStr += Constants.PARENTHESES_END_STR + Constants.SPACE_STR +
                                                                Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR;

        //process resource block
        /**
         * callableUnitBody : '{' statement* workerDeclaration* '}';
         * statement : variableDefinitionStatement | assignmentStatement | ifElseStatement | iterateStatement |
         *          whileStatement | continueStatement| breakStatement| forkJoinStatement| tryCatchStatement|
         *          throwStatement| returnStatement| replyStatement| workerInteractionStatement| commentStatement|
         *          actionInvocationStatement| functionInvocationStatement| transformStatement| transactionStatement|
         *          abortStatement;
         * variableDefinitionStatement : typeName Identifier ('=' (connectorInitExpression | actionInvocation | expression) )? ';';
         */
        BlockStmt blockStmt = resource.getCallableUnitBody();

        Statement[] statements = blockStmt.getStatements();
        for (Statement statement : statements) {
            statement.accept(this);
        }

        //end of resource statements block
        ballerinaSourceStr += Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR;

    }

    @Override
    public void visit(BallerinaFunction function) {
		logExecMethod ();
    }

    @Override
    public void visit(BTypeMapper typeMapper) {
		logExecMethod ();
    }

    @Override
    public void visit(BallerinaAction action) {
		logExecMethod ();
    }

    @Override
    public void visit(Worker worker) {
		logExecMethod ();
    }

    @Override
    public void visit(AnnotationAttachment annotation) {
		logExecMethod ();
    }

    @Override
    public void visit(ParameterDef parameterDef) {
		logExecMethod ();

        //TODO handle annotations for parameter

        ballerinaSourceStr += parameterDef.getTypeName() + Constants.SPACE_STR + parameterDef.getSymbolName();
    }

    @Override
    public void visit(VariableDef variableDef) {
		logExecMethod ();
    }

    @Override
    public void visit(StructDef structDef) {
		logExecMethod ();
    }

    @Override
    public void visit(AnnotationAttributeDef annotationAttributeDef) {
		logExecMethod ();
    }

    @Override
    public void visit(AnnotationDef annotationDef) {
		logExecMethod ();
    }

    @Override
    public void visit(VariableDefStmt varDefStmt) {
		logExecMethod ();

        /**
         * variableDefinitionStatement : typeName Identifier ('=' (connectorInitExpression | actionInvocation | expression) )? ';';
         */

        VariableDef variableDef = varDefStmt.getVariableDef();
        String varDefStr = variableDef.getTypeName().getName() + Constants.SPACE_STR + variableDef.getSymbolName() +
                                                        Constants.SPACE_STR + Constants.EQUAL_STR + Constants.SPACE_STR;

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

        ballerinaSourceStr += varDefStr + Constants.STMTEND_STR + Constants.NEWLINE_STR;
    }

    @Override
    public void visit(AssignStmt assignStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(BlockStmt blockStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(CommentStmt commentStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(IfElseStmt ifElseStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(ReplyStmt replyStmt) {
		logExecMethod ();

        /**
         * replyStatement : 'reply' expression ';';
         */
        ballerinaSourceStr += Constants.REPLY_STR + Constants.SPACE_STR;

        Expression replyExpression = replyStmt.getReplyExpr();
        if (replyExpression instanceof VariableRefExpr) {
            ballerinaSourceStr += ((VariableRefExpr)replyExpression).getSymbolName();
        }

        ballerinaSourceStr += Constants.STMTEND_STR + Constants.NEWLINE_STR;
    }

    @Override
    public void visit(ReturnStmt returnStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(WhileStmt whileStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(BreakStmt breakStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(TryCatchStmt tryCatchStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(ThrowStmt throwStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(FunctionInvocationStmt functionInvocationStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(ActionInvocationStmt actionInvocationStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(WorkerInvocationStmt workerInvocationStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(WorkerReplyStmt workerReplyStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(ForkJoinStmt forkJoinStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(TransformStmt transformStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(TransactionRollbackStmt transactionRollbackStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(AbortStmt abortStmt) {
		logExecMethod ();
    }

    @Override
    public void visit(AddExpression addExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(AndExpression andExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(BasicLiteral basicLiteral) {
		logExecMethod ();
    }

    @Override
    public void visit(DivideExpr divideExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(ModExpression modExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(EqualExpression equalExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(FunctionInvocationExpr functionInvocationExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(ActionInvocationExpr actionInvocationExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(GreaterEqualExpression greaterEqualExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(GreaterThanExpression greaterThanExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(LessEqualExpression lessEqualExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(LessThanExpression lessThanExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(MultExpression multExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(InstanceCreationExpr instanceCreationExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(NotEqualExpression notEqualExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(OrExpression orExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(SubtractExpression subtractExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(TypeCastExpression typeCastExpression) {
		logExecMethod ();
    }

    @Override
    public void visit(ArrayMapAccessExpr arrayMapAccessExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(FieldAccessExpr structAttributeAccessExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(JSONFieldAccessExpr jsonPathExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(BacktickExpr backtickExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(ArrayInitExpr arrayInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(RefTypeInitExpr refTypeInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(ConnectorInitExpr connectorInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(StructInitExpr structInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(MapInitExpr mapInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(JSONInitExpr jsonInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(JSONArrayInitExpr jsonArrayInitExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(KeyValueExpr keyValueExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(VariableRefExpr variableRefExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(NullLiteral nullLiteral) {
		logExecMethod ();
    }

    @Override
    public void visit(StackVarLocation stackVarLocation) {
		logExecMethod ();
    }

    @Override
    public void visit(ServiceVarLocation serviceVarLocation) {
		logExecMethod ();
    }

    @Override
    public void visit(GlobalVarLocation globalVarLocation) {
		logExecMethod ();
    }

    @Override
    public void visit(ConnectorVarLocation connectorVarLocation) {
		logExecMethod ();
    }

    @Override
    public void visit(ConstantLocation constantLocation) {
		logExecMethod ();
    }

    @Override
    public void visit(StructVarLocation structVarLocation) {
		logExecMethod ();
    }

    @Override
    public void visit(ResourceInvocationExpr resourceIExpr) {
		logExecMethod ();
    }

    @Override
    public void visit(MainInvoker mainInvoker) {
		logExecMethod ();
    }

    @Override
    public void visit(WorkerVarLocation workerVarLocation) {
		logExecMethod ();
    }
}
