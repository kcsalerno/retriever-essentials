/*
This script will test the DB by executing various common sql queries. Please make sure
you have run the re_createTest script first to create the DB and the procedure.
*/

-- Select a database to work with.
USE re_inventory_test;

-- Establish a known good state for testing the DB.
CALL set_known_good_state();

-- App User, Item, and Vendor Data
SELECT * FROM app_user;
SELECT * FROM item;
SELECT * FROM vendor;

-- Checkout Data, with Student and Authority Info
SELECT co.checkout_id, 
       co.student_id, 
       u.username AS authority_email, 
       co.self_checkout, 
       co.checkout_date
FROM checkout_order co
LEFT JOIN app_user u ON co.authority_id = u.app_user_id;

-- Purchase Data, with Admin and Vendor Info
SELECT po.purchase_id, 
       u.username AS admin_email, 
       v.vendor_name, 
       po.purchase_date
FROM purchase_order po
LEFT JOIN app_user u ON po.admin_id = u.app_user_id
LEFT JOIN vendor v ON po.vendor_id = v.vendor_id;

-- Checkout Order Items Data
SELECT ci.checkout_item_id, 
       co.student_id, 
       u.username AS authority_email, 
       i.item_name, 
       ci.quantity, 
       co.checkout_date
FROM checkout_item ci
JOIN checkout_order co ON ci.checkout_id = co.checkout_id
LEFT JOIN app_user u ON co.authority_id = u.app_user_id
JOIN item i ON ci.item_id = i.item_id;

-- Purchased Items Data
SELECT pi.purchase_item_id, 
       po.purchase_id, 
       u.username AS admin_email, 
       v.vendor_name, 
       i.item_name, 
       pi.quantity, 
       po.purchase_date
FROM purchase_item pi
JOIN purchase_order po ON pi.purchase_id = po.purchase_id
LEFT JOIN app_user u ON po.admin_id = u.app_user_id
LEFT JOIN vendor v ON po.vendor_id = v.vendor_id
JOIN item i ON pi.item_id = i.item_id;

-- Inventory Log Data
SELECT il.log_id, 
       u.username AS authority_email, 
       i.item_name, 
       il.quantity_change, 
       il.reason, 
       il.time_stamp
FROM inventory_log il
LEFT JOIN app_user u ON il.authority_id = u.app_user_id
JOIN item i ON il.item_id = i.item_id;

-- Popular Items Data
SELECT i.item_name, SUM(ci.quantity) AS total_checkouts
FROM checkout_item ci
JOIN item i ON ci.item_id = i.item_id
GROUP BY ci.item_id
ORDER BY total_checkouts DESC
LIMIT 5;

-- Popular Categories Data
SELECT i.category, SUM(ci.quantity) AS total_checkouts
FROM checkout_item ci
JOIN item i ON ci.item_id = i.item_id
GROUP BY i.category
ORDER BY total_checkouts DESC
LIMIT 5;


select * from checkout_item ci;
-- Checkout Items Data
SELECT 
    ci.checkout_id,
    ci.item_id,
    ci.quantity,

    i.item_name,
    i.item_description,
    i.category,
    i.price_per_unit,

    co.student_id,
    co.authority_id,
    co.self_checkout,

    au.app_user_id AS authority_user_id,
    au.username AS authority_email

FROM checkout_item ci
JOIN item i ON ci.item_id = i.item_id
JOIN checkout_order co ON ci.checkout_id = co.checkout_id
JOIN app_user au ON co.authority_id = au.app_user_id;