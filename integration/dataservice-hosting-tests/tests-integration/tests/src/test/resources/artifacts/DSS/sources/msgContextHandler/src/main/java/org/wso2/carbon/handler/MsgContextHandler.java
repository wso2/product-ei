package org.wso2.carbon.handler;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.dispatchers.AddressingBasedDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * This handler is capable of taking in the username from the header and and set it to he msgContext.
 */
public class MsgContextHandler extends AddressingBasedDispatcher {
    private static final Log log = LogFactory.getLog(MsgContextHandler.class);

    private static final String HTTP_SERVLET_REQUEST = "transport.http.servletRequest";
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String USERNAME = "username";

    /**
     * This method gets the JWT token from the transport header, and extracts the user name from the JWT and
     * sets it to the message context.
     *
     * @param arg0
     */
    public InvocationResponse invoke(MessageContext arg0) throws AxisFault {
        HttpServletRequest obj = (HttpServletRequest) arg0.
                getProperty(HTTP_SERVLET_REQUEST);

        if (obj != null) {
            //Get the "username" from the header.
            String username = obj.getHeader(USERNAME);
            arg0.setProperty(USERNAME, username);

            log.debug("********************** Username from msgcontext - "+arg0.getProperty(USERNAME));
        }
        return InvocationResponse.CONTINUE;
    }
}

