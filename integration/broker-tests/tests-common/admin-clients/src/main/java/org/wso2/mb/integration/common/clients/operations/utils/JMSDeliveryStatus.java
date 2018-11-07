package org.wso2.mb.integration.common.clients.operations.utils;

/**
 *
 */
public enum JMSDeliveryStatus {
    /**
     * JMS delivery statuses.
     */
    ORIGINAL("Original"), REDELIVERED("Redelivered");
    private String status;

    /**
     * Initializes JMS delivery status
     * @param status Delivery status.
     */
    JMSDeliveryStatus(String status) {
        this.status = status;
    }

    /**
     * Gets JMS delivery status.
     * @return Delivery status.
     */
    public String getStatus() {
        return status;
    }
}
