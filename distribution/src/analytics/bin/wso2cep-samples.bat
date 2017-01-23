@echo off

REM ---------------------------------------------------------------------------
REM        Copyright 2005-2016 WSO2, Inc. http://www.wso2.org
REM
REM  Licensed under the Apache License, Version 2.0 (the "License");
REM  you may not use this file except in compliance with the License.
REM  You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.

rem ---------------------------------------------------------------------------
rem Script for runnig the WSO2 CEP Server samples
rem
rem Environment Variable Prequisites
rem
rem   CARBON_HOME   Home of CARBON installation. If not set I will  try
rem                   to figure it out.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when the commands
rem                   is executed.
rem ---------------------------------------------------------------------------

SET cn=1

set CMD=%*

:initial
if "%1"=="-sn" goto sname
if "%1"=="" goto no_sample
shift
goto initial

:sname
shift
set cn=%1
if "%1"=="" goto invalid_number
if "%1"=="0" goto run
SET /A UserInputVal="%cn%"*1
IF %UserInputVal% EQU 0 GOTO invalid_number
goto copyfile

:lreturn
shift
goto initial

:run
wso2server.bat %CMD% -Daxis2.repo=../samples/cep/artifacts/%cn%
rem ----------- loading spark specific variables
call %CARBON_HOME%\bin\load-spark-env-vars.bat
"%JAVA_HOME%\bin\java" %CMD_LINE_ARGS% org.wso2.carbon.bootstrap.Bootstrap %CMD%
if "%ERRORLEVEL%"=="121" goto run
goto done

:copyfile
if not exist ..\samples\cep\artifacts\%cn%\webapps ( mkdir ..\samples\cep\artifacts\%cn%\webapps > nul
if exist ..\repository\deployment\server\webapps\inputwebsocket.war ( copy ..\repository\deployment\server\webapps\inputwebsocket.war ..\samples\cep\artifacts\%cn%\webapps\ > nul )
if exist ..\repository\deployment\server\webapps\outputwebsocket.war ( copy ..\repository\deployment\server\webapps\outputwebsocket.war ..\samples\cep\artifacts\%cn%\webapps\ > nul )
if exist ..\repository\deployment\server\webapps\shindig.war ( copy ..\repository\deployment\server\webapps\shindig.war ..\samples\cep\artifacts\%cn%\webapps\ > nul )
) else ( if not exist ..\samples\cep\artifacts\%cn%\webapps\inputwebsocket.war ( copy ..\repository\deployment\server\webapps\inputwebsocket.war ..\samples\cep\artifacts\%cn%\webapps\ > nul )
if not exist ..\samples\cep\artifacts\%cn%\webapps\outputwebsocket.war ( copy ..\repository\deployment\server\webapps\outputwebsocket.war ..\samples\cep\artifacts\%cn%\webapps\ > nul )
if not exist ..\samples\cep\artifacts\%cn%\webapps\shindig.war ( copy ..\repository\deployment\server\webapps\shindig.war ..\samples\cep\artifacts\%cn%\webapps\ > nul ) )

if not exist ..\samples\cep\artifacts\%cn%\jaggeryapps ( mkdir ..\samples\cep\artifacts\%cn%\jaggeryapps\portal > nul
xcopy ..\repository\deployment\server\jaggeryapps\portal ..\samples\cep\artifacts\%cn%\jaggeryapps\portal\ /s /q > nul
rmdir ..\samples\cep\artifacts\%cn%\jaggeryapps\portal\store /s /q > nul
mkdir ..\samples\cep\artifacts\%cn%\jaggeryapps\portal\store\carbon.super > nul
xcopy ..\repository\deployment\server\jaggeryapps\portal\store\carbon.super ..\samples\cep\artifacts\%cn%\jaggeryapps\portal\store\carbon.super /s /q > nul
rmdir ..\samples\cep\artifacts\%cn%\jaggeryapps\portal\store\carbon.super\fs\gadget /s /q > nul
mkdir ..\samples\cep\artifacts\%cn%\jaggeryapps\portal\store\carbon.super\fs\gadget > nul)
goto run

:invalid_number
echo "*** Specified sample number is not a number *** Please specify a valid sample number with the -sn option"
echo "Example, to run sample 1: wso2cep-samples.bat -sn 1"
goto done

:no_sample
echo "*** Sample number to be started is not specified *** Please specify a sample number to be started with the -sn option"
echo "Example, to run sample 1: wso2cep-samples.bat -sn 1"
goto done

:done