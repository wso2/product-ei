#!/bin/sh
#stop-all.sh
# ----------------------------------------------------------------------------
#  Copyright 2016 WSO2, Inc. http://www.wso2.org
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.


PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set CARBON_HOME if not already set
[ -z "$CARBON_HOME" ] && CARBON_HOME=`cd "$PRGDIR/.." ; pwd`

###########################################################################
NAME=stop-all
# Daemon name, where is the actual executables
EI_INIT_SCRIPT="$CARBON_HOME/bin/integrator.sh"
ANALYTICS_INIT_SCRIPT="$CARBON_HOME/wso2/analytics/bin/wso2server.sh"
BPS_INIT_SCRIPT="$CARBON_HOME/wso2/business-process/bin/wso2server.sh"
MB_INIT_SCRIPT="$CARBON_HOME/wso2/broker/bin/wso2server.sh"

sh $EI_INIT_SCRIPT stop
sh $ANALYTICS_INIT_SCRIPT stop
sh $BPS_INIT_SCRIPT stop
sh $MB_INIT_SCRIPT stop

exit
