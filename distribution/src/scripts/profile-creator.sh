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

PRG="$0"

while [ -h "$PRG" ]; do
	  ls=`ls -ld "$PRG"`
	  link=`expr "$ls" : '.*-> \(.*\)$'`
	  if expr "$link" : '.*/.*' > /dev/null; then
		 PRG="$link"
	  else
		 PRG=`dirname "$PRG"`/"$link"
	  fi
done

# Get standard environment variables
DIR=`dirname "$PRG"`
DISTRIBUTION="wso2ei-${product.ei.version}"
#get the desired profile
echo "*************************************************************************************"
echo "This tool will erase all the files which are not required for the selected profile "
echo "and also creates a distribution of this profile in the same folder <EI_HOME> resides."
echo "WARNING:This may cause loss of any changes to the other profiles."
echo "*************************************************************************************"
echo "WSO2 Enterprise Integrator Supports following profiles."
echo "	1.Integrator profile"
echo "	2.Analytics Profile"
echo "	3.Business Process profile"
echo "	4.Broker profile"
echo "	5.Msf4j profile"
echo "Please enter the desired profile number to create the profile specific distribution."
read profileNumber
#Integrator profile
if [ ${profileNumber} -eq 1 ]
then
	echo "Preparing the Integrator profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove business-process
	echo "Removing Business Process profile"
	rm -rf ${DIR}/../wso2/business-process
	rm -rf ${DIR}/../samples/business-process
	rm -rf ${DIR}/../wso2/components/business-process-default
	rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh
	#remove broker
	echo "Removing Broker profile"
	rm -rf ${DIR}/../wso2/broker
	rm -rf ${DIR}/../wso2/components/broker-default
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh
	#remove analytics
	echo "Removing Analytics profile"
	rm -rf ${DIR}/../wso2/analytics
	rm -rf ${DIR}/../wso2/components/analytics-default
	rm -rf ${DIR}/analytics-worker.bat
	rm -rf ${DIR}/analytics-worker.sh
	rm -rf ${DIR}/analytics-dashboard.bat
	rm -rf ${DIR}/analytics-dashboard.sh
	#remove msf4j
	rm -rf ${DIR}/../wso2/msf4j

	PROFILE="_integrator"

#Analytics profile
elif [ ${profileNumber} -eq 2 ]
then
	echo "Preparing the Analytics profile distribution"
	rm -rf ${DIR}/../conf
	rm -rf ${DIR}/../lib
	rm -rf ${DIR}/../dropins
	rm -rf ${DIR}/../dbscripts
	rm -rf ${DIR}/../patches
	rm -rf ${DIR}/../repository
	rm -rf ${DIR}/../resources
	rm -rf ${DIR}/../samples
	rm -rf ${DIR}/../servicepacks
	rm -rf ${DIR}/../webapp-mode
	rm -rf ${DIR}/../wso2/msf4j
	rm -rf ${DIR}/../wso2/broker
	rm -rf ${DIR}/../wso2/business-process
	rm -rf ${DIR}/../wso2/components
	rm -rf ${DIR}/../wso2/lib
	rm -rf ${DIR}/../wso2/tmp
	rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh
	rm -rf ${DIR}/msf4j.bat
	rm -rf ${DIR}/msf4j.sh
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh
	rm -rf ${DIR}/integrator.bat
	rm -rf ${DIR}/integrator.sh

	PROFILE="_analytics"

elif [ ${profileNumber} -eq 3 ]
then
	echo "Preparing the Business Process profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/business-process-default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove broker
	echo "Removing Broker profile"
	rm -rf ${DIR}/../wso2/broker
	rm -rf ${DIR}/../wso2/components/broker-default
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh
        #remove integrator
	echo "Removing Integrator profile"
	rm -rf ${DIR}/../conf
	rm -rf ${DIR}/../wso2/components/default
	rm -rf ${DIR}/../samples/service-bus
	rm -rf ${DIR}/../samples/data-services
	rm -rf ${DIR}/integrator.bat
	rm -rf ${DIR}/integrator.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh
	#remove analytics
	echo "Removing Analytics profile"
	rm -rf ${DIR}/../wso2/analytics
	rm -rf ${DIR}/../wso2/components/analytics-default
	rm -rf ${DIR}/analytics-worker.bat
	rm -rf ${DIR}/analytics-worker.sh
	rm -rf ${DIR}/analytics-dashboard.bat
	rm -rf ${DIR}/analytics-dashboard.sh
	#remove msf4j
	rm -rf ${DIR}/../wso2/msf4j

	PROFILE="_businessprocess"

elif [ ${profileNumber} -eq 4 ]
then
	echo "Preparing the Broker profile distribution"
	DEFAULT_BUNDLES="$(< ${DIR}/../wso2/components/broker-default/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info)"
	#remove analytics
	echo "Removing Analytics profile"
	rm -rf ${DIR}/../wso2/analytics
	rm -rf ${DIR}/../wso2/components/analytics-default
	rm -rf ${DIR}/analytics-worker.bat
	rm -rf ${DIR}/analytics-worker.sh
	rm -rf ${DIR}/analytics-dashboard.bat
	rm -rf ${DIR}/analytics-dashboard.sh
	#remove integrator
	echo "Removing Integrator profile"
    rm -rf ${DIR}/../conf
    rm -rf ${DIR}/../wso2/components/default
    rm -rf ${DIR}/../samples/service-bus
    rm -rf ${DIR}/../samples/data-services
    rm -rf ${DIR}/integrator.bat
    rm -rf ${DIR}/integrator.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh
	#remove business-process profile
	echo "Removing Business Process profile"
	rm -rf ${DIR}/../wso2/business-process
	rm -rf ${DIR}/../wso2/components/business-process-default
	rm -rf ${DIR}/../samples/business-process
	rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh
	#remove msf4j
	rm -rf ${DIR}/../wso2/msf4j

	PROFILE="_broker"

elif [ ${profileNumber} -eq 5 ]
then
    echo "Preparing the Msf4j profile distribution"
    rm -rf ${DIR}/../conf
    rm -rf ${DIR}/../lib
    rm -rf ${DIR}/../dropins
    rm -rf ${DIR}/../dbscripts
    rm -rf ${DIR}/../patches
    rm -rf ${DIR}/../repository
    rm -rf ${DIR}/../resources
    rm -rf ${DIR}/../samples
    rm -rf ${DIR}/../servicepacks
    rm -rf ${DIR}/../webapp-mode
    rm -rf ${DIR}/../wso2/analytics
    rm -rf ${DIR}/../wso2/broker
    rm -rf ${DIR}/../wso2/business-process
    rm -rf ${DIR}/../wso2/components
    rm -rf ${DIR}/../wso2/lib
    rm -rf ${DIR}/../wso2/tmp
    rm -rf ${DIR}/business-process.bat
	rm -rf ${DIR}/business-process.sh
	rm -rf ${DIR}/wso2ei-samples.bat
	rm -rf ${DIR}/wso2ei-samples.sh
	rm -rf ${DIR}/analytics-worker.bat
	rm -rf ${DIR}/analytics-worker.sh
	rm -rf ${DIR}/analytics-dashboard.bat
	rm -rf ${DIR}/analytics-dashboard.sh
	rm -rf ${DIR}/broker.bat
	rm -rf ${DIR}/broker.sh

    PROFILE="_msf4j"

else
	echo "Invalid profile number. Terminating."
	exit 3
fi


if [ ${profileNumber} != 5 ] && [ ${profileNumber} != 2 ]
then
    #remove unnecessary jar files
    echo "Removing unnecessary jars from plugins folder."
    mkdir -p ${DIR}/../wso2/components/tmp_plugins

    for BUNDLE in $DEFAULT_BUNDLES; do
        IFS=',' read -a bundleArray <<< "$BUNDLE"
        JAR=${bundleArray[0]}_${bundleArray[1]}.jar
        search_dir=${DIR}/../wso2/components/plugins
        file_count=$(find $search_dir -name $JAR | wc -l)
        if [[ $file_count -gt 0 ]]
        then
        cp ${DIR}/../wso2/components/plugins/${JAR} ${DIR}/../wso2/components/tmp_plugins
        fi
        done

    rm -r ${DIR}/../wso2/components/plugins
    mv ${DIR}/../wso2/components/tmp_plugins ${DIR}/../wso2/components/plugins
fi

echo "Preparing a profile distribution archive."
cd ${DIR}/../../
zip -r ${DISTRIBUTION}${PROFILE}.zip ${DISTRIBUTION}/ -x *profile-creator*

echo "Profile creation completed successfully."
exit 0
