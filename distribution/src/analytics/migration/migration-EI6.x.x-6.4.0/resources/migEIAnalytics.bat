@echo off

REM ---------------------------------------------------------------------------
REM   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
REM
REM   Licensed under the Apache License, Version 2.0 (the "License");
REM   you may not use this file except in compliance with the License.
REM   You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM   Unless required by applicable law or agreed to in writing, software
REM   distributed under the License is distributed on an "AS IS" BASIS,
REM   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM   See the License for the specific language governing permissions and
REM   limitations under the License.
REM ---------------------------------------------------------------------------

:while
echo Database types...
echo 1) MYSQL
echo 2) ORACLE
echo 3) POSTGRESQL
echo 4) MSSQL
set /p num="Enter no. of your database type: "
if %num%==1 goto :one
if %num%==2 goto :two
if %num%==3 goto :three
if %num%==4 goto :four
goto :while
	
:one
set dbType=MYSQL
set defaultPort=3306
goto :next

:two
set dbType=ORACLE
set defaultPort=1521
goto :next

:three
set dbType=POSTGRESQL
set defaultPort=5432
goto :next

:four
set dbType=MSSQL
set defaultPort=1433
goto :next

:next
echo ----Click enter to use default values----
set /p host="Enter your database host : "
IF NOT DEFINED host SET host=localhost
echo %host% 

set /p port="Enter your database port : "
IF NOT DEFINED port SET port=%defaultPort%
echo %port%

set /p dbName="Enter your database name : "
IF NOT DEFINED dbName SET dbName=EI_ANALYTICS
echo %dbName%

set /p user="Enter your database user : "
IF NOT DEFINED user SET user=root
echo %user%

set /p password="Enter your database password : "
IF NOT DEFINED password SET password=root
echo %password%

set /p dbDriver="Enter your database driver location [Absolute path] : "
IF NOT DEFINED dbDriver SET dbDriver=\
echo %dbDriver%

java -jar migEI.one-jar.jar %dbType% %host% %port% %dbName% %user% %password% %dbDriver%

pause
