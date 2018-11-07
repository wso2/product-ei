package org.wso2.mb.integration.common.clients.exceptions;

/**
 * Exception class for andes client.
 */
public class AndesClientException extends Exception {
    /**
     * Error message for exception
     */
    public String errorMessage;

    /**
     * Creates Andes exception
     */
    public AndesClientException() {
    }

    /**
     * Creates Andes exception with error message
     *
     * @param message Error message
     */
    public AndesClientException(String message) {
        super(message);
        errorMessage = message;
    }

    /**
     * Creates Andes exception with error message and throwable
     *
     * @param message Error message
     * @param cause   The throwable
     */
    public AndesClientException(String message, Throwable cause) {
        super(message, cause);
        errorMessage = message;
    }

    /**
     * Creates Andes exception with throwable.
     *
     * @param cause The throwable
     */
    public AndesClientException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return errorMessage;
    }
}
