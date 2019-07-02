/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Bryant
 */
public class TestDBSetUpMethods {

    @Autowired
    SuperDaoDBJdbcImpl testDao;

    public void emptyDB() {
        JdbcTemplate heySql = testDao.getHeySql();
        heySql.execute("DELETE FROM HeroesAndSuperPowers WHERE 1=1");
        heySql.execute("DELETE FROM HeroesAndSightings WHERE 1=1");
        heySql.execute("DELETE FROM HeroesAndOrganizations WHERE 1=1");
        heySql.execute("DELETE FROM Organizations WHERE 1=1");
        heySql.execute("DELETE FROM Sightings WHERE 1=1");
        heySql.execute("DELETE FROM Locations WHERE 1=1");
        heySql.execute("DELETE FROM SuperPowers WHERE 1=1");
        heySql.execute("DELETE FROM Heroes WHERE 1=1");

    }

    public void populateDB() {
        JdbcTemplate heySql = testDao.getHeySql();
        // locations
        heySql.execute("INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('1', 'Batcave', 'roomy', 'Manor', '10.00000001', '10.00000001')");
        heySql.execute("INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('2', 'Asylum', 'tight', 'Jail', '20.00000002', '20.00000002')");
        heySql.execute("INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('3', 'JusticeHQ', 'tall', 'big', '30.00000003', '30.00000003')");
        heySql.execute("INSERT INTO `locations` (`Id`, `LocName`, `Description`, `Address`, `Latitude`, `Longitude`) VALUES ('4', 'Sewer', 'narrow', 'sewage', '40.00000004', '40.00000004')");

        // heroes
        heySql.execute("INSERT INTO `heroes` (`Id`, `HeroName`, `IsHero`, `Description`) VALUES ('1', 'Batman', '1', 'batty')");
        heySql.execute("INSERT INTO `heroes` (`Id`, `HeroName`, `IsHero`, `Description`) VALUES ('2', 'Joker', '0', 'jokey')");

        // orgs
        heySql.execute("INSERT INTO `organizations` (`Id`, `LocId`, `OrgName`, `Description`, `Contact`) VALUES ('1', '3', 'Justice League', 'justicey', '8675309')");
        heySql.execute("INSERT INTO `organizations` (`Id`, `LocId`, `OrgName`, `Description`, `Contact`) VALUES ('2', '2', 'Baddy Inc', 'baddies', '8675300')");

        // heroes and orgs
        heySql.execute("INSERT INTO `heroesandorganizations` (`Id`, `HeroId`, `OrgId`) VALUES ('1', '1', '1')");
        heySql.execute("INSERT INTO `heroesandorganizations` (`Id`, `HeroId`, `OrgId`) VALUES ('2', '2', '2')");

        // super powers
        heySql.execute("INSERT INTO `superpowers` (`Id`, `PowerName`) VALUES ('1', 'Rich')");
        heySql.execute("INSERT INTO `superpowers` (`Id`, `PowerName`) VALUES ('2', 'Genius')");
        heySql.execute("INSERT INTO `superpowers` (`Id`, `PowerName`) VALUES ('3', 'Crazy')");

        // heroes and superpowers
        heySql.execute("INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('1', '1', '1')");
        heySql.execute("INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('2', '1', '2')");
        heySql.execute("INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('3', '2', '2')");
        heySql.execute("INSERT INTO `heroesandsuperpowers` (`Id`, `HeroId`, `SuperPowerId`) VALUES ('4', '2', '3')");

        // sightings
        heySql.execute("INSERT INTO `sightings` (`Id`, `LocId`, `DateTime`) VALUES ('1', '4', '2019-01-16 23:12:01')");
        heySql.execute("INSERT INTO `sightings` (`Id`, `LocId`, `DateTime`) VALUES ('2', '2', '2019-01-17 23:12:01')");

        // heroes and sightings
        heySql.execute("INSERT INTO `superheroes`.`heroesandsightings` (`Id`, `HeroId`, `SightingId`) VALUES ('1', '1', '1')");
        heySql.execute("INSERT INTO `superheroes`.`heroesandsightings` (`Id`, `HeroId`, `SightingId`) VALUES ('2', '2', '1')");
        heySql.execute("INSERT INTO `superheroes`.`heroesandsightings` (`Id`, `HeroId`, `SightingId`) VALUES ('3', '2', '2')");
    }

}