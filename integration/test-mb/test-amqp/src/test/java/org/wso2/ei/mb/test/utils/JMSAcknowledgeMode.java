package org.wso2.ei.mb.test.utils;

/**
 * Enumeration for acknowledge modes in JMS
 */
public enum JMSAcknowledgeMode {
    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#SESSION_TRANSACTED">SESSION_TRANSACTED
     * </a>
     */
    SESSION_TRANSACTED(0),

    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#AUTO_ACKNOWLEDGE">AUTO_ACKNOWLEDGE</a>
     */
    AUTO_ACKNOWLEDGE(1),

    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#CLIENT_ACKNOWLEDGE">CLIENT_ACKNOWLEDGE
     * </a>
     */
    CLIENT_ACKNOWLEDGE(2),

    /**
     * @see <a href="http://docs.oracle.com/javaee/7/api/javax/jms/Session.html#DUPS_OK_ACKNOWLEDGE">DUPS_OK_ACKNOWLEDGE
     * </a>
     */
    DUPS_OK_ACKNOWLEDGE(3),

    /**
     * Per message acknowledgement.
     */
    PER_MESSAGE_ACKNOWLEDGE(259);

    private int type;

    /**
     * Initializes acknowledge mode
     * @param type Acknowledge mode as per JMS
     */
    JMSAcknowledgeMode(int type) {
        this.type = type;
    }

    /**
     * Gets acknowledge mode
     * @return The Acknowledge mode value
     */
    public int getType() {
        return type;
    }
}
