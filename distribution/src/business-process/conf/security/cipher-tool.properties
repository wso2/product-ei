# Important: This properties file contains all the aliases to be used in carbon components. If any property need to be secured, you need to add alias name, file name and the xpath as follows:.
# The value goes as, the <file_name>//<xpath>,<true/false>
# where <file_name> - is the file (along with the file path) to be secured,
#       <xpath> - is the xpath to the property value to be secured
#       <true / false> - This is true if the last parameter in the xpath is parameter (starts with [ and ends with ]) and you want its value to be replaced with "password"

Carbon.Security.KeyStore.Password=repository/conf/carbon.xml//Server/Security/KeyStore/Password,false
Carbon.Security.KeyStore.KeyPassword=repository/conf/carbon.xml//Server/Security/KeyStore/KeyPassword,false
Carbon.Security.TrustStore.Password=repository/conf/carbon.xml//Server/Security/TrustStore/Password,false
UserManager.AdminUser.Password=repository/conf/user-mgt.xml//UserManager/Realm/Configuration/AdminUser/Password,false
Datasources.WSO2_CARBON_DB.Configuration.Password=repository/conf/datasources/master-datasources.xml//datasources-configuration/datasources/datasource[name='WSO2_CARBON_DB']/definition[@type='RDBMS']/configuration/password,false
Server.Service.Connector.keystorePass=repository/conf/tomcat/catalina-server.xml//Server/Service/Connector[@keystorePass],true