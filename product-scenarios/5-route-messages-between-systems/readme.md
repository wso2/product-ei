# 5. Route messages between systems

## Business use case narrative

Message routing is one of the most fundamental requirements of systems/services integration. It considers 
addressability, 
static/deterministic routing, content-based routing, rules-based routing and policy-based routing as ways of routing 
a message. WSO2 EI enables these routing capabilities by means of its mediator and endpoint concepts.

The following diagram shows the behavior of a message router. A request received by a message router is forwarded to one
 of the two recipients according to the content of the message .

![5-Message-Router-behavior](images/Message-Router-behavior.png)

Reference : [Routing Requests Based on Message Content](https://docs.wso2.com/display/EI610/Routing+Requests+Based+on+Message+Content)

## Sub-Scenarios
- [5.1 Route based on the content of the messages](https://github.com/wso2/product-ei/tree/product-scenarios/product-scenarios/5-Route-messages-between-systems/5.1-Route-based-on-the-content-of-the-messages)
- [5.3 Load balance messages among two or more systems](5.3-load-balance-messages-among-systems)
- [5.4 Failover routing](https://github.com/wso2/product-ei/tree/product-scenarios/product-scenarios/5-Route-messages-between-systems/5.4-Failover-routing)
