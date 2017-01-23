${product.name} v${product.version}


This file explains the usages of all the scripts contained within
this directory.

1. chpasswd.sh & chpasswd.bat
    - Utility for changing the passwords of users registered in the CARBON user
      database

	This script is designed to be used with any databases. Tested with H2, MSSQL and Derby. H2 database is embedded with CARBON.
	Open a console/shell and run the script from "$CARBON_HOME/bin" directory.

	Usage:
	# chpasswd.bat/sh --db-url jdbc:h2:/$CARBON_HOME/repository/database/WSO2CARBON_DB

	If the administrator wants to use other databases, he should configure the datasource in the "master-datasources.xml", which is in 
	"$CARBON_HOME/repository/conf/datasources" directory. This datasource is looked up in "registry.xml" and "user-mgt.xml" as a JNDI resource.
  	He also needs to keep the drivers of the database inside the "$CARBON_HOME/lib" directory.

	For eg,
	If you need to use MSSQL as your DB,
	a. Put the MSSQL driver inside the "$CARBON_HOME/lib" directory.
	b. Edit the datasource in master-datasources.xml file with your database's url, username, password and drivername details. 

	eg:
	<datasource>
            <name>WSO2_CARBON_DB</name>
            <description>The datasource used for registry and user manager</description>
            <jndiConfig>
                <name>jdbc/WSO2CarbonDB</name>
            </jndiConfig>
            <definition type="RDBMS">
                <configuration>
                    <url>jdbc:jtds:sqlserver://10.100.1.68:1433/USERDB</url>
                    <username>USER</username>
                    <password>USER</password>
                    <driverClassName>net.sourceforge.jtds.jdbc.Driver</driverClassName>
                    <maxActive>50</maxActive>
                    <maxWait>60000</maxWait>
                    <testOnBorrow>true</testOnBorrow>
                    <validationQuery>SELECT 1</validationQuery>
                    <validationInterval>30000</validationInterval>
                </configuration>
            </definition>
        </datasource>

	c. The above datasource is looked up using JNDI in "registry.xml" and "usr-mgt.xml" as below.
	
	In registry.xml;
	
	eg:
	<dbConfig name="wso2registry">
        	<dataSource>jdbc/WSO2CarbonDB</dataSource>
    	</dbConfig>

	In usr-mgt.xml;

	eg:
	<Configuration>
                <AdminRole>admin</AdminRole>
                <AdminUser>
                     <UserName>admin</UserName>
                     <Password>admin</Password>
                </AdminUser>
            <EveryOneRoleName>everyone</EveryOneRoleName>
            <Property name="dataSource">jdbc/WSO2CarbonDB</Property>
            <Property name="MultiTenantRealmConfigBuilder">org.wso2.carbon.user.core.config.multitenancy.SimpleRealmConfigBuilder</Property>
        </Configuration>

	d. Open a console/shell and run the script from "$CARBON_HOME/bin" directory. Stop the server before running this script.
	
	Usage 01:
	# chpasswd.bat/sh --db-url jdbc:jtds:sqlserver://10.100.1.68:1433/USERDB --db-driver net.sourceforge.jtds.jdbc.Driver --db-username wso2carbon --db-password wso2carbon --username admin --new-password admin123

	Usage 02: MySQL DB
    sh chpasswd.sh --db-url jdbc:mysql://mysql.carbon-test.org:3306/userstore  --db-driver com.mysql.jdbc.Driver  --db-username root --db-password root123 --username admin --new-password admin123

	e. Now you can access the admin console with your new password.

2. README.txt
    - This file

3. version.txt
    - A simple text file used for storing the product version

4. wso2server.sh & wso2server.bat
    - The main script file used for running the server.

    Usage: wso2server.sh [commands] [system-properties]

            commands:
                --start		Start Carbon using nohup
                --stop		Stop the Carbon server process
                --restart	Restart the Carbon server process
                --cleanRegistry Clean registry space.
                                [CAUTION] All Registry data will be lost..
                --debug <port>  Start the server in remote debugging mode.
                                port: The remote debugging port.
                --help      List all the available commands and system properties
                --version   The version of the product you are running.

            system-properties:

                -DosgiConsole=[port]
                                Start Carbon with Equinox OSGi console.
                                If the optional 'port' parameter is provided, a
                                telnet port will be opened.

                -DosgiDebugOptions
                                Start Carbon with OSGi debugging enabled.
                                Debug options are loaded from the file
                                repository/conf/etc/osgi-debug.options

                -Dsetup         Clean the Registry and other configuration,
                                recreate DB, re-populate the configuration,
                                and start Carbon.

                -DportOffset=<offset>
                                The number by which all ports defined in the runtime ports will be offset

                -DserverRoles<roles>
                                A comma separated list of roles. Used in deploying cApps

                -DworkerNode
                                Set this system property when starting as a worker node.
		
                -Dprofile=<profileName>
				Starts the server as the specified profile. e.g. worker profile.

                -Dtenant.idle.time=<timeInMinutes>
                                If a tenant is idle for the specified time, tenant will be unloaded. Default tenant idle time is 30mins.

5. wsdl2java.sh & wsdl2java.bat - Tool for generating Java code from WSDLs

6. java2wsdl.sh & java2wsdl.bat - Tool for generating WSDL from Java code

7. build.xml - Build configuration for the ant command.
      Default task - Running the ant command in this directory, will copy the libraries that are require to run remote registry clients in to the repository/lib directory.
      createWorker task - removes the front end components from the server runtime.
      localize task - Generates language bundles in the $CARBON_HOME/dropins to be picked at a locale change.


	RUNNING INSTRUCTIONS FOR LOCALIZE TASK
	--------------------------------------
	(i) Create a directory as resources in your $CARBON_HOME.
	(ii) Add the relevant resources files of your desired languages in to that directory  by following the proper naming conventions of the ui jars.

		For an example in your resources directory, you can add the resource files as follows.

		<resources directory/folder>
			|
			|-----org.wso2.carbon.identity.oauth.ui_4.0.7
			|            |---------resources_fr.properties
			|            |---------resources_fr_BE.properties
			|	     |---------what ever your required language files
 			|
			|------org.wso2.carbon.feature.mgt.ui_4.0.6
                        |            |-------resources_fr.properties
                        |            |-------resources_fr_BE.properties
			|            |-------what ever your required language files
			|
			|------create directories/folders for each and every ui bundle


	(iii) Navigate to the $CARBON_HOME/bin and run the following command in command prompt. 
		ant localize

      	This will create the language bundles in the $CARBON_HOME/dropins directory.

      	If you want to change the default locations of the resources directory and dropins directory, run the following command.
		ant localize -Dresources.directory=<your path to the resource directory> -Ddropins.directory=<path to the directory where you want to store generated language bundles>

8. yajsw - contains the wrapper.conf file to run a Carbon server as a windows service using YAJSW (Yet Another Java Service Wrapper)

9. wso2carbon-version.txt
    - A simple text file used for storing the Carbon kernel version

10. carbondump.sh & carbondump.bat - Carbondump is a tool for collecting all the necessary data from a running Carbon instance at the time of an error. The carbondump generates a zip archive with the collected data, which can be used to analyze your system and determine the problem which caused the error.
