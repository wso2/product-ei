@echo off

REM ---------------------------------------------------------------------------
REM        Copyright 2005-2009 WSO2, Inc. http://www.wso2.org
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
rem Main Script for WSO2 Carbon
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
goto run

:lreturn
shift
goto initial

:run
wso2server.bat %CMD% -Desb.sample=%cn% -Dcarbon.registry.root=/esb-samples/s%cn%
goto done

:invalid_number
echo "*** Specified sample number is not a number *** Please specify a valid sample number with the -sn option"
echo "Example, to run sample 0: wso2esb-samples.sh -sn 0"
goto done

:no_sample
echo "*** Sample number to be started is not specified *** Please specify a sample number to be started with the -sn option"
echo "Example, to run sample 0: wso2esb-samples.sh -sn 0"
goto done

:done