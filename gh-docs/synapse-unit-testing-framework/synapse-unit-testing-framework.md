# Synapse Unit Testing Framework
Unit testing framework for synapse provides an in-built way to execute a unit test against a given synapse configuration artifacts such as sequence, proxy, api, local entry, endpoint. Mainly it required a test artifact which is your main configuration, supportive artifacts which needs to execute the test artifact if exists and test cases with assertions. 

### Features
- Support deploy sequence, proxy, API, endpoint and local-entry artifacts
- Able to run multiple test cases against a test artifact and supportive artifacts
- Able to create, start and stop mock-services with multiple resources
- Assertion with `AssertEquals` and `AssertNotNull` properties
- Assertion for response body(`$body`), message context property(`$ctx`), axis2 property(`$axis2`) and transport property(`$trp`) values

### Running Unit Testing Framework

You can use the following command depending on the platform to start the unit testing framework in the Enterprise Integrator.
- MacOS/Linux/CentOS - `sh <MI_HOME>/bin/integrator.sh -DsynapseTest`
- Windows - `<MI_HOME>/bin/integrator.bat -DsynapseTest`

By default the unit testing framework starts on port 9008 and you can change the starting port by passing the ```-DsynapseTestPort=<port>``` property with above command.

### Stopping Unit Testing Framework
To stop the Unit Testing Framework runtime, press Ctrl+C in the command window and it will stop the server along with the Enterprise Integrator runtime.

### How To Send a TCP Request

You can use following code segment to send a sample test case to the unit testing framework and get the server response.
```java
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
 
class TCPClient {
 
    private static Socket clientSocket;

    public static void main(String args[]) {
        try {
            //Read data from test-case xml file
            String messageToBeSent = "<Input Test Case Data>";
            clientSocket = new Socket("<TCP host>", <TCP port>);
            OutputStream outputStream;
 
            //Send the message to the server
            outputStream = clientSocket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(messageToBeSent);
            outputStream.flush();
 
            //Get the return message from the server
            InputStream inputStream = clientSocket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            String response = (String) objectInputStream.readObject();
            System.out.println("Message received from the server : " + response);

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            //Closing the socket
            try {
                clientSocket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
} 
```

## Sample Test Cases
Each request needs to be sent to the unit testing server via a TCP transport (user defined port or default 9008 port). 

### Sample Test Case for Sequence Artifact

Client request via TCP:
```xml
<?xml version="1.0" encoding="utf-8" ?>
<synapse-unit-test>
    <artifacts>
        <!-- One or more xml files contains synapse configurations -->
        <test-artifact>
            <artifact>
                <sequence xmlns="http://ws.apache.org/ns/synapse" name="sequenceTest">
                    <log level="full" />
                    <enrich>
                        <source clone="true" type="inline">
                            <xsd:symbol xmlns:xsd="http://services.samples/xsd">SUN</xsd:symbol>
                        </source>
                        <target xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd" action="child" type="custom" xpath="//ser:getQuote/ser:request" />
                    </enrich>
                    <log level="full" />
                </sequence>
            </artifact>
        </test-artifact>
    </artifacts>

    <test-cases>
        <test-case>
            <input>
                <payload>
                    <![CDATA[
                        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"   xmlns:ser="http://services.samples" xmlns:xsd="http://services.samples/xsd">
                           <soapenv:Header/>
                           <soapenv:Body>
                              <ser:getQuote>
                                 <ser:request>
                                    <xsd:symbol>WSO2</xsd:symbol>
                                 </ser:request>
                              </ser:getQuote>
                           </soapenv:Body>
                        </soapenv:Envelope>
                    ]]>
                </payload>
                <properties>
                    <property name="prop1" value="val1"/>
                    <property name="prop2" scope="axis2" value="val2"/>
                    <property name="prop3" scope="transport" value="val3"/>
                </properties>
            </input>

            <assertions>
                <!-- assert for payload-->
                <assertEquals>
                    <actual>$body</actual>
                    <expected>
                        <![CDATA[
                            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://services.samples/xsd" xmlns:ser="http://services.samples">
                               <soapenv:Header/>
                               <soapenv:Body>
                                  <ser:getQuote>
                                     <ser:request>
                                        <xsd:symbol>WSO2</xsd:symbol>
                                        <xsd:symbol>SUN</xsd:symbol>
                                     </ser:request>
                                  </ser:getQuote>
                               </soapenv:Body>
                            </soapenv:Envelope>
                        ]]>
                    </expected>
                    <message>Expected payload not found</message>
                </assertEquals>

                <assertNotNull>
                    <actual>$body</actual>
                    <message>Payload is not available</message>
                </assertNotNull>

                <!-- assert for properties-->
                <assertNotNull>
                    <actual>$ctx:prop1</actual>
                    <message>prop1 not found</message>
                </assertNotNull>

                <assertEquals>
                    <actual>$ctx:prop1</actual>
                    <expected>val1</expected>
                    <message>Expected property value not found</message>
                </assertEquals>

                <assertEquals>
                    <actual>$axis2:prop2</actual>
                    <expected>val2</expected>
                    <message>Expected property value not found</message>
                </assertEquals>

                <assertEquals>
                    <actual>$trp:prop3</actual>
                    <expected>val3</expected>
                    <message>Expected property value not found</message>
                </assertEquals>
            </assertions>
        </test-case>
    </test-cases>
</synapse-unit-test>
```

Response from unit testing server:
```
{'test-cases':'SUCCESS'}
```

### Sample Test Case for Proxy Artifact

Client request via TCP:
```xml
<?xml version="1.0" encoding="utf-8" ?>
<synapse-unit-test>
    <artifacts>
        <!-- One or more xml files contains synapse configurations -->
        <test-artifact>
            <artifact>
                  <proxy name="HelloWorld" startOnLoad="true" transports="http https" xmlns="http://ws.apache.org/ns/synapse">
                      <target>
                          <inSequence>
                              <payloadFactory media-type="json">
                                  <format>{"Hello":"World"}</format>
                                  <args/>
                              </payloadFactory>
                              <respond/>
                          </inSequence>
                          <outSequence/>
                          <faultSequence/>
                      </target>
                  </proxy>
            </artifact>
        </test-artifact>
    </artifacts>

    <test-cases>
        <!-- One or more test cases with inputs and assertions -->
        <test-case>
            <assertions>
                <assertEquals>
                    <actual>$body</actual>
                    <expected>{"Hello":"World"}</expected>
                    <message>Failed the body assertion</message>
                </assertEquals>

                <assertNotNull>
                    <actual>$body</actual>
                    <message>Failed the body assertion (not null)</message>
                </assertNotNull>
            </assertions>
        </test-case>
    </test-cases>
</synapse-unit-test>
```
Response from unit testing server:
```
{'test-cases':'SUCCESS'}
```
### Sample Test Case for API Artifact

Client request via TCP:
```xml
<synapse-unit-test>
    <artifacts>
        <!-- One or more xml files contains synapse configurations -->
        <test-artifact>
            <artifact>
                <api xmlns="http://ws.apache.org/ns/synapse" name="apiTests3" context="/orders1e">
        	    <resource methods="GET" url-mapping="/">
                        <inSequence>
                            <log level="full" />
                            <send>
                                <endpoint key="Hospital" />
                            </send>
                        </inSequence>
                        <outSequence>
                            <log level="full" />
                            <respond/>
                        </outSequence>
                    </resource>
                </api>
            </artifact>
        </test-artifact>
        
        <supportive-artifacts>
            <artifact>
                <endpoint name="Hospital" xmlns="http://ws.apache.org/ns/synapse">
                	<address uri="http://localhost:9091/hello/sayHello" />
                </endpoint>
            </artifact>
        </supportive-artifacts>
    </artifacts>

    <test-cases>
        <!-- One or more test cases with inputs and assertions -->
        <test-case>
            <input>
                <request-path>/</request-path>
                <request-method>GET</request-method>
            </input>
            <assertions>
                <assertEquals>
                    <actual>$body</actual>
                    <expected>
                        {"fname":"Peter","lname":"Stallone", "age":22,"address":{"line":"20 Palm Grove","city":"Colombo 03","country":"Sri Lanka"}}
                    </expected>
                    <message>Failed the body assertion</message>
                </assertEquals>

                <assertNotNull>
                    <actual>$body</actual>
                    <message>Failed the body assertion (not null)</message>
                </assertNotNull>
            </assertions>
        </test-case>
    </test-cases>

    <mock-services>
        <!-- One or more mock services with multiple resources -->
        <mock-service>
            <service-name>Hospital</service-name>
            <port>9190</port>
            <context>/hello/sayHello</context>
            <resources>
                <resource>
                    <sub-context>/</sub-context>
                    <method>GET</method>
                    <response>
                        <payload>
                            {"fname":"Peter", "lname":"Stallone", "age":22, "address":{"line":"20 Palm Grove", "city":"Colombo 03", "country":"Sri Lanka"}}                        
                        </payload>
                    </response>
                </resource>
            </resources>
        </mock-service>
    </mock-services>
</synapse-unit-test>
```

Response from unit testing server:
```
{'test-cases':'SUCCESS'}
```

## Sample Failing Test Cases

If a unit test failed while reading input data, artifact deployment, mock-service creation, test case mediation or assertion, framework responses the error message to the client as follows.

```
{"<failed-phase>":"failed","exception":"<custom assertion or java exception message>"}
```

Following is an example of a custom error message sent by the unit testing framework due to un-matching actual payload and expected payload.

Client request via TCP:
```xml
<?xml version="1.0" encoding="utf-8" ?>
<synapse-unit-test>
    <artifacts>
        <!-- One or more xml files contains synapse configurations -->
        <test-artifact>
            <artifact>
                  <proxy name="HelloWorld" startOnLoad="true" transports="http https" xmlns="http://ws.apache.org/ns/synapse">
                      <target>
                          <inSequence>
                              <payloadFactory media-type="json">
                                  <format>{"Hello":"World"}</format>
                                  <args/>
                              </payloadFactory>
                              <respond/>
                          </inSequence>
                          <outSequence/>
                          <faultSequence/>
                      </target>
                  </proxy>
            </artifact>
        </test-artifact>
    </artifacts>

    <test-cases>
        <!-- One or more test cases with inputs and assertions -->
        <test-case>
            <assertions>
                <assertEquals>
                    <actual>$body</actual>
                    <expected>{"Hello":"Worlds"}</expected>
                    <message>Failed the Hello World body assertion</message>
                </assertEquals>
            </assertions>
        </test-case>
    </test-cases>
</synapse-unit-test>
```

Response from unit testing server:
```
{"mediation":"failed","exception":"Failed the Hello World body assertion"}
```
