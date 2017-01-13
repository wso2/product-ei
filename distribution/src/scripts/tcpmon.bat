@echo off
rem ---------------------------------------------------------------------------
rem Startup script for the tcpmon
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
if exist "%CARBON_HOME%\bin\tcpmon.bat" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set CARBON_HOME=%cd%
cd %CURRENT_DIR%

:gotHome
if exist "%CARBON_HOME%\bin\tcpmon.bat" goto okHome

set CARBON_HOME=%~dp0..
if exist "%CARBON_HOME%\bin\tcpmon.bat" goto okHome

echo The CARBON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
setlocal EnableDelayedExpansion
set CARBON_CLASSPATH=%CARBON_HOME%
set CARBON_CLASSPATH=%CARBON_HOME%\lib\patches;%CARBON_HOME%\conf;%CARBON_HOME%\bin\tcpmon-1.0.jar;%CARBON_CLASSPATH%

rem ----- Execute The Requested Command ---------------------------------------
echo Using CARBON_HOME:   %CARBON_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -cp "%CARBON_CLASSPATH%" org.apache.ws.commons.tcpmon.TCPMon
endlocal
:end
