USE TestSuperHeroes;

-- LOCATIONS --
INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('1', 'Batcave', 'roomy', 'Manor', '10.123456', '110.123456');
INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('2', 'Asylum', 'tight', 'Jail', '20.123456', '120.123456');
INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('3', 'JusticeHQ', 'tall', 'big', '30.123456', '130.123456');
INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('4', 'Sewer', 'narrow', 'sewage', '40.123456', '140.123456');

-- HEROES --
INSERT INTO `heroes` (`Id`, `HeroName`, `IsHero`, `Description`) VALUES ('1', 'Batman', '1', 'batty');
INSERT INTO `heroes` (`Id`, `HeroName`, `IsHero`, `Description`) VALUES ('2', 'Joker', '0', 'jokey');

-- ORGANIZATIONS --
INSERT INTO `organizations` (`Id`, `LocId`, `OrgName`, `Description`, `Contact`) VALUES ('1', '3', 'Justice League', 'justicey', '8675309');
INSERT INTO `organizations` (`Id`, `LocId`, `OrgName`, `Description`, `Contact`) VALUES ('2', '2', 'Baddy Inc', 'baddies', '8675300');

-- HEROES & ORGS --
INSERT INTO `heroesandorganizations` (`Id`, `HeroId`, `OrgId`) VALUES ('1', '1', '1');
INSERT INTO `heroesandorganizations` (`Id`, `HeroId`, `OrgId`) VALUES ('2', '2', '2');

-- SUPERPOWERS --
INSERT INTO `superpowers` (`Id`, `PowerName`) VALUES ('1', 'Rich');
INSERT INTO `superpowers` (`Id`, `PowerName`) VALUES ('2', 'Genius');
INSERT INTO `superpowers` (`Id`, `PowerName`) VALUES ('3', 'Crazy');

-- HEROES & SUPERPOWERS --
INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('1', '1', '1');
INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('2', '1', '2');
INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('3', '2', '2');
INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('4', '2', '3');

-- SIGHTINGS --
INSERT INTO `sightings` (`Id`, `LocId`, `DateTime`) VALUES ('1', '4', '2019-01-16 22:12');
INSERT INTO `sightings` (`Id`, `LocId`, `DateTime`) VALUES ('2', '2', '2019-01-17 23:12');

-- HEROES & SIGHTINGS --
INSERT INTO `heroesandsightings` (`Id`, `HeroId`, `SightingId`) VALUES ('1', '1', '1');
INSERT INTO `heroesandsightings` (`Id`, `HeroId`, `SightingId`) VALUES ('2', '2', '1');
INSERT INTO `heroesandsightings` (`Id`, `HeroId`, `SightingId`) VALUES ('3', '2', '2');

-- INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('-1', 'NA', 'NA', 'NA', '00.00000000', '00.00000000');