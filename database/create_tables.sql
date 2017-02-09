DROP TABLE IF EXISTS `markers`;
DROP TABLE IF EXISTS `accounts`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `markers` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `name` VARCHAR( 80 ) NOT NULL ,
    `organization` VARCHAR( 255 ) NOT NULL ,
    `latitude` FLOAT( 10, 6 ) NOT NULL ,
    `longitude` FLOAT( 10, 6 ) NOT NULL ,
    `verified` BOOLEAN NOT NULL DEFAULT '0'
  ) ENGINE = InnoDB ;

CREATE TABLE `accounts` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `email` VARCHAR( 255 ) NOT NULL ,
    `username` VARCHAR( 255 ) NOT NULL ,
    `password` VARCHAR( 255 ) NOT NULL ,
    `admin` BOOLEAN NOT NULL ,
    CONSTRAINT email_uq unique(email) ,
    CONSTRAINT username_uq unique(username)
    ) ENGINE = InnoDB ;

CREATE TABLE `users` (
    `id` INT( 10 ) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `first` VARCHAR( 32 ) NOT NULL ,
    `last` VARCHAR( 32 ) NOT NULL ,
    `password` VARCHAR( 32 ) NOT NULL ,
    `email` TEXT NOT NULL ,
    `hash` VARCHAR( 32 ) NOT NULL ,
    `active` BOOLEAN NOT NULL DEFAULT '0' ,
    `admin` BOOLEAN NOT NULL DEFAULT '0'
    ) ENGINE = InnoDB ;
