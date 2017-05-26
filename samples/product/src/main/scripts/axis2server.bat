@echo off

REM  Copyright 2001,2004-2005 The Apache Software Foundation
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
rem Startup script for the Simple Axis Server (with default parameters)
rem
rem Environment Variable Prequisites
rem
rem   AXIS2_HOME      Must point at your AXIS2 directory
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options
rem ---------------------------------------------------------------------------

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
if "%AXIS2_HOME%"=="" set AXIS2_HOME=%~dps0

rem find AXIS2_HOME if it does not exist due to either an invalid value passed
rem by the user or the %0 problem on Windows 9x

if exist "%AXIS2_HOME%\repository\conf\axis2.xml" goto checkJava

:noAxis2Home
echo AXIS2_HOME environment variable is set incorrectly or AXIS2 could not be located.
echo Please set the AXIS2_HOME variable appropriately
goto end

:checkJava
set _JAVACMD=%JAVACMD%
set _HTTPPORT=
set _HTTPSPORT=
set _SERVERNAME=

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe

:setupArgs
if ""%1""=="""" goto runAxis2
if ""%1""==""-http"" goto httpport
if ""%1""==""-https"" goto httpsport
if ""%1""==""-name"" goto servername
if ""%1""==""-xdebug"" goto xdebug
shift
goto setupArgs

rem is a custom port specified
:httpport
shift
set _HTTPPORT="-Dhttp_port=%1"
shift
goto setupArgs

:httpsport
shift
set _HTTPSPORT="-Dhttps_port=%1"
shift
goto setupArgs

:servername
shift
set _SERVERNAME="-Dserver_name=%1"
shift
goto setupArgs

rem is there is a -xdebug in the options
:xdebug
set _XDEBUG="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
shift
goto setupArgs

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
echo JAVA_HOME environment variable is set incorrectly or Java runtime could not be located.
echo Please set the JAVA_HOME variable appropriately
goto end


:runAxis2

setlocal EnableDelayedExpansion
cd %AXIS2_HOME%
echo "Starting Sample Axis2 Server ..."
echo Using AXIS2_HOME:        %AXIS2_HOME%
echo Using JAVA_HOME:       %JAVA_HOME%


if "%CARBON_HOME%"=="" set CARBON_HOME=%~sdp0..\..
set CMD=RUN %*

rem Set all jar folders to CARBON_CLASSPATH variable. 
set CARBON_CLASSPATH=..\..\wso2\lib\*;%CARBON_CLASSPATH%
set CARBON_CLASSPATH=..\..\lib;%CARBON_CLASSPATH%
set CARBON_CLASSPATH=..\..\wso2\components\plugins\*;%CARBON_CLASSPATH%
set CARBON_CLASSPATH=..\..\wso2\lib\core\WEB-INF\lib;%CARBON_CLASSPATH%
set CARBON_CLASSPATH=..\..\extensions;%CARBON_CLASSPATH%
set CARBON_CLASSPATH=..\..\wso2\lib\endorsed;%CARBON_CLASSPATH%
set CARBON_CLASSPATH=..\..\repository\axis2\client\lib\bcprov-jdk15on.jar;%CARBON_CLASSPATH%

set confpath=%AXIS2_HOME%repository\conf\axis2.xml
set AXIS2_ENDORSED=%AXIS2_HOME%..\..\wso2\lib\endorsed

rem Assign synapse-samples*.jar folders to SAMPLE_SERVERPATH variable. Since it is the jar contains the SampleServer classes.
FOR %%C in ("%CARBON_HOME%\wso2\components\plugins\synapse-samples*.jar") DO set SAMPLE_SERVERPATH=!SAMPLE_SERVERPATH, %CARBON_HOME%\wso2\components\plugins\%%~nC%%~xC

rem We use <code> samples.util.Bootstrap</code> to avoid long classpath windows OS issue to start the server. We pass the the jar files location as 
rem -Djar.class.paths=%CARBON_CLASSPATH% as a system property.(It is a MUST) Additionally we pass -Dsystem.home="."  property which is set to current directory.

"%JAVA_HOME%\bin\java"  -Xms256m -Xmx512m -XX:MaxPermSize=256m  -classpath "%SAMPLE_SERVERPATH%;%CARBON_CLASSPATH%" -Djava.io.tmpdir="%AXIS2_HOME%..\..\wso2\tmp" %_SERVERNAME% %_HTTPPORT% %_HTTPSPORT% %_XDEBUG% -Djava.endorsed.dirs="%AXIS2_ENDORSED%"  -Djar.class.paths=%CARBON_CLASSPATH% -Dsystem.home="."  samples.util.SampleAxis2Server  -repo "%AXIS2_HOME%\repository" -conf "%AXIS2_HOME%\repository\conf\axis2.xml"

:end
set _JAVACMD=
set AXIS2_CMD_LINE_ARGS=

if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal

:mainEnd

