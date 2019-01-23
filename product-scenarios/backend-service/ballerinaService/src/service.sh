# this script builds and deploys the ballerina backend service in the local environment
echo "Script: $0"
currentDirectory="$(pwd)"
targetDirectory="$currentDirectory/target"
echo "Compiling ballerina backend service..."
ballerina build testServices

 if [ $? -eq 0 ];
    then
        echo "testService successfully compiled."
        echo "Executable generated at $targetDirectory/testServices.balx"
        echo "Deploying testService.balx..."
        cd "$targetDirectory"
        echo "Directory changed to : $(pwd)"
        ballerina run testServices.balx
        if [ $? -eq 0 ];
            then
            echo "Service successfully deployed..."
        else
            echo "Service undeployed..."
        fi
        else
        echo "testServices compilation failed..."
 fi


