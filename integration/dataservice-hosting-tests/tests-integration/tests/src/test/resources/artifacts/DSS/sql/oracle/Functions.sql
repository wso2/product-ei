CREATE FUNCTION getStudentGrades
(
    class_name IN VARCHAR2,
    student_names IN names_table,
    student_grade OUT grades_table
)
RETURN Number AS
BEGIN
student_grade := grades_table();
student_grade.extend;
student_grade(1) := 10;
student_grade.extend;
student_grade(2) := 20;
RETURN 40;
END getStudentGrades;