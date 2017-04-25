DROP TABLE IF EXISTS `markers`;
DROP TABLE IF EXISTS `tests`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id`            INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `first`         VARCHAR( 32 ) NOT NULL ,
  `last`          VARCHAR( 32 ) NOT NULL ,
  `organization`  VARCHAR( 64 ) NOT NULL ,
  `password`      VARCHAR( 32 ) NOT NULL ,
  `email`         TEXT NOT NULL ,
  `hash`          VARCHAR( 32 ) NOT NULL ,
  `active`        BOOLEAN NOT NULL DEFAULT '0' ,
  `admin`         BOOLEAN NOT NULL DEFAULT '0'
  ) ENGINE = InnoDB ;

CREATE TABLE `tests` (
  `id`        INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `type`      VARCHAR( 32 ) NOT NULL ,
  CONSTRAINT  type_uq UNIQUE( `type` )
  ) ENGINE = InnoDB ;

CREATE TABLE `markers` (
  `id`            INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `user_id`       INT NOT NULL ,
  `date`          DATETIME NOT NULL ,
  `test_id`       INT NOT NULL ,
  `concentration` FLOAT ( 5, 2 ) NOT NULL ,
  `latitude`      FLOAT( 10, 6 ) NOT NULL ,
  `longitude`     FLOAT( 10, 6 ) NOT NULL ,
  `serial`        INT NOT NULL ,
  `city`          VARCHAR( 32 ) NOT NULL ,
  `state`         VARCHAR( 32 ) NOT NULL ,
  `temperature`   FLOAT( 5, 2 ) NOT NULL ,
  `precipitation` FLOAT( 4, 2 ) NOT NULL ,
  `comment`       VARCHAR( 256 ) ,
  `verified`      BOOLEAN NOT NULL DEFAULT '0' ,
  CONSTRAINT      users_fk
    FOREIGN KEY( `user_id` ) REFERENCES users( `id` ) ,
  CONSTRAINT      tests_fk
    FOREIGN KEY( `test_id` ) REFERENCES tests( `id` )
  ) ENGINE = InnoDB ;
