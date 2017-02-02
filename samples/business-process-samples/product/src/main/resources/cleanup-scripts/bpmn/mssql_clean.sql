IF (OBJECT_ID('cleanInstance') IS NOT NULL)
  DROP PROCEDURE cleanInstance;
GO
IF (OBJECT_ID('TEMP_ACT_HI_PROCINST') IS NOT NULL)
  DROP TABLE TEMP_ACT_HI_PROCINST;
GO
IF (OBJECT_ID('TEMP_ACT_HI_TASKINST') IS NOT NULL)
  DROP TABLE TEMP_ACT_HI_TASKINST;
GO


DECLARE @I_LIMIT INT;
SET @I_LIMIT = 2147483647;
DECLARE @L_ACTIVE DATETIME;
SET @L_ACTIVE=CURRENT_TIMESTAMP;

SELECT TOP (@I_LIMIT) PROC_INST_ID_ INTO TEMP_ACT_HI_PROCINST FROM ACT_HI_PROCINST WHERE END_TIME_ IS NOT NULL AND END_TIME_ < (SELECT convert(varchar, @L_ACTIVE, 120)) ORDER BY ID_ ASC;
SELECT taskInst.ID_ INTO TEMP_ACT_HI_TASKINST FROM ACT_HI_TASKINST AS taskInst INNER JOIN  TEMP_ACT_HI_PROCINST AS procInst ON taskInst.PROC_INST_ID_ = procInst.PROC_INST_ID_;

GO

CREATE PROCEDURE cleanInstance
AS
BEGIN
	PRINT ' Start deleting instance data with instance ids ';
	
	BEGIN TRANSACTION;
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
	PRINT ' End deleting instance data with instance ids ';
END
GO

BEGIN
  PRINT (' Starting cleanInstance procedure ');
  EXEC cleanInstance;
  PRINT (' Ending cleanInstance procedure '); 
END
GO
