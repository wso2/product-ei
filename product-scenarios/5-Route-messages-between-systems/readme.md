# 5. Route messages between systems

## Business use case narrative

The Message Routing is for reads the content of a message and routes it to a specific recipient based on its content.
A message router is concerned only with redirecting messages. As an example, if there is a function that pass incoming request on the correct service
is distributed over to multiple systems, an incoming request needs to be passed on the correct service,
based on the requested content. In such a scenarios, message router is useful.

Requests going through the Enterprise Integrator are called messages, and message mediation is the main part of an ESB.
The Switch and filter mediators of the ESB are route the Messages between systems.

The following diagram shows the Message Router's behavior. When it receives a request message, reads it and routes the
request to one of the two recipients according to the message's content.

![5-Message-Router-behavior.png](images/5-Message-Router-behavior.png)

You can find the reference documentation including the examples to try out with this link (https://docs.wso2.com/display/EI610/Routing+Requests+Based+on+Message+Content)


## Sub-Scenarios
- [5.1 Route based on the content of the messages](https://github.com/wso2/product-ei/tree/product-scenarios/product-scenarios/5-Route-messages-between-systems/5.1-Route-based-on-the-content-of-the-messages)
- [5.3 Load balance messages among two or more systems](https://github.com/wso2/product-ei/tree/product-scenarios/product-scenarios/5-Route-messages-between-systems/5.3-Load-balance-messages-among-two-or-more-systems)
- [5.4 Failover routing](https://github.com/wso2/product-ei/tree/product-scenarios/product-scenarios/5-Route-messages-between-systems/5.4-Failover-routing)




