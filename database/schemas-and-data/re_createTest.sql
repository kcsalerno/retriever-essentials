/*
This script will create the test DB. Run this prior to testing the API. It also creates a procedure for establishing
a known good state that can be run prior to each unit test of the API's persistence layer. This will drop any data or
changes from previous unit tests and re-populate the DB with known good data.
*/
DROP DATABASE IF EXISTS re_inventory_test;

-- Create the 're_inventory' database.
CREATE DATABASE re_inventory_test;

-- Select a database to work with.
USE re_inventory_test;

-- Table: app_user
/*
Primary Key (PK): user_id
Unique Constraints:
	email
*/
CREATE TABLE app_user (
    app_user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL DEFAULT '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', -- Passwords are set to "P@ssw0rd!" as default
    user_role ENUM('AUTHORITY', 'ADMIN') NOT NULL,
	enabled BOOLEAN DEFAULT TRUE
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
    item_limit INT NOT NULL DEFAULT 1 CHECK (item_limit >= 1),
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

-- -----------------------------------------------------
-- Known Good State
-- -----------------------------------------------------
DELIMITER //

CREATE PROCEDURE set_known_good_state()
BEGIN
    -- Disable foreign key checks to prevent constraint issues
    SET FOREIGN_KEY_CHECKS = 0;

    -- Truncate tables to reset all data
    TRUNCATE TABLE inventory_log;
    TRUNCATE TABLE checkout_item;
    TRUNCATE TABLE purchase_item;
    TRUNCATE TABLE checkout_order;
    TRUNCATE TABLE purchase_order;
    TRUNCATE TABLE vendor;
    TRUNCATE TABLE item;
    TRUNCATE TABLE app_user;

    -- Re-enable foreign key checks
    SET FOREIGN_KEY_CHECKS = 1;

    -- -----------------------------------------------------
    -- Data
    -- -----------------------------------------------------

    -- Initial user data, passwords set to "P@ssw0rd!" for now.
		INSERT INTO app_user (email, user_role) VALUES
        ('admin@umbc.com', 'ADMIN'),
        ('authority@umbc.com', 'AUTHORITY');

    -- Test data for Vendor
    INSERT INTO vendor (vendor_name, phone_number, contact_email) VALUES
        ('Patel Brothers', '999-555-1234', 'patel@brothers.com'),
        ('Test Vendor', '111-555-4321', 'test@vendor.com');

    -- Test data for Item (storing picture paths instead of BLOBs)
    INSERT INTO item (item_name, item_description, nutrition_facts, picture_path, category, current_count, price_per_unit) VALUES
        ('Rice', 'Long grain basmati rice', 'Calories: 200 per 100g', 'https://cloudinary.com/rice123', 'Grain', 100, 2.50),
        ('Milk', 'Organic whole milk', 'Calories: 150 per cup', 'https://cloudinary.com/milk123', 'Dairy', 50, 3.99),
        ('Bread', 'Whole wheat bread', 'Calories: 80 per slice', 'https://cloudinary.com/bread123', 'Bakery', 30, 2.25),
        ('Eggs', 'Free-range eggs', 'Calories: 70 per egg', 'https://cloudinary.com/eggs123', 'Dairy', 60, 4.50);

    -- Sample purchase order data
    INSERT INTO purchase_order (admin_id, vendor_id) VALUES
        (1, 1), (1, 2);

    -- Sample purchased items
    INSERT INTO purchase_item (purchase_id, item_id, quantity) VALUES
        (1, 1, 20), (1, 2, 30), (2, 3, 40), (2, 4, 50);

    -- Sample checkout orders (admin processing the orders)
    INSERT INTO checkout_order (student_id, authority_id, self_checkout) VALUES
        ('VF63056', 2, FALSE),
        ('VF99099', 2, TRUE);

    -- Sample checked-out items (students purchasing items)
    INSERT INTO checkout_item (checkout_id, item_id, quantity) VALUES
        (1, 1, 2), (1, 2, 1), (2, 3, 3), (2, 4, 2);

    -- Sample inventory log (tracking item adjustments)
    INSERT INTO inventory_log (authority_id, item_id, quantity_change, reason) VALUES
        (2, 1, -2, 'Spoiled'),
        (2, 2, -1, 'Package Damaged'),
        (1, 3, -3, 'Shrink'),
        (1, 4, 2, 'Corrected Item Count');

END //

DELIMITER ;