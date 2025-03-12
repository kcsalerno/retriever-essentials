-- This script will remove all traces of the 're_inventory' database and its data.
USE re_inventory;

-- Drop the Tables
DROP TABLE IF EXISTS inventory_log;
DROP TABLE IF EXISTS checkout_item;
DROP TABLE IF EXISTS purchase_item;

DROP TABLE IF EXISTS checkout_order;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS vendor;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS item;

-- Drop the Database
DROP DATABASE IF EXISTS re_inventory;