USE SuperHeroes;

SELECT * FROM locations;
SELECT * FROM heroes;
SELECT * FROM organizations;
SELECT * FROM heroesandorganizations;
SELECT * FROM superpowers;
SELECT * FROM heroesandsuperpowers;
SELECT * FROM sightings;
SELECT * FROM heroesandsightings;

SELECT * FROM heroes WHERE id = 1;

SELECT * FROM SuperPowers WHERE id = 1;

SELECT * FROM locations WHERE id = 1;

SELECT * FROM organizations WHERE id = 1;

SELECT * FROM sightings WHERE id = 1;

-- GETTING POWERS FOR HERO --
SELECT * FROM SuperPowers
JOIN heroesandsuperpowers ON heroesandsuperpowers.SuperPowerId = superpowers.Id
WHERE heroesandsuperpowers.HeroId = 1;


-- GETTING HEROES FOR ORG --
SELECT * FROM heroes
JOIN heroesandorganizations ON heroesandorganizations.HeroId = heroes.Id
JOIN heroesandsuperpowers ON heroesandsuperpowers.HeroId = heroes.Id
WHERE heroesandorganizations.OrgId = 1;

-- GETTING HEROES FOR SIGHTING --
SELECT * FROM heroes
JOIN heroesandsightings ON heroesandsightings.HeroId = heroes.Id
WHERE heroesandsightings.SightingId = 1;


-- UPDATES AND DELETES FOR DAO IMPL
-- UPDATE A HERO --
UPDATE heroes
SET HeroName = 'Batman', IsHero = '1', Description = 'battty'
WHERE Id = 1;

-- UPDATE AN ORG --
UPDATE organizations
SET LocId = '2', OrgName = 'JL', `Description` = 'ju', Contact = '0000000'
WHERE Id = 1;

-- UPDATE A SIGHTING --
UPDATE sightings
SET LocId = '2', `DateTime` = '2030-01-16 23:12:01'
WHERE Id = 1;

-- DELETE A HERO
DELETE FROM heroesandsuperpowers WHERE HeroId = 2;
DELETE FROM heroesandorganizations WHERE HeroId = 2;
DELETE FROM heroesandsightings WHERE HeroId = 2;
DELETE FROM heroes WHERE Id = 2;

-- DELETE A SIGHTING --
DELETE FROM heroesandsightings WHERE SightingId = 1;
DELETE FROM sightings WHERE Id = 1;

-- DELETE ORG --
DELETE FROM heroesandorganizations WHERE OrgId = 1;




-- UPDATE A POWER --
UPDATE superpowers SET PowerName = 'test' WHERE Id = 1;

-- DELETE A POWER --
DELETE FROM heroesandsuperpowers WHERE SuperPowerId = 1;
DELETE FROM superpowers WHERE Id = 1;

-- JUST CHECKING THAT MY DATA WAS INPUT PROPERLY --
SELECT
	heroes.Id AS HeroId,
	heroes.HeroName,
    -- superpowers.PowerName,
    organizations.OrgName,
    sightings.`DateTime` AS SightingTime,
    locations.LocName AS SightingLocation
FROM heroes
-- INNER JOIN heroesandsuperpowers ON heroesandsuperpowers.HeroId = heroes.Id
-- INNER JOIN superpowers ON heroesandsuperpowers.SuperPowerId = superpowers.Id
INNER JOIN heroesandorganizations ON heroesandorganizations.HeroId = heroes.Id
INNER JOIN organizations ON heroesandorganizations.OrgId = organizations.Id
INNER JOIN heroesandsightings ON heroesandsightings.HeroId = heroes.Id
INNER JOIN sightings ON heroesandsightings.SightingId = sightings.Id
INNER JOIN locations ON sightings.LocId = locations.Id
;

SELECT
	organizations.OrgName,
	organizations.LocId,
	locations.LocName,
    locations.Latitude,
	locations.Longitude
FROM organizations
INNER JOIN locations ON organizations.LocId = locations.Id;