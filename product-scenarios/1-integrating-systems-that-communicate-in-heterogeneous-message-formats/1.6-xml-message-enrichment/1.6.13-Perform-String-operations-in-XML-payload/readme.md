# 1.6.13 Perform String operations in XML payload

## When to use
Data Mapper mediator is a data mapping solution that can be integrated into a mediation sequence. It converts and transforms one data format to another, or changes the structure of the data in a message. It provides a WSO2 Developer Studio-based tool to create a graphical mapping configuration and generates the files required to execute this graphical mapping configuration by the WSO2 Data Mapper engine.

## Sample use case
This use case focusses on converting particular values of an element to Upper case. 
![1.6.13-Performing String operations using datamapper mediator](images/datamapper-mediator.png)


## Prerequisites
Download WSO2 EI developer tools. 

## Development guidelines

We need to have following files provided. 
* Data Mapping Configuration   
* Input Schema
* Output Schema
* Input Type
* Output Type

1. Open the WSO2 Developer Studio.
2. Create ESB Solution Project. 
3. Right click and create a REST API. 
4. Drag and drop a Data Mapper mediator and a Respond mediator as shown in the above picture. 
5. Click on the API Resource, and then click on its Properties tab, and select True as the value for the Post method as shown below, to create the API resource listening to POST requests.
6. Double click on the Data Mapping mediator to configure it. You view a dialog box to create a registry resource project.
7. Enter a name for the configuration, and point the Registry Resource project to save it. 
8. Click OK. You view the following Data Mapper diagram editor in the new WSO2 Data Mapper Graphical perspective.
9. Create an XML file by copying the following xml file content, and save it in your local file system.

FoodMenu.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<breakfast_menu>
    <food>
        <name>Belgian Waffles</name>
        <price>$5.95</price>
        <description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>
        <calories>650</calories>
        <orgin>Belgian</orgin>
        <veg>true</veg>
    </food>
    <food>
        <name>Strawberry Belgian Waffles</name>
        <price>$7.95</price>
        <description>Light Belgian waffles covered with strawberries and whipped cream</description>
        <calories>900</calories>
        <orgin>Belgian</orgin>
        <veg>true</veg>
    </food>
    <food>
        <name>Berry-Berry Belgian Waffles</name>
        <price>$8.95</price>
        <description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description>
        <calories>900</calories>
        <orgin>Belgian</orgin>
        <veg>true</veg>
    </food>
    <food>
        <name>French Toast</name>
        <price>$4.50</price>
        <description>Thick slices made from our homemade sourdough bread</description>
        <calories>600</calories>
        <orgin>French</orgin>
        <veg>true</veg>
    </food>
    <food>
        <name>Homestyle Breakfast</name>
        <price>$6.95</price>
        <description>Two eggs, bacon or sausage, toast, and our ever-popular hash browns</description>
        <calories>950</calories>
        <orgin>French</orgin>
        <veg>false</veg>
    </food>
</breakfast_menu>
```

10. Right-click on the top title bar of the Input box and, click Load Input. 
11. Select XML as the Resource Type.
12. Click the file system link in Select resource from, select the XML file you saved in your local file system in step 9, and click Open.  
13. Create another XML file by copying the following sample and save it in your local file system. 

FoodMenuResult.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<menu>
    <item>
        <name>Belgian Waffles</name>
        <price>$5.95</price>
        <calories>650</calories>
        <orgin>Belgian</orgin>
        <veg>true</veg>
        <description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>
    </item>
    <item>
        <name>Strawberry Belgian Waffles</name>
        <price>$7.95</price>
        <calories>900</calories>
        <orgin>Belgian</orgin>
        <veg>true</veg>
        <description>Light Belgian waffles covered with strawberries and whipped cream</description>
    </item>
    <item>
        <name>Berry-Berry Belgian Waffles</name>
        <price>$8.95</price>
        <calories>900</calories>
        <orgin>Belgian</orgin>
        <veg>true</veg>
        <description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description>
    </item>
    <item>
        <name>French Toast</name>
        <price>$4.50</price>
        <calories>600</calories>
        <orgin>French</orgin>
        <veg>true</veg>
        <description>Thick slices made from our homemade sourdough bread</description>
    </item>
    <item>
        <name>Homestyle Breakfast</name>
        <price>$6.95</price>
        <calories>950</calories>
        <orgin>French</orgin>
        <veg>false</veg>
        <description>Two eggs, bacon or sausage, toast, and our ever-popular hash browns</description>
    </item>
</menu>
```

14. Right-click on the top title bar of the Output box and, click Load Output. 
15. Click the file system link in Select resource from, select the XML file you saved in your local file system in step 13, and click Open.  
16. Do the mapping as preferred using operators as shown in the example below.  
![1.6.13-Datamapping configuration of String Operation](images/uppercase.png)

17. Press Ctrl+S keys in each tab, to save all the configurations. 
18. Login to WSO2 EI Management Console and deploy the carbon application to WSO2 EI. 
19. Click Main, and then click APIs in the Service Bus menu. You view the deployed.

API 'FoodMenu' is as below. 

```xml
<api xmlns="http://ws.apache.org/ns/synapse" name="FoodMenu" context="/convertMenutoCaps">
   <resource methods="POST GET">
      <inSequence>
         <datamapper config="gov:datamapper/ConvertFood.dmc" inputSchema="gov:datamapper/ConvertFood_inputSchema.json" outputSchema="gov:datamapper/ConvertFood_outputSchema.json" inputType="XML" outputType="XML"/>
         <respond/>
      </inSequence>
      <outSequence/>
      <faultSequence/>
   </resource>
</api>
                                   
```


## Invoking the created REST API. 
1. Follow the steps below to test invoking the created REST API.
2. Enter the following details to create the client message, 
    URL: http://<EI_HOST>:<EI_PORT>8280/convertMenutoCaps
    Method: POST
    Body: raw xml/application
    Message: Enter the input

    ![1.6.13-Postman Request](images/request.png)


The response will be as below. 
![1.6.13-Postman Respnse](images/response.png)


## Deployment guidelines

* Described in Step 18 in Development guidelines. 


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                |
| ----------|:-----------------------------------------------------: |
| 1.6.13.1  | Concatanate strings using datamapper mediator          |
| 1.6.13.2  | Spilit given string value using datamapper mediator    |
| 1.6.13.3  | Convert to Lower case using datamapper mediator        |
| 1.6.13.4  | Convert to Upper case using datamapper mediator        |
| 1.6.13.5  | Substring a given string using datamapper mediator     |
| 1.6.13.6  | Replace a given string using datamapper mediator       |
                                                           

You can refer more on https://docs.wso2.com/display/EI600/Using+Data+Mapper+Mediator+in+WSO2+EI#UsingDataMapperMediatorinWSO2EI-CreatingtheESBconfigurationproject 
