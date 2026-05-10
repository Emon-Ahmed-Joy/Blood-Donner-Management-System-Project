-- Create the database
CREATE DATABASE IF NOT EXISTS blood_donor_db;
USE blood_donor_db;

-- Table for Users (includes both regular users and donors)
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    location VARCHAR(100),
    is_donor BOOLEAN DEFAULT FALSE,
    is_blocked BOOLEAN DEFAULT FALSE,
    has_update BOOLEAN DEFAULT FALSE,
    -- Donor specific fields
    blood_group VARCHAR(5),
    medical_condition TEXT,
    is_available BOOLEAN DEFAULT TRUE
);

-- Table for Admins
CREATE TABLE IF NOT EXISTS admins (
    admin_id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL
);

-- Table for Blood Requests
CREATE TABLE IF NOT EXISTS blood_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_email VARCHAR(100),
    requester_name VARCHAR(100),
    donor_email VARCHAR(100),
    blood_group VARCHAR(5),
    patient_name VARCHAR(100),
    hospital_name VARCHAR(100),
    location VARCHAR(100),
    medical_condition TEXT,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (requester_email) REFERENCES users(email) ON DELETE CASCADE,
    FOREIGN KEY (donor_email) REFERENCES users(email) ON DELETE CASCADE
);

-- Insert initial sample data
INSERT IGNORE INTO admins (admin_id, password) VALUES ('admin', 'admin123');

INSERT IGNORE INTO users (name, email, password, blood_group, state, location, medical_condition, is_donor, is_available) 
VALUES ('Emon Ahmed Joy', 'emon@gmail.com', 'pass123', 'O+', 'Dhaka', 'Savar', 'None', TRUE, TRUE);

INSERT IGNORE INTO users (name, email, password, blood_group, state, location, medical_condition, is_donor, is_available) 
VALUES ('Durjoy', 'durjoy@gmail.com', 'pass456', 'A+', 'Dhaka', 'Khagan', 'None', TRUE, TRUE);

INSERT IGNORE INTO users (name, email, password, state, location, is_donor) 
VALUES ('Normal User', 'user@gmail.com', 'pass123', 'Chittagong', 'Pahartali', FALSE);
