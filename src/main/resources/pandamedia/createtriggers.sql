DROP TRIGGER IF EXISTS after_track_insert;

DELIMITER //
CREATE TRIGGER after_track_insert
AFTER
INSERT
ON track
FOR EACH ROW
BEGIN
	DECLARE num INT;
	SELECT COUNT(*) INTO num FROM track WHERE album_id = NEW.album_id;
	UPDATE album SET num_tracks = num WHERE id = NEW.album_id;
END//
DELIMITER ;