DELIMITER $$
DROP TABLE IF EXISTS TEMP_ACT_HI_PROCINST$$
DROP TABLE IF EXISTS TEMP_ACT_HI_TASKINST$$
DROP PROCEDURE IF EXISTS cleanInstance$$
CREATE PROCEDURE cleanInstance(limitCount INT, lastActive DATETIME)
BEGIN
        SELECT(' Start deleting activiti instance data with instance ids ');
	START TRANSACTION;

        CREATE TEMPORARY TABLE TEMP_ACT_HI_PROCINST AS SELECT * from ACT_HI_PROCINST  WHERE END_TIME_ is not NULL AND END_TIME_ < lastActive order by ID_ asc LIMIT limitCount OFFSET 0; 
        CREATE TEMPORARY TABLE TEMP_ACT_HI_TASKINST AS ( SELECT ACT_HI_TASKINST.ID_ FROM ACT_HI_TASKINST INNER JOIN  TEMP_ACT_HI_PROCINST ON 
        ACT_HI_TASKINST.PROC_INST_ID_ = TEMP_ACT_HI_PROCINST.PROC_INST_ID_);
        

        DELETE actinst FROM ACT_HI_ACTINST actinst INNER JOIN TEMP_ACT_HI_PROCINST tempProcinst ON actinst.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_ WHERE actinst.PROC_INST_ID_ is not null;
        DELETE historyComment FROM ACT_HI_COMMENT historyComment INNER JOIN TEMP_ACT_HI_TASKINST taskInst ON historyComment.TASK_ID_ = taskInst.ID_ WHERE historyComment.TASK_ID_ is not null;
        DELETE historyComment FROM ACT_HI_COMMENT historyComment INNER JOIN TEMP_ACT_HI_PROCINST tempProcinst ON historyComment.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_  WHERE historyComment.PROC_INST_ID_ is not null;
        DELETE identityLink FROM ACT_HI_IDENTITYLINK identityLink INNER JOIN TEMP_ACT_HI_TASKINST  tempTaskinst ON identityLink.TASK_ID_ = tempTaskinst.ID_ WHERE identityLink.TASK_ID_ is not null;
        DELETE identityLink FROM ACT_HI_IDENTITYLINK identityLink INNER JOIN TEMP_ACT_HI_PROCINST tempProcinst ON identityLink.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_ WHERE  
               identityLink.PROC_INST_ID_ is not null;
     
        DELETE varinst FROM ACT_HI_VARINST varinst INNER JOIN TEMP_ACT_HI_TASKINST taskInst ON varinst.TASK_ID_ = taskInst.ID_ WHERE varinst.TASK_ID_ is not null;
        DELETE varinst FROM ACT_HI_VARINST varinst INNER JOIN TEMP_ACT_HI_PROCINST tempProcinst ON varinst.PROC_INST_ID_ = tempProcinst.PROC_INST_ID_ WHERE varinst.PROC_INST_ID_ is not null;
        DELETE attachment FROM ACT_HI_ATTACHMENT attachment INNER JOIN TEMP_ACT_HI_TASKINST taskInst ON attachment.TASK_ID_ = taskInst.ID_ WHERE attachment.TASK_ID_ is not null;
        DELETE attachment FROM ACT_HI_ATTACHMENT attachment INNER JOIN TEMP_ACT_HI_PROCINST taskInst ON attachment.PROC_INST_ID_ = taskInst.PROC_INST_ID_ WHERE attachment.PROC_INST_ID_ is not null;
        DELETE taskInst FROM ACT_HI_TASKINST taskInst INNER JOIN TEMP_ACT_HI_TASKINST tempTaskInst ON taskInst.ID_ = tempTaskInst.ID_ WHERE taskInst.ID_ is not null;
        DELETE procInst FROM ACT_HI_PROCINST procInst INNER JOIN TEMP_ACT_HI_PROCINST tempProcInst ON procInst.PROC_INST_ID_ = tempProcInst.PROC_INST_ID_ WHERE procInst.PROC_INST_ID_ is not null;

        COMMIT;
        SELECT(' End deleting activiti instance data with instance ids ');
END$$
DELIMITER ;

SET @LIMIT_COUNT =2147483647;
SELECT NOW() DAY INTO @LAST_ACTIVE; 
SELECT(' Starting cleanInstance procedure ');
CALL cleanInstance(@LIMIT_COUNT, @LAST_ACTIVE);
SELECT (' Ending cleanInstance procedure '); 



