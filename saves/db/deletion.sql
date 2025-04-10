DELETE FROM grades;
DELETE FROM submissions;
DELETE FROM enrollments;
DELETE FROM assignments;
DELETE FROM subjects;
DELETE FROM users;

DROP TABLE grades;
DROP TABLE submissions;
DROP TABLE enrollments;
DROP TABLE assignments;
DROP TABLE subjects;
DROP TABLE users;
DROP CAST IF EXISTS (varchar AS role_type);
DROP TYPE role_type;