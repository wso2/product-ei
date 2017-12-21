package samples.messaging;

import ballerina.net.http;
import ballerina.net.reliable.storejms;
import ballerina.net.reliable.processor;
import ballerina.net.reliable.httpr;

@Description{value:"Place a coffee order reliably to queue"}
@http:configuration {basePath:"/coffee"}
service<http> coffeeOrderService {

    processor:GuaranteedProcessor guaranteedConfig = {
             interval:3000,
             retryCount:5,
             config:{ "initialContextFactory":"org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                        "providerUrl":"tcp://localhost:61616",
                        "connectionFactoryName":"QueueConnectionFactory"
                    },
             store: storejms:store,
             retrieve: storejms:retrieve,
             handler: httpr:handle
                                                     };

    boolean status = guaranteedConfig.startProcessor();

    @http:resourceConfig {
        methods:["POST"],
        path:"/order"
    }
    resource coffeeResource (http:Request req, http:Response res) {

        endpoint<httpr:HttpGuaranteedClient> orderDispatchEp {
            create httpr:HttpGuaranteedClient("http://localhost:9091/dispatch", {}, guaranteedConfig);
        }
        // create a reliable delivery http client connector
        http:Response coffeeResponse;
        http:HttpConnectorError coffeeSendError;

        // action invocations are same as HttpClient connector
        coffeeResponse,coffeeSendError= orderDispatchEp.post("/coffeeOrder", req);

        // create a custom response based on the response received for successful persistence of the message
        // if the response is 202, from here onwards Ballerina will take care of the message
        if (coffeeResponse != null && coffeeResponse.getStatusCode() == 202) {
            coffeeResponse.setJsonPayload({status:"Coffee Order Processed"});
        } else {
            coffeeResponse.setJsonPayload({status:"Coffee Order cannot be Processed"});
        }

        _=res.forward(coffeeResponse);
    }
}
