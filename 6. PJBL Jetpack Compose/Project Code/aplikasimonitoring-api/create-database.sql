-- ========================================
-- Create Database Script
-- Database: aplikasimonitoringkelas
-- ========================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS aplikasimonitoringkelas
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Show confirmation
SELECT 'Database aplikasimonitoringkelas created successfully!' AS Message;

-- Use the database
USE aplikasimonitoringkelas;

-- Show current database
SELECT DATABASE() AS 'Current Database';
