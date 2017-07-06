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
if exist "%CARBON_HOME%\bin\processcleanuptool.bat" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set CARBON_HOME=%cd%
cd %CARBON_HOME%

:gotHome
if exist "%CARBON_HOME%\bin\processcleanuptool.bat" goto okHome

rem set CARBON_HOME=%~dp0..
if exist "%CARBON_HOME%\bin\processcleanuptool.bat" goto okHome

echo The CARBON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
setlocal EnableDelayedExpansion
rem loop through the libs and add them to the class path
cd "%CARBON_HOME%"


FOR %%C in ("%CARBON_HOME%\..\..\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;".\..\..\lib\%%~nC%%~xC"
FOR %%E in ("%CARBON_HOME%\..\components\plugins\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;".\..\components\plugins\%%~nE%%~xE"

rem ----- Execute The Requested Command ---------------------------------------
set _RUNJAVA="%JAVA_HOME%\bin\java"

rem set CARBON_CLASSPATH=.\..\..\lib;%CARBON_CLASSPATH%
set CARBON_CLASSPATH_CUSTOM=%CARBON_HOME%\..\components\plugins\*;%CARBON_HOME%\..\..\lib\*;%CARBON_HOME%\..\lib\*
set JAVA_ENDORSED=".\lib\bps-endorsed";"%JAVA_HOME%\jre\lib\endorsed";"%JAVA_HOME%\lib\endorsed"

set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -cp "%CARBON_CLASSPATH_CUSTOM%" -Dcarbon.home="%CARBON_HOME%" org.wso2.ei.businessprocess.utils.processcleanup.CleanupExecutor %*
cd bin
endlocal
:end
