package samples.order;

import ballerina.net.http;
import ballerina.data.sql;

const string NOT_PAID = "Not paid";
const string PAID = "Paid";
const string COMPLETED = "Completed";
const string PENDING = "Pending";
const string CANCELLED = "Cancelled";
@http:configuration {basePath:"/order"} @Description {value:"This service represents an inbuilt order management
system"}
service<http> OrderService {
    endpoint<sql:ClientConnector> orderInfoDB {
        create sql:ClientConnector(
        sql:DB.MYSQL, "localhost", 3306, "OrderManagementRegistry", "root", "", {maximumPoolSize:5});
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/place"
    }
    resource PlaceOrder (http:Request request, http:Response response) {
        json orders = request.getJsonPayload();
        int length = lengthof orders;
        if (length == 0) {
            response.setStatusCode(400);
            response.setJsonPayload("The order should have atleast one order item");
            _ = response.send();
            return;
        }
        sql:Parameter[][] sqlParams = [];
        int totalPrice = 0;
        sql:Parameter sqlPrice = {sqlType:sql:Type.INTEGER, value:totalPrice};
        sql:Parameter sqlStatus = {sqlType:sql:Type.VARCHAR, value:PENDING};
        sql:Parameter sqlPaymentStatus = {sqlType:sql:Type.VARCHAR, value:NOT_PAID};
        _ = orderInfoDB.update("INSERT INTO `Order` (totalprice, orderstatus, paymentStatus) VALUES (?,?,?)", [sqlPrice,
                                                                                                               sqlStatus, sqlPaymentStatus]);
        var result, _ = <json>orderInfoDB.select("SELECT LAST_INSERT_ID() as orderId", null, null);
        var orderId, _ = (int)result[0].orderId;
        sql:Parameter sqlOrderId = {sqlType:sql:Type.INTEGER, value:orderId};
        int i = 0;
        while (i < length) {
            var drinkName, _ = (string)orders[i].drinkName;
            var additions, _ = (string)orders[i].additions;
            var cost, _ = (int)orders[i].cost;
            sql:Parameter sqlDrinkName = {sqlType:sql:Type.VARCHAR, value:drinkName};
            sql:Parameter sqlAdditions = {sqlType:sql:Type.VARCHAR, value:additions};
            sql:Parameter sqlCost = {sqlType:sql:Type.INTEGER, value:cost};
            sqlParams[i] = [sqlOrderId, sqlDrinkName, sqlAdditions, sqlCost];
            totalPrice = totalPrice + cost;
            i = i + 1;
        }
        sqlPrice = {sqlType:sql:Type.INTEGER, value:totalPrice};
        _ = orderInfoDB.batchUpdate("INSERT INTO `OrderItem` (orderId, drinkName, additions, cost) VALUES (?,?,?,?)",
                                    sqlParams);
        _ = orderInfoDB.update("UPDATE `Order` SET totalprice = ? WHERE orderid = ?", [sqlPrice, sqlOrderId]);
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderId` = ?", [sqlOrderId], null);
        response.setJsonPayload(orderResult[0]);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/get/{orderId}"
    }
    resource GetOrderDetails (http:Request request, http:Response response, string orderId) {
        var orderIdInt, _ = <int>orderId;
        sql:Parameter sqlOrderId = {sqlType:sql:Type.INTEGER, value:orderIdInt};
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderId` = ?", [sqlOrderId], null);
        if (lengthof orderResult == 0) {
            response.setStatusCode(404);
            response.setJsonPayload("The order " + orderId + " does not exist");
            _ = response.send();
            return;
        }
        json order = orderResult[0];
        var orderItems, _ = <json>orderInfoDB.select("SELECT drinkName, additions, cost FROM `OrderItem` WHERE `orderId`
         = ?", [sqlOrderId], null);
        json responsePayload = {"orderid":order.orderid, "totalprice":order.totalprice, "orderstatus":order.orderstatus,
                                   "paymentStatus":order.paymentStatus, "orderItems":orderItems};
        response.setJsonPayload(responsePayload);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["POST"],
        path:"/pay"
    }
    resource makePayment (http:Request request, http:Response response) {
        json payment = request.getJsonPayload();
        var orderId, _ = <int>payment.orderId.toString();
        sql:Parameter sqlOrderId = {sqlType:sql:Type.INTEGER, value:orderId};
        sql:Parameter sqlPaymentStatus = {sqlType:sql:Type.VARCHAR, value:PAID};
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderId` = ?", [sqlOrderId], null);
        if (lengthof orderResult == 0) {
            response.setStatusCode(404);
            response.setJsonPayload("The order " + orderId + " does not exist");
            _ = response.send();
            return;
        } else if (orderResult[0].orderstatus.toString() == CANCELLED) {
            response.setStatusCode(400);
            response.setJsonPayload("The order " + orderId + " has been cancelled");
            _ = response.send();
            return;
        } else if (orderResult[0].paymentStatus.toString() == PAID) {
            response.setStatusCode(400);
            response.setJsonPayload("The order " + orderId + " has already been paid for.");
            _ = response.send();
            return;
        }

        // Here we have to make external calls to make payment as it is not safe to handle card details internally.
        // For simplicity card details are not processed.
        int update = orderInfoDB.update("UPDATE `Order` SET paymentStatus = ? WHERE orderid = ?", [sqlPaymentStatus,
                                                                                                   sqlOrderId]);
        if (update > 0) {
            response.setJsonPayload("Payment succesful for Order " + orderId);
        } else {
            response.setJsonPayload("Payment Failed for Order " + orderId + ". Please try again.");
        }

        _ = response.send();
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/complete/{orderId}"
    }
    resource completeOrder (http:Request request, http:Response response, string orderId) {
        var orderIdInt, _ = <int>orderId;
        sql:Parameter sqlOrderId = {sqlType:sql:Type.INTEGER, value:orderIdInt};
        var orderResult, _ = <json>orderInfoDB.select("SELECT paymentStatus FROM `Order` WHERE `orderId` = ?",
                                                      [sqlOrderId], null);
        if (lengthof orderResult == 0) {
            response.setStatusCode(404);
            response.setJsonPayload("The order " + orderId + " does not exist");
            _ = response.send();
            return;
        }
        if (orderResult[0].paymentStatus.toString() == PAID) {
            sql:Parameter sqlOrderStatus = {sqlType:sql:Type.VARCHAR, value:COMPLETED};
            _ = orderInfoDB.update("UPDATE `Order` SET orderstatus = ? WHERE orderid = ?", [sqlOrderStatus, sqlOrderId]);
            orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderId` = ?", [sqlOrderId], null);
            json responsePayload = orderResult[0];
            response.setJsonPayload(responsePayload);
        } else {
            response.setStringPayload("\"Payment has to be made to complete the order\"");
            response.setStatusCode(402);
        }
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/payStatus/{orderId}"
    }
    resource paymentStatus (http:Request request, http:Response response, string orderId) {
        var orderIdInt, _ = <int>orderId;
        sql:Parameter sqlOrderId = {sqlType:sql:Type.INTEGER, value:orderIdInt};
        var orderResult, _ = <json>orderInfoDB.select("SELECT paymentStatus FROM `Order` WHERE `orderId` = ?",
                                                      [sqlOrderId], null);
        if (lengthof orderResult == 0) {
            response.setStatusCode(404);
            response.setJsonPayload("The order " + orderId + " does not exist");
            _ = response.send();
            return;
        }
        json responsePayload = orderResult[0];
        response.setJsonPayload(responsePayload);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/listCompleted"
    }
    resource listCompletedOrders (http:Request request, http:Response response) {
        sql:Parameter sqlOrderStatus = {sqlType:sql:Type.VARCHAR, value:COMPLETED};
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderstatus` = ?", [sqlOrderStatus],
                                                      null);
        response.setJsonPayload(orderResult);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/listPending"
    }
    resource listPendingOrders (http:Request request, http:Response response) {
        sql:Parameter sqlOrderStatus = {sqlType:sql:Type.VARCHAR, value:"Pending"};
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderstatus` = ?", [sqlOrderStatus],
                                                      null);
        response.setJsonPayload(orderResult);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["DELETE"],
        path:"/deleteCompleted"
    }
    resource deleteCompletedOrders (http:Request request, http:Response response) {
        sql:Parameter sqlOrderStatus = {sqlType:sql:Type.VARCHAR, value:"Completed"};
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderstatus` = ?", [sqlOrderStatus],
                                                      null);
        _ = orderInfoDB.update("DELETE FROM `Order` WHERE `orderstatus` = ?", [sqlOrderStatus]);
        json responsePayload = {"Deleted":orderResult};
        response.setJsonPayload(responsePayload);
        _ = response.send();
    }

    @http:resourceConfig {
        methods:["GET"],
        path:"/cancel/{orderId}"
    }
    resource cancelOrder (http:Request request, http:Response response, string orderId) {
        var orderIdInt, _ = <int>orderId;
        sql:Parameter sqlOrderId = {sqlType:sql:Type.INTEGER, value:orderIdInt};
        sql:Parameter sqlOrderStatus = {sqlType:sql:Type.VARCHAR, value:CANCELLED};
        var orderResult, _ = <json>orderInfoDB.select("SELECT * FROM `Order` WHERE `orderId` = ?", [sqlOrderId], null);
        if (lengthof orderResult == 0) {
            response.setStatusCode(404);
            response.setJsonPayload("The order " + orderId + " does not exist");
            _ = response.send();
            return;
        }
        json order = orderResult[0];
        if (order.orderstatus.toString() == PENDING && order.paymentStatus.toString() == NOT_PAID) {
            _ = orderInfoDB.update("UPDATE `Order` SET orderstatus = ? WHERE orderid = ?", [sqlOrderStatus,
                                                                                            sqlOrderId]);
            response.setJsonPayload("Order " + orderId + " successfully cancelled");
        } else {
            response.setStatusCode(412);
            response.setJsonPayload("Order " + orderId + " cannot be cancelled");
        }
        _ = response.send();
    }
}