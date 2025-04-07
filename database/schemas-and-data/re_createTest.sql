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
    username VARCHAR(255) UNIQUE NOT NULL, -- UMBC Email address
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
    item_name VARCHAR(55) UNIQUE NOT NULL,
    item_description TEXT NULL,
    nutrition_facts TEXT NULL,
	picture_path VARCHAR(255) NULL,
    category VARCHAR(55) NOT NULL,
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
		INSERT INTO app_user (username, user_role) VALUES
        ('admin@umbc.com', 'ADMIN'),
        ('authority1@umbc.com', 'AUTHORITY'),
        ('authority2@umbc.com', 'AUTHORITY');

    -- Test data for Vendor
    INSERT INTO vendor (vendor_name, phone_number, contact_email) VALUES
        ('Patel Brothers', '999-555-1234', 'contact@patelbros.com'),
        ('Sunrise Foods', '888-555-9876', 'hello@sunrisefoods.com');

   -- Items with unified "Region - Type" categories
    INSERT INTO item (item_name, item_description, nutrition_facts, picture_path, category, current_count, price_per_unit) VALUES
        -- South Asian - Staples
        ('Sona Masoori Rice', '1 lb of 20 lb bag', 'Calories: 160 per 1/4 cup', 'https://cloudinary.com/item1', 'South Asian - Staple', 200, 25.00),
        ('Toor Dahl (Red Lentils)', '1 lb of 179 lbs bulk', 'Calories: 180 per 1/4 cup', 'https://cloudinary.com/item2', 'South Asian - Staple', 300, 1.25),
        ('Black Chickpeas (Channa)', '1 lb of 85 lbs bulk', 'Calories: 170 per 1/4 cup', 'https://cloudinary.com/item3', 'South Asian - Staple', 200, 1.25),

        -- South Asian - Snacks
        ('Maggi Noodles', 'Instant noodles 1 pack', 'Calories: 350', 'https://cloudinary.com/item4', 'South Asian - Snack', 400, 0.50),
        ('PARLE KREAM BOUR', 'Pack of cookies', 'Calories: 90 per 2 cookies', 'https://cloudinary.com/item5', 'South Asian - Snack', 100, 1.00),
        ('HID SEEK BOURB', 'Sweet biscuit', 'Calories: 120 per 3 pieces', 'https://cloudinary.com/item6', 'South Asian - Snack', 100, 0.49),
        ('SW MASL BANA', 'Banana chips', 'Calories: 150 per oz', 'https://cloudinary.com/item7', 'South Asian - Snack', 100, 1.99),
        ('GOP SNACK PE CHO', 'Chickpea snack', 'Calories: 140 per oz', 'https://cloudinary.com/item8', 'South Asian - Snack', 100, 1.29),
        ('AD BANGA MIX', 'Spicy snack mix', 'Calories: 180 per oz', 'https://cloudinary.com/item9', 'South Asian - Snack', 100, 1.99),
        ('SW BHEL CUP', 'Instant bhel puri', 'Calories: 160 per cup', 'https://cloudinary.com/item10', 'South Asian - Snack', 100, 1.29),
        ('MAGIC MAS UPMA', 'Ready-to-eat upma', 'Calories: 200 per container', 'https://cloudinary.com/item11', 'South Asian - Snack', 100, 1.29),
        ('KURKURE MSL', 'Masala chips', 'Calories: 110 per 20g', 'https://cloudinary.com/item12', 'South Asian - Snack', 100, 1.29),
        ('LAYS CHILE LIMON', 'Spicy lime chips', 'Calories: 150 per oz', 'https://cloudinary.com/item13', 'South Asian - Snack', 100, 0.89),

        -- South Asian - Prepared Meals
        ('MTR navaratan korma', 'Vegetable curry', 'Calories: 220 per pouch', 'https://cloudinary.com/item14', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR alu muttar', 'Potato peas curry', 'Calories: 210 per pouch', 'https://cloudinary.com/item15', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR mutter paneer', 'Peas with cheese', 'Calories: 230 per pouch', 'https://cloudinary.com/item16', 'South Asian - Prepared Meal', 100, 2.99),
        ('Mixed vegetable curry', 'Assorted veggie curry', 'Calories: 200', 'https://cloudinary.com/item17', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR palak paneer', 'Spinach cheese curry', 'Calories: 240', 'https://cloudinary.com/item18', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR shahi paneer', 'Rich cottage cheese curry', 'Calories: 250', 'https://cloudinary.com/item19', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR bhindi masala', 'Okra curry', 'Calories: 180', 'https://cloudinary.com/item20', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR chana masala', 'Chickpea curry', 'Calories: 210', 'https://cloudinary.com/item21', 'South Asian - Prepared Meal', 100, 2.99),
        ('MTR kadhi pakora', 'Yogurt curry', 'Calories: 200', 'https://cloudinary.com/item22', 'South Asian - Prepared Meal', 100, 2.99),

        -- Global Snacks and Staples (Sunrise Foods)
        ('Japanese Rice Crackers', 'Crunchy rice snack mix', 'Calories: 110 per oz', 'https://cloudinary.com/item23', 'East Asian - Snack', 100, 1.89),
        ('Plantain Chips', 'Fried plantain slices', 'Calories: 150 per oz', 'https://cloudinary.com/item24', 'Latin American - Snack', 100, 1.99),
        ('Spicy Seaweed Snacks', 'Thin roasted seaweed', 'Calories: 25 per pack', 'https://cloudinary.com/item25', 'Korean - Snack', 100, 2.29),
        ('Hummus & Pita Chips', 'Chickpea dip and pita chips', 'Calories: 220 per pack', 'https://cloudinary.com/item26', 'Mediterranean - Snack', 100, 2.99),
        ('Nacho Cheese Tortilla Chips', 'Cheesy corn chips', 'Calories: 140 per oz', 'https://cloudinary.com/item27', 'Western - Snack', 100, 1.49),
        ('Jasmine Rice', 'Aromatic Thai rice', 'Calories: 160 per 1/4 cup', 'https://cloudinary.com/item28', 'Thai - Staple', 200, 2.99),
        ('Canned Black Beans', 'Cooked black beans', 'Calories: 110 per 1/2 cup', 'https://cloudinary.com/item29', 'Latin American - Staple', 200, 1.19),
        ('Olive Oil', 'Extra virgin olive oil', 'Calories: 120 per tbsp', 'https://cloudinary.com/item30', 'Mediterranean - Staple', 100, 5.99),
        ('Pasta', 'Durum wheat spaghetti', 'Calories: 200 per 2 oz', 'https://cloudinary.com/item31', 'Italian - Staple', 150, 1.49),
        ('Cornmeal', 'Stone-ground yellow cornmeal', 'Calories: 110 per 1/4 cup', 'https://cloudinary.com/item32', 'West African - Staple', 150, 2.59),
        ('Frozen Chicken Tikka Masala', 'Chicken curry with rice', 'Calories: 320 per meal', 'https://cloudinary.com/item33', 'South Asian - Prepared Meal', 100, 4.99),
        ('Frozen Bulgogi Beef Bowl', 'Korean marinated beef with rice', 'Calories: 450 per bowl', 'https://cloudinary.com/item34', 'Korean - Prepared Meal', 100, 5.49),
        ('Microwave Pad Thai', 'Thai-style noodle dish', 'Calories: 400 per container', 'https://cloudinary.com/item35', 'Thai - Prepared Meal', 100, 3.99),
        ('Frozen Burrito', 'Beans, cheese & rice', 'Calories: 300 per burrito', 'https://cloudinary.com/item36', 'Mexican-American - Prepared Meal', 100, 2.29),
        ('Mac & Cheese Cup', 'Creamy pasta in a cup', 'Calories: 250 per cup', 'https://cloudinary.com/item37', 'Western - Prepared Meal', 100, 1.49);

    -- Purchase Orders (admin_id = 1, vendor_id = 1 = Patel Brothers)
    INSERT INTO purchase_order (admin_id, vendor_id) VALUES
        (1, 1),
        (1, 1);

    -- Purchase Items
    INSERT INTO purchase_item (purchase_id, item_id, quantity) VALUES
        (1, 1, 10), (1, 2, 20), (1, 4, 50), (1, 5, 30), (1, 6, 30),
        (2, 23, 5), (2, 27, 10), (2, 29, 60), (2, 32, 20), (2, 36, 10);
   
	-- Adjusted Checkout Orders to simulate busiest hours per open day
	INSERT INTO checkout_order (student_id, authority_id, self_checkout, checkout_date) VALUES        
		-- Monday (2 checkouts around 12 PM)
		('VF21042', 2, FALSE, '2025-03-31 12:15:00'), -- Mon
		('VF16549', 3, FALSE, '2025-03-31 12:45:00'), -- Mon

		-- Tuesday (2 checkouts around 11 AM)
		('VF74878', 2, TRUE,  '2025-04-01 11:05:00'), -- Tue
		('VF21221', 2, TRUE,  '2025-04-01 11:50:00'), -- Tue

		-- Wednesday (3 checkouts, 2 around 1 PM, 1 at 4 PM)
		('VF87923', 3, TRUE,  '2025-04-02 13:00:00'), -- Wed
		('VF72705', 3, FALSE, '2025-04-02 13:30:00'), -- Wed
		('VF39336', 2, TRUE,  '2025-04-02 16:15:00'), -- Wed

		-- Friday (3 checkouts, 2 around 3 PM, 1 at 11 AM)
		('VF96841', 2, FALSE, '2025-04-04 15:10:00'), -- Fri
		('VF28040', 2, TRUE,  '2025-04-04 15:45:00'), -- Fri
		('VF73068', 3, TRUE,  '2025-04-04 11:00:00'); -- Fri

    -- Checkout Items
    INSERT INTO checkout_item (checkout_id, item_id, quantity) VALUES
        (1, 1, 2), (1, 4, 1),
        (2, 2, 3), (2, 5, 1),
        (3, 3, 1), (3, 6, 2),
        (4, 7, 2), (4, 1, 1),
        (5, 8, 1), (5, 2, 1),
        (6, 9, 1), (6, 3, 2),
        (7, 10, 2), (7, 24, 1),
        (8, 11, 2), (8, 25, 2),
        (9, 12, 2), (9, 23, 1),
        (10, 25, 1);

    -- Sample inventory log (tracking item adjustments)
    INSERT INTO inventory_log (authority_id, item_id, quantity_change, reason) VALUES
        (2, 4, -2, 'Damaged Packaging'),
        (2, 5, -2, 'Expired cookies removed'),
        (1, 6, 2, 'Re-stock correction'),
        (1, 4, 1, 'Extra bag in box from vendor.');

END //

DELIMITER ;