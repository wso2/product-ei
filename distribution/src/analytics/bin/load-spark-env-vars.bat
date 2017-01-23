echo Loading spark environment variables 
SET CARBON_SPARK_HOME=%CARBON_HOME%
SET _SPARK_ASSEMBLY=%CARBON_SPARK_HOME%\repository\components\plugins\spark-core_2.10_*.wso2*.jar
SET SPARK_SCALA_VERSION=2.10
if not exist %CARBON_SPARK_HOME%\lib_managed\jars mkdir %CARBON_SPARK_HOME%\lib_managed\jars


rem *** creating spark classpath is now handled in the code itself. check DAS-105
rem java -cp %CARBON_SPARK_HOME%\repository\components\plugins\* org.wso2.carbon.analytics.spark.utils.ComputeClasspath %CARBON_HOME% > sparkClasspath.tmp

rem setlocal EnableDelayedExpansion

rem set SPARK_CLASSPATH=

rem for /f "delims=" %%x in (sparkClasspath.tmp) do (
rem set currentline=%%x
rem set SPARK_CLASSPATH=!SPARK_CLASSPATH!!currentline!
rem )
rem del sparkClasspath.tmp

rem *** this approach is not working, because the input string exceeds 8191 characters!
rem set SPARK_CLASSPATH="
rem  for /R %%a in (java -cp %CARBON_SPARK_HOME%\repository\components\plugins\* org.wso2.carbon.analytics.spark.utils.ComputeClasspath %CARBON_HOME%) do (
rem    set SPARK_CLASSPATH=!SPARK_CLASSPATH!;%%a
rem  )
rem  set SPARK_CLASSPATH=!SPARK_CLASSPATH!"

rem IF "%SPARK_CLASSPATH%"=="" echo WARN: SPARK_CLASSPATH is empty^^!

rem  set SPARK_CLASSPATH="
rem   for /R %CARBON_SPARK_HOME%\repository\components\lib %%a in (*.jar) do (
rem     set SPARK_CLASSPATH=!SPARK_CLASSPATH!;%%a
rem   )
rem   set SPARK_CLASSPATH=!SPARK_CLASSPATH!"


endlocal