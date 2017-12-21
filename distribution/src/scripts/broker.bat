@echo off

REM ---------------------------------------------------------------------------
REM  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
REM
REM  WSO2 Inc. licenses this file to you under the Apache License,
REM  Version 2.0 (the "License"); you may not use this file except
REM  in compliance with the License.
REM  You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM  Unless required by applicable law or agreed to in writing,
REM  software distributed under the License is distributed on an
REM  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM  KIND, either express or implied. See the License for the
REM  specific language governing permissions and limitations
REM  under the License.
REM
REM ---------------------------------------------------------------------------

rem ----- if JAVA_HOME is not set we're not happy ------------------------------
:checkJava
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto checkServer

:noJavaHome
echo "You must set the JAVA_HOME variable before running the Broker"
goto end

rem ----- set MESSAGE_BROKER_HOME ----------------------------
:checkServer
rem %~sdp0 is expanded pathname of the current script under NT with spaces in the path removed
set MESSAGE_BROKER_HOME=%~sdp0..\wso2\broker
SET curDrive=%cd:~0,1%
SET brokerDrive=%MESSAGE_BROKER_HOME:~0,1%
if not "%curDrive%" == "%brokerDrive%" %brokerDrive%:

goto updateClasspath

:noServerHome
echo MESSAGE_BROKER_HOME is set incorrectly or MESSAGE_BROKER could not be located. Please set MESSAGE_BROKER_HOME.
goto end

rem ----- update classpath -----------------------------------------------------
:updateClasspath
setlocal EnableDelayedExpansion
set MESSAGE_BROKER_CLASSPATH=
FOR %%C in ("%MESSAGE_BROKER_HOME%\lib\*.jar") DO set MESSAGE_BROKER_CLASSPATH=!MESSAGE_BROKER_CLASSPATH!;"%MESSAGE_BROKER_HOME%\lib\%%~nC%%~xC"

rem ----- Process the input command -------------------------------------------
:setupArgs
if ""%1""=="""" goto doneStart

:doneStart
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal
goto runServer

rem ----------------- Execute The Requested Command ----------------------------

:runServer
set CMD=%*

rem ---------- Add jars to classpath ----------------
set MESSAGE_BROKER_CLASSPATH=.\lib;%MESSAGE_BROKER_CLASSPATH%

set CMD_LINE_ARGS=-Xbootclasspath/a:%MESSAGE_BROKER_XBOOTCLASSPATH% -Xms256m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="%MESSAGE_BROKER_HOME%\heap-dump.hprof" -classpath %MESSAGE_BROKER_CLASSPATH% %JAVA_OPTS% -Dmessage.broker.home="%MESSAGE_BROKER_HOME%" -Djava.command="%JAVA_HOME%\bin\java" -Djava.opts="%JAVA_OPTS%" -Dlog4j.configuration="file:%MESSAGE_BROKER_HOME%\conf\log4j.properties" -Dbroker.config="%MESSAGE_BROKER_HOME%\conf\broker.yaml" -Dbroker.classpath=%MESSAGE_BROKER_CLASSPATH% -Dfile.encoding=UTF8

:runJava
"%JAVA_HOME%\bin\java" %CMD_LINE_ARGS% org.wso2.broker.Main %CMD%

:end