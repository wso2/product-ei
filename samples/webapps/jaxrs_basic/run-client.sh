#!/bin/sh
# -----------------------------------------------------------------------------
#
# Environment Variable Prequisites
#
#   CARBON_HOME   Home of WSO2 AppServer installation. If not set I will  try
#                   to figure it out.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running WSO2 AppServer."
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

# Only set WSO2AppServer_HOME if not already set
[ -z "$CARBON_HOME" ] && CARBON_HOME=`cd "$PRGDIR/../../.." ; pwd`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CARBON_HOME" ] && WSO2AppServer_HOME=`cygpath --unix "$CARBON_HOME"`
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
  QIBM_MULTI_THREADED=Y
  export QIBM_MULTI_THREADED
fi

# update classpath
CLIENT_CLASSPATH=""
for f in "$CARBON_HOME"/samples/webapps/jaxrs_basic/build/lib/*.jar
do
  CLIENT_CLASSPATH=$CLIENT_CLASSPATH:$f
done
CLIENT_CLASSPATH=$CLIENT_CLASSPATH:$CLASSPATH


# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CARBON_HOME=`cygpath --absolute --windows "$CARBON_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

echo Using CARBON_HOME: $CARBON_HOME
echo Using JAVA_HOME    : $JAVA_HOME

CLIENT_CLASSPATH="$CARBON_HOME/samples/webapps/jaxrs_basic/build/classes":$CLIENT_CLASSPATH

$JAVA_HOME/bin/java -Dwso2appserver.home="$CARBON_HOME" -classpath "$CLIENT_CLASSPATH" \
-Djava.endorsed.dirs="$CARBON_HOME/lib/endorsed":"$JAVA_HOME/jre/lib/endorsed":"$JAVA_HOME/lib/endorsed" \
demo.jaxrs.client.Client http://localhost:8280/jaxrs_basic/services/customers/customerservice$*
