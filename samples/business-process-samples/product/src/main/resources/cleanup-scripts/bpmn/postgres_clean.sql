/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
CREATE OR REPLACE FUNCTION cleanInstances(limitCount INT, lastActive timestamp)
RETURNS void AS $$
BEGIN

        CREATE TABLE TEMP_ACT_HI_PROCINST AS SELECT * from ACT_HI_PROCINST  WHERE END_TIME_ is not NULL AND END_TIME_ < lastActive order by ID_ asc LIMIT limitCount OFFSET 0; 
        CREATE TABLE TEMP_ACT_HI_TASKINST AS ( SELECT ACT_HI_TASKINST.ID_ FROM ACT_HI_TASKINST INNER JOIN  TEMP_ACT_HI_PROCINST ON ACT_HI_TASKINST.PROC_INST_ID_ = TEMP_ACT_HI_PROCINST.PROC_INST_ID_);


        DELETE FROM ACT_HI_ACTINST AS actinst USING TEMP_ACT_HI_PROCINST AS tempProcinst WHERE actinst.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_  AND actinst.PROC_INST_ID_ is not null;
        DELETE FROM ACT_HI_COMMENT AS historyComment USING TEMP_ACT_HI_TASKINST AS taskInst WHERE historyComment.TASK_ID_ = taskInst.ID_ AND historyComment.TASK_ID_ is not null;
        DELETE FROM ACT_HI_COMMENT AS historyComment USING TEMP_ACT_HI_PROCINST AS tempProcinst WHERE historyComment.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_  AND historyComment.PROC_INST_ID_ is not null;
        DELETE FROM ACT_HI_IDENTITYLINK AS identityLink USING TEMP_ACT_HI_TASKINST AS tempTaskinst WHERE identityLink.TASK_ID_ = tempTaskinst.ID_ AND identityLink.TASK_ID_ is not null;
        DELETE FROM ACT_HI_IDENTITYLINK AS identityLink USING TEMP_ACT_HI_PROCINST AS tempProcinst WHERE identityLink.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_ AND  identityLink.PROC_INST_ID_ is not null;
     
        DELETE FROM ACT_HI_VARINST AS varinst USING TEMP_ACT_HI_TASKINST AS taskInst WHERE varinst.TASK_ID_ = taskInst.ID_ AND varinst.TASK_ID_ is not null;
        DELETE FROM ACT_HI_VARINST AS varinst USING TEMP_ACT_HI_PROCINST AS tempProcinst WHERE varinst.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_ AND varinst.PROC_INST_ID_ is not null;
        DELETE FROM ACT_HI_ATTACHMENT AS attachment USING TEMP_ACT_HI_TASKINST AS taskInst WHERE attachment.TASK_ID_ = taskInst.ID_ AND attachment.TASK_ID_ is not null;
        DELETE FROM ACT_HI_ATTACHMENT AS attachment USING TEMP_ACT_HI_PROCINST AS taskInst WHERE attachment.PROC_INST_ID_ = taskInst.PROC_INST_ID_ AND attachment.PROC_INST_ID_ is not null;
        DELETE FROM ACT_HI_TASKINST AS taskInst USING TEMP_ACT_HI_TASKINST AS tempTaskInst WHERE taskInst.ID_ = tempTaskInst.ID_ AND taskInst.ID_ is not null;
        DELETE FROM ACT_HI_PROCINST AS procInst USING TEMP_ACT_HI_PROCINST AS tempProcInst WHERE procInst.PROC_INST_ID_ = tempProcInst.PROC_INST_ID_ AND procInst.PROC_INST_ID_ is not null;

        DROP TABLE TEMP_ACT_HI_PROCINST;
        DROP TABLE TEMP_ACT_HI_TASKINST;
END;
$$
LANGUAGE plpgsql;

BEGIN;
--deletes bpel instances
--default instance state to delete - 30
--time interval to start deletion - now()
\echo 'Invoking the cleanInstances method for activiti completed instances'
SELECT cleanInstances(2147483647,localtimestamp);
\echo 'Completed the cleanInstances method for activiti completed instances'
COMMIT;
