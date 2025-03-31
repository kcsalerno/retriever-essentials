/* 
This script implements the DDL portion of the Retriever Essentials
Inventory and Checkout database schema. It will create all the necessary
tables, establish relationships between these tables, and declare
all data types and constraints. If this is not your first time running this script
then run the re_dropAll.sql script first to ensure a clean database and prevent errors.
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
);

-- Table: item
/*
Primary Key (PK): item_id
Unique Constraint:
	item_name
*/
CREATE TABLE item (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(20) UNIQUE NOT NULL,
    item_description TEXT NULL,
    nutrition_facts TEXT NULL,
	picture_path VARCHAR(255) NULL,
    category VARCHAR(20) NOT NULL,
    current_count INT NOT NULL DEFAULT 0 CHECK (current_count >= 0),
    item_limit INT NOT NULL,
    price_per_unit DECIMAL(7,2) NULL,
	enabled BOOLEAN DEFAULT TRUE
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
    authority_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity_change INT NOT NULL CHECK (quantity_change <> 0),
    reason VARCHAR(255) NOT NULL,
    time_stamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authority_id) REFERENCES app_user(app_user_id),
    FOREIGN KEY (item_id) REFERENCES item(item_id)
);

-- Table: checkout_order
/*
Primary Key (PK): checkout_id
Foreign Key (FK):
	authority_id --> app_user(app_user_id)
*/
CREATE TABLE checkout_order (
    checkout_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(10) NOT NULL,  -- Recorded manually or via myUMBC login in self-checkout
    authority_id INT NOT NULL,  -- Authority that performed checkout or enabled self-checkout
    self_checkout BOOLEAN NOT NULL DEFAULT FALSE,
    checkout_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (authority_id) REFERENCES app_user(app_user_id)
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
    item_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (checkout_id) REFERENCES checkout_order(checkout_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(item_id),
    UNIQUE (checkout_id, item_id)
);

-- Table: vendor
/*
Primary Key (PK): vendor_id
Unique Constraint:
	vendor_name, contact_email
*/
CREATE TABLE vendor (
    vendor_id INT PRIMARY KEY AUTO_INCREMENT,
    vendor_name VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NULL,
    contact_email VARCHAR(255) NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT TRUE
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
    admin_id INT NOT NULL,
    vendor_id INT NOT NULL,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES app_user(app_user_id),
    FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id)
);

-- Table: purchase_item
/*
Primary Key (PK): purchase_item_id
Foreign Key (FK):
	purchase_id --> purchase_order(purchase_id)
    item_id --> item(item_id)
*/
CREATE TABLE purchase_item (
    purchase_item_id INT PRIMARY KEY AUTO_INCREMENT,
    purchase_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (purchase_id) REFERENCES purchase_order(purchase_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(item_id),
    UNIQUE (purchase_id, item_id)
);

-- Index to speed up queries for busiest hours.
CREATE INDEX idx_checkout_order_date ON checkout_order(checkout_date);

-- Index to improve performance when retrieving most popular items.
CREATE INDEX idx_checkout_item_quantity ON checkout_item(quantity);

-- Index to optimize category-based filtering.
CREATE INDEX idx_item_category ON item(category);

-- Index to track which items are most popular on a per-day basis.
CREATE INDEX idx_checkout_item_date ON checkout_item(checkout_id, quantity);

-- Indexes for frequent queries by item_id.
CREATE INDEX idx_checkout_item_item_id ON checkout_item(item_id);
CREATE INDEX idx_purchase_item_item_id ON purchase_item(item_id);

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