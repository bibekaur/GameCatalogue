drop table user;
drop table suspension;
drop table game;
drop table developer;
drop table review;
drop table platform;
drop table developer;
drop table review;
drop table available;
drop table developed;
drop table owns;
drop table wishes;
drop table rate;

CREATE TABLE user
( userId INTEGER NOT NULL PRIMARY KEY,
username VARCHAR(20) NOT NULL,
password VARCHAR(30) NOT NULL,
joinDate DATE NOT NULL DEFAULT CONVERT(DATE, GETDATE()),
isModerator BIT DEFAULT 0 );

CREATE TABLE suspension 
( fromDate DATE NOT NULL,
fromTime TIME NOT NULL,
toDate DATE NOT NULL,
toTime TIME NOT NULL,
regular_userId INTEGER NOT NULL,
moderator_userId INTEGER NOT NULL,
PRIMARY KEY (fromDate, fromTime, toDate, toTime, regular_userId, moderator_userId),
FOREIGN KEY (regular_userId) REFERENCES user,
FOREIGN KEY (moderator_userId) REFERENCES user);

CREATE TABLE game
( gameId INTEGER NOT NULL PRIMARY KEY,
gameName VARCHAR(100) NOT NULL,
gameGenre VARCHAR(30) NOT NULL);

CREATE TABLE developer
( dId INTEGER NOT NULL PRIMARY KEY,
dName VARCHAR(100) NOT NULL,
founded DATE NOT NULL);

CREATE TABLE review
( rId INTEGER NOT NULL PRIMARY KEY,
description VARCHAR(3000),
rating INTEGER,
userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
FOREIGN KEY (userId, gameId) REFERENCES owns);

CREATE TABLE platform
( pId INTEGER NOT NULL PRIMARY KEY,
pName VARCHAR(100) NOT NULL,
cost FLOAT,
releaseDate DATE);

CREATE TABLE developer
( dId INTEGER NOT NULL PRIMARY KEY,
dName VARCHAR(100) NOT NULL,
founded DATE NOT NULL);

CREATE TABLE review
( rId INTEGER NOT NULL,
description VARCHAR(3000) NOT NULL,
rating INTEGER NOT NULL,
userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
PRIMARY KEY (rId, userId, gameId),
FOREIGN KEY (userId, gameId) references owns);

CREATE TABLE available
( gameId INTEGER NOT NULL,
pName VARCHAR(100) NOT NULL,
price FLOAT,
releaseDate DATE
PRIMARY KEY (gameId, pName)
FOREIGN KEY (gameId) references game
FOREIGN KEY (pName) references platform);

CREATE TABLE developed
( gameId INTEGER NOT NULL,
dId VARCHAR (100) NOT NULL,
PRIMARY KEY (gameId, dName),
FOREIGN KEY (gameId) references game,
FOREIGN KEY (dId) references developer);

CREATE TABLE owns
( userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
since DATE NOT NULL,
rating INTEGER,
PRIMARY KEY (userId,gameId),
FOREIGN KEY (userId) references user,
FOREIGN KEY (gameId) references game);

CREATE TABLE wishes
( userId INTEGER NOT NULL,
gameId INTEGER NOT NULL,
rank INTEGER NOT NULL,
PRIMARY KEY (userId, gameId),
FOREIGN KEY (userId) references user,
FOREIGN KEY (gameId) references game);

CREATE TABLE rate
( rater_userId INTEGER NOT NULL,
rated_userId INTEGER NOT NULL,
rating INTEGER NOT NULL,
PRIMARY KEY (rater_userId,rated_userId),
FOREIGN KEY (rater_userId) references user,
FOREIGN KEY (rated_userId) references user);