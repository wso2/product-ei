@echo off

REM ---------------------------------------------------------------------------
REM   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
REM
REM   Licensed under the Apache License, Version 2.0 (the "License");
REM   you may not use this file except in compliance with the License.
REM   You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM   Unless required by applicable law or agreed to in writing, software
REM   distributed under the License is distributed on an "AS IS" BASIS,
REM   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM   See the License for the specific language governing permissions and
REM   limitations under the License.

rem ---------------------------------------------------------------------------
rem Main Script for WSO2 Carbon
rem
rem Environment Variable Prerequisites
rem
rem   CARBON_HOME   Home of CARBON installation. If not set I will  try
rem                   to figure it out.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when the commands
rem                   is executed.
rem ---------------------------------------------------------------------------

rem ----- if JAVA_HOME is not set we're not happy ------------------------------
:checkJava

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto checkServer

:noJavaHome
echo "You must set the JAVA_HOME variable before running CARBON."
goto end

rem ----- Only set CARBON_HOME if not already set ----------------------------
:checkServer
rem %~sdp0 is expanded pathname of the current script under NT with spaces in the path removed
set CARBON_HOME=%~sdp0..
echo "%CARBON_HOME%\bin\version.txt"
SET curDrive=%cd:~0,1%
SET wsasDrive=%CARBON_HOME:~0,1%
if not "%curDrive%" == "%wsasDrive%" %wsasDrive%:

rem find CARBON_HOME if it does not exist due to either an invalid value passed
rem by the user or the %0 problem on Windows 9x
if not exist "%CARBON_HOME%\bin\version.txt" goto noServerHome

goto startServer

:noServerHome
echo CARBON_HOME is set incorrectly or CARBON could not be located. Please set CARBON_HOME.
echo %CARBON_HOME%
goto end

:startServer
%CARBON_HOME%\wso2\dashboard\bin\carbon.bat %*

:end
goto endlocal

:endlocal
:END