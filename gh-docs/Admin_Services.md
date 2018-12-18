# How to create admin-services
Admin Services generally follow the following steps. Here, the Message Processor has been considered as an example

### Creation Flow

1. Define your Admin Service function
2. Generate the stub using wsdl
3. Define function in MessageProcessorAdminServiceClient to call Admin Service function
4. Call the function from the Message Processor UI jsp

### 1. Defining your Admin Service
Message Processor Admin Services are defined in the 'MessageProcessorAdminService.java' class in the carbon-mediation repo.

Go to `<repo-home>/components/mediation-admin/org.wso2.carbon.message.processor/` and navigate to the 'service' directory
to find the 'MessageProcessorAdminService.java' class.

Once you define your function, you will have to generate the MessageProcessorAdminServiceStub file. 

### 2. Generating the MessageProcessorAdminServiceStub using wsdl 
To generate the stub file, the WSDL file in `<repo-home>/service-stubs/mediation-admin/org.wso2.carbon.message.processor.stub/`
needs to be edited and then built to generate the stub. 

#### To obtain the contents of the WSDL

 1. Build 'MessageProcessorAdminService.java' using 'mvn clean install' on the IntelliJ console. 
 
 2. Copy the generated '.jar' file found in `<repo-home>/components/mediation-admin/org.wso2.carbon.message.processor/target/org.wso2.carbon.message.processor-4.6.106-SNAPSHOT.jar`
 
 3. Paste the '.jar' file into the 'patches' folder in the product-ui 
 
 4. Run 'sh integrator.sh -DosgiConsole' from the product-ui 'bin' folder and list the admin services using 
 'listAdminServices' command
 
 5. From the list of admin services, locate the MessageProcessorAdminService URL and then paste it in your browser with '?wsdl' at the end
 It should look something like `https://<machine.local>:8243/services/MessageProcessorAdminService?wsdl`
 
 6. Select-all and copy the contents of the WSDL on the webpage
 
 7. Overwrite the copied contents onto MessageProcessorAdminService.wsdl found in '<repo-home>/service-stubs/mediation-admin/org.wso2.carbon.message.processor.stub'
 
Now that the contents of the WSDL file in the stub folder have been properly edited, you need to build the message.processor.stub folder to 
generate the  message.processor.stub.jar file. 

### 3. Defining the Admin Service Client function 
If the stub has been generate properly you can call the defined function from the MessageProcessorAdminServiceClient using the 'stub' object. 
A function needs to be defined here to be called from the front-end Message Processor UI jsp.  

### 4. Calling the defined function from MessageProcessor UI jsp. 
If the calling function has been properly defined in MessageProcessorAdminServiceClient.java, you can call the function using 
the client object defined in the jsp. 

```
    String url = CarbonUIUtil.getServerURL(this.getServletConfig().getServletContext(),session);
    ConfigurationContext configContext = (ConfigurationContext)config.getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    
    MessageProcessorAdminServiceClient client = new MessageProcessorAdminServiceClient(cookie,url,configContext);
```
           