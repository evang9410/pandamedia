-- Users
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\user.csv'
INTO TABLE shop_user FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (title,last_name,first_name,street_address,city,province_id,country,postal_code,home_phone,email,hashed_pw,salt,last_genre_searched);

-- Invoice
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\invoice.csv'
INTO TABLE invoice FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (user_id,sale_date,total_net_value,pst_tax,gst_tax,hst_tax,total_gross_value);

-- Invoice Track
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\invoice_track.csv'
INTO TABLE invoice_track FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (invoice_id,track_id,final_price);

-- Invoice Album
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\invoice_album.csv'
INTO TABLE invoice_album FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (invoice_id,album_id,final_price);

-- Review
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\review.csv'
INTO TABLE review FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (track_id,user_id,date_entered,rating,review_content,approval_status);

-- Survey
LOAD DATA LOCAL INFILE 'C:\\pandamedia\\real\\survey.csv'
INTO TABLE survey FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' (question,answer_a,answer_b,answer_c,answer_d,votes_a,votes_b,votes_c,votes_d);

-- Front Page Settings
INSERT INTO front_page_settings(survey_id, newsfeed_id, ad_a_id) VALUES (1, 1, 1);