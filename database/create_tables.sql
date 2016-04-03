drop table rate;
drop table wishes;
drop table developed;
drop table available;
drop table review;
drop table owns;
drop table developer;
drop table platform;
drop table game;
drop table suspension;
drop table users;
drop sequence seq_users;
drop sequence seq_developer;
drop sequence seq_game;
drop sequence seq_platform;
drop sequence seq_review;



CREATE TABLE users
( userId INTEGER NOT NULL PRIMARY KEY,
username VARCHAR(20) NOT NULL,
password VARCHAR(30) NOT NULL,
joinDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
isModerator CHAR(1) DEFAULT '0',
CONSTRAINT constraint_users_isModerator CHECK (isModerator IN ('1', '0')) );

CREATE SEQUENCE seq_users;

CREATE OR REPLACE TRIGGER trg_users
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
	SELECT seq_users.nextval INTO :new.userId FROM DUAL;
END;
/

CREATE TABLE suspension 
( fromDate DATE NOT NULL,
fromTime TIMESTAMP NOT NULL,
toDate DATE NOT NULL,
toTime TIMESTAMP NOT NULL,
regular_userId INTEGER NOT NULL,
moderator_userId INTEGER NOT NULL,
PRIMARY KEY (fromDate, fromTime, toDate, toTime, regular_userId, moderator_userId),
FOREIGN KEY (regular_userId) REFERENCES users(userId) ON DELETE CASCADE,
FOREIGN KEY (moderator_userId) REFERENCES users(userId) );

CREATE TABLE game
( gameId INTEGER NOT NULL PRIMARY KEY,
gameName VARCHAR(100) NOT NULL,
gameGenre VARCHAR(100) NOT NULL);

CREATE SEQUENCE seq_game;

CREATE OR REPLACE TRIGGER trg_game
BEFORE INSERT ON game
FOR EACH ROW
BEGIN
	SELECT seq_game.nextval INTO :new.gameId FROM DUAL;
END;
/

CREATE TABLE platform
( pId INTEGER NOT NULL PRIMARY KEY,
pName VARCHAR(100) NOT NULL,
cost FLOAT,
releaseDate DATE);

CREATE SEQUENCE seq_platform;

CREATE OR REPLACE TRIGGER trg_platform
BEFORE INSERT ON platform
FOR EACH ROW
BEGIN
	SELECT seq_platform.nextval INTO :new.pId FROM DUAL;
END;
/

CREATE TABLE developer
( dId INTEGER NOT NULL PRIMARY KEY,
dName VARCHAR(100) NOT NULL,
founded DATE);

CREATE SEQUENCE seq_developer;

CREATE OR REPLACE TRIGGER trg_developer
BEFORE INSERT ON developer
FOR EACH ROW
BEGIN
	SELECT seq_developer.nextval INTO :new.dId FROM DUAL;
END;
/

CREATE TABLE owns
( userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
since DATE NOT NULL,
rating INTEGER,
CONSTRAINT check_owns_rating CHECK (rating BETWEEN 1 and 10),
PRIMARY KEY (userId,gameId),
FOREIGN KEY (userId) references users(userId) ON DELETE CASCADE,
FOREIGN KEY (gameId) references game(gameId) ON DELETE CASCADE);

CREATE TABLE review
( rId INTEGER NOT NULL,
description VARCHAR(3000) NOT NULL,
rating INTEGER NOT NULL,
userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
PRIMARY KEY (rId, userId, gameId),
FOREIGN KEY (userId, gameId) references owns(userId, gameId) ON DELETE CASCADE);

CREATE SEQUENCE seq_review;

CREATE OR REPLACE TRIGGER trg_review
BEFORE INSERT ON review
FOR EACH ROW
BEGIN
	SELECT seq_review.nextval INTO :new.rId FROM DUAL;
END;
/

CREATE TABLE available
( gameId INTEGER NOT NULL,
pId INTEGER NOT NULL,
price FLOAT,
releaseDate DATE,
PRIMARY KEY (gameId, pId),
FOREIGN KEY (gameId) references game(gameId) ON DELETE CASCADE,
FOREIGN KEY (pId) references platform(pId));

CREATE TABLE developed
( gameId INTEGER NOT NULL,
dId INTEGER NOT NULL,
PRIMARY KEY (gameId, dId),
FOREIGN KEY (gameId) references game(gameId) ON DELETE CASCADE,
FOREIGN KEY (dId) references developer(dId));

CREATE TABLE wishes
( userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
rank INTEGER NOT NULL,
PRIMARY KEY (userId, gameId),
FOREIGN KEY (userId) references users(userId) ON DELETE CASCADE,
FOREIGN KEY (gameId) references game(gameId) ON DELETE CASCADE);

CREATE TABLE rate
( rater_userId INTEGER NOT NULL,
rated_userId INTEGER NOT NULL,
rating INTEGER NOT NULL,
CONSTRAINT check_rate_rating CHECK (rating BETWEEN 1 and 10),
PRIMARY KEY (rater_userId,rated_userId),
FOREIGN KEY (rater_userId) references users(userId) ON DELETE CASCADE,
FOREIGN KEY (rated_userId) references users(userId) ON DELETE CASCADE);

insert into users
values (DEFAULT, 'mod_user', 'mod_user', CURRENT_TIMESTAMP,1);

insert into users
values (DEFAULT, 'user1', 'user1', CURRENT_TIMESTAMP, 0);

insert into users
values (DEFAULT, 'user2', 'user2', CURRENT_TIMESTAMP, 0);

insert into game
values (DEFAULT, 'The Witness', 'Puzzle');

insert into game
values (DEFAULT, 'Undertale', 'Role-playing game');

insert into game
values (DEFAULT, 'Halo 5: Guardians', 'First-person shooter');

insert into game
values (DEFAULT, 'Bloodborne', 'Action role-playing');

insert into game
values (DEFAULT, 'Bastion', 'Action role-playing');

insert into game
values (DEFAULT, 'Recettear: An Item Shop''s Tale', 'Action role-playing, business simulation');

insert into game
values (DEFAULT, 'Fire Emblem: Conquest', 'Strategy role-playing');

insert into game
values (DEFAULT, 'Fire Emblem: Birthright', 'Strategy role-playing');

insert into game
values (DEFAULT, 'Pokemon Omega Ruby', 'Role-playing game');

insert into game
values (DEFAULT, 'Pokemon Aqua Sapphire', 'Role-playing game');

insert into platform
values (DEFAULT, 'PC', null, null);

insert into platform
values (DEFAULT, 'Xbox 360', 249.99, '2005-11-22');

insert into platform
values (DEFAULT, 'PlayStation 4', 429.99, '2013-11-15');

insert into platform 
values (DEFAULT, 'Nintendo 3DS', 199.99, '2011-03-27');

insert into developer
values (DEFAULT, 'Thekla, Inc.', null);

insert into developer
values (DEFAULT, 'EasyGameStation', '2001-07-13');

insert into developer
values (DEFAULT, '343 Industries', '2007-10-05');

insert into developer
values (DEFAULT, 'Supergiant Games', '2009-09-01');

insert into developer
values (DEFAULT, 'FromSoftware', '2009-09-01');

insert into developer
values (DEFAULT, 'tobyfox', null);

insert into developer
values (DEFAULT, 'Intelligent Systems', '1986-12-01');

insert into developer
values (DEFAULT, 'Game Freak', '1989-04-26');

insert into developed
values (1, 1);

insert into developed
values (1, 2);

insert into developed
values (1, 3);

insert into developed
values (2, 6);

insert into developed
values (3, 3);

insert into developed
values (4, 5);

insert into developed
values (5, 4);

insert into developed
values (6, 2);

insert into developed 
values (7, 7);

insert into developed
values (8, 7);

insert into developed
values (9, 8);

insert into developed
values (10, 8);

insert into available
values (1, 1, 43.99, '2016-01-26');

insert into available
values (1, 3, 43.99, '2016-01-26');

insert into available
values (2, 1, 10.99, '2015-09-15');

insert into available
values (3, 2, 59.99, '2015-10-27');

insert into available
values (4, 3, 69.99, '2015-03-24');

insert into available
values (5, 1, 14.99, '2011-07-16');

insert into available
values (6, 1, 21.99, '2010-09-10');

insert into available
values (7, 4, 39.99, '2016-02-19');

insert into available
values (8, 4, 39.99, '2016-02-19');

insert into available
values (9, 4, 39.99, '2014-11-21');

insert into available
values (10, 4, 39.99, '2014-11-21');

insert into wishes
values (1, 5, 1);

insert into wishes
values (1, 1, 2);

insert into wishes
values (1, 2, 4);

insert into wishes
values (1, 3, 3);

insert into owns
values (1, 7, CURRENT_TIMESTAMP, 10);

insert into owns
values (1, 9, CURRENT_TIMESTAMP, 8);

insert into owns
values (2, 7, CURRENT_TIMESTAMP, 10);

insert into owns
values (2, 10, CURRENT_TIMESTAMP, 9);

insert into owns
values (3, 1, CURRENT_TIMESTAMP, 5);

insert into owns
values (3, 2, CURRENT_TIMESTAMP, 6);

insert into owns
values (3, 4, CURRENT_TIMESTAMP, 9);

insert into owns
values (3, 6, CURRENT_TIMESTAMP, 1);

insert into owns
values (3, 7, CURRENT_TIMESTAMP, 10);

insert into review
values (DEFAULT, 'Fire Emblem: Conquest was a really great game! The strategy involved was excellent. Chapter 10 was brutal, but it was really satisfying to play!', 10, 1, 7);

insert into review
values (DEFAULT, 'it was alright', 8, 3, 1);

insert into review
values (DEFAULT, 'it was alright', 8, 3, 4);

insert into review
values (DEFAULT, 'it was alright', 9, 3, 7);
