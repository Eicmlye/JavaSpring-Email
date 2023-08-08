DROP
	SCHEMA IF EXISTS `springemail`;
CREATE 
	SCHEMA `springemail`;
USE `springemail`;

CREATE 
	TABLE `Users`
		(
			`id` 				BIGINT			PRIMARY KEY		,
            `email`				CHAR(30)		UNIQUE			,
            `password`			CHAR(30)						,
            `name`				CHAR(30)
        );
        
INSERT INTO
	`Users`
VALUES
	(1, "bob@example.com", "password", "Bob");
INSERT INTO
	`Users`
VALUES
	(2, "alice@example.com", "password", "Alice");
INSERT INTO
	`Users`
VALUES
	(3, "tom@example.com", "password", "Tom");