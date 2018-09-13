# ---------------------------------------------------------------------------
#  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
#  WSO2 Inc. licenses this file to you under the Apache License,
#  Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied. See the License for the
#  specific language governing permissions and limitations
#  under the License.
# ---------------------------------------------------------------------------

# Base image
FROM openjdk:8-jre-alpine
MAINTAINER WSO2 Docker Maintainers "dev@wso2.org"

# User details
ARG USER=wso2ei
ARG USER_GROUP=wso2ei
ARG USER_HOME=/home/${USER}

# Resource directory
ARG FILES=./files

# Broker details
ARG MICROESB_NAME=wso2ei
ARG MICROESB_VERSION=${product.version}
ARG MICROESB_DISTRIBUTION=${MICROESB_NAME}-${MICROESB_VERSION}
ARG MICROESB_HOME=${USER_HOME}/${MICROESB_DISTRIBUTION}

# Add a user group and a user
RUN addgroup -S ${USER_GROUP} && adduser -S -G ${USER_GROUP} ${USER}

# Copy mico esb distribution to user's home directory
#COPY --chown=wso2ei:wso2ei ${FILES}/${MICROESB_DISTRIBUTION} ${MICROESB_HOME}
COPY ${FILES}/${MICROESB_DISTRIBUTION} ${MICROESB_HOME}
RUN chown -R wso2ei:wso2ei ${MICROESB_HOME}
#COPY --chown=wso2ei:wso2ei ${FILES}/carbonapps  ${MICROESB_HOME}/wso2/micro-integrator/repository/deployment/server/carbonapps
COPY ${FILES}/carbonapps  ${MICROESB_HOME}/wso2/micro-integrator/repository/deployment/server/carbonapps

# set the user and work directory
USER ${USER}
WORKDIR ${USER_HOME}

# Set environment variable
ENV MICROESB_HOME ${MICROESB_HOME}

# Expose mico esb ports
EXPOSE 8290 8253

# Execute micro esb startup script
CMD ["sh", "-c", "${MICROESB_HOME}/bin/micro-integrator.sh"]
