/*
This script will test the DB by executing various common sql queries. Please make sure
you have run the re_testSchema script first to create the DB and the procedure.
*/

-- Select a database to work with.
USE re_inventory_test;

-- Establish a known good state for testing the DB.
CALL set_known_good_state();

