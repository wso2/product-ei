# Templates for EI patterns

Currently following templates are included:

* Message Router
* Message Translator
* Message Filter
* Pipes and Filters
* Aggregator
* Message Endpoint

### Message Router
This template represents the Message Router EIP pattern which reads the content of a message and routes it to a specific recipient based on its content. This service can be run using a json file similar to the following:
   
    {"quote": {"symbol": "foo"}}
    
### Message Translator
This template represents the Message Translator EIP pattern which is responsible for translating messages between applications. This service also can be run using a json file similar to the following:

    {"quote": {"symbol": "foo"}}

    
### Message Filter
This template represents the Message Filter EIP pattern which checks an incoming message against a certain criteria that the message should adhere to. This service can also be run using a json file similar to the following:

    {"quote": {"symbol": "foo"}}

### Aggregator
This template represents the Aggregator EIP pattern which is responsible for collecting and storing individual messages until a complete set of related messages has been received.

### Message Endpoint
This template represents the Message Endpoint EIP pattern which is responsible encapsulating the messaging system inside an application.

### Message Filter
This template represents the Pipes and Filters EIP pattern. This service can be run using a json file similar to the following:

    {
       "credentials": {
          "name":"UserName",
          "id":"001"
       }
    }

