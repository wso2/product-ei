#!/bin/bash
#------------------------------------------------------------------------
# Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at

# http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#-------------------------------------------------------------------------
# Profile creator tool for EI
#-------------------------------------------------------------------------



DIR="$(dirname "${BASH_SOURCE[0]}")"
DISTRIBUTION="wso2ei-6.0.0"
#get the desired profile
echo "This tool will erase all the files which are not required for the selected profile "
echo "and also creates a distribution of this profile in the same folder <EI_HOME> resides."
echo "WARNING:This may cause loss of any changes to the other profiles."
echo "WSO2 Enterprise Integrator Supports following profiles."
echo "	1.Integrator profile"
echo "	2.Analytics Profile"
echo "	3.Business Process profile"
echo "	4.Broker profile"
echo "Please enter the desired profile number to create the profile specific distribution."
read profileNumber
#Integrator profile
if [ ${profileNumber} -eq 1 ]
then
	echo "Preparing the Integrator profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	WORKER_BUNDLES="$(< ${DIR}/../wso2/components/worker/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove business-process
	echo "Removing Business Process profile"
	rm -rf ${DIR}/../wso2/business-process
	rm -rf ${DIR}/../samples/business-process
	rm -rf ${DIR}/../wso2/components/business-process-default ${DIR}/../wso2/components/business-process-worker
	rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh
	#remove broker
	echo "Removing Broker profile"
	rm -rf ${DIR}/../wso2/broker
	rm -rf ${DIR}/../wso2/components/broker-default ${DIR}/../wso2/components/broker-worker
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh
	#remove analytics
	echo "Removing Analytics profile"
	rm -rf ${DIR}/../wso2/analytics
	rm -rf ${DIR}/../wso2/components/analytics-default ${DIR}/../wso2/components/analytics-worker
	rm -rf ${DIR}/analytics.bat
	rm -rf ${DIR}/analytics.sh
		
	PROFILE="_integrator"

#Analytics profile
elif [ ${profileNumber} -eq 2 ]
then
	echo "Preparing the Analytics profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/analytics-default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	WORKER_BUNDLES="$(< ${DIR}/../wso2/components/analytics-worker/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove business-process profile
	echo "Removing Business Process profile"
	rm -rf ${DIR}/../wso2/business-process
	rm -rf ${DIR}/../wso2/components/business-process-default ${DIR}/../wso2/components/business-process-worker
	rm -rf ${DIR}/../samples/business-process
	rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh
	#remove broker
	echo "Removing Broker profile"
	rm -rf ${DIR}/../wso2/broker
	rm -rf ${DIR}/../wso2/components/broker-default ${DIR}/../wso2/components/broker-worker
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh
	#remove intergrator
	echo "Removing Integrator profile"
	rm -rf ${DIR}/../conf
	rm -rf ${DIR}/../wso2/components/default ${DIR}/../wso2/components/worker
	rm -rf ${DIR}/../samples/service-bus/data-services
	rm -rf ${DIR}/integrator.bat
	rm -rf ${DIR}/integrator.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh

	PROFILE="_analytics"

elif [ ${profileNumber} -eq 3 ]
then
	echo "Preparing the Business Process profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/business-process-default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	WORKER_BUNDLES="$(< ${DIR}/../wso2/components/business-process-worker/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove broker
	echo "Removing Broker profile"
	rm -rf ${DIR}/../wso2/broker
	rm -rf ${DIR}/../wso2/components/broker-default ${DIR}/../wso2/components/broker-worker
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh
        #remove intergrator
	echo "Removing Integrator profile"
	rm -rf ${DIR}/../conf
	rm -rf ${DIR}/../wso2/components/default ${DIR}/../wso2/components/worker
	rm -rf ${DIR}/../samples/service-bus/data-services
	rm -rf ${DIR}/integrator.bat
	rm -rf ${DIR}/integrator.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh
	#remove analytics
	echo "Removing Analytics profile"
	rm -rf ${DIR}/../wso2/analytics
	rm -rf ${DIR}/../wso2/components/analytics-default ${DIR}/../wso2/components/analytics-worker
	rm -rf ${DIR}/analytics.bat
	rm -rf ${DIR}/analytics.sh

	PROFILE="_businessprocess"

elif [ ${profileNumber} -eq 4 ]
then
	echo "Preparing the Broker profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/broker-default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	WORKER_BUNDLES="$(< ${DIR}/../wso2/components/broker-worker/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove analytics
	echo "Removing Analytics profile"
	rm -rf ${DIR}/../wso2/analytics
	rm -rf ${DIR}/../wso2/components/analytics-default ${DIR}/../wso2/components/analytics-worker
	rm -rf ${DIR}/analytics.bat
	rm -rf ${DIR}/analytics.sh
	#remove intergrator
	echo "Removing Integrator profile"
        rm -rf ${DIR}/../conf
        rm -rf ${DIR}/../wso2/components/default ${DIR}/../wso2/components/worker
        rm -rf ${DIR}/../samples/service-bus/data-services
        rm -rf ${DIR}/integrator.bat
        rm -rf ${DIR}/integrator.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh
	#remove business-process profile
	echo "Removing Business Process profile"
	rm -rf ${DIR}/../wso2/business-process
	rm -rf ${DIR}/../wso2/components/business-process-default ${DIR}/../wso2/components/business-process-worker
	rm -rf ${DIR}/../samples/business-process
	rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh

	PROFILE="_broker"

else
	echo "Invalid profile number. Terminating."
	exit 3
fi

#remove start all scripts
rm -rf ${DIR}/start-all.sh
rm -rf ${DIR}/start-all.bat

#remove unnecessary jar files
mkdir -p ${DIR}/../wso2/components/tmp_plugins

echo "Removing unnecessary jars from plugins folder."
for BUNDLE in $DEFAULT_BUNDLES; do
	IFS=',' read -a bundleArray <<< "$BUNDLE"
	JAR=${bundleArray[0]}_${bundleArray[1]}.jar
	cp ${DIR}/../wso2/components/plugins/${JAR} ${DIR}/../wso2/components/tmp_plugins
    done

for BUNDLE in $WORKER_BUNDLES; do
	IFS=',' read -a bundleArray <<< "$BUNDLE"
	JAR=${bundleArray[0]}_${bundleArray[1]}.jar
	cp ${DIR}/../wso2/components/plugins/${JAR} ${DIR}/../wso2/components/tmp_plugins
    done

rm -r ${DIR}/../wso2/components/plugins
mv ${DIR}/../wso2/components/tmp_plugins ${DIR}/../wso2/components/plugins

echo "Preparing a profile distribution archive."
cd ${DIR}/../../
zip -r ${DISTRIBUTION}${PROFILE}.zip ${DISTRIBUTION}/

echo "Profile creation completed successfully."
exit 0
