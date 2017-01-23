#!/bin/bash
# Process name ( For display )
# OS specific support.  $var _must_ be set to either true or false.
#ulimit -n 100000

cygwin=false;
darwin=false;
os400=false;
mingw=false;
case "`uname`" in
CYGWIN*) cygwin=true;;
MINGW*) mingw=true;;
OS400*) os400=true;;
Darwin*) darwin=true
        if [ -z "$JAVA_VERSION" ] ; then
             JAVA_VERSION="CurrentJDK"
           else
             echo "Using Java version: $JAVA_VERSION"
           fi
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
           fi
           ;;
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

# Only set CARBON_HOME if not already set
[ -z "$CARBON_HOME" ] && CARBON_HOME=`cd "$PRGDIR/.." ; pwd`


## Set AXIS2_HOME. Needed for One Click JAR Download
#AXIS2_HOME="$CARBON_HOME"

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$CARBON_HOME" ] && CARBON_HOME=`cygpath --unix "$CARBON_HOME"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  QIBM_MULTI_THREADED=Y
  export QIBM_MULTI_THREADED
fi

# For Migwn, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  [ -n "$CARBON_HOME" ] &&
    CARBON_HOME="`(cd "$CARBON_HOME"; pwd)`"
  # TODO classpath?
fi

###########################################################################
NAME=start-all
# Daemon name, where is the actual executable
EI_INIT_SCRIPT="$CARBON_HOME/bin/integrator.sh"
ANALYTICS_INIT_SCRIPT="$CARBON_HOME/wso2/analytics/bin/wso2server.sh"
BPS_INIT_SCRIPT="$CARBON_HOME/wso2/business-process/bin/wso2server.sh"
MB_INIT_SCRIPT="$CARBON_HOME/wso2/broker/bin/wso2server.sh"

analyticsoffset=1
bpsoffset=2
mboffset=3

echo "$CARBON_HOME"

# If the daemon is not there, then exit.
test -x $EI_INIT_SCRIPT || exit 5

$EI_INIT_SCRIPT &
sleep 5
nohup sh $ANALYTICS_INIT_SCRIPT start -DportOffset="$analyticsoffset" -Dprofile="analytics-default" &
sleep 5
nohup sh $BPS_INIT_SCRIPT start -DportOffset="$bpsoffset" -Dprofile="business-process-default" &
sleep 5
nohup sh $MB_INIT_SCRIPT start -DportOffset="$mboffset" -Dprofile="broker-default" &
