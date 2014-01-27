# Users schema
 
# --- !Ups
 
CREATE TABLE User (
    id INT NOT NULL AUTO_INCREMENT,
    email varchar(255) NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    is_admin boolean NOT NULL,
    create_date DATETIME NOT NULL,
    text VARCHAR(255) NULL,
    PRIMARY KEY (id)
);

CREATE TABLE User2 (
    id INT NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    is_admin boolean NOT NULL,
    create_date DATETIME NOT NULL,
    text VARCHAR(255) NULL,
    PRIMARY KEY (id)
);

INSERT INTO User (email, password, fullname, is_admin, create_date)
    VALUES ('test@test.com', 'pw', 'name', true, NOW());

INSERT INTO User (email, password, fullname, is_admin, create_date)
    VALUES ('test2@test.com', 'pw2', 'name2', true, NOW());

INSERT INTO User2 (email, password, fullname, is_admin, create_date)
    VALUES ('test3@test.com', 'pw', 'name', true, NOW());

INSERT INTO User2 (email, password, fullname, is_admin, create_date)
    VALUES ('test4@test.com', 'pw2', 'name2', true, NOW());


# --- !Downs
 
DROP TABLE User;
DROP TABLE User2;
