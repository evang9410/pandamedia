-- Not best practice, but makes it easier to view data when constantly re-running this script
-- Can't use truncate with FK constraints
DELETE FROM invoice_track;
DELETE FROM invoice_album;
DELETE FROM review;
DELETE FROM invoice;
DELETE FROM shop_user;
DELETE FROM track;
DELETE FROM album;
DELETE FROM genre;
DELETE FROM province;
DELETE FROM cover_art;
DELETE FROM artist;
DELETE FROM songwriter;
DELETE FROM recording_label;
-- Also not best practice
ALTER TABLE invoice_track AUTO_INCREMENT = 1;
ALTER TABLE invoice_album AUTO_INCREMENT = 1;
ALTER TABLE review AUTO_INCREMENT = 1;
ALTER TABLE invoice AUTO_INCREMENT = 1;
ALTER TABLE shop_user AUTO_INCREMENT = 1;
ALTER TABLE track AUTO_INCREMENT = 1;
ALTER TABLE album AUTO_INCREMENT = 1;
ALTER TABLE genre AUTO_INCREMENT = 1;
ALTER TABLE province AUTO_INCREMENT = 1;
ALTER TABLE cover_art AUTO_INCREMENT = 1;
ALTER TABLE artist AUTO_INCREMENT = 1;
ALTER TABLE songwriter AUTO_INCREMENT = 1;
ALTER TABLE recording_label AUTO_INCREMENT = 1;