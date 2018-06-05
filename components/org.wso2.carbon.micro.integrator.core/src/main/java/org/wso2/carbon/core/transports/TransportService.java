/*
 * Copyright 2005-2008 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.core.transports;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.wso2.carbon.core.transports.util.TransportParameter;

/**
 * All the transport specific bundles should have an implementation of this interface. The
 * transport service implementation can be used for various transport management activities
 * at runtime.
 */
public interface TransportService {

	/**
	 * Get the name of the transport protocol.
     *
     * @return Name of the transport as a string
	 */
	String getName();

	/**
	 * Get the set of globally defined transport parameters.
     *
     * @param listener Transport listener or sender
     * @param axisConfig AxisConfiguration of the tenant
     * @return An array of transport parameters or null
     * @throws Exception On error
	 */
	TransportParameter[] getGlobalTransportParameters(
            boolean listener, AxisConfiguration axisConfig) throws Exception;

    /**
	 * Get the set of service specific transport parameters.
     *
     * @param service Name of the service
     * @param listener Transport listener or sender
     * @param axisConfig AxisConfiguration of the tenant
     * @return An array of transport parameters or null
     * @throws Exception On error
	 */
    TransportParameter[] getServiceLevelTransportParameters(String service, boolean listener,
                                                            AxisConfiguration axisConfig) throws Exception;

    /**
	 * Check whether the transport is available for management activities
     *
     * @param listener Transport listener or sender
     * @param axisConfig AxisConfiguration of the tenant
     * @return a boolean value
	 */
	boolean isAvailable(boolean listener, AxisConfiguration axisConfig);

	/**
	 * Whether the transport is enabled
     *
     * @param listener Transport listener or sender
     * @param axisConfig AxisConfiguration of the tenant
     * @return true if the listener is active and false if not
	 */
	boolean isEnabled(boolean listener, AxisConfiguration axisConfig);

    /**
     * Update the global transport parameters
     *
     * @param params latest set of transport parameters
     * @param listener Transport listener or sender
     * @param cfgCtx ConfigurationContext of the tenant
     * @throws Exception on error
     */
    void updateGlobalTransportParameters(TransportParameter[] params,
                                         boolean listener, ConfigurationContext cfgCtx) throws Exception;

    /**
     * Update the service level transport parameters
     *
     * @param service Name of the service
     * @param params latest set of transport parameters
     * @param listener transport listener or sender
     * @param cfgCtx ConfigurationContext of the tenant
     * @throws Exception on error
     */
    void updateServiceLevelTransportParameters(String service, TransportParameter[] params,
                                               boolean listener, ConfigurationContext cfgCtx) throws Exception;

    /**
     * Whether the dependencies required by the transport are available
     *
     * @param params An array of transport parameters
     * @return true if the dependencies are available and false if not
     */
    boolean dependenciesAvailable(TransportParameter[] params);

    /**
     * Shutdown the transport
     *
     * @param listener transport listener or sender
     * @param axisConfig AxisConfiguration of the tenant
     */
    void disableTransport(boolean listener, AxisConfiguration axisConfig) throws Exception;

    void addTransportParameter(TransportParameter param,
                               boolean listener, ConfigurationContext cfgCtx) throws Exception;

    void removeTransportParameter(String param,
                                  boolean listener, ConfigurationContext cfgCtx) throws Exception;
}
