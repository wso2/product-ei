@echo OFF

REM ---------------------------------------------------------------------------
REM        Copyright 2017 WSO2, Inc. http://www.wso2.org
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
REM ---------------------------------------------------------------------------
REM Profile creator tool for EI
REM ---------------------------------------------------------------------------

set DIR=%~dp0
set DISTRIBUTION=wso2ei-@product.version@
REM get the desired profile
echo ***********************************************************************************
echo This tool will erase all the files which are not required for the selected profile.
echo WARNING:This may cause loss of any changes to the other profiles.
echo ***********************************************************************************
echo WSO2 Enterprise Integrator Supports following profiles.
echo 	1.Integrator profile
echo 	2.Analytics Profile
echo 	3.Business Process profile
echo 	4.Broker profile

set /p profileNumber= [Please enter the desired profile number to create the profile specific distribution]

IF /I "%profileNumber%" EQU "1" goto Integrator
IF /I "%profileNumber%" EQU "2" goto Analytics
IF /I "%profileNumber%" EQU "3" goto BPS
IF /I "%profileNumber%" EQU "4" goto Broker

echo Invalid profile identifier.
goto Exit

:Integrator
	echo Preparing the Integrator profile distribution.
	set DEFAULT_BUNDLES=%DIR%..\wso2\components\default\configuration\org.eclipse.equinox.simpleconfigurator\bundles.info
    call :Remove_BPS
    call :Remove_BROKER
    call :Remove_ANALYTICS
    call :Remove_JARS
    echo Integrator profile created successfully.
	goto Exit

:Broker
    echo Preparing the Broker profile.
    set DEFAULT_BUNDLES=%DIR%..\wso2\components\broker-default\configuration\org.eclipse.equinox.simpleconfigurator\bundles.info
    call :Remove_BPS
    call :Remove_INTEGRATOR
    call :Remove_ANALYTICS
    call :Remove_JARS
    echo Broker profile created successfully.
    goto Exit

:BPS
    echo Preparing the Business Process profile.
    set DEFAULT_BUNDLES=%DIR%..\wso2\components\business-process-default\configuration\org.eclipse.equinox.simpleconfigurator\bundles.info
    call :Remove_BROKER
    call :Remove_INTEGRATOR
    call :Remove_ANALYTICS
    call :Remove_JARS
    echo Business Process profile created successfully.
    goto Exit

:Analytics
    echo Preparing the Analytics profile.
    call :Remove_BPS
    call :Remove_BROKER
    call :Remove_INTEGRATOR
    IF EXIST %DIR%\..\conf @RD /S /Q %DIR%\..\conf
    IF EXIST %DIR%\..\dbscripts @RD /S /Q %DIR%\..\dbscripts
    IF EXIST %DIR%\..\patches @RD /S /Q %DIR%\..\patches
    IF EXIST %DIR%\..\repository @RD /S /Q %DIR%\..\repository
    IF EXIST %DIR%\..\resources @RD /S /Q %DIR%\..\resources
    IF EXIST %DIR%\..\samples @RD /S /Q %DIR%\..\samples
    IF EXIST %DIR%\..\servicepacks @RD /S /Q %DIR%\..\servicepacks
    IF EXIST %DIR%\..\webapp-mode @RD /S /Q %DIR%\..\webapp-mode
    IF EXIST %DIR%\..\wso2\analytics @RD /S /Q %DIR%\..\wso2\msf4j
    IF EXIST %DIR%\..\wso2\broker @RD /S /Q %DIR%\..\wso2\broker
    IF EXIST %DIR%\..\wso2\business-process @RD /S /Q %DIR%\..\wso2\business-process
    IF EXIST %DIR%\..\wso2\tmp @RD /S /Q %DIR%\..\wso2\tmp
    echo Analytics profile created successfully.
    goto Exit

:Remove_BPS
    echo Removing Business Process profile
    IF EXIST %DIR%\..\wso2\business-process @RD /S /Q %DIR%\..\wso2\business-process
    IF EXIST %DIR%\..\samples\business-process @RD /S /Q %DIR%\..\samples\business-process
    IF EXIST %DIR%\..\wso2\components\business-process-default @RD /S /Q %DIR%\..\wso2\components\business-process-default
    IF EXIST %DIR%\business-process.bat del %DIR%\business-process.bat
    IF EXIST %DIR%\business-process.sh del %DIR%\business-process.sh
    goto :eof

:Remove_BROKER
    echo Removing Broker profile
    IF EXIST %DIR%\..\wso2\broker @RD /S /Q %DIR%\..\wso2\broker
    IF EXIST %DIR%\..\wso2\components\broker-default @RD /S /Q %DIR%\..\wso2\components\broker-default
    IF EXIST %DIR%\broker.bat del %DIR%\broker.bat
    IF EXIST %DIR%\broker.sh del %DIR%\broker.sh
    goto :eof

:Remove_ANALYTICS
    echo Removing Analytics profile
    IF EXIST %DIR%\..\wso2\analytics @RD /S /Q %DIR%\..\wso2\analytics
    IF EXIST %DIR%\..\wso2\components\analytics-default @RD /S /Q %DIR%\..\wso2\components\analytics-default
    IF EXIST %DIR%\analytics-worker.bat del %DIR%\analytics-worker.bat
    IF EXIST %DIR%\analytics-worker.sh del %DIR%\analytics-worker.sh
    IF EXIST %DIR%\analytics-dashboard.bat del %DIR%\analytics-dashboard.bat
    IF EXIST %DIR%\analytics-dashboard.sh del %DIR%\analytics-dashboard.sh
    goto :eof

:Remove_INTEGRATOR
    echo Removing Integrator profile
    IF EXIST %DIR%\..\conf @RD /S /Q %DIR%\..\conf
    IF EXIST %DIR%\..\wso2\components\default @RD /S /Q %DIR%\..\wso2\components\default
    IF EXIST %DIR%\..\samples\service-bus @RD /S /Q %DIR%\..\samples\service-bus
    IF EXIST %DIR%\integrator.bat del %DIR%\integrator.bat
    IF EXIST %DIR%\integrator.sh del %DIR%\integrator.sh
    IF EXIST %DIR%\wso2ei-samples.bat del %DIR%\wso2ei-samples.bat
    IF EXIST %DIR%\wso2ei-samples.sh del %DIR%\wso2ei-samples.sh
    goto :eof

:Remove_JARS
    echo Removing unnecessary jars
    mkdir %DIR%\..\wso2\components\tmp_plugins

    FOR /F "tokens=1,2* delims=, " %%i in (%DEFAULT_BUNDLES%) do copy %DIR%\..\wso2\components\plugins\%%i_%%j.jar %DIR%\..\wso2\components\tmp_plugins

    @RD /S /Q %DIR%\..\wso2\components\plugins
    rename %DIR%\..\wso2\components\tmp_plugins plugins
    goto :eof

:Exit
    pause
