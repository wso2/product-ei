#!/bin/sh
#

echo 'Loading spark environment variables '
export CARBON_SPARK_HOME=$CARBON_HOME
export _SPARK_ASSEMBLY=$CARBON_SPARK_HOME/repository/components/plugins/spark-core_2.10_*.wso2*.jar
export SPARK_SCALA_VERSION=2.10
# *** jars will be added to the spark classpath in the code itself. check DAS-105
# export SPARK_CLASSPATH=`java -cp $CARBON_SPARK_HOME/repository/components/plugins/org.wso2.carbon.analytics.spark.utils*.jar org.wso2.carbon.analytics.spark.utils.ComputeClasspath $CARBON_HOME`
# export SPARK_CLASSPATH=$SPARK_CLASSPATH:$(echo $CARBON_SPARK_HOME/repository/components/lib/*.jar | tr ' ' ':')
mkdir -p $CARBON_SPARK_HOME/lib_managed/jars