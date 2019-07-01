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

SELECT * FROM SuperPowers
JOIN heroesandsuperpowers ON heroesandsuperpowers.SuperPowerId = superpowers.Id
WHERE heroesandsuperpowers.HeroId = 1;





-- UPDATES AND DELETES FOR DAO IMPL
UPDATE heroes
SET HeroName = 'Batman', IsHero = '1', Description = 'battty'
WHERE Id = 1;

DELETE FROM heroesandsuperpowers WHERE HeroId = 1;

UPDATE superpowers
SET 

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