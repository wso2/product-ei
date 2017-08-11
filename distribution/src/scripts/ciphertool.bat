@echo off
REM Copyright 2005-2009 WSO2, Inc. (http://wso2.com)
REM Licensed to the Apache Software Foundation (ASF) under one
REM or more contributor license agreements.  See the NOTICE file
REM distributed with this work for additional information
REM regarding copyright ownership.  The ASF licenses this file
REM to you under the Apache License, Version 2.0 (the
REM "License"); you may not use this file except in compliance
REM with the License.  You may obtain a copy of the License at
REM
REM    http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing,
REM software distributed under the License is distributed on an
REM  # "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM KIND, either express or implied.  See the License for the
REM specific language governing permissions and limitations
REM under the License.

rem ---------------------------------------------------------------------------
rem Startup script for the ciphertool
rem
rem Environment Variable Prerequisites
rem
rem   CARBON_HOME      Must point at your CARBON directory
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options
rem ---------------------------------------------------------------------------

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
setlocal EnableDelayedExpansion
set CURRENT_DIR=%cd%
if not "%CARBON_HOME%" == "" goto gotHome
set CARBON_HOME=%CURRENT_DIR%
if exist "%CARBON_HOME%\bin\ciphertool.bat" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set CARBON_HOME=%cd%
cd %CARBON_HOME%

:gotHome
if exist "%CARBON_HOME%\bin\ciphertool.bat" goto okHome

rem set CARBON_HOME=%~sdp0..
set CARBON_HOME=%~sdp0..
if exist "%CARBON_HOME%\bin\ciphertool.bat" goto okHome

echo The CARBON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
rem loop through the libs and add them to the class path
cd "%CARBON_HOME%"

set CARBON_CLASSPATH=.\conf
FOR %%c in ("%CARBON_HOME%\wso2\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;".\wso2\lib\%%~nc%%~xc"
FOR %%C in ("%CARBON_HOME%\repository\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;".\repository\lib\%%~nC%%~xC"

rem ----- Execute The Requested Command ---------------------------------------
echo Using CARBON_HOME:   %CARBON_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -Dcarbon.home="%CARBON_HOME%" -cp "%CARBON_CLASSPATH%" org.wso2.ciphertool.CipherTool %*
endlocal
:end
