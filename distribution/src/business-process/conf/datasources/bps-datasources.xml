<datasources-configuration xmlns:svns="http://org.wso2.securevault/configuration">
  
   <providers>
        <provider>org.wso2.carbon.ndatasource.rdbms.RDBMSDataSourceReader</provider>
    </providers>
  
  <datasources>      
        <datasource>
            <name>BPS_DS</name>
            <description></description>
            <jndiConfig>
                <name>bpsds</name>
            </jndiConfig>
            <definition type="RDBMS">
                <configuration>
                    <url>jdbc:h2:file:repository/database/jpadb;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE</url>
                    <username>wso2carbon</username>
                    <password>wso2carbon</password>
                    <driverClassName>org.h2.Driver</driverClassName>
                    <testOnBorrow>true</testOnBorrow>
                    <validationQuery>SELECT 1</validationQuery>
                    <validationInterval>30000</validationInterval>
                    <useDataSourceFactory>false</useDataSourceFactory>
		    <defaultAutoCommit>true</defaultAutoCommit>
 		    <maxActive>100</maxActive>
                    <maxIdle>20</maxIdle>
		    <maxWait>10000</maxWait>
                </configuration>
            </definition>
        </datasource>
    </datasources>
</datasources-configuration>
