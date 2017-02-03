@echo off

REM ---------------------------------------------------------------------------
REM        Copyright 2005-2012 WSO2, Inc. http://www.wso2.org
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
rem  Samples Script for deploying WSO2 BPS samples
rem
rem Environment Variable Prerequisites
rem
rem   CARBON_HOME     Home of CARBON installation. If not set I will  try
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
if "%1"=="-s" goto checkServer
if "%1"=="-S" goto checkServer
if "%1"=="" goto no_sample
shift
goto initial

:checkServer
rem %~sdp0 is expanded pathname of the current script under NT with spaces in the path removed
if "%BPS_HOME%"=="" set BPS_HOME=%~sdp0..
if not exist "%BPS_HOME%\bin\version.txt" goto noServerHome
goto sname

:sname
shift
set cn=%1
if "%1"=="" goto invalid_sample_name
echo %BPS_HOME%\repository\samples\bpel\%cn%
IF EXIST %BPS_HOME%\repository\samples\bpel\%cn% goto copy_bpel_samples
IF EXIST %BPS_HOME%\repository\samples\humantask\%cn% goto copy_ht_samples
goto missing_sample

:copy_ht_samples
xcopy /I /E /Y %BPS_HOME%\repository\samples\humantask\%cn% %BPS_HOME%\repository\deployment\server\humantask\
goto run

:copy_bpel_samples
xcopy /I /E /Y %BPS_HOME%\repository\samples\bpel\%cn% %BPS_HOME%\repository\deployment\server\bpel\
goto run

:run
%BPS_HOME%\bin\wso2server.bat
goto done

:missing_sample
echo "The specified sample does not exist in the file system. Please specify an existing sample"
goto done

:invalid_sample_name
echo "*** Specified sample is not present *** Please specify a valid sample with the -s option"
echo "Example, to run sample HelloWorld2.zip : wso2bps-samples.sh -s HelloWorld2.zip"
goto done

:no_sample
echo "*** Sample to be started is not specified *** Please specify a sample to be started with the -s option"
echo "Example, to run sample HelloWorld2.zip : wso2bps-samples.sh -s HelloWorld2.zip"
goto done

:noServerHome
echo "Invalid directory location specified as server home"
goto done


:done
