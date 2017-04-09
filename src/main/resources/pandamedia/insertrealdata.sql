-- 1 - Genres
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\genre.csv'
INTO TABLE genre FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (name);

-- 2 - Artists
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\artist.csv'
INTO TABLE artist FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (name);

-- 3 - Recording Labels
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\label.csv'
INTO TABLE recording_label FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (name);

-- 4 - Songwriters
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\songwriter.csv'
INTO TABLE songwriter FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (name);

-- 5 - Cover art
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\cover.csv'
INTO TABLE cover_art FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (image_path);

-- 6 - Provinces
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\province.csv'
INTO TABLE province FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (name,pst_rate,gst_rate,hst_rate);

-- Inserting albums using a temporary table to ensure correct foreign key ids
DROP TABLE IF EXISTS temp_album;

CREATE TABLE temp_album
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	release_date DATE NOT NULL,
	artist_name VARCHAR(255) NOT NULL,
	genre_name VARCHAR(255) NOT NULL,
	recording_label_name VARCHAR(255) NOT NULL,
	cover_art_name VARCHAR(255) NOT NULL,
	num_tracks INT NOT NULL DEFAULT 0,
	date_entered DATE NOT NULL,
	cost_price DOUBLE NOT NULL,
	list_price DOUBLE NOT NULL,
	sale_price DOUBLE NOT NULL DEFAULT 0.0,
	removal_status TINYINT NOT NULL DEFAULT 0,
	removal_date DATE,
	CHECK (num_tracks > 0),
	CHECK (sale_price < list_price)
);

LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\album.csv'
INTO TABLE temp_album FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (title,release_date,artist_name,recording_label_name,cover_art_name,num_tracks,date_entered,cost_price,list_price,genre_name);

INSERT INTO album(title,release_date,num_tracks,date_entered,cost_price,list_price,artist_id,recording_label_id,genre_id,cover_art_id)
	SELECT temp_album.title, temp_album.release_date, temp_album.num_tracks, temp_album.date_entered, temp_album.cost_price, 
		temp_album.list_price, artist.id, recording_label.id, genre.id, cover_art.id
	FROM temp_album, artist, recording_label, genre, cover_art
	WHERE temp_album.artist_name = artist.name 
		AND temp_album.recording_label_name = recording_label.name
		AND temp_album.genre_name = genre.name
		AND temp_album.cover_art_name = cover_art.image_path
	ORDER BY temp_album.title;

DROP TABLE temp_album;

-- Inserting tracks using a temporary table to ensure correct foreign key ids
DROP TABLE IF EXISTS temp_track;

CREATE TABLE temp_track
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	album_name VARCHAR(255) NOT NULL,
	artist_name VARCHAR(255) NOT NULL,
	songwriter_name VARCHAR(255) NOT NULL,
	genre_name VARCHAR(255) NOT NULL,
	release_date DATE NOT NULL,
	play_length VARCHAR(10) NOT NULL,
	album_track_number INT NOT NULL,
	cover_art_path VARCHAR(255) NOT NULL,
	date_entered DATE NOT NULL,
	part_of_album TINYINT NOT NULL DEFAULT 0,
	cost_price DOUBLE NOT NULL,
	list_price DOUBLE NOT NULL,
	sale_price DOUBLE NOT NULL DEFAULT 0.0,
	removal_status TINYINT NOT NULL DEFAULT 0,
	removal_date DATE,
	CHECK (play_length > 0.0),
	CHECK (album_track_number > 0),
	CHECK (sale_price < list_price)
);

LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\track.csv'
INTO TABLE temp_track FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (title,album_name,artist_name,genre_name,release_date,songwriter_name,play_length,
	album_track_number,cost_price,list_price,cover_art_path,date_entered,part_of_album);

INSERT INTO track(title,date_entered,cost_price,list_price,release_date,play_length,album_track_number,part_of_album,
	artist_id,genre_id,album_id,songwriter_id,cover_art_id)
	SELECT temp_track.title, temp_track.date_entered, temp_track.cost_price, temp_track.list_price, temp_track.release_date, 
		temp_track.play_length, temp_track.album_track_number, temp_track.part_of_album,
		artist.id, genre.id, album.id, songwriter.id, cover_art.id
	FROM temp_track, artist, genre, album, songwriter, cover_art
	WHERE temp_track.artist_name = artist.name 
		AND temp_track.genre_name = genre.name
		AND temp_track.album_name = album.title
		AND temp_track.songwriter_name = songwriter.name
		AND temp_track.cover_art_path = cover_art.image_path
	ORDER BY temp_track.title;

DROP TABLE temp_track;

-- Newsfeed
INSERT INTO newsfeed(url) VALUES ("http://rss.cbc.ca/lineup/politics.xml");

-- Advertisement
INSERT INTO advertisement(ad_path) VALUES ("carlll");