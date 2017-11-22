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
            
        // Call Lisense Issuer    
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