#!/bin/bash
# ---------------------------------------------------------------------------
#  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
#  WSO2 Inc. licenses this file to you under the Apache License,
#  Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied. See the License for the
#  specific language governing permissions and limitations
#  under the License.
# ---------------------------------------------------------------------------

# ---------------------------------------------------------------------------
# Startup Script for WSO2 Message Broker
#
# Environment Variable Prerequisites
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the commands
#                   is executed.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# ---------------------------------------------------------------------------


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

# set MESSAGE_BROKER_HOME
MESSAGE_BROKER_HOME=`cd "$PRGDIR/../wso2/broker" ; pwd`

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=java
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  exit 1
fi

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running the Broker."
  exit 1
fi

MESSAGE_BROKER_CLASSPATH=""
for j in "$MESSAGE_BROKER_HOME"/lib/*.jar
do
    MESSAGE_BROKER_CLASSPATH="$MESSAGE_BROKER_CLASSPATH":$j
done

$JAVACMD \
    -Xms256m -Xmx1024m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath="$MESSAGE_BROKER_HOME/heap-dump.hprof" \
    $JAVA_OPTS \
    -classpath "$MESSAGE_BROKER_CLASSPATH" \
    -Dfile.encoding=UTF8 \
    -Dmessage.broker.home="$MESSAGE_BROKER_HOME" \
    -Dlog4j.configuration="file:$MESSAGE_BROKER_HOME/conf/log4j.properties" \
    -Dbroker.config="$MESSAGE_BROKER_HOME/conf/broker.yaml" \
    org.wso2.broker.Main