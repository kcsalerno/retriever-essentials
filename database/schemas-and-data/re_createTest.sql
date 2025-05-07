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
    INSERT INTO item (item_name, item_description, nutrition_facts, picture_path, category, current_count, price_per_unit, item_limit) VALUES
	-- South Asian - Staples
	('Sona Masoori Rice', '1 lb of 20 lb bag', 'Calories: 160, Protein: 3, Carbs: 36, Fat: 1, Sodium: 0', 'https://res.cloudinary.com/re-images/image/upload/v1746579583/ztudus2yurfyuvoc3pgs.png', 'South Asian - Staple', 200, 25.00, 5),
	('Toor Dahl (Red Lentils)', '1 lb of 179 lbs bulk', 'Calories: 180, Protein: 9, Carbs: 30, Fat: 0.5, Sodium: 5', 'https://res.cloudinary.com/re-images/image/upload/v1746579607/y90t72euospubaxfig42.jpg', 'South Asian - Staple', 300,  1.25, 5),
	('Black Chickpeas (Channa)', '1 lb of 85 lbs bulk', 'Calories: 170, Protein: 10, Carbs: 25, Fat: 2, Sodium: 15', 'https://res.cloudinary.com/re-images/image/upload/v1746579621/yujcogzjz4oo7xsem2en.jpg', 'South Asian - Staple', 200, 1.25, 1),

	-- South Asian - Snacks
	('Maggi Noodles', 'Instant noodles 1 pack', 'Calories: 350, Protein: 6, Carbs: 45, Fat: 14, Sodium: 800', 'https://res.cloudinary.com/re-images/image/upload/v1746579637/semnrfgyenx3sbw9dmsk.jpg', 'South Asian - Snack', 400, 0.50, 5),
	('PARLE KREAM BOUR', 'Pack of cookies', 'Calories: 90, Protein: 1, Carbs: 12, Fat: 4, Sodium: 60', 'https://res.cloudinary.com/re-images/image/upload/v1746579694/usde6kbxbwkjronl3nev.jpg', 'South Asian - Snack', 100, 1.00, 5),
	('HID SEEK BOURB', 'Sweet biscuit', 'Calories: 120, Protein: 2, Carbs: 18, Fat: 5, Sodium: 50', 'https://res.cloudinary.com/re-images/image/upload/v1746579714/gnhqrragodlxd0qzxbho.jpg', 'South Asian - Snack', 100, 0.49, 5),
	('SW MASL BANA', 'Banana chips', 'Calories: 150, Protein: 1, Carbs: 17, Fat: 9, Sodium: 30', 'https://res.cloudinary.com/re-images/image/upload/v1746585731/jpk712yivrwqcyzrv5no.jpg', 'South Asian - Snack', 100, 1.99, 5),
	('GOP SNACK PE CHO', 'Chickpea snack', 'Calories: 140, Protein: 5, Carbs: 15, Fat: 6, Sodium: 100', 'https://res.cloudinary.com/re-images/image/upload/v1746585926/dzhx097aecik7astwpcp.jpg', 'South Asian - Snack', 100, 1.29, 5),
	('AD BANGA MIX', 'Spicy snack mix', 'Calories: 180, Protein: 4, Carbs: 20, Fat: 8, Sodium: 250', 'https://res.cloudinary.com/re-images/image/upload/v1746586018/aizndttrkaxqrbmigjyj.png', 'South Asian - Snack', 100, 1.99, 5),
	('SW BHEL CUP', 'Instant bhel puri', 'Calories: 160, Protein: 3, Carbs: 22, Fat: 5, Sodium: 180', 'https://res.cloudinary.com/re-images/image/upload/v1746586636/furdhwtnaczwkiijussv.jpg', 'South Asian - Snack', 100, 1.29, 5),
	('MAGIC MAS UPMA', 'Ready-to-eat upma', 'Calories: 200, Protein: 4, Carbs: 30, Fat: 7, Sodium: 220', 'https://res.cloudinary.com/re-images/image/upload/v1746586498/yymxyalcgu7c9zrnfc28.jpg', 'South Asian - Snack', 100, 1.29, 5),
	('KURKURE MSL', 'Masala chips', 'Calories: 110, Protein: 2, Carbs: 13, Fat: 6, Sodium: 160', 'https://res.cloudinary.com/re-images/image/upload/v1746586510/ysmaxkgvytkktt3yngp8.png', 'South Asian - Snack', 100, 1.29, 5),
	('LAYS CHILE LIMON', 'Spicy lime chips', 'Calories: 150, Protein: 2, Carbs: 16, Fat: 9, Sodium: 210', 'https://res.cloudinary.com/re-images/image/upload/v1746586521/cy7rbqtythplveizj4qg.png', 'South Asian - Snack', 100, 0.89, 5),

	-- South Asian - Prepared Meals
	('MTR navaratan korma', 'Vegetable curry', 'Calories: 220, Protein: 6, Carbs: 28, Fat: 10, Sodium: 400', 'https://res.cloudinary.com/re-images/image/upload/v1746586374/e2voteeomc3l8dygyeku.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR alu muttar', 'Potato peas curry', 'Calories: 210, Protein: 5, Carbs: 30, Fat: 8, Sodium: 350', 'https://res.cloudinary.com/re-images/image/upload/v1746586384/erlfifuim9k7z16iz7go.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR mutter paneer', 'Peas with cheese', 'Calories: 230, Protein: 7, Carbs: 26, Fat: 11, Sodium: 390', 'https://res.cloudinary.com/re-images/image/upload/v1746586396/anfcme1lwruvbk7rnxwz.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('Mixed vegetable curry', 'Assorted veggie curry', 'Calories: 200, Protein: 4, Carbs: 22, Fat: 8, Sodium: 310', 'https://res.cloudinary.com/re-images/image/upload/v1746586431/rq3nc5zbdfhke51q1dig.png', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR palak paneer', 'Spinach cheese curry', 'Calories: 240, Protein: 8, Carbs: 18, Fat: 13, Sodium: 420', 'https://res.cloudinary.com/re-images/image/upload/v1746586443/zejyyonoxi4sql7izfoz.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR shahi paneer', 'Rich cottage cheese curry', 'Calories: 250, Protein: 9, Carbs: 20, Fat: 14, Sodium: 430', 'https://res.cloudinary.com/re-images/image/upload/v1746586457/quycfjko3xwwvb6xcfx9.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR bhindi masala', 'Okra curry', 'Calories: 180, Protein: 4, Carbs: 16, Fat: 9, Sodium: 300', 'https://res.cloudinary.com/re-images/image/upload/v1746586535/kkzogsgdk34mzac3l0nx.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR chana masala', 'Chickpea curry', 'Calories: 210, Protein: 6, Carbs: 25, Fat: 8, Sodium: 350', 'https://res.cloudinary.com/re-images/image/upload/v1746586546/gb6ebrv0ysxmmlmrpkgf.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),
	('MTR kadhi pakora', 'Yogurt curry', 'Calories: 200, Protein: 5, Carbs: 18, Fat: 9, Sodium: 330', 'https://res.cloudinary.com/re-images/image/upload/v1746586578/ezlhzncan6s5gju0ukgt.jpg', 'South Asian - Prepared Meal', 100, 2.99, 5),

	-- Global Snacks and Staples
	('Japanese Rice Crackers', 'Crunchy rice snack mix', 'Calories: 110, Protein: 2, Carbs: 23, Fat: 3, Sodium: 190', 'https://res.cloudinary.com/re-images/image/upload/v1746586360/y4r9hqwhrgajksrztnbt.jpg', 'East Asian - Snack', 100, 1.89, 5),
	('Plantain Chips', 'Fried plantain slices', 'Calories: 150, Protein: 1, Carbs: 18, Fat: 9, Sodium: 160', 'https://res.cloudinary.com/re-images/image/upload/v1746586331/um3pov8uwgxfz4bf8gnn.jpg', 'Latin American - Snack', 100, 1.99, 5),
	('Spicy Seaweed Snacks', 'Thin roasted seaweed', 'Calories: 25, Protein: 1, Carbs: 2, Fat: 1.5, Sodium: 65', 'https://res.cloudinary.com/re-images/image/upload/v1746586318/lutyv07ce9wozkz8gkxr.jpg', 'Korean - Snack', 100, 2.29, 5),
	('Hummus & Pita Chips', 'Chickpea dip and pita chips', 'Calories: 220, Protein: 5, Carbs: 28, Fat: 10, Sodium: 300', 'https://res.cloudinary.com/re-images/image/upload/v1746586302/lgpsyoasok3rq9myfv1j.png', 'Mediterranean - Snack', 100, 2.99, 5),
	('Nacho Cheese Tortilla Chips', 'Cheesy corn chips', 'Calories: 140, Protein: 2, Carbs: 16, Fat: 7, Sodium: 210', 'https://res.cloudinary.com/re-images/image/upload/v1746586287/xbmlj865bxbi60oehera.jpg', 'Western - Snack', 100, 1.49, 5),
	('Jasmine Rice', 'Aromatic Thai rice', 'Calories: 160, Protein: 3, Carbs: 36, Fat: 1, Sodium: 0', 'https://res.cloudinary.com/re-images/image/upload/v1746586271/cnjqk7upc2icavwe8xbv.jpg', 'Thai - Staple', 200, 2.99, 5),
	('Canned Black Beans', 'Cooked black beans', 'Calories: 110, Protein: 7, Carbs: 19, Fat: 0.5, Sodium: 140', 'https://res.cloudinary.com/re-images/image/upload/v1746586255/kk018ddwnmamicqo8bh7.jpg', 'Latin American - Staple', 200, 1.19, 5),
	('Olive Oil', 'Extra virgin olive oil', 'Calories: 120, Protein: 0, Carbs: 0, Fat: 14, Sodium: 0', 'https://res.cloudinary.com/re-images/image/upload/v1746586238/vc5ktlx7hdeny9gmjtxy.jpg', 'Mediterranean - Staple', 100, 5.99, 5),
	('Pasta', 'Durum wheat spaghetti', 'Calories: 200, Protein: 7, Carbs: 42, Fat: 1, Sodium: 0', 'https://res.cloudinary.com/re-images/image/upload/v1746586219/epkx9ocg5qx83a48zobn.jpg', 'Italian - Staple', 150, 1.49, 5),
	('Cornmeal', 'Stone-ground yellow cornmeal', 'Calories: 110, Protein: 2, Carbs: 24, Fat: 0.5, Sodium: 0', 'https://res.cloudinary.com/re-images/image/upload/v1746586206/hqrusmoe3qpqt6fy1nlt.png', 'West African - Staple', 150, 2.59, 5),
	('Frozen Chicken Tikka Masala', 'Chicken curry with rice', 'Calories: 320, Protein: 17, Carbs: 35, Fat: 12, Sodium: 700', 'https://res.cloudinary.com/re-images/image/upload/v1746586590/e6rfodc01tnmpgecwwmg.jpg', 'South Asian - Prepared Meal', 100, 4.99, 5),
	('Frozen Bulgogi Beef Bowl', 'Korean marinated beef with rice', 'Calories: 450, Protein: 21, Carbs: 50, Fat: 18, Sodium: 800', 'https://res.cloudinary.com/re-images/image/upload/v1746586190/shyhmzobe26nadbmfgdh.jpg', 'Korean - Prepared Meal', 100, 5.49, 5),
	('Microwave Pad Thai', 'Thai-style noodle dish', 'Calories: 400, Protein: 9, Carbs: 44, Fat: 16, Sodium: 650', 'https://res.cloudinary.com/re-images/image/upload/v1746586174/wvltycycvtby3n8fjfsa.jpg', 'Thai - Prepared Meal', 100, 3.99, 5),
	('Frozen Burrito', 'Beans, cheese & rice', 'Calories: 300, Protein: 11, Carbs: 36, Fat: 9, Sodium: 590', 'https://res.cloudinary.com/re-images/image/upload/v1746586143/xonjiss0mj6iyqbrb8mr.jpg', 'Mexican-American - Prepared Meal', 100, 2.29, 5),
	('Mac & Cheese Cup', 'Creamy pasta in a cup', 'Calories: 250, Protein: 6, Carbs: 31, Fat: 11, Sodium: 480', 'https://res.cloudinary.com/re-images/image/upload/v1746586128/jmiaffb5l0yvanpowtkx.jpg', 'Western - Prepared Meal', 100, 1.49, 5),

	-- NEW: Low-stock items
	('Sambar Powder', 'Spice mix for sambar soup', 'Calories: 10, Protein: 1, Carbs: 2, Fat: 0, Sodium: 50', 'https://res.cloudinary.com/re-images/image/upload/v1746580913/mfepeozgh7ktjbowd5ia.jpg', 'South Asian - Staple', 2, 0.99, 5),
	('Miso Soup Pack', 'Instant miso soup packet', 'Calories: 35, Protein: 2, Carbs: 5, Fat: 1, Sodium: 550', 'https://res.cloudinary.com/re-images/image/upload/v1746586116/ca08ogao0ads71wxobbd.jpg', 'Japanese - Prepared Meal', 1, 1.19, 5);
	   
    -- Purchase Orders (admin_id = 1, vendor_id = 1 = Patel Brothers)
    INSERT INTO purchase_order (admin_id, vendor_id, purchase_date) VALUES
        (1, 1, '2025-03-18 12:15:00'),
        (1, 1, '2025-03-20 15:45:00'),
        (1, 2, '2025-03-27 11:05:00');

    -- Purchase Items
    INSERT INTO purchase_item (purchase_id, item_id, quantity) VALUES
        (1, 1, 10), (1, 2, 20), (1, 4, 50), (1, 5, 30), (1, 6, 30),
        (2, 13, 5), (2, 17, 10), (2, 19, 60), (2, 12, 20), (2, 16, 10),
        (3, 23, 12), (3, 27, 8), (3, 29, 4), (3, 28, 20), (3, 26, 10);
        
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