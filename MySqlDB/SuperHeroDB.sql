DROP DATABASE IF EXISTS SuperHeroes;
CREATE DATABASE SuperHeroes;

USE SuperHeroes;

CREATE TABLE Heroes(
Id INT PRIMARY KEY AUTO_INCREMENT,
HeroName VARCHAR(100) NOT NULL,
IsHero BOOLEAN NOT NULL,
`Description` VARCHAR(250) NOT NULL
);

CREATE TABLE SuperPowers(
Id INT PRIMARY KEY AUTO_INCREMENT,
PowerName VARCHAR(100) NOT NULL
);

CREATE TABLE Locations(
Id INT PRIMARY KEY AUTO_INCREMENT,
LocName VARCHAR(100) NOT NULL,
`Description` VARCHAR(250) NOT NULL,
Address VARCHAR (150) NOT NULL,
Latitude Decimal(10,8) NOT NULL,
Longitude Decimal(10,8) NOT NULL
);

CREATE TABLE Sightings(
Id INT PRIMARY KEY AUTO_INCREMENT,
LocId INT NOT NULL,
`DateTime` DATETIME NOT NULL
);

CREATE TABLE Organizations(
Id INT PRIMARY KEY AUTO_INCREMENT,
LocId INT NOT NULL,
OrgName VARCHAR (100) NOT NULL,
`Description` VARCHAR(250) NULL,
Contact VARCHAR (50) NOT NULL
);

CREATE TABLE HeroesAndOrganizations(
Id INT PRIMARY KEY AUTO_INCREMENT,
HeroId INT NOT NULL,
OrgId INT NOT NULL
);

CREATE TABLE HeroesAndSuperPowers(
Id INT PRIMARY KEY AUTO_INCREMENT,
HeroId INT NOT NULL,
SuperPowerId INT NOT NULL
);

CREATE TABLE HeroesAndSightings(
Id INT PRIMARY KEY AUTO_INCREMENT,
HeroId INT NOT NULL,
SightingId INT NOT NULL
);

ALTER TABLE Sightings ADD CONSTRAINT fk_SightingToLocation
FOREIGN KEY (LocId) REFERENCES Locations(Id);

ALTER TABLE Organizations ADD CONSTRAINT fk_OrganizationToLocation
FOREIGN KEY (LocId) REFERENCES Locations(Id);

ALTER TABLE HeroesAndOrganizations ADD CONSTRAINT fk_HeroesAndOrganizationsToHeroes
FOREIGN KEY (HeroId) REFERENCES Heroes(Id);

ALTER TABLE HeroesAndOrganizations ADD CONSTRAINT fk_HeroesAndOrganizationsToOrganizations
FOREIGN KEY (OrgId) REFERENCES Organizations(Id);

ALTER TABLE HeroesAndSightings ADD CONSTRAINT fk_HeroesAndSightingsToSightings
FOREIGN KEY (SightingId) REFERENCES Sightings(Id);

ALTER TABLE HeroesAndSightings ADD CONSTRAINT fk_HeroesAndSightingsToHeroes
FOREIGN KEY (HeroId) REFERENCES Heroes(Id);

ALTER TABLE HeroesAndSuperPowers ADD CONSTRAINT fk_HeroesAndSuperPowersToHeroes
FOREIGN KEY (HeroId) REFERENCES Heroes(Id);

ALTER TABLE HeroesAndSuperPowers ADD CONSTRAINT fk_HeroesAndSuperPowersToSuperPowers
FOREIGN KEY (SuperPowerId) REFERENCES SuperPowers(Id);

-- INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('-1', 'NA', 'NA', 'NA', '00.00000000', '00.00000000');
-- INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('-2', 'Temp Loc', 'NA', 'NA', '00.00000000', '00.00000000');
