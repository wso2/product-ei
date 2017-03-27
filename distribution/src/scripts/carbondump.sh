#!/bin/bash
# ----------------------------------------------------------------------------
#  Copyright 2005-2009 WSO2, Inc. http://www.wso2.org
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
#
# Environment Variable Prequisites
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the commands
#                   is executed.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# -----------------------------------------------------------------------------

DARWIN_TOOLS=""
# OS specific support.  $var _must_ be set to either true or false.
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
             DARWIN_TOOLS=$JAVA_HOME/Classes/Classes.jar
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

CARBON_DUMP_HOME=`cd "$PRGDIR/.." ; pwd`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
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
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
fi

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
  echo "You must set the JAVA_HOME variable."
  exit 1
fi


# ----- Process the input command ----------------------------------------------
for c in $*
do
	if [ "$c" = "--carbonHome" ] || [ "$c" = "-carbonHome" ] || [ "$c" = "carbonHome" ]; then
		  TEMP="--carbonHome"
		  continue

    	elif [ "$TEMP" = "--carbonHome" ]; then
		  TEMP=""  
		  if [ -z "$CARBON_HOME" ]; then
		        CARBON_HOME=$c
		  fi 

	elif [ "$c" = "--pid" ] || [ "$c" = "-pid" ] || [ "$c" = "pid" ]; then
		  TEMP="--pid"
		  continue

    	elif [ "$TEMP" = "--pid" ]; then
		  TEMP=""  
		  if [ -z "$PID" ]; then 
		  	PID=$c
          	  fi
	fi   
done

if [ -z "$CARBON_HOME" ] || [ -z "$PID" ] ; then
echo "Usage: carbondump.sh [-carbonHome path] [-pid of the carbon instance]"
echo "  e.g. carbondump.sh -carbonHome /home/user/wso2carbon-3.0.0/ -pid 5151"

exit 1
fi

#retrieve java version
SYS_JAVA_VERSION=$("$JAVA_HOME/bin/java" -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f2 )
echo "Java Version : $SYS_JAVA_VERSION"

#time stamp that we are going to use for this execution.
DATE_TIME=`date +%F`_`date +%H-%M-%S`

OUTPUT_ROOT_DIR=$CARBON_DUMP_HOME/carbondump
OUTPUT_DIR=$OUTPUT_ROOT_DIR/carbondump-$DATE_TIME
MEMORY_DUMP_DIR=$OUTPUT_DIR/memoryinfo
OS_INFO=$OUTPUT_DIR/osinfo
JAVA_INFO=$OUTPUT_DIR/javainfo
REPO_DIR=$OUTPUT_DIR/repository

#Checks whether the output directory exists or not.
if [ -d "$OUTPUT_ROOT_DIR" ]; 
then
	rm -rf $OUTPUT_ROOT_DIR
fi

mkdir $OUTPUT_ROOT_DIR
mkdir $OUTPUT_DIR
mkdir $MEMORY_DUMP_DIR
mkdir $OS_INFO
mkdir -p $REPO_DIR/logs
mkdir $REPO_DIR/conf
mkdir $REPO_DIR/database

echo "\ncarbondump.sh##Generating the java memory dump..."
jmap  $PID > $MEMORY_DUMP_DIR/shared_object_mappings.txt
jmap -dump:format=b,file=$MEMORY_DUMP_DIR/java_heap_memory_dump.jmap $PID
jmap -histo $PID > $MEMORY_DUMP_DIR/java_heap_histogram.txt 
jmap -finalizerinfo $PID > $MEMORY_DUMP_DIR/objects_awaiting_finalization.txt 
jmap -heap $PID > $MEMORY_DUMP_DIR/java_heap_summary.txt 


if [ $SYS_JAVA_VERSION -ge "8" ]; then
    echo Java 8 or higher version
    jmap -clstats $PID > $MEMORY_DUMP_DIR/java_clstats_statistics.txt 
else
    echo Java 7 or early version
    jmap -permstat $PID > $MEMORY_DUMP_DIR/java_permgen_statistics.txt
fi

echo "\ncarbondump.sh##Generating the thread dump..."
jstack $PID > $OUTPUT_DIR/thread_dump.txt

echo "\ncarbondump.sh##Capturing OS information..."
lsmod > $OS_INFO/os_module_list.txt

cat /proc/meminfo > $OS_INFO/os_meminfo.txt
cat /proc/cpuinfo > $OS_INFO/os_cpuinfo.txt

netstat -tulpn > $OS_INFO/os_open_ports.txt
ifconfig -a > $OS_INFO/os_network_cards.txt
dpkg --list > $OS_INFO/os_installed_software_unix_linux.txt
rpm -qa > $OS_INFO/os_installed_software_redhat_fedora.txt
w > $OS_INFO/os_system_up_time.txt

lsmod | mawk '{print $1}' | xargs modinfo 2>/dev/null > $OS_INFO/os_module_info.txt

echo "\ncarbondump.sh##Capturing the list of running task in the system..."
top -b -n1 > $OS_INFO/os_running_tasks.txt

echo "\ncarbondump.sh##Capturing OS Environment Variables..."
env > $OS_INFO/os_env_variables.txt

echo "\ncarbondump.sh##Generating the checksums of all the files in the CARBON_HOME directory..."
find $CARBON_HOME -iname "*" -type f | grep -v ./samples | grep -v ./docs | sort | sed -e 's/\ /\\\ /g' | xargs md5sum > $OUTPUT_DIR/checksum_values.txt

##TODO out all the carbon info to a single file, java, vesion, os version, carbon version
echo "Product"'\t\t\t'": "`cat $CARBON_HOME/bin/version.txt` > $OUTPUT_DIR/carbon_server_info.txt
echo "WSO2 Carbon Framework"'\t'": "`cat $CARBON_HOME/bin/wso2carbon-version.txt` >> $OUTPUT_DIR/carbon_server_info.txt
echo "Carbon Home"'\t\t'": "`echo $CARBON_HOME` >> $OUTPUT_DIR/carbon_server_info.txt
echo "Operating System Info"'\t'": "`uname -a` >> $OUTPUT_DIR/carbon_server_info.txt
echo "Java Home"'\t\t'": "`echo $JAVA_HOME` >> $OUTPUT_DIR/carbon_server_info.txt
java -version 2> $OUTPUT_DIR/temp_java_version.txt
echo "Java Version"'\t\t'": "`cat $OUTPUT_DIR/temp_java_version.txt | grep -h "java version" |  mawk '{print $3}'` >> $OUTPUT_DIR/carbon_server_info.txt
echo "Java VM"'\t\t\t'": "`cat $OUTPUT_DIR/temp_java_version.txt | grep -h "Java HotSpot"` >> $OUTPUT_DIR/carbon_server_info.txt
rm -rf $OUTPUT_DIR/temp_java_version.txt

echo "\ncarbondump.sh##Copying log files..."
cp -r $CARBON_HOME/repository/logs/* $REPO_DIR/logs

echo "\ncarbondump.sh##Copying conf files..."
cp -r $CARBON_HOME/conf/* $REPO_DIR/conf

echo "\ncarbondump.sh##Copying database..."	
cp -r $CARBON_HOME/repository/database/* $REPO_DIR/database

echo "\ncarbondump.sh##Getting a directory listing..."	
find $CARBON_HOME -type d | sort | grep -v ./samples | grep -v ./docs > $OUTPUT_DIR/directory_listing.txt

echo "\ncarbondump.sh##Compressing the carbondump..."
cd $OUTPUT_ROOT_DIR
zip -r $CARBON_DUMP_HOME/carbondump-$DATE_TIME.zip carbondump-$DATE_TIME

echo "\ncarbondump: "$CARBON_DUMP_HOME/carbondump-$DATE_TIME.zip"\n"
rm -rf $OUTPUT_ROOT_DIR
exit
