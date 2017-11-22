# Service Orchestration

![Service Orchestration](https://github.com/isudana/product-ei/blob/7.0.x/samples/service-orchestration/orchestration-scenario.png "Service Orchestration")


This scenario is about an online vehicle license renewal system. 
Vehicle owners have to renew their vehicle license annually. 
To renew the license, the vehicle should have a valid insurance policy, a valid emission test certificate, 
and a credit card to make the payment.
The system is built by orchestrating several services.

## Services

### Insurance Service
Insurance service takes the vehicle registration number as the input data and gives out whether the vehicle has a valid insurance policy or not.

Sample service written in ballerina:
```
package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/insurance"}
service<http> InsuranceService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/validate/{vehicleId}"
    }
    resource validatePolicy (http:Request req, http:Response resp, string vehicleId) {
        println("Validating insurance policy");

        json payload;
        if (vehicleId == "11111") { 
            // This is just a dummy validation
            payload = {"Status":"Valid"};
        } else {
            payload = {"Status":"Invalid"};
        }

        resp.setJsonPayload(payload);
        resp.send();
    }
}
```

### Emission Test Service
This service takes the vehicle registration number as the input data and gives out whether vehicle has a valid emission test certificate.

Sample service written in ballerina:

```
package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/emission"}
service<http> EmissionService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/validate/{vehicleId}"
    }
    resource validateCert (http:Request req, http:Response resp, string vehicleId) {
        println("Validating emission test certificate");

        json payload;
        if (vehicleId == "11111") { 
            // This is just a dummy validation
            payload = {"Status":"Valid"};
        } else {
            payload = {"Status":"Invalid"};
        }

        resp.setJsonPayload(payload);
        resp.send();
    }
}
```

### License Issuer Service

This is responsible for issuing the license.

Sample service written in ballerina:
```
package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/licenseIssuer"}
service<http> LicenseIssuerService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/{vehicleId}"
    }
    resource licenseIssuerResource (http:Request req, http:Response resp, string vehicleId) {
        println("Generating License");

        json payload = {"License Certificate":"XLO1029302020", "Vehicle ID": vehicleId};
        resp.setJsonPayload(payload);
        resp.send();
    }
}
```


### Payment Gateway
This service handles the payment.

Sample service written in ballerina:
```
package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/payment"}
service<http> PaymentGatewayService {
    @http:resourceConfig {
        methods:["POST"],
        path:"/pay"
    }
    resource validateCert (http:Request req, http:Response resp) {
        println("Processing Payment");

        // dummy payment service
        json payload = req.getJsonPayload();
        println(payload);
        var cardNo,_ = (int) payload.creditCardNo;
        if (cardNo != 0) { 
            payload = {"Status":"Successful"};
        } else {
            payload = {"Status":"Failed"};
        }
        
        resp.setJsonPayload(payload);
        resp.send();
    }
}
```


## Orchestrating Services
Revenue License Service orchestrates all the above services and offers a service which can be used to revenue the license.

```
package samples.orchestration;

import ballerina.net.http;

@http:configuration {basePath:"/license"}
service <http> RevenueLicenseService {
    
    endpoint<http:HttpClient> paymentGatewayEP {
        create http:HttpClient("http://localhost:9090/payment", {});
    }
    
    endpoint<http:HttpClient> licenseIssuerEP {
        create http:HttpClient("http://localhost:9090/licenseIssuer", {});
    }
    
    @http:resourceConfig {
        methods:["POST"],
        path:"/renew"
    }
    resource licenseResource (http:Request req, http:Response resp) {

        json clientPayload = req.getJsonPayload();
        var vehicleId,_ = (string) clientPayload.Vehicle.ID;
        
        // Validate Insurance Certificate and Emission Certificate
        boolean isValid;
        string statusMessage;
        isValid, statusMessage = validateCertificates(req);
        if (!isValid) {
            json responsePayload = {"Error":statusMessage};
            resp.setJsonPayload(responsePayload);
            resp.send();
            return;
        }
        
        // Make payment
        var creditCardNo,_ = (string) clientPayload.card.no;
        var creditCardCVV,_ = (string) clientPayload.card.cvv;
        var cardNo,_ = <int> creditCardNo;
        var cvv,_ = <int> creditCardCVV;

        CreditCard creditCard = {"cardNo": cardNo, "cvv": cvv};
        json<Payment> payment = <json<Payment>, buildPaymentRequest(30)> creditCard;
        
        http:Request paymentGatewayReq = {};
        paymentGatewayReq.setJsonPayload(payment);
        
        http:Response paymentResponse = {};
        paymentResponse,_ = paymentGatewayEP.post("/pay", paymentGatewayReq);
        var paymentStatusJsonPayload = paymentResponse.getJsonPayload();
        println(paymentStatusJsonPayload);
        var status,_ = (string) paymentStatusJsonPayload.Status;
        if (status != "Successful") {
            json responsePayload = {"Error":"Payment Failed"};
            resp.setJsonPayload(responsePayload);
            resp.send();
            return;
        }
            
        // Call License Issuer    
        http:Response licenseResponse = {};
        licenseResponse,_ = licenseIssuerEP.get("/" + vehicleId, req);
        resp.forward(licenseResponse);
        
    }
    
    
    @http:resourceConfig {
        methods:["POST"],
        path:"/validateCerts"
    }
    resource validateCertsResource (http:Request req, http:Response resp) {
        json clientPayload = req.getJsonPayload();
        var vehicleId,_ = (string) clientPayload.Vehicle.ID;
        
        boolean isValid;
        string statusMessage;
        isValid, statusMessage = validateCertificates(req);
        if (!isValid) {
            json responsePayload = {"Error":statusMessage};
            resp.setJsonPayload(responsePayload);
            resp.send();
            return;
        }
        json payload = {"Status":"Valid"};
        resp.setJsonPayload(payload);
        resp.send();
    }
    
}

function validateCertificates (http:Request req) (boolean, string)  {
        
    endpoint<http:HttpClient> insuranceServiceEP {
        create http:HttpClient("http://localhost:9090/insurance", {});}
    endpoint<http:HttpClient> emissionServiceEP {
        create http:HttpClient("http://localhost:9090/emission", {});}
        
    json clientPayload = req.getJsonPayload();
    var vehicleId,_ = (string) clientPayload.Vehicle.ID;

    fork {
        worker InsuranceValidatorWorker {
            println("Inside Insurance worker");
            http:Response insuranceResponse = {}; 
            http:Request insuranceReq = {};
            insuranceResponse,_ = insuranceServiceEP.get("/validate/" + vehicleId, insuranceReq);
            insuranceResponse -> fork;
        }
        worker EmissionCertValidatorWorker {
            println("Inside emission worker");
            http:Response emissionResponse = {}; 
            http:Request emissionReq = {};
            emissionResponse,_ = emissionServiceEP.get("/validate/" + vehicleId, emissionReq);
            emissionResponse -> fork;
        }
    } join (all) (map workerResponses) {
        var workerResult, _ = (any[]) workerResponses["InsuranceValidatorWorker"];
        var workerHTTPResponse, _ = (http:Response) workerResult[0];
            
        json workerJsonPayload = workerHTTPResponse.getJsonPayload();
        var status,_ = (string) workerJsonPayload.Status;
        if (status != "Valid") {
            return false, "No valid insurance policy exists for this vehicle";
        }
            
        workerResult, _ = (any[]) workerResponses["EmissionCertValidatorWorker"];
        workerHTTPResponse, _ = (http:Response) workerResult[0];
            
        workerJsonPayload = workerHTTPResponse.getJsonPayload();
        status,_ = (string) workerJsonPayload.Status;
        if (status != "Valid") {
            return false, "No valid emission certificate exists for this vehicle";
        }
        return true, "";
    }
}

transformer <CreditCard creditCard, json<Payment> payment> buildPaymentRequest(int amount) {
    payment.creditCardNo = creditCard.cardNo;
    payment.creditCardCVV = creditCard.cvv;
    payment.amount = amount;
}

struct Payment {
    int creditCardNo;
    int creditCardCVV;
    int amount;
}

struct CreditCard {
    int cardNo;
    int cvv;
}


```












