#!/bin/bash
#This script execute the tests.

set -o xtrace
TEST_DIR='product-scenarios'
DIR=$2
export DATA_BUCKET_LOCATION=$DIR

echo "Building scenarios-Commons"
mvn clean install -f ../scenarios-commons/pom.xml
echo "Building scenarios-Commons completed"

echo "Building Scenario-1"
mvn clean install
echo "Building Scenario-1 completed"

echo "Copying surefire-reports to data bucket"

cp -r 1.1-converting-soap-to-json/1.1.1-soap-to-json-using-payloadfactory-mediator/target/surefire-reports ${DIR}
ls ${DIR}
