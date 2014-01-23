# Users schema
 
# --- !Ups
 
CREATE TABLE User (
    id INT NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,

    PRIMARY KEY (id)
);

INSERT INTO User (email, password, fullname, isAdmin) VALUES ('asdf@asdf.com', 'pw', 'name', true);
 
# --- !Downs
 
DROP TABLE User;
