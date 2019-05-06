# How to create admin-services
When creating Admin Services you would generally follow the same set of steps. In this instance, the Message Processor is used as an example.

### Creation Flow

1. Define your Admin Service function
2. Generate the stub using WSDL
3. Define function in MessageProcessorAdminServiceClient to call Admin Service function
4. Call the function from the Message Processor UI JSP

### 1. Defining your Admin Service
Message Processor Admin Services are defined in the `MessageProcessorAdminService.java` class in the carbon-mediation repo. 
Repo Link : `components/mediation-admin/org.wso2.carbon.message.processor/src/main/java/org/wso2/carbon/message/processor/service/MessageProcessorAdminService.java`

Go to `<repo-home>/components/mediation-admin/org.wso2.carbon.message.processor/` and navigate to the 'service' directory
to find the 'MessageProcessorAdminService.java' class.


In this example we can consider this function 
```$xslt
public String getMessage(String processorName) {
        SynapseConfiguration configuration = getSynapseConfiguration();
        MessageConsumer messageConsumer = 
            getMessageConsumer(configuration,processorName);

        String msg = getMessageAsString(messageConsumer);
        messageConsumer.cleanup();
        return msg;
    }
```

Once you define your function, generate the `MessageProcessorAdminServiceStub.java` file. 

### 2. Generating the MessageProcessorAdminServiceStub using WSDL
To generate the stub file, the WSDL file in `<repo-home>/service-stubs/mediation-admin/org.wso2.carbon.message.processor.stub/`
needs to be edited and then built to generate the stub. 

#### To obtain the contents of the WSDL
 1. Build `MessageProcessorAdminService.java` using `$ mvn clean install` on the IntelliJ console. 
 
 2. Copy the generated '.jar' file found in `<repo-home>/components/mediation-admin/org.wso2.carbon.message.processor/target/org.wso2.carbon.message.processor-4.6.106-SNAPSHOT.jar`
 
 3. Create a `patch9999` folder in `/Patches` directory and Paste the '.jar' file into it.
 
 4. Run `$ sh integrator.sh -DosgiConsole` from the product-ui `/bin` folder and list the admin services using 
 `$ listAdminServices` command. Make sure you set the <HideAdminServiceWSDLs> element to false in the `<product-home>/repository/conf/carbon.xml` file.
 
 5. From the list of admin services, locate the `MessageProcessorAdminService` URL and then paste it in your browser with '?wsdl' at the end
 It should look something like `https://<machine.local>:8243/services/MessageProcessorAdminService?wsdl`
 
 6. Select-all and copy the contents of the WSDL on the webpage
 
 7. Overwrite the copied contents onto `MessageProcessorAdminService.wsdl` found in `<repo-home>/service-stubs/mediation-admin/org.wso2.carbon.message.processor.stub`
 
For further clarification refer 'Discovering Admin Services" : https://docs.wso2.com/display/EI650/Working+with+Admin+Services'

If you generated the WSDL properly it should include your defined function

```$xslt
 <xs:element name="getMessage">
   <xs:complexType>
    <xs:sequence>
     <xs:element minOccurs="0" name="processorName" nillable="true" type="xs:string"/>
    </xs:sequence>
   </xs:complexType>
 </xs:element>
```
 
Now that the contents of the WSDL file in the stub folder have been properly edited, you need to build the message.processor.stub folder to 
generate the  message.processor.stub.jar file. 

### 3. Defining the Admin Service Client function 
If the stub has been generate properly you can call the defined function from the `MessageProcessorAdminServiceClient.java` using the 'stub' object. 

This is an example of how the you can use the stub to call the function.
```$xslt
public String getMessage(String processorName) throws Exception {
        String msg = null;
        try{
            if(processorName!=null) {
                msg = stub.getMessage(processorName);
            }
        } catch (Exception e) {
            handleException(e);
        }
        return msg;
    }
```

A function needs to be defined here to be called from the front-end Message Processor UI JSP.  



### 4. Calling the defined function from MessageProcessor UI JSP. 
If the calling function has been properly defined in MessageProcessorAdminServiceClient.java, you can call the function using 
the client object defined in the JSP. 

```
    String url = CarbonUIUtil.getServerURL(this.getServletConfig().getServletContext(),session);
    ConfigurationContext configContext = (ConfigurationContext)config.getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    
    MessageProcessorAdminServiceClient client = new MessageProcessorAdminServiceClient(cookie,url,configContext);
```
