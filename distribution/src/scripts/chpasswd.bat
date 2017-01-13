@echo off
rem ---------------------------------------------------------------------------
rem Script for changing the password of a CARBON user
rem
rem Environment Variable Prequisites
rem
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

set CURRENT_DIR = %cd%
rem check the CARBON_HOME environment variable
if not "%CARBON_HOME%" == "" goto gotHome
set CARBON_HOME=%CURRENT_DIR%
if exist "%CARBON_HOME\bin\version.txt" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set CARBON_HOME=%cd%
cd %CURRENT_DIR% >> NULL

:gotHome
if not exist "%CARBON_HOME%\bin\version.txt" goto pathError

SET curDrive=%cd:~0,1%
SET wsasDrive=%CARBON_HOME:~0,1%
if not "%curDrive%" == "%wsasDrive%" %wsasDrive%:

goto okHome

set CARBON_HOME=%~dp0..
if exist "%CARBON_HOME%\bin\version.txt" goto okHome

:pathError
echo The CARBON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
setlocal EnableDelayedExpansion

rem loop through the libs and add them to the class path
cd %CARBON_HOME%

call ant -buildfile "%CARBON_HOME%"\bin\build.xml

set CARBON_CLASSPATH=%CARBON_HOME%
FOR %%C in ("%CARBON_HOME%\wso2\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;./wso2/lib/%%~nC%%~xC
FOR %%C in ("%CARBON_HOME%\wso2\lib\api*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;./wso2/lib/api/%%~nC%%~xC
FOR %%C in ("%CARBON_HOME%\repository\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;./repository/lib/%%~nC%%~xC
set CARBON_CLASSPATH=.\lib\patches;.\conf;%CARBON_CLASSPATH%
rem ----- Execute The Requested Command ---------------------------------------
set _RUNJAVA="%JAVA_HOME%\bin\java"

%_RUNJAVA% %JAVA_OPTS% -cp "%CARBON_CLASSPATH%" org.wso2.carbon.core.util.PasswordUpdater %*
endlocal
if not "%curDrive%" == "%wsasDrive%" %curDrive%:
:end
