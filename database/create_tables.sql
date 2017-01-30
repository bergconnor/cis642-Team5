DROP TABLE IF EXISTS markers;
DROP TABLE IF EXISTS accounts;

CREATE TABLE markers (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    name VARCHAR( 80 ) NOT NULL ,
    organization VARCHAR( 255 ) NOT NULL ,
    latitude FLOAT( 10, 6 ) NOT NULL ,
    longitude FLOAT( 10, 6 ) NOT NULL );

CREATE TABLE accounts (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    email VARCHAR( 255 ) NOT NULL ,
    username VARCHAR( 255 ) NOT NULL ,
    password VARCHAR( 255 ) NOT NULL ,
    CONSTRAINT email_uq unique(email) ,
    CONSTRAINT username_uq unique(username) );