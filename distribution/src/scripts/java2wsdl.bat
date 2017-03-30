@echo off
rem ---------------------------------------------------------------------------
rem Startup script for the WSDLJava
rem
rem Environment Variable Prequisites
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
if exist "%CARBON_HOME%\bin\Java2WSDL.bat" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set CARBON_HOME=%cd%
cd %CURRENT_DIR%

:gotHome
if exist "%CARBON_HOME%\bin\Java2WSDL.bat" goto okHome

set CARBON_HOME=%~dp0..
if exist "%CARBON_HOME%\bin\Java2WSDL.bat" goto okHome

echo The CARBON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
setlocal EnableDelayedExpansion
rem loop through the libs and add them to the class path
cd "%CARBON_HOME%"
set CARBON_CLASSPATH=.\lib\patches;.\conf

rem Run the setup script
call ant -buildfile "%CARBON_HOME%\bin\build.xml" -q

FOR %%c in ("%CARBON_HOME%\wso2\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;".\wso2\lib\%%~nc%%~xc"
FOR %%C in ("%CARBON_HOME%\repository\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;".\repository\lib\%%~nC%%~xC"

rem ----- Execute The Requested Command ---------------------------------------
echo Using CARBON_HOME:   %CARBON_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
set _RUNJAVA="%JAVA_HOME%\bin\java"

set CARBON_CLASSPATH=%CARBON_HOME%\conf;%CARBON_CLASSPATH%;
set JAVA_ENDORSED=".\wso2\lib\endorsed";"%JAVA_HOME%\jre\lib\endorsed";"%JAVA_HOME%\lib\endorsed"
%_RUNJAVA% %JAVA_OPTS% -cp "%CARBON_CLASSPATH%" -Dcarbon.config.dir.path="%CARBON_HOME%\conf" -Dcarbon.home="%CARBON_HOME%" -Djava.io.tmpdir="%CARBON_HOME%\wso2\tmp" -Djava.endorsed.dirs=%JAVA_ENDORSED%  org.apache.ws.java2wsdl.Java2WSDL %*
endlocal
:end

