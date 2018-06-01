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

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.ParameterInclude;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.core.transports.util.TransportParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class implements the TransportService interface and encapsulates
 * the functionality common to all transport service implementations. It is
 * recommended that all transport service implementations extends this class
 * instead of directly implementing the TransportService interface, to prevent
 * code duplication.
 */
public abstract class AbstractTransportService implements TransportService {

    protected String transportName;

    private static final Log log = LogFactory.getLog(org.wso2.carbon.core.transports.AbstractTransportService.class);

    public AbstractTransportService(String transportName) {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + transportName + " transport service");
        }
        this.transportName = transportName;
    }

    public TransportParameter[] getGlobalTransportParameters(
            boolean listener, AxisConfiguration axisConfig) throws Exception {

        ParameterInclude transport;
//        TransportPersistenceManager transportPM = new TransportPersistenceManager(axisConfig);
        if (listener) {
            transport = axisConfig.getTransportIn(transportName);
        } else {
            transport = axisConfig.getTransportOut(transportName);
        }

        return getParameters(transport);
    }

    public String getName() {
        return transportName;
    }

    public TransportParameter[] getServiceLevelTransportParameters(
            String service, boolean listener, AxisConfiguration axisConfig) throws Exception {

        if (axisConfig.getService(service) == null) {
            throw new CarbonException("No service exists by the name : " + service);
        }

        return getGlobalTransportParameters(listener, axisConfig);
    }

    public boolean isAvailable(boolean listener, AxisConfiguration axisConfig) {
//        axisConfig.getT //TODO need to implement
    return true;
    }

    public boolean isEnabled(boolean listener, AxisConfiguration axisConfig) {
        if (listener) {
            return axisConfig.getTransportIn(transportName) != null;
        } else {
            return axisConfig.getTransportOut(transportName) != null;
        }
    }

    public void updateGlobalTransportParameters(
            TransportParameter[] params, boolean listener, ConfigurationContext cfgCtx) throws Exception {

        AxisConfiguration axisConfig = cfgCtx.getAxisConfiguration();
        if (listener) {
            TransportInDescription transportIn = axisConfig.getTransportIn(transportName);
            if (transportIn != null) {
                transportIn.getReceiver().stop();
                setParameters(transportIn, params);

                try {
                    transportIn.getReceiver().init(cfgCtx, transportIn);
                    transportIn.getReceiver().start();
                } catch (Throwable t) {
                    axisConfig.getTransportsIn().remove(transportName);
                    String msg = "Error while initializing the " + transportName + " listener";
                    log.error(msg, t);
                    throw new AxisFault(msg, t);
                }

            }

        } else {
            TransportOutDescription transportOut = axisConfig.getTransportOut(transportName);
            if (transportOut != null) {
                transportOut.getSender().stop();
                setParameters(transportOut, params);

                try {
                    transportOut.getSender().init(cfgCtx, transportOut);
                } catch (Throwable t) {
                    axisConfig.getTransportsOut().remove(transportName);
                    String msg = "Error while initializing the " + transportName + " sender";
                    log.error(msg, t);
                    throw new AxisFault(msg, t);
                }
            }
        }
    }

    public void updateServiceLevelTransportParameters(String service, TransportParameter[] params,
                                                      boolean listener, ConfigurationContext cfgCtx) throws Exception {

        AxisConfiguration axisConfig = cfgCtx.getAxisConfiguration();
        if (axisConfig.getService(service) == null) {
            throw new CarbonException("No service exists by the name : " + service);
        }

        updateGlobalTransportParameters(params, listener, cfgCtx);
    }

    public void disableTransport(boolean listener, AxisConfiguration axisConfig) throws Exception {
        if (listener) {
            TransportInDescription transport = axisConfig.getTransportIn(transportName);
            if (transport != null) {
                transport.getReceiver().stop();
                axisConfig.getTransportsIn().remove(transportName);
            } else {
                log.warn(transportName + " listener is already disabled");
            }
        } else {
            TransportOutDescription transport = axisConfig.getTransportOut(transportName);
            if (transport != null) {
                transport.getSender().stop();
                axisConfig.getTransportsOut().remove(transportName);
            } else {
                log.warn(transportName + " sender is already disabled");
            }
        }

    }

    public void addTransportParameter(TransportParameter param,
                                      boolean listener, ConfigurationContext cfgCtx) throws Exception {
        ParameterInclude transport;
        AxisConfiguration axisConfig = cfgCtx.getAxisConfiguration();
        if (listener) {
            transport = axisConfig.getTransportIn(transportName);
        } else {
            transport = axisConfig.getTransportOut(transportName);
        }

        if (transport == null) {

        } else {
            TransportParameter[] newParams;
            TransportParameter[] params = getGlobalTransportParameters(listener, axisConfig);

            if (params == null) {
                newParams = new TransportParameter[]{param};
            } else {
                boolean overwritten = false;
                for (int i = 0; i < params.length; i++) {
                    if (params[i].getName().equals(param.getName())) {
                        params[i] = param;
                        overwritten = true;
                        break;
                    }
                }

                if (overwritten) {
                    newParams = params;
                } else {
                    newParams = new TransportParameter[params.length + 1];
                    System.arraycopy(params, 0, newParams, 0, params.length);
                    newParams[newParams.length - 1] = param;
                }
            }

            updateGlobalTransportParameters(newParams, listener, cfgCtx);
        }
    }

    public void removeTransportParameter(String param,
                                         boolean listener, ConfigurationContext cfgCtx) throws Exception {
        ParameterInclude transport;
        AxisConfiguration axisConfig = cfgCtx.getAxisConfiguration();
        if (listener) {
            transport = axisConfig.getTransportIn(transportName);
        } else {
            transport = axisConfig.getTransportOut(transportName);
        }

        if (transport == null) {
        } else {
            TransportParameter[] params = getGlobalTransportParameters(listener, axisConfig);
            if (params != null) {
                List<TransportParameter> newParams = new ArrayList<TransportParameter>();
                boolean paramFound = false;
                for (TransportParameter p : params) {
                    if (p.getName().equals(param)) {
                        paramFound = true;
                        continue;
                    }
                    newParams.add(p);
                }

                if (paramFound) {
                    updateGlobalTransportParameters(newParams.toArray(
                            new TransportParameter[newParams.size()]), listener, cfgCtx);
                    return;
                }
            }

            throw new CarbonException("The transport parameter : " + param + " does not exist");
        }
    }

    public abstract boolean dependenciesAvailable(TransportParameter[] params);

    private void setParameters(ParameterInclude transport,
                               TransportParameter[] params) throws Exception {

        ArrayList<Parameter> currentParams = transport.getParameters();
        for (Parameter param : currentParams) {
        	transport.removeParameter(param);
        }
        if (params != null) {
            for (TransportParameter p : params) {
                transport.addParameter(TransportBuilderUtils.toAxisParameter(p));
            }
        }
    }

    private TransportParameter[] getParameters(ParameterInclude transport) {
        if (transport == null || transport.getParameters() == null ||
            transport.getParameters().size() == 0) {
            return null;
        }

        List<TransportParameter> params = new ArrayList<TransportParameter>();
        List<Parameter> axisParams = transport.getParameters();

        for (Parameter p : axisParams) {
            TransportParameter transportParam = new TransportParameter();
            transportParam.setName(p.getName());
            transportParam.setValue(p.getValue().toString());
            transportParam.setParamElement(p.getParameterElement().toString());
            params.add(transportParam);
        }

        return params.toArray(new TransportParameter[params.size()]);
    }
}
