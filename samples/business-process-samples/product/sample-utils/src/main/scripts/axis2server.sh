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
# SimpleAxis2Server Script
#
# Environment Variable Prerequisites
#
#   AXIS2_HOME   Home of Axis2 installation. If not set I will  try
#                   to figure it out.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# -----------------------------------------------------------------------------

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running Axis2 Script."
  exit 1
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
esac

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

# Only set AXIS2_HOME if not already set
[ -z "$AXIS2_HOME" ] && AXIS2_HOME=`cd "$PRGDIR" ; pwd`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$AXIS2_HOME" ] && AXIS2_HOME=`cygpath --unix "$AXIS2_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# update classpath
AXIS2_CLASSPATH="$AXIS2_HOME/../../lib"
for f in "$AXIS2_HOME"/../../repository/components/plugins/*.jar
do
  syn_mod=`ls $f | grep org.apache.synapse.module`
  if [ ! -e "$syn_mod" ]; then
    AXIS2_CLASSPATH="$AXIS2_CLASSPATH":$f
  fi
done
for f in "$AXIS2_HOME"/../../lib/*.jar
do
  AXIS2_CLASSPATH="$AXIS2_CLASSPATH":$f
done
for f in "$AXIS2_HOME"/../../lib/core/WEB-INF/lib/*.jar
do
  AXIS2_CLASSPATH="$AXIS2_CLASSPATH":$f
done
for f in "$AXIS2_HOME"/../../repository/components/extensions/*.jar
do
  AXIS2_CLASSPATH="$AXIS2_CLASSPATH":$f
done
for f in "$AXIS2_HOME"/../../repository/components/lib/*.jar
do
  AXIS2_CLASSPATH="$AXIS2_CLASSPATH":$f
done
for f in "$AXIS2_HOME"/../../lib/api/*.jar
do
  AXIS2_CLASSPATH="$AXIS2_CLASSPATH":$f
done

AXIS2_CLASSPATH="$JAVA_HOME/lib/tools.jar":"$AXIS2_CLASSPATH":"$CLASSPATH"

for f in $AXIS2_HOME/../../repository/lib/*.jar
do
  AXIS2_CLASSPATH=$f:$AXIS2_CLASSPATH
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  AXIS2_HOME=`cygpath --absolute --windows "$AXIS2_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

# endorsed dir
AXIS2_ENDORSED=$AXIS2_HOME/../../lib/endorsed

echo " Using JAVA_HOME:   $JAVA_HOME"
echo " Using AXIS2 Repository :   $AXIS2_HOME/repository"
echo " Using AXIS2 Configuration :   $AXIS2_HOME/repository/conf/axis2.xml"

HTTP_PORT_SET="FALSE"
HTTPS_PORT_SET="FALSE"

JAVACMD="java"

PROGRAM_PARAMS=""
while [ "$1" != "" ]; do

    if [ "$1" = "-xdebug" ]; then
        PROGRAM_PARAMS="$PROGRAM_PARAMS""-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,address=8000 "
        shift

    elif [ "$1" = "-name" ]; then
        PROGRAM_PARAMS="$PROGRAM_PARAMS""-Dserver_name=$2 "
        shift
        shift

    elif [ "$1" = "-http" ]; then
        PROGRAM_PARAMS="$PROGRAM_PARAMS""-Dhttp_port=$2 "
	    HTTP_PORT_SET="TRUE"
        shift
        shift

    elif [ "$1" = "-https" ]; then
        PROGRAM_PARAMS="$PROGRAM_PARAMS""-Dhttps_port=$2 "
	    HTTPS_PORT_SET="TRUE"
        shift
        shift

    elif [ "$1" = "test" ]; then
        JAVACMD="exec ""$JAVACMD"
        shift

    fi

done

if [ "$HTTP_PORT_SET" = "FALSE" ]; then
	PROGRAM_PARAMS="$PROGRAM_PARAMS""-Dhttp_port=9000 "
fi

if [ "$HTTPS_PORT_SET" = "FALSE" ]; then
	PROGRAM_PARAMS="$PROGRAM_PARAMS""-Dhttps_port=9002 "
fi

$JAVACMD -Dcarbon.home="$AXIS2_HOME/../../" $PROGRAM_PARAMS -Djava.io.tmpdir="$AXIS2_HOME/../../tmp/sampleServer" \
 -Djava.endorsed.dirs="$AXIS2_ENDORSED" -classpath "$AXIS2_CLASSPATH" org.wso2.bps.samples.util.SampleAxis2Server \
 -repo "$AXIS2_HOME/repository" -conf "$AXIS2_HOME/repository/conf/axis2.xml"

