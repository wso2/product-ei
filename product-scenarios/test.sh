#!/bin/bash

# Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -o xtrace

HOME=`pwd`
TEST_SCRIPT=test.sh
MVNSTATE=1

function usage()
{
    echo "
    Usage bash test.sh --input-dir /workspace/data-bucket.....
    Following are the expected input parameters. all of these are optional
    --input-dir       | -i    : input directory for test.sh
    --output-dir      | -o    : output directory for test.sh
    "
}

function runTestProfile()
{
    mvn clean install -Dmaven.repo.local="${INPUT_DIR}/m2" -Dinvocation.uuid="$UUID" -Ddata.bucket.location="${INPUT_DIR}" \
    -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -fae -B -f ./pom.xml \
     -P $1
}

optspec=":hiom-:"
while getopts "$optspec" optchar; do
    case "${optchar}" in
        -)
            case "${OPTARG}" in
                input-dir)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    INPUT_DIR=$val
                    ;;
                output-dir)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    OUTPUT_DIR=$val
                    ;;
                mvn-opts)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    MAVEN_OPTS=$val
                    ;;
                *)
                    usage
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        echo "Unknown option --${OPTARG}" >&2
                    fi
                    ;;
            esac;;
        h)
            usage
            exit 2
            ;;
        o)
            OUTPUT_DIR=$val
            ;;
        m)
            MVN_OPTS=$val
            ;;
        i)
            INPUT_DIR=$val
            ;;
        *)
            usage
            if [ "$OPTERR" != 1 ] || [ "${optspec:0:1}" = ":" ]; then
                echo "Non-option argument: '-${OPTARG}'" >&2
            fi
            ;;
    esac
done

echo "working Directory : ${HOME}"
echo "input directory : ${INPUT_DIR}"
echo "output directory : ${OUTPUT_DIR}"

#export DATA_BUCKET_LOCATION=${INPUT_DIR}

#=============== Execute Scenarios ===============================================

#generate uuid representing the test run
UUID=$(uuidgen)
#Retreive product version
PRODUCT_VERSION_FOUND=false
while IFS= read -r line
do
  IFS='=' tokens=( $line )
  key=${tokens[0]}
  value=${tokens[1]}
  if [ "$key" = "ProductVersion" ]; then
    productVersion=${tokens[1]}
    case ${productVersion} in
        ESB-5.0.0|EI-6.0.0|EI-6.1.0|EI-6.1.1|EI-6.2.0|EI-6.3.0|EI-6.4.0|EI-6.5.0|EI-6.6.0)
            echo "Executing tests for the product version: $productVersion"
            runTestProfile profile_general ;;
        ESB-4.9.0)
            echo "Executing tests for the product version: $productVersion"
            runTestProfile profile_490 ;;
        *)
            echo "ERROR: Unknown product version: " ${productVersion} "read from deployment.properties. Aborting the execution.";;
    esac
    PRODUCT_VERSION_FOUND=true
    break
  fi
done < "${INPUT_DIR}/deployment.properties"

if ! $PRODUCT_VERSION_FOUND ; then
    echo "deployment.properties file does not contain the product version. Executing the default suite ."
    runTestProfile profile_general
fi

MVNSTATE=$?

#=============== Copy Surefire Reports ===========================================
echo
echo "------------------------------------------------------------------------"
echo "Copying surefire-reports to ${OUTPUT_DIR}/scenarios"
mkdir -p ${OUTPUT_DIR}/scenarios
find ./* -name "surefire-reports" -exec cp --parents -r {} ${OUTPUT_DIR}/scenarios \;
ls -al ${OUTPUT_DIR}/scenarios

#=============== Code Coverage Report Generation ===========================================
echo
echo "------------------------------------------------------------------------"
echo "Generating Scenario Code Coverage Reports"
source ${HOME}/code-coverage/code-coverage.sh
generate_code_coverage ${INPUT_DIR} ${OUTPUT_DIR}

echo "------------------------------------------------------------------------"
echo "test.sh execution completed."
