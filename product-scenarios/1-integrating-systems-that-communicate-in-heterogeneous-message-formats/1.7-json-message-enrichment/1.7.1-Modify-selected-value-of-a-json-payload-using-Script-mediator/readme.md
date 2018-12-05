# 1.7.1 Modify selected value of a json payload using Script mediator

## When to use
The Script mediator in JavaScript is useful when you need to create payloads that have recurring structures such as arrays of objects. The Script mediator defines the following important methods that can be used to manipulate payloads in many different ways:

* getPayloadJSON
* setPayloadJSON
* getPayloadXML
* setPayloadXML

We can perform various operations (such as deleting individual keys, modifying selected values, and inserting new objects) on JSON payloads to transform from one JSON format to another JSON format by using the getPayloadJSON and setPayloadJSON methods. 

## Sample use case
Following is an example of a JSON to JSON transformation performed by the Script mediator. In this usecase we select particular value and construct a new json payload. 

original payload
```
{
   "results" : [
      {
         "geometry" : {
            "location" : {
               "lat" : -33.867260,
               "lng" : 151.1958130
            }
         },
         "icon" : "bar-71.png",
         "id" : "7eaf7",
         "name" : "Biaggio Cafe",
         "opening_hours" : {
            "open_now" : true
         },
         "photos" : [
            {
               "height" : 600,
               "html_attributions" : [],
               "photo_reference" : "CoQBegAAAI",
               "width" : 900
            }
         ],
         "price_level" : 1,
         "reference" : "CnRqAAAAtz",
         "types" : [ "bar", "restaurant", "food", "establishment" ],
         "vicinity" : "48 Pirrama Road, Pyrmont"
      },
      {
         "geometry" : {
            "location" : {
               "lat" : -33.8668040,
               "lng" : 151.1955790
            }
         },
         "icon" : "generic_business-71.png",
         "id" : "3ef98",
         "name" : "Doltone House",
         "photos" : [
            {
               "height" : 600,
               "html_attributions" : [],
               "photo_reference" : "CqQBmgAAAL",
               "width" : 900
            }
         ],
         "reference" : "CnRrAAAAV",
         "types" : [ "food", "establishment" ],
         "vicinity" : "48 Pirrama Road, Pyrmont"
      }
   ],
   "status" : "OK"
}
```


Required json payload
```
[
    {
        "name": "Biaggio Cafe",
        "tags": [
            "bar",
            "restaurant",
            "food",
            "establishment"
        ],
        "id": "ID:7eaf7"
    },
    {
        "name": "Doltone House",
        "tags": [
            "food",
            "establishment"
        ],
        "id": "ID:3ef98"
    }
]
```

This modification can be done using the following script mediator. 

mediator
```
mc.getPayloadJSON();
results = payload.results;
var response = new Array();
for (i = 0; i & lt; results.length; ++i) {
   location_object = results[i];
   l = new Object();
   l.name = location_object.name;
   l.tags = location_object.types;
   l.id = "ID:" + (location_object.id);
   response[i] = l;
}
mc.setPayloadJSON(response);
```

## Prerequisites
N/A

## Development guidelines

Sequence and proxy Configuration

Create the following sequence and add to the proxy service. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse" name="js2" startOnLoad="true" statistics="disable" trace="disable" transports="http,https">
   <target>
      <inSequence>
         <log level="full" />
         <script language="js">payload = mc.getPayloadJSON();
    results = payload.results;
    var response = new Array();
    for (i = 0; i &lt; results.length; ++i) {
        location_object = results[i];
        l = new Object();
        l.name = location_object.name;
        l.tags = location_object.types;
        l.id = "ID:" + (location_object.id);
        response[i] = l;
    }
    mc.setPayloadJSON(response);</script>
         <log level="full" />
         <respond />
      </inSequence>
   </target>
   <description />
</proxy> 
```

Invoke the proxy with the above mentioned original payload using Postman. 
```
Proxy: http://localhost:8280/services/js2
POST: Content-Type: application/json
```

You will get the above mentioned requested payload. 

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the proxy in Management Console and deploy.


## Supported versions
This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                                         |
| ----------|:------------------------------------------------------------------------------: |
| 1.7.1.1.1 | Construct a new json payload using existing json payload                        |
| 1.7.1.1.2 | Transform json to xml using javascript                                          |
| 1.7.1.1.3 | Include original XML body into JSON payload                                     |
| 1.7.1.1.4 | Invoke proxy service with a null json payload                                   |
| 1.7.1.1.5 | Append json array to an existing payload                                        |
                                                           

