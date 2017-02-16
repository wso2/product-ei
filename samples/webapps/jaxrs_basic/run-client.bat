@echo off
REM ---------------------------------------------------------------------------
REM  Copyright 2005,2006 WSO2, Inc. http://www.wso2.org
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
rem Client script for the Jaxws-Jaxrs/jaxrs_basic Sample 
rem
rem Environment Variable Prequisites
rem
rem   WSO2AppServer_HOME      Must point at your WSO2 AppServer directory
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

rem check the WSO2AppServer_HOME environment variable
if not "%WSO2AppServer_HOME%" == "" goto gotHome
set WSO2AppServer_HOME=%CURRENT_DIR%
if exist "%WSO2AppServer_HOME\bin\version.txt" goto okHome

rem guess the home. Jump two directories up to check if that is the home
cd ..\..\..
set WSO2AppServer_HOME=%cd%
cd %CURRENT_DIR%

:gotHome
if exist "%WSO2AppServer_HOME%\bin\version.txt" goto okHome

set WSO2AppServer_HOME=%~dp0..\..
if exist "%WSO2AppServer_HOME%\bin\version.txt" goto okHome

echo The WSO2AppServer_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
setlocal EnableDelayedExpansion
rem loop through the libs and add them to the class path
cd %WSO2AppServer_HOME%\samples\Jaxws-Jaxrs\jaxrs_basic
set CLIENT_CLASSPATH=.\conf;.\build\classes
FOR %%C in (.\build\lib\*.jar) DO set CLIENT_CLASSPATH=!CLIENT_CLASSPATH!;.\build\lib\%%~nC%%~xC

rem ----- Execute The Requested Command ---------------------------------------
echo Using WSO2AppServer_HOME:   %WSO2AppServer_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -Dwso2appserver.home="%WSO2AppServer_HOME%" -cp "%CLIENT_CLASSPATH%" -Djava.endorsed.dirs="%WSO2AppServer_HOME%\lib\endorsed";"%JAVA_HOME%\jre\lib\endorsed";"%JAVA_HOME%\lib\endorsed" demo.jaxrs.client.Client http://localhost:9763/jaxrs_basic/services/customers/customerservice %*
cd %CURRENT_DIR%
endlocal
:end
