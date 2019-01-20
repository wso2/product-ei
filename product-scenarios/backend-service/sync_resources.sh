#!/bin/bash

echo "[$(date -u)] #################################### Syncing resources #############################################"

SERVICES_DIR=~/services
REST_SERVICE1_LOCATION=$SERVICES_DIR/RestService1
REST_SERVICE2_LOCATION=$SERVICES_DIR/RestService2
BACKEND_LOCATION=$SERVICES_DIR/product-ei/product-scenarios/backend-service
BALLERINA_LOCATION=$BACKEND_LOCATION/ballerinaService
BALLERINA_RESOURCE_LOCATION=$BALLERINA_LOCATION/resources

#check if git is installed
LIST_GIT=$(dpkg -l  git)
echo "$LIST_GIT"
if [[ $LIST_GIT == "" ]]; then
    echo "[$(date -u)] git is not currently installed, hence installing git"
    apt-get update
    apt-get install git
    echo "[$(date -u)] Successfully installed git"
else
    echo "[$(date -u)] git is already installed"
fi
cd $SERVICES_DIR
git clone --single-branch --branch product-scenarios-dev https://github.com/wso2/product-ei.git
echo "[$(date -u)] cloned branch product-scenarios-dev https://github.com/wso2/product-ei.git"

rsync -avu --delete "$BALLERINA_RESOURCE_LOCATION" "$REST_SERVICE1_LOCATION/"
echo "[$(date -u)] copied resources to service1"
rsync -avu --delete "$BALLERINA_RESOURCE_LOCATION" "$REST_SERVICE2_LOCATION/"
#Modify resource folder in test service2 to contain the loadBalanceMsg with server:2
sed -i 's/\"server\"\:1/\"server\"\:2/g' $REST_SERVICE2_LOCATION/resources/json/response/loadBalanceMsg.json
echo "[$(date -u)] copied resources to service2"

#cleaning up directories
rm -rf $SERVICES_DIR/product-ei
echo "[$(date -u)] Removed product-ei repository"
echo "[$(date -u)] ################################## Successfully synced resources ###################################"


