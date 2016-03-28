--drop table rate;
--drop table wishes;
--drop table developed;
--drop table available;
--drop table review;
--drop table owns;
--drop table developer;
--drop table platform;
--drop table game;
--drop table suspension;
--drop table users;

CREATE TABLE users
( userId INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(20) NOT NULL,
password VARCHAR(30) NOT NULL,
joinDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
isModerator BIT DEFAULT 0 );

CREATE TABLE suspension 
( fromDate DATE NOT NULL,
fromTime TIME NOT NULL,
toDate DATE NOT NULL,
toTime TIME NOT NULL,
regular_userId INTEGER NOT NULL,
moderator_userId INTEGER NOT NULL,
PRIMARY KEY (fromDate, fromTime, toDate, toTime, regular_userId, moderator_userId),
FOREIGN KEY (regular_userId) REFERENCES users(userId),
FOREIGN KEY (moderator_userId) REFERENCES users(userId));

CREATE TABLE game
( gameId INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
gameName VARCHAR(100) NOT NULL,
gameGenre VARCHAR(100) NOT NULL);

CREATE TABLE platform
( pId INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
pName VARCHAR(100) NOT NULL,
cost FLOAT,
releaseDate DATE);

CREATE TABLE developer
( dId INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
dName VARCHAR(100) NOT NULL,
founded DATE);

CREATE TABLE owns
( userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
since DATE NOT NULL,
rating INTEGER,
PRIMARY KEY (userId,gameId),
FOREIGN KEY (userId) references users(userId),
FOREIGN KEY (gameId) references game(gameId));

CREATE TABLE review
( rId INTEGER NOT NULL AUTO_INCREMENT,
description VARCHAR(3000) NOT NULL,
rating INTEGER NOT NULL,
userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
PRIMARY KEY (rId, userId, gameId),
FOREIGN KEY (userId, gameId) references owns(userId, gameId));

CREATE TABLE available
( gameId INTEGER NOT NULL,
pId INTEGER NOT NULL,
price FLOAT,
releaseDate DATE,
PRIMARY KEY (gameId, pId),
FOREIGN KEY (gameId) references game(gameId),
FOREIGN KEY (pId) references platform(pId));

CREATE TABLE developed
( gameId INTEGER NOT NULL,
dId INTEGER NOT NULL,
PRIMARY KEY (gameId, dId),
FOREIGN KEY (gameId) references game(gameId),
FOREIGN KEY (dId) references developer(dId));

CREATE TABLE wishes
( userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
rank INTEGER NOT NULL,
PRIMARY KEY (userId, gameId),
FOREIGN KEY (userId) references users(userId),
FOREIGN KEY (gameId) references game(gameId));

CREATE TABLE rate
( rater_userId INTEGER NOT NULL,
rated_userId INTEGER NOT NULL,
rating INTEGER NOT NULL,
PRIMARY KEY (rater_userId,rated_userId),
FOREIGN KEY (rater_userId) references users(userId),
FOREIGN KEY (rated_userId) references users(userId));

insert into users
values (DEFAULT, 'mod_user', 'mod_user', CURDATE(),1);

insert into users
values (DEFAULT, 'user1', 'user1', CURDATE(), 0);

insert into users
values (DEFAULT, 'user2', 'user2', CURDATE(), 0);

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

insert into platform
values (DEFAULT, 'PC', null, null);

insert into platform
values (DEFAULT, 'Xbox 360', 249.99, '2005-11-22');

insert into platform
values (DEFAULT, 'PlayStation 4', 429.99, '2013-11-15');

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