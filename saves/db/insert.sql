-- Insert sample data (5 records per table)
INSERT INTO users (email, password, first_name, last_name, role, operation, last_modified) VALUES
('admin@eduapp.com', /* admin */'$2a$10$2V/z7WYML43/1n73odfo0ORsxUWXT/zKJKyogg5EDbhqagpjpA916','Admin', 'User', 'ADMIN', NULL, NULL),
('john.doe@eduapp.com',/* abc123 */'$2a$10$qEH9PQcoLhZ.ZmuCwWtud.x1L9xx5p8Ou.wiHkdfLU.aXAmQPmIq6', 'John', 'Doe', 'TEACHER', NULL, NULL),
('jane.smith@eduapp.com',/* abc124 */'$2a$10$Ijcdn1K0MWA2ZzwSE33TB.KtNCFn6PhEUKv6rqPdhKRI80THMJ0K6', 'Jane', 'Smith', 'TEACHER', NULL, NULL),
('alice.johnson@eduapp.com',/* abc125 */ '$2a$10$4W43r.mfTgpRzDymR1UHIO4FdFQMtSDgCET.QgYQNSCQqSL7mXyje', 'Alice', 'Johnson', 'STUDENT', NULL, NULL),
('bob.williams@eduapp.com',/* abc126 */ '$2a$10$b.TsqsJFMIyU2TguAeOQLuzplzc4chNW4apRHf6HnmjiFQiWSwQEC', 'Bob', 'Williams', 'STUDENT', NULL, NULL);


INSERT INTO subjects (name, description, teacher_id) VALUES
('Mathematics', 'Advanced Math Course', 2),
('Java', 'Introduction Java', 3),
('Electrical Engineering', 'Electrical circuits', 3),
('English', 'Basic english idk', 2),
('Computer Science', 'Intro to Programming', 3);

INSERT INTO assignments (name, description, max_points, deadline, subject_id) VALUES
('Algebra Homework', 'Solve linear equations', 100, '2025-04-01 12:00:00', 1),
('Java Homework', 'Write an ASCII art program', 50, '2025-04-05 12:00:00', 2),
('Arduino', 'Create a train semaphore', 75, '2025-04-10 12:00:00', 3),
('English Exam 1', 'Midterm test', 60, '2025-04-15 12:00:00', 4),
('English Essay', 'Any topic', 60, '2025-04-15 12:00:00', 4),
('Java Exam', 'Midterm test', 100, '2025-04-20 12:00:00', 2);

INSERT INTO enrollments (student_id, subject_id) VALUES
(4, 1), (4, 2), (5, 3), (5, 4), (4, 5);

INSERT INTO submissions (submitted_at, student_comment, student_id, assignment_id) VALUES
('2025-03-20 10:00:00', 'Here is my algebra work', 4, 1),
('2025-03-22 14:30:00', 'Java homework done ez', 4, 2),
('2025-03-25 16:45:00', 'Simple Arduino semaphore', 5, 3),
('2025-03-27 18:00:00', 'Test ENG', 5, 4),
('2025-03-27 18:00:00', 'Boeing Essay', 5, 5),
('2025-03-30 09:15:00', 'Test Java', 4, 6);

INSERT INTO grades (score, feedback, submission_id, teacher_id) VALUES
(95, 'Excellent work!', 1, 2),
(38, 'Good thinking', 2, 3),
(35, 'Could be simpler', 3, 3),
(59, 'Very well', 4, 2),
(50, 'Great analysis', 5, 2),
(80, 'Solid understanding', 6, 3);