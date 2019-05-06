#!/bin/bash

#----------------------------------------------------------------------------
#  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#----------------------------------------------------------------------------

#script that ask for user's database type, host, port, username, password & database driver location
flag=1

while [ $flag == 1 ]
do

    echo "Database types..."
    echo "1) MYSQL"
    echo "2) ORACLE"
    echo "3) POSTGRESQL"
    echo "4) MSSQL"
    echo "Enter no. of your database type :"
	read num

	case $num in
		"1") 
			dbType="MYSQL"																																																																																																																																																																																																																																																																																																																																		
			defaultPort="3306"
			;;
		"2") 
			dbType="ORACLE"
			defaultPort="1521"
			;;
		"3") 
			dbType="POSTGRESQL"
			defaultPort="5432"
			;;
		"4") 
			dbType="MSSQL"
			defaultPort="1433"
			;;
		*) 
			dbType="Invalid"
			;;
	esac

    if [ $dbType == "MYSQL" -o $dbType == "ORACLE" -o $dbType == "POSTGRESQL" -o $dbType == "MSSQL" ];
    then

		echo "----Click enter to use default database values----"

		read -p "Enter your database host : " host
		host=${host:-localhost}
		echo $host

	    read -p "Enter your database port : " port
	    port=${port:-$defaultPort}
		echo $port

		read -p "Enter your database name : " dbName
		dbName=${dbName:-EI_ANALYTICS}
		echo $dbName

	    read -p "Enter your database user : " user
	    user=${user:-root}
		echo $user

	    read -p "Enter your database passsword : " password
	    password=${password:-root}
		echo $password

	    read -p "Enter your database driver location [Absolute path] : " dbDriver
	    dbDriver=${dbDriver:-/}
		echo $dbDriver

	    java -jar migEI.one-jar.jar $dbType $host $port $dbName $user $password $dbDriver

	    flag=0

    else

	    echo "Enter a valid database type!!!"	

    fi

done
		