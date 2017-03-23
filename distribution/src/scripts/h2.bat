@echo off
rem ----------------------------------------------------------------------------
rem  Copyright 2016 WSO2, Inc. http://www.wso2.org
rem
rem  Licensed under the Apache License, Version 2.0 (the "License");
rem  you may not use this file except in compliance with the License.
rem  You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem  Unless required by applicable law or agreed to in writing, software
rem  distributed under the License is distributed on an "AS IS" BASIS,
rem  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem  See the License for the specific language governing permissions and
rem  limitations under the License.
rem ---------------------------------------------------------------------------
rem Startup script for the h2 client
rem
rem Environment Variable Prequisites
rem
rem   CARBON_HOME      Must point at your WSO2 Carbon directory
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options
rem ---------------------------------------------------------------------------
set CURRENT_DIR=%cd%

rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo The JAVA_HOME environment variable is not defined
echo This environment variable is needed to run this program
goto end
:gotJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto okJavaHome
:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
echo NB: JAVA_HOME should point to a JDK/JRE
goto end
:okJavaHome

rem check the CARBON_HOME environment variable
if not "%CARBON_HOME%" == "" goto gotHome
set CARBON_HOME=%CURRENT_DIR%
if exist "%CARBON_HOME%\bin\h2.bat" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set CARBON_HOME=%cd%
cd %CURRENT_DIR%

:gotHome
if exist "%CARBON_HOME%\bin\h2.bat" goto okHome

set CARBON_HOME=%~dp0..
if exist "%CARBON_HOME%\bin\h2.bat" goto okHome

echo The CARBON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
setlocal EnableDelayedExpansion
set CARBON_CLASSPATH=%CARBON_HOME%
set CARBON_CLASSPATH=%CARBON_HOME%\wso2\components\plugins\h2_1.3.175.wso2v1.jar;%CARBON_CLASSPATH%

rem ----- Execute The Requested Command ---------------------------------------
echo Using CARBON_HOME:   %CARBON_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -cp "%CARBON_CLASSPATH%" org.h2.tools.Server
endlocal
:end
