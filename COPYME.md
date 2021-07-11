CREATE DATABASE edloomdb;
USE edloomdb;
SET GLOBAL max_allowed_packet =1073741824;
CREATE TABLE user (
	email VARCHAR(90) NOT NULL PRIMARY KEY,
    password VARCHAR(90) NOT NULL,
    first_name VARCHAR(90) NOT NULL,
    last_name VARCHAR(90) NOT NULL,
    registered DATE,
    banned BOOLEAN,
    admin BOOLEAN
);

CREATE TABLE course (
    ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(90) NOT NULL,
year INT NOT NULL,
description VARCHAR(90) NOT NULL,
publisher_name VARCHAR(90) NOT NULL,
publisher_email VARCHAR(90) NOT NULL,
enrollment_key VARCHAR(90) NOT NULL,
listed BOOLEAN NOT NULL,
modules JSON NOT NULL
);

CREATE TABLE enrollment (
    user  VARCHAR(90) NOT NULL,
    course SMALLINT UNSIGNED NOT NULL,
title VARCHAR(90) NOT NULL,
publisher_name VARCHAR(90) NOT NULL,
object JSON NOT NULL,
progress VARCHAR(20) NOT NULL,
    PRIMARY KEY (user, course),
    CONSTRAINT constr_enrollment_user_fk
        FOREIGN KEY email_fk (user) REFERENCES user (email)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT constr_enrollment_course_fk
        FOREIGN KEY ID_fk (course) REFERENCES course (ID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

delimiter $$
CREATE TRIGGER `expel`     
  AFTER DELETE ON `user`     
  FOR EACH ROW     
BEGIN
  DELETE FROM enrollment where user = OLD.email;
END
$$
delimiter ;

INSERT INTO user (email, password, first_name, last_name, registered, banned, admin)
VALUES 
('flaviuschiau@gmail.com','password123', 'Flaviu', 'Schiau', curdate(), false, true),
('robot@spam.io','password123', 'Sad', 'Face', curdate(), true, false);
