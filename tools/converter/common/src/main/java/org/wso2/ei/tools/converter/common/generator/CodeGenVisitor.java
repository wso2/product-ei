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
import org.ballerinalang.model.Function;
import org.ballerinalang.model.GlobalVariableDef;
import org.ballerinalang.model.ImportPackage;
import org.ballerinalang.model.NamespaceDeclaration;
import org.ballerinalang.model.NodeVisitor;
import org.ballerinalang.model.ParameterDef;
import org.ballerinalang.model.Resource;
import org.ballerinalang.model.Service;
import org.ballerinalang.model.SimpleVariableDef;
import org.ballerinalang.model.StructDef;
import org.ballerinalang.model.VariableDef;
import org.ballerinalang.model.Worker;
import org.ballerinalang.model.expressions.ActionInvocationExpr;
import org.ballerinalang.model.expressions.AddExpression;
import org.ballerinalang.model.expressions.AndExpression;
import org.ballerinalang.model.expressions.ArrayInitExpr;
import org.ballerinalang.model.expressions.BasicLiteral;
import org.ballerinalang.model.expressions.ConnectorInitExpr;
import org.ballerinalang.model.expressions.DivideExpr;
import org.ballerinalang.model.expressions.EqualExpression;
import org.ballerinalang.model.expressions.Expression;
import org.ballerinalang.model.expressions.FunctionInvocationExpr;
import org.ballerinalang.model.expressions.GreaterEqualExpression;
import org.ballerinalang.model.expressions.GreaterThanExpression;
import org.ballerinalang.model.expressions.InstanceCreationExpr;
import org.ballerinalang.model.expressions.JSONArrayInitExpr;
import org.ballerinalang.model.expressions.JSONInitExpr;
import org.ballerinalang.model.expressions.KeyValueExpr;
import org.ballerinalang.model.expressions.LambdaExpression;
import org.ballerinalang.model.expressions.LessEqualExpression;
import org.ballerinalang.model.expressions.LessThanExpression;
import org.ballerinalang.model.expressions.MapInitExpr;
import org.ballerinalang.model.expressions.ModExpression;
import org.ballerinalang.model.expressions.MultExpression;
import org.ballerinalang.model.expressions.NotEqualExpression;
import org.ballerinalang.model.expressions.NullLiteral;
import org.ballerinalang.model.expressions.OrExpression;
import org.ballerinalang.model.expressions.RefTypeInitExpr;
import org.ballerinalang.model.expressions.StringTemplateLiteral;
import org.ballerinalang.model.expressions.StructInitExpr;
import org.ballerinalang.model.expressions.SubtractExpression;
import org.ballerinalang.model.expressions.TypeCastExpression;
import org.ballerinalang.model.expressions.TypeConversionExpr;
import org.ballerinalang.model.expressions.UnaryExpression;
import org.ballerinalang.model.expressions.XMLCommentLiteral;
import org.ballerinalang.model.expressions.XMLElementLiteral;
import org.ballerinalang.model.expressions.XMLLiteral;
import org.ballerinalang.model.expressions.XMLPILiteral;
import org.ballerinalang.model.expressions.XMLQNameExpr;
import org.ballerinalang.model.expressions.XMLSequenceLiteral;
import org.ballerinalang.model.expressions.XMLTextLiteral;
import org.ballerinalang.model.expressions.variablerefs.FieldBasedVarRefExpr;
import org.ballerinalang.model.expressions.variablerefs.IndexBasedVarRefExpr;
import org.ballerinalang.model.expressions.variablerefs.SimpleVarRefExpr;
import org.ballerinalang.model.expressions.variablerefs.XMLAttributesRefExpr;
import org.ballerinalang.model.statements.AbortStmt;
import org.ballerinalang.model.statements.ActionInvocationStmt;
import org.ballerinalang.model.statements.AssignStmt;
import org.ballerinalang.model.statements.BlockStmt;
import org.ballerinalang.model.statements.BreakStmt;
import org.ballerinalang.model.statements.CommentStmt;
import org.ballerinalang.model.statements.ContinueStmt;
import org.ballerinalang.model.statements.ForkJoinStmt;
import org.ballerinalang.model.statements.FunctionInvocationStmt;
import org.ballerinalang.model.statements.IfElseStmt;
import org.ballerinalang.model.statements.NamespaceDeclarationStmt;
import org.ballerinalang.model.statements.ReplyStmt;
import org.ballerinalang.model.statements.RetryStmt;
import org.ballerinalang.model.statements.ReturnStmt;
import org.ballerinalang.model.statements.Statement;
import org.ballerinalang.model.statements.ThrowStmt;
import org.ballerinalang.model.statements.TransactionStmt;
import org.ballerinalang.model.statements.TransformStmt;
import org.ballerinalang.model.statements.TryCatchStmt;
import org.ballerinalang.model.statements.VariableDefStmt;
import org.ballerinalang.model.statements.WhileStmt;
import org.ballerinalang.model.statements.WorkerInvocationStmt;
import org.ballerinalang.model.statements.WorkerReplyStmt;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @{@link CodeGenVisitor} implements @{@link NodeVisitor} to traverse through Ballerina model of the integration flow
 * and serialize to ballerina source
 */
public class CodeGenVisitor implements NodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(CodeGenVisitor.class);

    private StringBuilder balSourceBuilder = new StringBuilder();
    private int indentDepth = 0;
    private int previousIndentDepth = 0;
    private String indentStr = "";
    private BLangProgram balProgram = null;

    @Override
    public void visit(BLangProgram bLangProgram) {
        logger.debug("Visit - BLangProgram");
        balProgram = bLangProgram;

        //process each ServicePackages
        for (BLangPackage bLangPackage : balProgram.getPackages()) {
            //add import packages
            for (ImportPackage importPackage : bLangPackage.getImportPackages()) {
                appendToBalSource(importPackage.getSymbolName().toString() + Constants.NEWLINE_STR);
            }

            //process struct definitions
            for (StructDef structDef : bLangPackage.getStructDefs()) {
                structDef.accept(this);
            }

            //process services
            for (Service service : bLangPackage.getServices()) {
                service.accept(this);
            }

            //process functions within the package
            for (Function function : bLangPackage.getFunctions()) {
                function.accept(this);
            }
        }
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
            if (Constants.IMPLICIT_PACKAGE.equals(importPackage.getSymbolName().getName())) {
                continue;
            }
            //no need to consider indentation due to imports happens at the beginning of the file
            appendToBalSource(Constants.IMPORT_STR + Constants.SPACE_STR + importPackage.getSymbolName().getName()
                    + Constants.STMTEND_STR + Constants.NEWLINE_STR);
        }

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
            appendToBalSource(Constants.NEWLINE_STR);
        }

        /**
         serviceDefinition : 'service' Identifier serviceBody;
         * */
        appendToBalSource(
                getIndentationForCurrentLine() + Constants.SERVICE_STR + Constants.ANGLE_BRACKET_START_STR + service
                        .getProtocolPkgName() + Constants.ANGLE_BRACKET_END_STR +
                        Constants.SPACE_STR + service.getName() + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR
                        + Constants.NEWLINE_STR);
        ++indentDepth;

        /**
         * serviceBody: '{' variableDefinitionStatement* resourceDefinition* '}';
         * resourceDefinition: annotationAttachment* 'resource' Identifier '(' parameterList ')' callableUnitBody;
         * annotationAttachment: '@' nameReference '{' annotationAttributeList? '}'
         */
        for (VariableDefStmt variableDefStmt : service.getVariableDefStmts()) {
            variableDefStmt.accept(this);
        }

        //Visit Resource definitions
        Resource[] resources = service.getResources();
        for (Resource resource : resources) {
            resource.accept(this);
        }

        //Service visit completed
        appendToBalSource(Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
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
            appendToBalSource(getIndentationForCurrentLine());
            annotationAttachment.accept(this);
            appendToBalSource(Constants.NEWLINE_STR);
        }

        appendToBalSource(getIndentationForCurrentLine() + Constants.RESOURCE_STR + Constants.SPACE_STR +
                resource.getIdentifier().getName() + Constants.SPACE_STR + Constants.PARENTHESES_START_STR);

        ParameterDef[] parameterDefs = resource.getParameterDefs();
        for (int i = 0; i < parameterDefs.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            parameterDefs[i].accept(this);
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

        for (Worker worker : resource.getWorkers()) {
            worker.accept(this);
        }

        --indentDepth;
        //end of resource statements block
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(BallerinaFunction function) {
        logger.debug("Visit - BallerinaFunction");
        /**
         * functionDefinition
         :   'native' 'function'  callableUnitSignature ';'
         |   'function' callableUnitSignature callableUnitBody
         ;
         * callableUnitSignature
         :   Identifier '(' parameterList? ')' returnParameters?
         ;
         * returnParameters
         : '(' (parameterList | returnTypeList) ')'
         ;
         */

        appendToBalSource(Constants.FUNCTION_STR + Constants.SPACE_STR + function.getName() + Constants.SPACE_STR +
                Constants.PARENTHESES_START_STR);
        //process parameterList
        ParameterDef[] parameterDefs = function.getParameterDefs();
        for (int i = 0; i < parameterDefs.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            parameterDefs[i].accept(this);
        }
        appendToBalSource(Constants.PARENTHESES_END_STR + Constants.SPACE_STR);

        if (function.getReturnParameters().length > 0) {
            appendToBalSource(Constants.PARENTHESES_START_STR);
            //process return parameters
            ParameterDef[] returnParamDefs = function.getReturnParameters();
            for (int i = 0; i < returnParamDefs.length; i++) {
                if (i > 0) {
                    appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
                }
                returnParamDefs[i].accept(this);
            }
            appendToBalSource(Constants.PARENTHESES_END_STR + Constants.SPACE_STR);
        }

        appendToBalSource(Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
        ++indentDepth;
        function.getCallableUnitBody().accept(this);
        --indentDepth;
        appendToBalSource(Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(BTypeMapper typeMapper) {

    }

    @Override
    public void visit(BallerinaAction action) {

    }

    @Override
    public void visit(Worker worker) {
        logger.debug("Visit - Worker");
        appendToBalSource(getIndentationForCurrentLine() + Constants.WORKER_STR + Constants.SPACE_STR + worker.getName()
                + Constants.SPACE_STR +
                Constants.STMTBLOCK_START_STR);
        ++indentDepth;
        appendToBalSource(Constants.NEWLINE_STR);
        BlockStmt blockStmt = worker.getCallableUnitBody();
        blockStmt.accept(this);
        --indentDepth;
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(AnnotationAttachment annotation) {
        logger.debug("Visit - AnnotationAttachment");
        /**
         *  annotationAttachment : '@' nameReference '{' annotationAttributeList? '}';
         */
        appendToBalSource(annotation.toString());
    }

    @Override
    public void visit(ParameterDef parameterDef) {

        //TODO handle annotations for parameter
        if (parameterDef.getAnnotations().length > 0) {
            for (AnnotationAttachment annotation : parameterDef.getAnnotations()) {
                annotation.accept(this);
                appendToBalSource(Constants.SPACE_STR);
            }
        }
        appendToBalSource(parameterDef.getTypeName() + Constants.SPACE_STR + parameterDef.getSymbolName());
    }

    @Override
    public void visit(SimpleVariableDef simpleVariableDef) {

    }

    @Override
    public void visit(StructDef structDef) {
        logger.debug("Visit - StructDef");

        /**
         * structDefinition :'struct' Identifier structBody;
         * structBody : '{' fieldDefinition* '}';
         * fieldDefinition : typeName Identifier ('=' simpleLiteral)? ';';
         */

        appendToBalSource(getIndentationForCurrentLine() + Constants.STRUCT_STR + Constants.SPACE_STR +
                structDef.getName() + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
        ++indentDepth;
        //process fieldDefinition
        for (VariableDefStmt variableDefStmt : structDef.getFieldDefStmts()) {
            variableDefStmt.accept(this);
        }
        --indentDepth;
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(AnnotationAttributeDef annotationAttributeDef) {

    }

    @Override
    public void visit(AnnotationDef annotationDef) {

    }

    @Override
    public void visit(VariableDefStmt varDefStmt) {
        logger.debug("Visit - VariableDefStmt");
        /**
         * variableDefinitionStatement : typeName Identifier ('=' (connectorInitExpression | actionInvocation |
         *                                                                                      expression) )? ';';
         */

        VariableDef variableDef = varDefStmt.getVariableDef();
        String varLHSDefStr = variableDef.getTypeName().toString() + Constants.SPACE_STR + variableDef.getSymbolName();

        appendToBalSource(getIndentationForCurrentLine() + varLHSDefStr);
        if (varDefStmt.getRExpr() != null) {
            //have RHS expression eg: string str = <Some connectorInitExpression | actionInvocation | expression>;
            appendToBalSource(Constants.SPACE_STR + Constants.EQUAL_STR + Constants.SPACE_STR);
            varDefStmt.getRExpr().accept(this);
        }
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(AssignStmt assignStmt) {
        logger.debug("Visit - AssignStmt");
        /**
         * assignmentStatement
         : variableReferenceList '=' (connectorInitExpression | actionInvocation | expression) ';';
         * variableReferenceList
         : variableReference (',' variableReference)*;
         * variableReference
         :   nameReference                               # simpleVariableIdentifier// simple identifier
         |   nameReference ('['expression']')+           # mapArrayVariableIdentifier// arrays and map reference
         |   variableReference ('.' variableReference)+  # structFieldIdentifier// struct field reference
         ;
         */
        //handle lhs
        appendToBalSource(getIndentationForCurrentLine());
        Expression[] lhsExpressions = assignStmt.getLExprs();
        for (Expression lhsExpression : lhsExpressions) {
            lhsExpression.accept(this);
        }
        appendToBalSource(Constants.SPACE_STR + Constants.EQUAL_STR + Constants.SPACE_STR);
        //handle rhs
        assignStmt.getRExpr().accept(this);
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(BlockStmt blockStmt) {
        logger.debug("Visit - BlockStmt");

        //traverse statements
        for (Statement statement : blockStmt.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(CommentStmt commentStmt) {
        logger.debug("Visit - CommentStmt");
        appendToBalSource(getIndentationForCurrentLine() + commentStmt.getComment() + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(IfElseStmt ifElseStmt) {
        logger.debug("Visit - IfElseStmt");
        /**
         * ifElseStatement : ifClause elseIfClause* elseClause?;
         * ifClause : 'if' '(' expression ')' '{' statement* '}';
         * elseIfClause : 'else' 'if' '(' expression ')' '{' statement* '}';
         * elseClause : 'else' '{' statement*'}';
         */
        appendToBalSource(getIndentationForCurrentLine() + Constants.IF_STR + Constants.SPACE_STR +
                Constants.PARENTHESES_START_STR);
        //process if clause expression
        ifElseStmt.getCondition().accept(this);
        appendToBalSource(Constants.PARENTHESES_END_STR + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR +
                Constants.NEWLINE_STR);

        //process then block
        ++indentDepth;
        ifElseStmt.getThenBody().accept(this);
        --indentDepth;
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);

        //process else if clauses
        if (ifElseStmt.getElseIfBlocks().length > 0) {
            for (IfElseStmt.ElseIfBlock elseIfBlock : ifElseStmt.getElseIfBlocks()) {
                appendToBalSource(Constants.SPACE_STR + Constants.ELSE_STR + Constants.SPACE_STR + Constants.IF_STR +
                        Constants.SPACE_STR + Constants.PARENTHESES_START_STR);
                elseIfBlock.getElseIfCondition().accept(this);
                appendToBalSource(Constants.PARENTHESES_END_STR + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR +
                        Constants.NEWLINE_STR);
                ++indentDepth;
                elseIfBlock.getElseIfBody().accept(this);
                --indentDepth;
                appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);
            }
        }

        //process else block
        appendToBalSource(Constants.SPACE_STR + Constants.ELSE_STR + Constants.SPACE_STR +
                Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
        ++indentDepth;
        ifElseStmt.getElseBody().accept(this);
        --indentDepth;
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ReplyStmt replyStmt) {
        /**
         * replyStatement : 'reply' expression ';';
         */
        appendToBalSource(getIndentationForCurrentLine() + Constants.REPLY_STR + Constants.SPACE_STR);

        Expression replyExpression = replyStmt.getReplyExpr();
        if (replyExpression instanceof SimpleVarRefExpr) {
            appendToBalSource(((SimpleVarRefExpr) replyExpression).getSymbolName().toString());
        }

        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ReturnStmt returnStmt) {
        logger.debug("Visit - ReturnStmt");
        /**
         * returnStatement : 'return' expressionList? ';';
         * expressionList:expression (',' expression)*;
         */
        appendToBalSource(getIndentationForCurrentLine() + Constants.RETURN_STR + Constants.SPACE_STR);
        Expression[] expressions = returnStmt.getExprs();
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR);
            }
            expressions[i].accept(this);
        }
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(WhileStmt whileStmt) {

    }

    @Override
    public void visit(BreakStmt breakStmt) {

    }

    @Override
    public void visit(ContinueStmt continueStmt) {

    }

    @Override
    public void visit(TryCatchStmt tryCatchStmt) {
        logger.debug("Visit - TryCatchStmt");
        /**
         * tryCatchStatement:   'try' '{' statement* '}' catchClauses;
         * catchClauses: catchClause+ finallyClause?| finallyClause;
         * catchClause:  'catch' '(' typeName Identifier ')' '{' statement* '}';
         * finallyClause: 'finally' '{' statement* '}';
         */
        //process try block
        appendToBalSource(getIndentationForCurrentLine() + Constants.TRY_STR + Constants.SPACE_STR +
                Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
        ++indentDepth;
        tryCatchStmt.getTryBlock().accept(this);
        --indentDepth;
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);
        //process catch blocks
        for (TryCatchStmt.CatchBlock catchBlock : tryCatchStmt.getCatchBlocks()) {
            appendToBalSource(Constants.SPACE_STR + Constants.CATCH_STR + Constants.SPACE_STR +
                    Constants.PARENTHESES_START_STR);
            catchBlock.getParameterDef().accept(this);
            appendToBalSource(Constants.PARENTHESES_END_STR + Constants.SPACE_STR + Constants.STMTBLOCK_START_STR +
                    Constants.NEWLINE_STR);
            ++indentDepth;
            catchBlock.getCatchBlockStmt().accept(this);
            --indentDepth;
            appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);
        }
        //process finally block
        if (tryCatchStmt.getFinallyBlock() != null) {
            appendToBalSource(Constants.SPACE_STR + Constants.FINALLY_STR + Constants.SPACE_STR +
                    Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
            ++indentDepth;
            tryCatchStmt.getFinallyBlock().getFinallyBlockStmt().accept(this);
            --indentDepth;
            appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR + Constants.NEWLINE_STR);
        }
        appendToBalSource(Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ThrowStmt throwStmt) {

    }

    @Override
    public void visit(FunctionInvocationStmt functionInvocationStmt) {
        logger.debug("Visit - FunctionInvocationStmt");
        /**
         * functionInvocationStatement : nameReference '(' expressionList? ')' ';';
         */
        appendToBalSource(getIndentationForCurrentLine());
        functionInvocationStmt.getFunctionInvocationExpr().accept(this);
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ActionInvocationStmt actionInvocationStmt) {
        logger.debug("Visit - ActionInvocationStmt");
        /**
         * actionInvocationStatement
         :   actionInvocation ';'
         |   variableReferenceList '=' actionInvocation ';'
         ;
         */
        appendToBalSource(getIndentationForCurrentLine());
        actionInvocationStmt.getActionInvocationExpr().accept(this);
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(WorkerInvocationStmt workerInvocationStmt) {
        logger.debug("Visit - WorkerInvocationStmt");
        appendToBalSource(getIndentationForCurrentLine());
        for (Expression expressions : workerInvocationStmt.getExpressionList()) {
            expressions.accept(this);
            if (workerInvocationStmt.getExpressionList().length > 1) {
                appendToBalSource(Constants.COMMA_STR);
            }
        }
        appendToBalSource(Constants.SPACE_STR + Constants.SEND + Constants.SPACE_STR + workerInvocationStmt.getName());
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(WorkerReplyStmt workerReplyStmt) {
        logger.debug("Visit - WorkerReplyStmt");
        appendToBalSource(getIndentationForCurrentLine());
        for (Expression expressions : workerReplyStmt.getExpressionList()) {
            expressions.accept(this);
            if (workerReplyStmt.getExpressionList().length > 1) {
                appendToBalSource(Constants.COMMA_STR);
            }
        }
        appendToBalSource(
                Constants.SPACE_STR + Constants.RECEIVE + Constants.SPACE_STR + workerReplyStmt.getWorkerName());
        appendToBalSource(Constants.STMTEND_STR + Constants.NEWLINE_STR);
    }

    @Override
    public void visit(ForkJoinStmt forkJoinStmt) {

    }

    @Override
    public void visit(TransformStmt transformStmt) {

    }

    @Override
    public void visit(TransactionStmt transactionStmt) {
        logger.debug("Visit - TransactionStmt");

        /**
         * transactionStatement : 'transaction' '{' statement* '}' transactionHandlers;
         * transactionHandlers : abortedClause? committedClause? | committedClause? abortedClause? ;
         * abortedClause : 'aborted' '{' statement* '}';
         * committedClause : 'committed' '{' statement* '}';
         */
        appendToBalSource(getIndentationForCurrentLine() + Constants.TRANSACTION_STR + Constants.SPACE_STR +
                Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
        ++indentDepth;
        //process transaction block
        transactionStmt.getTransactionBlock().accept(this);
        --indentDepth;
        appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);

        //process transaction handlers
        if (transactionStmt.getCommittedBlock() != null) {
            //committed block exists, process it
            appendToBalSource(Constants.SPACE_STR + Constants.COMMITTED_STR + Constants.SPACE_STR +
                    Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
            ++indentDepth;
            transactionStmt.getCommittedBlock().getCommittedBlockStmt().accept(this);
            --indentDepth;
            appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);
        }

        if (transactionStmt.getAbortedBlock() != null) {
            //aborted block exists, process it
            appendToBalSource(Constants.SPACE_STR + Constants.ABORTED_STR + Constants.SPACE_STR +
                    Constants.STMTBLOCK_START_STR + Constants.NEWLINE_STR);
            ++indentDepth;
            transactionStmt.getAbortedBlock().getAbortedBlockStmt().accept(this);
            --indentDepth;
            appendToBalSource(getIndentationForCurrentLine() + Constants.STMTBLOCK_END_STR);
        }
        appendToBalSource(Constants.NEWLINE_STR);

    }

    @Override
    public void visit(AbortStmt abortStmt) {

    }

    @Override
    public void visit(RetryStmt retryStmt) {

    }

    @Override
    public void visit(NamespaceDeclarationStmt namespaceDeclarationStmt) {

    }

    @Override
    public void visit(NamespaceDeclaration namespaceDeclaration) {

    }

    @Override
    public void visit(AddExpression addExpr) {
        logger.debug("Visit - AddExpression");
        /**
         * expression
         : ......
         | expression ('+' | '-') expression  # binaryAddSubExpression
         | ....
         */
        addExpr.getLExpr().accept(this);
        appendToBalSource(Constants.SPACE_STR + addExpr.getOperator().toString() + Constants.SPACE_STR);
        addExpr.getRExpr().accept(this);
    }

    @Override
    public void visit(AndExpression andExpression) {

    }

    @Override
    public void visit(BasicLiteral basicLiteral) {
        if (basicLiteral.getBValue() instanceof BString) {
            appendToBalSource(Constants.QUOTE_STR + basicLiteral.getBValue().stringValue() + Constants.QUOTE_STR);
        } else if (basicLiteral.getBValue() instanceof BInteger) {
            appendToBalSource(basicLiteral.getBValue().stringValue());
        }
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
        logger.debug("Visit - FunctionInvocationExpr");
        /**
         * expression : simpleLiteral                        # simpleLiteralExpression
         |   arrayLiteral                                    # arrayLiteralExpression
         |   ..................
         |   nameReference '(' expressionList? ')'           # functionInvocationExpression
         */
        appendToBalSource((functionInvocationExpr.getPackageName() == null ?
                "" :
                functionInvocationExpr.getPackageName() + Constants.COLON_STR) +
                functionInvocationExpr.getName() + Constants.PARENTHESES_START_STR);
        //process expression list
        Expression[] argExpressions = functionInvocationExpr.getArgExprs();
        for (int i = 0; i < argExpressions.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            argExpressions[i].accept(this);
        }
        //end of expressionList
        appendToBalSource(Constants.PARENTHESES_END_STR);
    }

    @Override
    public void visit(ActionInvocationExpr actionInvocationExpr) {
        logger.debug("Visit - ActionInvocationExpr");
        /**
         * actionInvocation : nameReference '.' Identifier '(' expressionList? ')';
         * nameReference : (Identifier ':')? Identifier;
         */
        appendToBalSource(actionInvocationExpr.getPackageName() + Constants.COLON_STR +
                actionInvocationExpr.getConnectorName() + Constants.PERIOD_STR + actionInvocationExpr.getName() +
                Constants.PARENTHESES_START_STR);
        //process expression list
        Expression[] expressions = actionInvocationExpr.getArgExprs();
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            expressions[i].accept(this);
        }
        appendToBalSource(Constants.PARENTHESES_END_STR);
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
        logger.debug("Visit - AddExpression");
        /**
         * expression
         : ......
         | expression ('+' | '-') expression  # binaryAddSubExpression
         | ....
         */
        subtractExpression.getLExpr().accept(this);
        appendToBalSource(Constants.SPACE_STR + subtractExpression.getOperator().toString() + Constants.SPACE_STR);
        subtractExpression.getRExpr().accept(this);
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        logger.debug("Visit - UnaryExpression");
        /**
         * expression
         : ......
         | ('+' | '-' | '!') expression    # unaryExpression
         | ....
         */
        appendToBalSource(unaryExpression.getOperator().toString());
        unaryExpression.getRExpr().accept(this);

    }

    @Override
    public void visit(TypeCastExpression typeCastExpression) {
        logger.debug("Visit - TypeCastExpression");
        /**
         * expression
         : ......
         | '(' typeName ')' expression   # typeCastingExpression
         | ....
         */
        if (typeCastExpression.getTypeName() != null) {
            appendToBalSource(Constants.PARENTHESES_START_STR + typeCastExpression.getTypeName() +
                    Constants.PARENTHESES_END_STR + Constants.SPACE_STR);
        }
        typeCastExpression.getRExpr().accept(this);

    }

    @Override
    public void visit(TypeConversionExpr typeConversionExpr) {

    }

  /*  @Override
    public void visit(ArrayMapAccessExpr arrayMapAccessExpr) {
        logger.debug("Visit - ArrayMapAccessExpr");
        *//**
     * variableReference
     :   nameReference                               # simpleVariableIdentifier// simple identifier
     |   nameReference ('['expression']')+           # mapArrayVariableIdentifier// arrays and map reference
     |   variableReference ('.' variableReference)+  # structFieldIdentifier// struct field reference
     ;
     *//*
        appendToBalSource(arrayMapAccessExpr.getVarName() + Constants.ARRAY_START_STR);
        arrayMapAccessExpr.getIndexExprs()[0].accept(this);
        appendToBalSource(Constants.ARRAY_END_STR);
    }*/

  /*  @Override
    public void visit(ArrayLengthExpression arrayLengthExpression) {

    }

    @Override
    public void visit(FieldAccessExpr structAttributeAccessExpr) {
        logger.debug("Visit - FieldAccessExpr");
        */

    /**
     * variableReference
     * :   nameReference                               # simpleVariableIdentifier// simple identifier
     * |   nameReference ('['expression']')+           # mapArrayVariableIdentifier// arrays and map reference
     * |   variableReference ('.' variableReference)+  # structFieldIdentifier// struct field reference
     * ;
     *//*
        structAttributeAccessExpr.getVarRef().accept(this);
        if (structAttributeAccessExpr.getFieldExpr() != null) {
            appendToBalSource(Constants.PERIOD_STR);
            structAttributeAccessExpr.getFieldExpr().accept(this);
        }
    }

    @Override
    public void visit(JSONFieldAccessExpr jsonPathExpr) {
        logger.debug("Visit - JSONFieldAccessExpr");

        if (jsonPathExpr.getVarRef() instanceof BasicLiteral) {
            appendToBalSource(((BasicLiteral) jsonPathExpr.getVarRef()).getBValue().stringValue());
        } else {
            jsonPathExpr.getVarRef().accept(this);
        }
        if (jsonPathExpr.getFieldExpr() != null) {
            appendToBalSource(Constants.PERIOD_STR);
            jsonPathExpr.getFieldExpr().accept(this);
        }
    }*/

    /*@Override
    public void visit(BacktickExpr backtickExpr) {
        logger.debug("Visit - BacktickExpr");
        appendToBalSource("`" + backtickExpr.getTemplateStr() + "`");
    }*/
    @Override
    public void visit(ArrayInitExpr arrayInitExpr) {
        logger.debug("Visit - ArrayInitExpr");
        /**
         * arrayLiteral : '[' expressionList? ']';
         */
        appendToBalSource(Constants.ARRAY_START_STR);
        Expression[] expressArgs = arrayInitExpr.getArgExprs();
        for (int i = 0; i < expressArgs.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            expressArgs[i].accept(this);
        }
        appendToBalSource(Constants.ARRAY_END_STR);
    }

    @Override
    public void visit(RefTypeInitExpr refTypeInitExpr) {
        logger.debug("Visit - RefTypeInitExpr");
        appendToBalSource(Constants.STMTBLOCK_START_STR);

        if (refTypeInitExpr.getArgExprs().length > 0) {
            Expression[] args = refTypeInitExpr.getArgExprs();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
                }
                args[i].accept(this);
            }
        }

        appendToBalSource(Constants.STMTBLOCK_END_STR);
    }

    @Override
    public void visit(ConnectorInitExpr connectorInitExpr) {
        /**
         * connectorInitExpression : 'create' nameReference '(' expressionList? ')';
         * expressionList : expression (',' expression)*;
         */
        appendToBalSource(Constants.CREATE_STR).append(Constants.SPACE_STR).
                append(connectorInitExpr.getTypeName().toString()).append(Constants.PARENTHESES_START_STR);

        Expression[] expressArgs = connectorInitExpr.getArgExprs();
        for (int i = 0; i < expressArgs.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            /*if (expressArgs[i] instanceof BasicLiteral) {
                BValue arg = ((BasicLiteral) expressArgs[i]).getBValue();
                appendToBalSource(arg.stringValue());
            }*/
            expressArgs[i].accept(this);
        }
        appendToBalSource(Constants.PARENTHESES_END_STR);
    }

    @Override
    public void visit(StructInitExpr structInitExpr) {
        logger.debug("Visit - StructInitExpr");
        //TODO assume StructInitExpr similar as MapInitExpr for now
        appendToBalSource(Constants.STMTBLOCK_START_STR);
        Expression[] expressions = structInitExpr.getArgExprs();
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            expressions[i].accept(this);
        }
        appendToBalSource(Constants.STMTBLOCK_END_STR);
    }

    @Override
    public void visit(MapInitExpr mapInitExpr) {
        logger.debug("Visit - MapInitExpr");

        /**
         * mapStructLiteral : '{' (mapStructKeyValue (',' mapStructKeyValue)*)? '}';
         * mapStructKeyValue : expression ':' expression
         ;
         */
        appendToBalSource(Constants.STMTBLOCK_START_STR);
        Expression[] expressions = mapInitExpr.getArgExprs();
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            expressions[i].accept(this);
        }
        appendToBalSource(Constants.STMTBLOCK_END_STR);
    }

    @Override
    public void visit(JSONInitExpr jsonInitExpr) {
        logger.debug("Visit - JSONInitExpr");
        appendToBalSource(Constants.STMTBLOCK_START_STR);
        Expression[] expressions = jsonInitExpr.getArgExprs();
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) {
                appendToBalSource(Constants.COMMA_STR + Constants.SPACE_STR);
            }
            expressions[i].accept(this);
        }
        appendToBalSource(Constants.STMTBLOCK_END_STR);
    }

    @Override
    public void visit(JSONArrayInitExpr jsonArrayInitExpr) {

    }

    @Override
    public void visit(KeyValueExpr keyValueExpr) {
        logger.debug("Visit - KeyValueExpr");
        keyValueExpr.getKeyExpr().accept(this);
        appendToBalSource(Constants.COLON_STR);
        keyValueExpr.getValueExpr().accept(this);

    }

    @Override
    public void visit(SimpleVarRefExpr simpleVarRefExpr) {
        logger.debug("Visit - SimpleVarRefExpr");
        appendToBalSource(simpleVarRefExpr.getVarName());
    }

    @Override
    public void visit(FieldBasedVarRefExpr fieldBasedVarRefExpr) {

    }

    @Override
    public void visit(IndexBasedVarRefExpr indexBasedVarRefExpr) {

    }

    @Override
    public void visit(XMLAttributesRefExpr xmlAttributesRefExpr) {

    }

    @Override
    public void visit(XMLQNameExpr xmlqNameExpr) {

    }

    /*@Override
    public void visit(VariableRefExpr variableRefExpr) {
        appendToBalSource(variableRefExpr.getSymbolName().toString());
    }*/

    @Override
    public void visit(NullLiteral nullLiteral) {

    }

    @Override
    public void visit(XMLLiteral xmlLiteral) {

    }

    @Override
    public void visit(XMLElementLiteral xmlElementLiteral) {

    }

    @Override
    public void visit(XMLCommentLiteral xmlCommentLiteral) {

    }

    @Override
    public void visit(XMLTextLiteral xmlTextLiteral) {
        logger.debug("Visit - XMLTextLiteral");
        appendToBalSource(xmlTextLiteral.getType().getName() + " ");
        xmlTextLiteral.getContent().accept(this);
    }

    @Override
    public void visit(XMLPILiteral xmlpiLiteral) {

    }

    @Override
    public void visit(XMLSequenceLiteral xmlSequenceLiteral) {

    }

    @Override
    public void visit(LambdaExpression lambdaExpression) {

    }

    @Override
    public void visit(StringTemplateLiteral stringTemplateLiteral) {

    }

    public String getBallerinaSourceStr() {
        return balSourceBuilder.toString();
    }

    private StringBuilder appendToBalSource(String str) {
        return balSourceBuilder.append(str);
    }

    /*private void appendToBalSourceWithNewLine(String str) {
        ballerinaSourceStr += Constants.NEWLINE_STR + str;
    }*/

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
