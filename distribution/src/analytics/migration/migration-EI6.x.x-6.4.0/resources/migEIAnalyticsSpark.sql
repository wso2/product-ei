        CREATE TEMPORARY TABLE esbServiceStatPerHourMig USING CarbonAnalytics
        OPTIONS (tableName "org_wso2_esb_analytics_stream_StatPerHour",
        mergeSchema "true");
        
        CREATE TEMPORARY TABLE ESBStatAgg_HOURS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "ESBStatAgg_HOURS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, AGG_LAST_EVENT_TIMESTAMP LONG, eventTimestamp LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint");
        
        INSERT INTO TABLE ESBStatAgg_HOURS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2000','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':',hour,'00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':',hour,'00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':',hour,'00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as eventTimestamp, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM esbServiceStatPerHourMig
        WHERE componentType!='ALL'
        GROUP BY meta_tenantId, year, month, day, hour, componentId, componentType, componentName, entryPoint;
 
 	    CREATE TEMPORARY TABLE esbServiceStatPerDayMig USING CarbonAnalytics
        OPTIONS (tableName "org_wso2_esb_analytics_stream_StatPerDay",
        mergeSchema "true");
        
        CREATE TEMPORARY TABLE ESBStatAgg_DAYS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "ESBStatAgg_DAYS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, AGG_LAST_EVENT_TIMESTAMP LONG, eventTimestamp LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint");
        
        INSERT INTO TABLE ESBStatAgg_DAYS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2001','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as eventTimestamp, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM esbServiceStatPerDayMig
        WHERE componentType!='ALL'
        GROUP BY meta_tenantId, year, month, day, componentId, componentType, componentName, entryPoint;
        
        CREATE TEMPORARY TABLE esbServiceStatPerMonthMig USING CarbonAnalytics
        OPTIONS (tableName "org_wso2_esb_analytics_stream_StatPerMonth",
        mergeSchema "true");
        
        CREATE TEMPORARY TABLE ESBStatAgg_MONTHS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "ESBStatAgg_MONTHS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, AGG_LAST_EVENT_TIMESTAMP LONG, eventTimestamp LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint");
        
        INSERT INTO TABLE ESBStatAgg_MONTHS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2002','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,'01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,'01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,'01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as eventTimestamp, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM esbServiceStatPerMonthMig
        WHERE componentType!='ALL'
        GROUP BY meta_tenantId, year, month, componentId, componentType, componentName, entryPoint;
        
        CREATE TEMPORARY TABLE ESBStatAgg_YEARS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "ESBStatAgg_YEARS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, AGG_LAST_EVENT_TIMESTAMP LONG, eventTimestamp LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint");
        
        INSERT INTO TABLE ESBStatAgg_YEARS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2003','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,'01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,'01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,'01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as eventTimestamp, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM esbServiceStatPerMonthMig
        WHERE componentType!='ALL'
        GROUP BY meta_tenantId, year, componentId, componentType, componentName, entryPoint;
  
  	    CREATE TEMPORARY TABLE mediatorStatPerHourMig USING CarbonAnalytics
        OPTIONS (tableName "org_wso2_esb_analytics_stream_MediatorStatPerHour",
        mergeSchema "true");
        
        CREATE TEMPORARY TABLE MediatorStatAgg_HOURS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "MediatorStatAgg_HOURS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, entryPointHashcode STRING, hashCode STRING, AGG_LAST_EVENT_TIMESTAMP LONG, startTime LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode");
        
        INSERT INTO TABLE MediatorStatAgg_HOURS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2000','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':',hour,'00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, entryPointHashcode, hashCode, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':',hour,'00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, startTime as startTime, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM mediatorStatPerHourMig
        GROUP BY meta_tenantId, year, month, day, hour, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode, startTime;
   
   	    CREATE TEMPORARY TABLE mediatorStatPerDayMig USING CarbonAnalytics
        OPTIONS (tableName "org_wso2_esb_analytics_stream_MediatorStatPerDay",
        mergeSchema "true");
        
        CREATE TEMPORARY TABLE MediatorStatAgg_DAYS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "MediatorStatAgg_DAYS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, entryPointHashcode STRING, hashCode STRING, AGG_LAST_EVENT_TIMESTAMP LONG, startTime LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode");
        
        INSERT INTO TABLE MediatorStatAgg_DAYS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2001','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, entryPointHashcode, hashCode, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,day),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, startTime as startTime, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM mediatorStatPerDayMig
        GROUP BY meta_tenantId, year, month, day, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode, startTime;
        
        CREATE TEMPORARY TABLE mediatorStatPerMonthMig USING CarbonAnalytics
        OPTIONS (tableName "org_wso2_esb_analytics_stream_MediatorStatPerMonth",
        mergeSchema "true");
        
        CREATE TEMPORARY TABLE MediatorStatAgg_MONTHS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "MediatorStatAgg_MONTHS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, entryPointHashcode STRING, hashCode STRING, AGG_LAST_EVENT_TIMESTAMP LONG, startTime LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode");
        
        INSERT INTO TABLE MediatorStatAgg_MONTHS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2002','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,'01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, entryPointHashcode, hashCode, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,month,'01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, startTime as startTime, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM mediatorStatPerMonthMig
        GROUP BY meta_tenantId, year, month, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode, startTime;
        
        CREATE TEMPORARY TABLE MediatorStatAgg_YEARS USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "MediatorStatAgg_YEARS",
        schema "AGG_TIMESTAMP LONG, AGG_EVENT_TIMESTAMP LONG, metaTenantId INTEGER, componentId STRING, componentName STRING, componentType STRING, entryPoint STRING, entryPointHashcode STRING, hashCode STRING, AGG_LAST_EVENT_TIMESTAMP LONG, startTime LONG, AGG_SUM_duration LONG, AGG_COUNT LONG, AGG_MIN_duration LONG, AGG_MAX_duration LONG, AGG_SUM_faultCount LONG",
        primaryKeys "AGG_TIMESTAMP, AGG_EVENT_TIMESTAMP, metaTenantId, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode");
        
        INSERT INTO TABLE MediatorStatAgg_YEARS
        SELECT (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-','2003','01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_TIMESTAMP, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,'01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_EVENT_TIMESTAMP, meta_tenantId as metaTenantId, componentId, componentName, lower(componentType) as componentType, entryPoint, entryPointHashcode, hashCode, (cast(to_unix_timestamp(concat_ws(' ',concat_ws('-',year,'01','01'),concat_ws(':','00','00','00'),'+0000'),'yyyy-MM-dd hh:mm:ss Z') as LONG) * 1000) as AGG_LAST_EVENT_TIMESTAMP, startTime as startTime, sum(totalDuration) as AGG_SUM_duration, sum(noOfInvocation) as AGG_COUNT, cast(min(minDuration) as LONG) as AGG_MIN_duration, cast(max(maxDuration) as LONG) as AGG_MAX_duration, cast(sum(faultCount) as LONG) as AGG_SUM_faultCount
        FROM mediatorStatPerMonthMig
        GROUP BY meta_tenantId, year, componentId, componentType, componentName, entryPoint, entryPointHashcode, hashCode, startTime;
      
        CREATE TEMPORARY TABLE esbEventTableMig USING CarbonAnalytics
	    OPTIONS (tableName "org_wso2_esb_analytics_stream_Event",
	    mergeSchema "true");
	
	    CREATE TEMPORARY TABLE ESBEventTable USING CarbonJDBC
	    OPTIONS (dataSource "EI_ANALYTICS", tableName "ESBEventTable",
	    schema "metaTenantId INTEGER, messageFlowId STRING, host STRING, hashCode STRING, componentName STRING, componentType STRING, componentIndex INTEGER, componentId STRING, startTime LONG, endTime LONG, duration LONG, beforePayload STRING(5000), afterPayload STRING(5000), contextPropertyMap STRING(5000), transportPropertyMap STRING(5000), children STRING, entryPoint STRING, entryPointHashcode STRING, faultCount INTEGER, eventTimestamp LONG",
	    indices "metaTenantId, messageFlowId, host, hashCode, componentId, componentType, componentName, componentIndex, startTime, endTime, entryPoint, entryPointHashcode, faultCount");
	
	    INSERT INTO TABLE ESBEventTable
	    SELECT meta_tenantId as metaTenantId, messageFlowId, host, hashCode, componentName, componentType, componentIndex, componentId, startTime, endTime, duration, beforePayload, afterPayload, contextPropertyMap, transportPropertyMap, children, entryPoint, entryPointHashcode, faultCount, startTime as eventTimestamp
	    FROM esbEventTableMig;
        
        CREATE TEMPORARY TABLE ComponentNameTable USING CarbonJDBC
        OPTIONS (dataSource "EI_ANALYTICS", tableName "ComponentNameTable",
        schema "componentId STRING, componentName STRING, componentType STRING",
        primaryKeys "componentId",
        indices "componentType");       

	    INSERT INTO TABLE ComponentNameTable
	    SELECT DISTINCT componentId as componentId, componentName as componentName, componentType as componentType
	    FROM mediatorStatPerHourMig; 
                            
                            