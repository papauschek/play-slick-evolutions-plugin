# Users schema
 
# --- !Ups
 
CREATE TABLE User (
    id INT NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,
    createDate DATETIME NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO User (email, password, fullname, isAdmin, createDate)
    VALUES ('test@test.com', 'pw', 'name', true, NOW());
 
# --- !Downs
 
DROP TABLE User;
