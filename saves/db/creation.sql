-- Create database (if not already created)
-- CREATE DATABASE edu_app;

-- Switch to the database
-- \c edu_app;

-- Native ENUM
CREATE TYPE role_type AS ENUM ('STUDENT', 'TEACHER', 'ADMIN');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role role_type NOT NULL,
    operation VARCHAR(255),
    last_modified VARCHAR(255)
);

CREATE TABLE subjects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    teacher_id INT NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE assignments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    max_points DOUBLE PRECISION NOT NULL,
    deadline TIMESTAMP NOT NULL,
    subject_id INT NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE enrollments (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE submissions (
    id SERIAL PRIMARY KEY,
    submitted_at TIMESTAMP NOT NULL,
    student_comment TEXT,
    student_id INT NOT NULL,
    assignment_id INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE
);

CREATE TABLE grades (
    id SERIAL PRIMARY KEY,
    score INT NOT NULL,
    feedback TEXT,
    submission_id INT NOT NULL UNIQUE,
    teacher_id INT NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);
