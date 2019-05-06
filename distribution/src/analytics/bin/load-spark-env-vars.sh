#!/bin/sh
#

echo 'Loading spark environment variables '
export CARBON_SPARK_HOME=$CARBON_HOME
export _SPARK_ASSEMBLY=$CARBON_SPARK_HOME/../components/plugins/spark-core_2.10_*.wso2*.jar
export SPARK_SCALA_VERSION=2.10
export CARBON_CONFIG_DIR_PATH=$CARBON_HOME/conf
export CARBON_INTERNAL_LIB_DIR_PATH=$CARBON_HOME/../lib
export CARBON_EXTERNAL_LIB_DIR_PATH=$CARBON_HOME/../../lib
export CARBON_DROPINS_DIR_PATH=$CARBON_HOME/../../dropins
export COMPONENTS_REPO=$CARBON_HOME/../components/plugins
export CARBON_DATA_DIR_PATH=$CARBON_HOME/repository/data
# *** jars will be added to the spark classpath in the code itself. check DAS-105
# export SPARK_CLASSPATH=`java -cp $CARBON_SPARK_HOME/repository/components/plugins/org.wso2.carbon.analytics.spark.utils*.jar org.wso2.carbon.analytics.spark.utils.ComputeClasspath $CARBON_HOME`
# export SPARK_CLASSPATH=$SPARK_CLASSPATH:$(echo $CARBON_SPARK_HOME/repository/components/lib/*.jar | tr ' ' ':')
mkdir -p $CARBON_SPARK_HOME/lib_managed/jars
