#!/bin/sh

#  Copyright 2001,2004-2006 The Apache Software Foundation
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

# ----------------------------------------------------------------------------
# Script for running the WSO2 BPS Server samples
#
# Environment Variable Prerequisites
#
#   CARBON_HOME     Home of WSO2 Carbon installation. If not set I will  try
#                   to figure it out.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the commands
#                   is executed.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# -----------------------------------------------------------------------------

# resolve links - $0 may be a softlink
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

# ----- Process the input command ----------------------------------------------
BPEL_DEP_DIR=$PRGDIR/../repository/deployment/server/bpel
HT_DEP_DIR=$PRGDIR/../repository/deployment/server/humantasks
BPEL_SAM_DIR=$PRGDIR/../repository/samples/bpel
HT_SAM_DIR=$PRGDIR/../repository/samples/humantask
SAMPLE=""

if [ ! -e $BPEL_DEP_DIR ] ;then
    mkdir -p $BPEL_DEP_DIR
fi

if [ ! -e $HT_DEP_DIR ] ;then
    mkdir -p $HT_DEP_DIR
fi

if [ "$1" = "-s" ] || [ "$1" = "s" ]; then

    for var in "$@"
    do
         if [ "$var" != "-s" ] && [ "$var" != "s" ]; then

            SAMPLE=$var
            if [ -e $BPEL_SAM_DIR/$SAMPLE ] ;then
              cp -f $BPEL_SAM_DIR/$SAMPLE $BPEL_DEP_DIR/$SAMPLE
              echo "sample copied to " $BPEL_DEP_DIR/$SAMPLE
            else
            if [ -e $HT_SAM_DIR/$SAMPLE ];  then
                    cp -f $HT_SAM_DIR/$SAMPLE $HT_DEP_DIR/$SAMPLE
                    echo "sample copied to " $HT_DEP_DIR/$SAMPLE
            else
                echo "*** Specified sample cannot be found  *** Please specify a valid sample with the -s option"
                    echo "Example, to run sample 0: wso2bps-samples.sh -s HelloWorld2.zip"
                    exit
            fi
            fi
         fi
    done

else
    echo "Sample to be started is not specified. Please specify a sample to run"
    echo "Example, to run sample 0: wso2bps-samples.sh -s HelloWorld2.zip"
    exit
fi

sh $PRGDIR/wso2server.sh
