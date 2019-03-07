#!/bin/bash

#functions
verify_service_start () {
    PORT=$1
    PROCESS_ID=$(lsof -n -i6TCP:$1 | grep LISTEN | awk '{ print $2 }')
    if [ "$PROCESS_ID" == "" ]; then
        return 0
    else
        echo "Process with pid: $PROCESS_ID listening on port: $PORT . Do you want to force start service
        (y/n) ?"
        read confirmed
        if [ "$confirmed" == "y" ] || [ "$confirmed" = "Y" ]; then
            kill -9 $PROCESS_ID
            return 0
        elif [ "$confirmed" == "n" ] || [ "$confirmed" = "N" ]; then
            echo "Skipping service initialization on port: $PORT"
            return 1
        else
            echo "unexpected input: $confirmed. Skipping service initialization on port: $PORT"
            return 1
        fi
    fi
}

kill_existing_services () {
    PORT=$1
    PROCESS_ID=$(lsof -n -i6TCP:$1 | grep LISTEN | awk '{ print $2 }')
    if [ "$PROCESS_ID" == "" ]; then
        return 0
    else
        kill -9 $PROCESS_ID
    fi
}

cd

### Install ballerina and tomcat if not installed
echo "################################### Installing Ballerina and Tomcat #############################################"
INSTALLATION_DIR=~/installation

#create installation folder if it does not exist
mkdir -p $INSTALLATION_DIR
cd $INSTALLATION_DIR

source ~/.profile

BALLERINA=$(echo $BALLERINA_HOME)
if [[ "$BALLERINA" == "" ]]; then
  echo "BALLERINA_HOME is not configured, hence installing ballerina"
  wget https://product-dist.ballerina.io/downloads/0.982.0/ballerina-platform-0.982.0.zip
  LIST_UNZIP=$(dpkg -l  unzip)
    echo "$LIST_UNZIP"
    if [[ "$LIST_UNZIP" == "" ]]; then
        echo "unzip is not currently installed, hence installing unzip"
        apt-get update
        sudo apt-get install unzip
        echo "Successfully installed unzip"
    else
        echo "unzip is already installed"
    fi
  unzip ballerina-platform-0.982.0.zip
  echo export BALLERINA_HOME="$INSTALLATION_DIR/ballerina-platform-0.982.0" >> ~/.profile
  echo export PATH=\$PATH:\$\{BALLERINA_HOME\}/bin >> ~/.profile
  echo "Successfully installed ballerina"
else
  echo "BALLERINA_HOME is already configured"
fi

#installing tomcat if not already installed
TOMCAT=$(echo $TOMCAT_HOME)
if [[ "$TOMCAT" == "" ]]; then
  echo "TOMCAT_HOME is not configured, hence installing tomcat"
  wget https://www-eu.apache.org/dist/tomcat/tomcat-8/v8.5.37/bin/apache-tomcat-8.5.37.zip
  unzip apache-tomcat-8.5.37.zip
  chmod -R 777 $INSTALLATION_DIR/apache-tomcat-8.5.37/bin
  echo export TOMCAT_HOME="$INSTALLATION_DIR/apache-tomcat-8.5.37" >> ~/.profile
  echo export PATH=\$PATH:\$\{TOMCAT_HOME\}/bin >> ~/.profile
  echo "Successfully installed tomcat"
else
  echo "TOMCAT_HOME is already configured"
fi

source ~/.profile

echo "################################# End of Ballerina and Tomcat installation ######################################"

echo "BALLERINA_HOME=$BALLERINA_HOME"
echo "TOMCAT_HOME=$TOMCAT_HOME"

echo "############################################ Starting Services ##################################################"

SERVICES_DIR=~/services
REST_SERVICE1_LOCATION=$SERVICES_DIR/RestService1
REST_SERVICE2_LOCATION=$SERVICES_DIR/RestService2
STOCK_QUOTE_SERVICE_LOCATION=$TOMCAT_HOME/webapps/
BACKEND_LOCATION=$SERVICES_DIR/product-ei/product-scenarios/backend-service
BALLERINA_LOCATION=$BACKEND_LOCATION/ballerinaService
BALLERINA_SRC_LOCATION=$BALLERINA_LOCATION/src
BALLERINA_RESOURCE_LOCATION=$BALLERINA_LOCATION/resources

cd

mkdir -p $SERVICES_DIR
cd $SERVICES_DIR
START_REST1=false
START_REST2=false
START_STOCK_QUOTE=false
PORT1=9090
PORT2=9091
PORT_STOCK_QUOTE=8080

# check if service restart is forced
FORCE=$1
if [ "$FORCE" = "-f" ] || [ "$FORCE" = "--force" ]; then
    echo "force restarting services"
    kill_existing_services $PORT1
    START_REST1=true
    kill_existing_services $PORT2
    START_REST2=true
    kill_existing_services $PORT_STOCK_QUOTE
    START_STOCK_QUOTE=true
else
    verify_service_start $PORT1
    START_REST1=$?
    verify_service_start $PORT2
    START_REST2=$?
    verify_service_start $PORT_STOCK_QUOTE
    START_STOCK_QUOTE=$?
fi

if [[ $START_REST1 -eq 0 ]] || [[ $START_REST2 -eq 0 ]] || [[ $START_STOCK_QUOTE -eq 0 ]]; then
    #check if git is installed
    LIST_GIT=$(dpkg -l  git)
    echo "$LIST_GIT"
    if [[ $LIST_GIT == "" ]]; then
      echo "git is not currently installed, hence installing git"
      apt-get update
      apt-get install git
      echo "Successfully installed git"
    else
      echo "git is already installed"
    fi

    git clone --single-branch --branch product-scenarios-dev https://github.com/wso2/product-ei.git
    echo "cloned branch product-scenarios-dev https://github.com/wso2/product-ei.git"

    if [[ $START_REST1 -eq 0 ]] || [[ $START_REST2 -eq 0 ]]; then
        #Building the ballerina service
        cd $BALLERINA_SRC_LOCATION
        ballerina build
        echo "Built ballerina service"

        if [[ $START_REST1 -eq 0 ]]; then
            mkdir -p $REST_SERVICE1_LOCATION
            cd $REST_SERVICE1_LOCATION
            cp $BALLERINA_SRC_LOCATION/target/testServices.balx $REST_SERVICE1_LOCATION/
            cp -rf $BALLERINA_RESOURCE_LOCATION $REST_SERVICE1_LOCATION/
            nohup ballerina run -e port=$PORT1 $REST_SERVICE1_LOCATION/testServices.balx >> $REST_SERVICE1_LOCATION/service.log &
            echo "started rest service1 on port: " $PORT1
        fi

        if [[ $START_REST2 -eq 0 ]]; then
            mkdir -p $REST_SERVICE2_LOCATION
            cd $REST_SERVICE2_LOCATION
            cp $BALLERINA_SRC_LOCATION/target/testServices.balx $REST_SERVICE2_LOCATION/
            cp -rf $BALLERINA_RESOURCE_LOCATION $REST_SERVICE2_LOCATION/
            #Modify resource folder in test service2 to contain the loadBalanceMsg with server:2
            sed -i 's/\"server\"\:1/\"server\"\:2/g' $REST_SERVICE2_LOCATION/resources/json/response/loadBalanceMsg.json
            nohup ballerina run -e port=$PORT2 $REST_SERVICE2_LOCATION/testServices.balx >> $REST_SERVICE2_LOCATION/service.log &
            echo "started rest service2 on port: " $PORT2
        fi
    fi
    if [[ $START_STOCK_QUOTE -eq 0 ]]; then
        #Copying the stockQuote service
        rm -r $TOMCAT_HOME/webapps/axis2*
        cp $BACKEND_LOCATION/stockQuoteService/axis2.war $TOMCAT_HOME/webapps/
        sh $TOMCAT_HOME/bin/startup.sh
        echo "started stock quote service"

    fi
fi

echo "############################################# Started Services ##################################################"

#cleaning up directories
rm -rf $SERVICES_DIR/product-ei
echo "Removed product-ei repository"

crontab -r
echo "cleared user's crontab"

cd
./schedule_sync_resources.sh
echo "scheduled task to sync resources"

./schedule_clear_invocations.sh
echo "scheduled task to clear invocations"

