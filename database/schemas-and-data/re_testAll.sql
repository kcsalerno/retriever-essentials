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
       u.email AS authority_email, 
       co.self_checkout, 
       co.checkout_date
FROM checkout_order co
LEFT JOIN app_user u ON co.authority_id = u.app_user_id;

-- Purchase Data, with Admin and Vendor Info
SELECT po.purchase_id, 
       u.email AS admin_email, 
       v.vendor_name, 
       po.purchase_date
FROM purchase_order po
LEFT JOIN app_user u ON po.admin_id = u.app_user_id
LEFT JOIN vendor v ON po.vendor_id = v.vendor_id;

-- Checkout Order Items Data
SELECT ci.checkout_item_id, 
       co.student_id, 
       u.email AS authority_email, 
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
       u.email AS admin_email, 
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
       u.email AS authority_email, 
       i.item_name, 
       il.quantity_change, 
       il.reason, 
       il.time_stamp
FROM inventory_log il
LEFT JOIN app_user u ON il.authority_id = u.app_user_id
JOIN item i ON il.item_id = i.item_id;