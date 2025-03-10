/* 
This script implements the DDL portion of the Retriever Essentials
Inventory and Checkout database schema. It will create all the necessary
tables, establish relationships between these tables, and declare
all data types and constraints. If this is not your first time running this script
then run the re_dropAll.sql scrip first to ensure a clean database and prevent errors.
*/

-- Create the 're_inventory' database.
CREATE DATABASE re_inventory;

-- Show that the database has been created.
SHOW DATABASES;

-- Select a database to work with.
USE re_inventory;

-- Table: app_user
/*
Primary Key (PK): user_id
Unique Constraints:
	email
*/
CREATE TABLE app_user (
    app_user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_role ENUM('AUTHORITY', 'ADMIN') NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
--     user_first_name varchar(50) NULL,
--     user_last_name varchar(50) NULL
);

-- Table: item
/*
Primary Key (PK): item_id
Unique Constraint:
	item_name
*/
CREATE TABLE item (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(4) UNIQUE NOT NULL,
    item_description TEXT NULL,
    nutrition_facts TEXT NULL,
	picture MEDIUMBLOB NULL,
    category VARCHAR(20) NOT NULL,
    current_count INT NOT NULL DEFAULT 0 CHECK (current_count >= 0),
    price_per_unit DECIMAL(7,2) NULL
);

-- Table: inventory_log
/*
Primary Key (PK): log_id
Foreign Key (FK):
	authority_id --> app_user(app_user_id)
    item_id --> item(item_id)
*/
CREATE TABLE inventory_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    authority_id INT NULL,
    item_id INT NOT NULL,
    quantity_change INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    time_stamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authority_id) REFERENCES app_user(app_user_id) ON DELETE SET NULL,
    FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE
);

-- Table: checkout_order
/*
Primary Key (PK): checkout_id
Foreign Key (FK):
	authority_id --> app_user(app_user_id)
*/
CREATE TABLE checkout_order (
    checkout_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(10) NULL,  -- Recorded manually or via myUMBC login in self-checkout
    authority_id INT NULL,  -- Authority processing the order (NULL if self-checkout)
    self_checkout BOOLEAN NOT NULL DEFAULT FALSE,
    checkout_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authority_id) REFERENCES app_user(app_user_id) ON DELETE SET NULL
);

-- Table: ordered_item (bridge table for orders and their items)
/*
Primary Key (PK): checkout_item_id
Foreign Key (FK):
	checkout_id --> checkout_order(checkout_id)
    item_id --> item(item_id)
*/
CREATE TABLE checkout_item (
    checkout_item_id INT PRIMARY KEY AUTO_INCREMENT,
    checkout_id INT NOT NULL,
    item_id INT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (checkout_id) REFERENCES checkout_order(checkout_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE SET NULL
);

-- Table: vendor
/*
Primary Key (PK): vendor_id
Unique Constraint:
	item_name, contact_email
*/
CREATE TABLE vendor (
    vendor_id INT PRIMARY KEY AUTO_INCREMENT,
    vendor_name VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NULL,
    contact_email VARCHAR(255) NOT NULL UNIQUE
);

-- Table: purchase_order
/*
Primary Key (PK): purchase_id
Foreign Key (FK):
	admin_id --> app_user(app_user_id)
    vendor_id --> vendor(vendor_id)
*/
CREATE TABLE purchase_order (
    purchase_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NULL,
    vendor_id INT NULL,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES app_user(app_user_id) ON DELETE SET NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id) ON DELETE SET NULL
);

-- Table: purchase_item
/*
Primary Key (PK): purchase_item_id
Foreign Key (FK):
	purchase_id --> purchase(purchase_id)
    item_id --> item(item_id)
*/
CREATE TABLE purchase_item (
    purchase_item_id INT PRIMARY KEY AUTO_INCREMENT,
    purchase_id INT NOT NULL,
    item_id INT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (purchase_id) REFERENCES purchase_order(purchase_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE SET NULL
);

-- Index to speed up queries for busiest hours.
CREATE INDEX idx_checkout_order_date ON checkout_order(checkout_date);

-- Index to improve performance when retrieving most popular items.
CREATE INDEX idx_checkout_item_quantity ON checkout_item(quantity);

-- Index to optimize category-based filtering.
CREATE INDEX idx_item_category ON item(category);

-- Index to track which items are most popular on a per-day basis.
CREATE INDEX idx_checkout_item_date ON checkout_item(checkout_id, quantity);

-- Show the Tables in the re_inventory DB.
SHOW TABLES;

-- Show the Indexes for Tables in the re_inventory DB.
SHOW INDEXES FROM checkout_order;
SHOW INDEXES FROM item;
SHOW INDEXES FROM inventory_log;
SHOW INDEXES FROM purchase_order;
SHOW INDEXES FROM checkout_item;

-- Initial data to get started, passwords are set to "P@ssw0rd!" for now.
insert into app_user (email, password_hash, user_role) values
	('admin@umbc.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 'ADMIN'),
	('authority@umbc.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 'AUTHORITY');