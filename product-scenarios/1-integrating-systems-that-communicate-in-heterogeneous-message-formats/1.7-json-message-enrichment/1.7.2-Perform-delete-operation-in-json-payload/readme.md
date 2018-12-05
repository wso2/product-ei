# 1.7.2 Perform delete operation in json payload


## When to use
The Script mediator in JavaScript is useful when you need to create payloads that have recurring structures such as arrays of objects. The Script mediator defines the following important methods that can be used to manipulate payloads in many different ways:

* getPayloadJSON
* setPayloadJSON
* getPayloadXML
* setPayloadXML

We can perform various operations (such as deleting individual keys, modifying selected values, and inserting new objects) on JSON payloads to transform from one JSON format to another JSON format by using the getPayloadJSON and setPayloadJSON methods.


## Sample use case
In this usecase we are deleting ```<id>``` element from the below original paylaod. 

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

Request Payload
```
[
   {
      "name":"Biaggio Cafe",
      "tags":["bar", "restaurant", "food", "establishment", "pub"]
   },
   {
      "name":"Doltone House",
      "tags":["food", "establishment", "pub"]
   }
]
```

## Prerequisites
A REST client like cURL to invoke the EI proxy.

## Development guidelines

## Deployment guidelines

* We can simply deploy by copying the CAR archive into <EI_HOME>/repository/deployment/server/carbonapps directory, and it will be deployed.

<p align="center"><b> OR </b></p>

* We can create the api in Management Console and deploy.

## Supported versions

This is supported in all the EI and ESB versions

## Test cases

| ID        | Summary                                                   |
| ----------|:---------------------------------------------------------:|
| 1.7.1.2.1 | Remove a field from json payload                          |
| 1.7.1.2.1 | Handling a malformed SOAP messages                        |



