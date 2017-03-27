@echo off

REM ---------------------------------------------------------------------------
REM        Copyright 2005-2009 WSO2, Inc. http://www.wso2.org
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
rem Main Script for WSO2 Carbon
rem
rem Environment Variable Prequisites
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

set CARBON_DUMP_HOME=%~sdp0..
SET curDirectory=%cd%
SET curDrive=%cd:~0,1%
SET wsasDrive=%CARBON_DUMP_HOME:~0,1%
if not "%curDrive%" == "%wsasDrive%" %wsasDrive%:

goto processInputs

:noJavaHome
echo "You must set the JAVA_HOME variable before running CARBON."
goto end

rem ----- Process the input command -------------------------------------------
:processInputs
set CARBON_HOME=
set PID=

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).

:setupArgs
if ""%1""=="""" goto invalidUsage

:checkCarbonHome
if ""%1""==""-carbonHome""   goto setupCarbonHome
if ""%1""==""--carbonHome""  goto setupCarbonHome
if ""%1""==""carbonHome""    goto setupCarbonHome

:checkPID
if ""%1""==""-pid""     goto setupPID
if ""%1""==""--pid""    goto setupPID
if ""%1""==""pid""      goto setupPID

if "%CARBON_HOME%"=="" goto invalidUsage
if "%PID%"=="" goto invalidUsage
goto startCollectingData

rem ----- setupCarbonHome-------------------------------------------------------
:setupCarbonHome
shift
set CARBON_HOME=%1
if "%CARBON_HOME%"=="" goto invalidUsage
shift
goto checkPID

rem ----- setupPID-------------------------------------------------------
:setupPID
shift
set PID=%1
if "%PID%"=="" goto invalidUsage
shift
goto checkCarbonHome

:startCollectingData
echo Start Collectiong data


SET DATE_TIME=%date:~-4,4%-%date:~-10,2%-%date:~-7,2%_%time:~0,2%-%time:~3,2%
echo %DATE_TIME%

set OUTPUT_ROOT_DIR=%CARBON_DUMP_HOME%\carbondump
set OUTPUT_DIR=%OUTPUT_ROOT_DIR%\carbondump-%DATE_TIME%
set MEMORY_DUMP_DIR=%OUTPUT_DIR%\memoryinfo
set OS_INFO=%OUTPUT_DIR%\osinfo
set JAVA_INFO=%OUTPUT_DIR%\javainfo
set REPO_DIR=%OUTPUT_DIR%\repository

if exist %OUTPUT_ROOT_DIR% rd /q/s %OUTPUT_ROOT_DIR%

mkdir %OUTPUT_ROOT_DIR%
mkdir %OUTPUT_DIR%
mkdir %MEMORY_DUMP_DIR%
mkdir %OS_INFO%
mkdir %REPO_DIR%
mkdir %REPO_DIR%\logs
mkdir %REPO_DIR%\conf
mkdir %REPO_DIR%\database

echo carbondump.bat##Generating the java memory dump...
jmap -dump:format=b,file=%MEMORY_DUMP_DIR%\java_heap_memory_dump.jmap %PID%
jmap -histo %PID% > %MEMORY_DUMP_DIR%\java_heap_histogram.txt
REM jmap -finalizerinfo %PID% > %MEMORY_DUMP_DIR%\objects_awaiting_finalization.txt
REM jmap -heap %PID% >> %MEMORY_DUMP_DIR%\java_heap_summary.txt
REM jmap -permstat %PID% >> %MEMORY_DUMP_DIR%\java_permgen_statistics.txt

echo carbondump.bat##Generating the thread dump...
jstack %PID% > %OUTPUT_DIR%\thread_dump.txt

echo carbondump.bat##Capturing system configuration information...
systeminfo > %OS_INFO%\system_information.txt

echo carbondump.bat##Capturing information about the network connection and IP information...
ipconfig /all > %OS_INFO%\network_connection_information.txt

echo carbondump.bat##Capturing information on active TCP connections, ports on which the computer is listening...
netstat -a -o > %OS_INFO%\activec_connections.txt

echo carbondump.bat##Capturing information list of running tasks...
tasklist /v > %OS_INFO%\running_tasks_list.txt

echo carbondump.bat##Directory structure...
echo Use the command "type directory_structure.txt" to view the content properly > %OUTPUT_DIR%\directory_structure.txt
tree >> %OUTPUT_DIR%\directory_structure.txt

echo carbondump.bat##Capturing OS Environment Variables...
set > %OS_INFO%\os_env_variables.txt

REM echo "\ncarbondump.sh##Generating the checksums of all the files in the CARBON_HOME directory..."
REM check fciv is exit or not
where fciv > temp.txt
if errorlevel==1 goto noFCIV
set /p _fciv=< temp.txt
if not exist %_fciv% goto noFCIV
fciv %CARBON_HOME% -r -md5 > %OUTPUT_DIR%\checksum_values.txt
goto endmd5

:noFCIV
echo  File Checksum Integrity Verifier(FCIV) doesn't exist in class path
goto endmd5

:endmd5
del temp.txt

REM ##TODO out all the carbon info to a single file, java, vesion, os version, carbon version
REM echo "Product"'\t\t\t'": "`cat $CARBON_HOME/bin/version.txt` > $OUTPUT_DIR/carbon_server_info.txt
REM echo "WSO2 Carbon Framework"'\t'": "`cat $CARBON_HOME/bin/wso2carbon-version.txt` >> $OUTPUT_DIR/carbon_server_info.txt
REM echo "Carbon Home"'\t\t'": "`echo $CARBON_HOME` >> $OUTPUT_DIR/carbon_server_info.txt
REM echo "Operating System Info"'\t'": "`uname -a` >> $OUTPUT_DIR/carbon_server_info.txt
REM echo "Java Home"'\t\t'": "`echo $JAVA_HOME` >> $OUTPUT_DIR/carbon_server_info.txt
REM java -version 2> $OUTPUT_DIR/temp_java_version.txt
REM echo "Java Version"'\t\t'": "`cat $OUTPUT_DIR/temp_java_version.txt | grep -h "java version" |  mawk '{print $3}'` >> $OUTPUT_DIR/carbon_server_info.txt
REM echo "Java VM"'\t\t\t'": "`cat $OUTPUT_DIR/temp_java_version.txt | grep -h "Java HotSpot"` >> $OUTPUT_DIR/carbon_server_info.txt
REM rm -rf $OUTPUT_DIR/temp_java_version.txt

echo carbondump.bat##Copying log files...
copy  %CARBON_HOME%\repository\logs\ %REPO_DIR%\logs

echo carbondump.bat##Copying conf files...
copy  %CARBON_HOME%\conf\ %REPO_DIR%\conf

echo carbondump.bat##Copying database...
copy  %CARBON_HOME%\repository\database\ %REPO_DIR%\database

echo carbondump.bat##Compressing the carbondump...
cd %OUTPUT_ROOT_DIR%
if exist %CARBON_DUMP_HOME%\carbondump_%DATE_TIME%.zip del %CARBON_DUMP_HOME%\carbondump_%DATE_TIME%.zip
jar cvf %CARBON_DUMP_HOME%\carbondump_%DATE_TIME%.jar carbondump-%DATE_TIME%
cd %CARBON_DUMP_HOME%
rename carbondump_%DATE_TIME%.jar carbondump_%DATE_TIME%.zip

echo carbondump: %CARBON_DUMP_HOME%\carbondump_%DATE_TIME%.zip
rd /q/s %OUTPUT_ROOT_DIR%
cd %curDirectory%

goto end

:invalidUsage
echo Usage: carbondump.bat [-carbonHome path] [-pid of the carbon instance]
echo   e.g. carbondump.bat -carbonHome C:\user\wso2carbon-3.2.0\ -pid 5151

:END
