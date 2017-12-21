package samples.orchestration;

import ballerina.data.sql;
import ballerina.net.http;
import ballerina.log;

@http:configuration {basePath:"/license"}
service<http> RevenueLicenseService {

    endpoint<http:HttpClient> paymentGatewayEP {
        create http:HttpClient("http://localhost:9090/payment", {});
    }

    endpoint<http:HttpClient> licenseIssuerEP {
        create http:HttpClient("http://localhost:9090/licenseIssuer", {});
    }

    endpoint<sql:ClientConnector> vehicleInfoDB {
        create sql:ClientConnector(
        sql:DB.MYSQL, "localhost", 3306, "VehicleRegistry", "root", "root123", {maximumPoolSize:5});
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/renew"
    }
    resource licenseResource (http:Request req, http:Response resp) {

        json clientPayload = req.getJsonPayload();
        var vehicleId, _ = (string)clientPayload.Vehicle.ID;

        // Validate Insurance Certificate and Emission Certificate
        boolean isValid;
        string statusMessage;
        isValid, statusMessage = validateCertificates(req);
        if (!isValid) {
            json responsePayload = {"Error":statusMessage};
            resp.setJsonPayload(responsePayload);

            http:HttpConnectorError respondError = null;
            respondError = resp.send();
            if (respondError != null) {
                log:printError("Error occured at RevenueLicenseService while responding back");
            }

            return;
        }

        // Make payment
        var creditCardNo, _ = (string)clientPayload.card.no;
        var creditCardCVV, _ = (string)clientPayload.card.cvv;
        var cardNo, _ = <int>creditCardNo;
        var cvv, _ = <int>creditCardCVV;

        CreditCard creditCard = {"cardNo":cardNo, "cvv":cvv};
        json<Payment> payment = <json<Payment>, buildPaymentRequest(30)>creditCard;

        http:Request paymentGatewayReq = {};
        paymentGatewayReq.setJsonPayload(payment);

        http:Response paymentResponse = {};
        paymentResponse, _ = paymentGatewayEP.post("/pay", paymentGatewayReq);
        var paymentStatusJsonPayload = paymentResponse.getJsonPayload();
        println(paymentStatusJsonPayload);
        var status, _ = (string)paymentStatusJsonPayload.Status;
        if (status != "Successful") {
            json responsePayload = {"Error":"Payment Failed"};
            resp.setJsonPayload(responsePayload);

            http:HttpConnectorError respondError = null;
            respondError = resp.send();
            if (respondError != null) {
                log:printError("Error occured at RevenueLicenseService while responding back");
            }

            return;
        }

        // Call license issuer
        http:Response licenseResponse = {};
        licenseResponse, _ = licenseIssuerEP.get("/" + vehicleId, req);

        http:HttpConnectorError respondError = null;
        respondError = resp.forward(licenseResponse);
        if (respondError != null) {
            log:printError("Error occured at RevenueLicenseService while responding back");
        }
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/validateCerts"
    }
    resource validateCertsResource (http:Request req, http:Response resp) {
        json clientPayload = req.getJsonPayload();
        var vehicleId, _ = (string)clientPayload.Vehicle.ID;

        boolean isValid;
        string statusMessage;
        isValid, statusMessage = validateCertificates(req);
        if (!isValid) {
            json responsePayload = {"Error":statusMessage};
            resp.setJsonPayload(responsePayload);

            http:HttpConnectorError respondError = null;
            respondError = resp.send();
            if (respondError != null) {
                log:printError("Error occured at RevenueLicenseService while responding back");
            }
            return;
        }
        json payload = {"Status":"Valid"};
        resp.setJsonPayload(payload);


        http:HttpConnectorError respondError = null;
        respondError = resp.send();
        if (respondError != null) {
            log:printError("Error occured at RevenueLicenseService while responding back");
        }

    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/checkStatus/{vehicleId}"
    }
    resource checkStatusResource (http:Request req, http:Response resp, string vehicleId) {

        sql:Parameter[] params = [];
        sql:Parameter para1 = {sqlType:sql:Type.VARCHAR, value:vehicleId};
        params = [para1];

        // Fetch vehicle information
        datatable vehicleInfoDt =
                    vehicleInfoDB.select("SELECT * from VehicleDetails where VehicleNumber = ?", params, null);
        Vehicle vehicle;
        while (vehicleInfoDt.hasNext()) {
            vehicle, _ = (Vehicle)vehicleInfoDt.getNext();
        }
        if (vehicle == null) {
            json payload = {"Status":"No record found for vehicle", "Vehicle ID":vehicleId};
            resp.setJsonPayload(payload);

            http:HttpConnectorError respondError = null;
            respondError = resp.send();
            if (respondError != null) {
                log:printError("Error occured at RevenueLicenseService while responding back");
            }
            return;
        }

        // Fetch license expiry
        datatable licenseInfoDt =
                    vehicleInfoDB.select("SELECT * from LicenseDetails where VehicleNumber = ?", params, null);
        var expiry = "No valid license found";
        var result, _ = <json>licenseInfoDt;
        if (lengthof result != 0) {
            expiry, _ = (string)result[0].LicenseExpiry;
        }

        // Fetch license fee
        para1 = {sqlType:sql:Type.VARCHAR, value:vehicle.VehicleClass};
        params = [para1];
        datatable licenseFeeDt =
                    vehicleInfoDB.select("SELECT LicenseFee from LicenseFees where VehicleClass = ?", params, null);
        result, _ = <json>licenseFeeDt;
        var fee, _ = (int)result[0].LicenseFee;

        // Construct the response message
        json<Status> status = <json<Status>, buildStatusMessage(fee, expiry)>vehicle;

        resp.setJsonPayload(status);
        http:HttpConnectorError respondError = null;
        respondError = resp.send();
        if (respondError != null) {
            log:printError("Error occured at RevenueLicenseService while responding back");
        }
    }
}

function validateCertificates (http:Request req) (boolean, string) {

    endpoint<http:HttpClient> insuranceServiceEP {
        create http:HttpClient("http://localhost:9090/insurance", {});}
    endpoint<http:HttpClient> emissionServiceEP {
        create http:HttpClient("http://localhost:9090/emission", {});}

    json clientPayload = req.getJsonPayload();
    var vehicleId, _ = (string)clientPayload.Vehicle.ID;

    fork {
        worker InsuranceValidatorWorker {
            println("Inside Insurance worker");
            http:Response insuranceResponse = {};
            http:Request insuranceReq = {};
            insuranceResponse, _ = insuranceServiceEP.get("/validate/" + vehicleId, insuranceReq);
            insuranceResponse -> fork;
        }
        worker EmissionCertValidatorWorker {
            println("Inside emission worker");
            http:Response emissionResponse = {};
            http:Request emissionReq = {};
            emissionResponse, _ = emissionServiceEP.get("/validate/" + vehicleId, emissionReq);
            emissionResponse -> fork;
        }
    } join (all) (map workerResponses) {
        var workerResult, _ = (any[])workerResponses["InsuranceValidatorWorker"];
        var workerHTTPResponse, _ = (http:Response)workerResult[0];

        json workerJsonPayload = workerHTTPResponse.getJsonPayload();
        var status, _ = (string)workerJsonPayload.Status;
        if (status != "Valid") {
            return false, "No valid insurance policy exists for this vehicle";
        }

        workerResult, _ = (any[])workerResponses["EmissionCertValidatorWorker"];
        workerHTTPResponse, _ = (http:Response)workerResult[0];

        workerJsonPayload = workerHTTPResponse.getJsonPayload();
        status, _ = (string)workerJsonPayload.Status;
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

transformer <Vehicle vehicle, json<Status> status> buildStatusMessage(int fee, string expiry) {
    status.VehicleNumber = vehicle.EngineNumber;
    status.VehicleClass = vehicle.VehicleClass;
    status.Make = vehicle.Make;
    status.Model = vehicle.Model;
    status.LicenseExpiry = expiry;
    status.LicenseFee = fee;
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

struct Vehicle {
    string VehicleNumber;
    string EngineNumber;
    string VehicleClass;
    string Make;
    string Model;
    int YOM;
}

struct Status {
    string VehicleNumber;
    string VehicleClass;
    string Make;
    string Model;
    string LicenseExpiry;
    int LicenseFee;
}