# SimpleStockQuote Deployable jar Sample

This sample demonstrates how to create a deployable microservice for hot deployment.

In this sample we have exposed the StockQuoteService as an deployable microservice that implements
org.wso2.msf4j.Microservice interface as shown in the following code.

```java
@Path("/stockquote")
public class StockQuoteService implements Microservice {

    // resource methods are here..

}
```

You have to add full classpath of the microservice class in <microservice.resourceClasses> under properties in the pom
.xml of deployable jar as shown in the following code.

```xml
    <properties>
        <microservice.resourceClasses>org.wso2.msf4j.stockquote.example.StockQuoteService</microservice.resourceClasses>
    </properties>
```

## How to build the sample

From this directory, run

```
mvn clean install
```

## How to run the sample

Go to the <EI_HOME>/wso2/msf4j/bin directory
Then run the following command to start the MSF4J profile.

```
./carbon.sh
```

The copy the target/stockquote-deployable-jar-<-version->.jar to <EI_HOME>/wso2/deployment/microservices directory of MSF4J profile.
Then the jar will be automatically deployed to the server runtime.

## How to test the sample

Use following cURL commands.
```
curl http://localhost:9090/stockquote/IBM
```