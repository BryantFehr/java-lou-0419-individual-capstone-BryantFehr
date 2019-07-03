/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.Location;
import com.tsg.superherosighting.dto.Organization;
import com.tsg.superherosighting.dto.Sighting;
import com.tsg.superherosighting.dto.SuperPower;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Bryant
 */
@Repository
public class SuperDaoDBJdbcImpl implements SuperDao {

    @Autowired
    @Getter
    JdbcTemplate heySql;

    /*
 .----------------.  .----------------.  .----------------.  .----------------. 
| .--------------. || .--------------. || .--------------. || .--------------. |
| |  ____  ____  | || |  _________   | || |  _______     | || |     ____     | |
| | |_   ||   _| | || | |_   ___  |  | || | |_   __ \    | || |   .'    `.   | |
| |   | |__| |   | || |   | |_  \_|  | || |   | |__) |   | || |  /  .--.  \  | |
| |   |  __  |   | || |   |  _|  _   | || |   |  __ /    | || |  | |    | |  | |
| |  _| |  | |_  | || |  _| |___/ |  | || |  _| |  \ \_  | || |  \  `--'  /  | |
| | |____||____| | || | |_________|  | || | |____| |___| | || |   `.____.'   | |
| |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------' 
     */
    @Override
    @Transactional
    public Hero addHero(Hero hero) {
        final String INSERT_HERO_INTO_HEROES = "INSERT INTO heroes (HeroName, IsHero, Description) VALUES (?,?,?)";
        heySql.update(INSERT_HERO_INTO_HEROES, hero.getName(), hero.getIsHero(), hero.getDescription());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        hero.setId(newId);
        return hero;
    }

    @Override
    public Hero getAHero(int id) {
        final String GET_A_SINGLE_HERO = "SELECT * FROM heroes WHERE id = ?";
        Hero aHero = heySql.queryForObject(GET_A_SINGLE_HERO, new HeroMapper(), id);
        List<SuperPower> powersForHero = this.getAllPowersForHero(aHero.getId());
        aHero.setHeroPowers(powersForHero);
        return aHero;
    }

    @Override
    public List<Hero> getAllHeroes() {
        final String GET_ALL_HEROES = "SELECT * FROM heroes";
        List<Hero> heroes = heySql.query(GET_ALL_HEROES, new HeroMapper());
        for (Hero aHero : heroes) {
            List<SuperPower> powersForHero = this.getAllPowersForHero(aHero.getId());
            aHero.setHeroPowers(powersForHero);
        }

        return heroes;
    }

    @Override
    public void editHero(Hero hero) {
        final String SQL_UPDATE_A_HERO = "UPDATE heroes "
                + "SET HeroName = ?, IsHero = ?, Description = ? "
                + "WHERE Id = ?";

        heySql.update(SQL_UPDATE_A_HERO,
                hero.getName(), hero.getIsHero(), hero.getDescription(),
                hero.getId());

        this.removeAllPowersFromHero(hero.getId());
        this.addPowersToHero(hero.getHeroPowers(), hero.getId());
    }

    @Override
    @Transactional
    public void removeHero(int id) {
        this.removeAllPowersFromHero(id);
        this.removeAllOrgsFromHero(id);
        this.removeAllSightingsFromHero(id);
        this.removeSightingsWithNoHeroes();
        final String SQL_DELETE_HERO = "DELETE FROM heroes WHERE id = ?";
        heySql.update(SQL_DELETE_HERO, id);
    }

    public class HeroMapper implements RowMapper<Hero> {

        @Override
        public Hero mapRow(ResultSet rs, int index) throws SQLException {
            Hero hero = new Hero();
            hero.setId(rs.getInt("Id"));
            hero.setName(rs.getString("HeroName"));
            hero.setIsHero(rs.getBoolean("IsHero"));
            hero.setDescription(rs.getString("Description"));

            return hero;
        }
    }

    /*
 .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .-----------------.
| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
| |   _____      | || |     ____     | || |     ______   | || |      __      | || |  _________   | || |     _____    | || |     ____     | || | ____  _____  | |
| |  |_   _|     | || |   .'    `.   | || |   .' ___  |  | || |     /  \     | || | |  _   _  |  | || |    |_   _|   | || |   .'    `.   | || ||_   \|_   _| | |
| |    | |       | || |  /  .--.  \  | || |  / .'   \_|  | || |    / /\ \    | || | |_/ | | \_|  | || |      | |     | || |  /  .--.  \  | || |  |   \ | |   | |
| |    | |   _   | || |  | |    | |  | || |  | |         | || |   / ____ \   | || |     | |      | || |      | |     | || |  | |    | |  | || |  | |\ \| |   | |
| |   _| |__/ |  | || |  \  `--'  /  | || |  \ `.___.'\  | || | _/ /    \ \_ | || |    _| |_     | || |     _| |_    | || |  \  `--'  /  | || | _| |_\   |_  | |
| |  |________|  | || |   `.____.'   | || |   `._____.'  | || ||____|  |____|| || |   |_____|    | || |    |_____|   | || |   `.____.'   | || ||_____|\____| | |
| |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' 
     */
    @Override
    @Transactional
    public Location addLocation(Location location) {
        final String INSERT_LOCATION_INTO_LOCATIONS = "INSERT INTO locations (LocName, Description, Address, Latitude, Longitude) VALUES (?, ?, ?, ?, ?)";
        heySql.update(INSERT_LOCATION_INTO_LOCATIONS, location.getName(), location.getDescription(), location.getAddress(), location.getLatitude(), location.getLongitude());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        location.setId(newId);
        return location;
    }

    @Override
    public Location getALocation(int id) {
        final String GET_A_SINGLE_LOCATION = "SELECT * FROM locations WHERE id = ?";
        Location aLoc = heySql.queryForObject(GET_A_SINGLE_LOCATION, new LocMapper(), id);
        return aLoc;
    }

    @Override
    public List<Location> getAllLocations() {
        final String GET_ALL_LOCATIONS = "SELECT * FROM locations";
        List<Location> locations = heySql.query(GET_ALL_LOCATIONS, new LocMapper());
        return locations;
    }

    @Override
    public void editLocation(Location location) {
        // edit sightings and orgs to NA location then edit back?
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void removeLocation(int id) {
        // remove sighting because sighting is useless without a location
        // update orgs to NA location
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class LocMapper implements RowMapper<Location> {

        @Override
        public Location mapRow(ResultSet rs, int index) throws SQLException {
            Location location = new Location();
            location.setId(rs.getInt("Id"));
            location.setName(rs.getString("LocName"));
            location.setDescription(rs.getString("Description"));
            location.setAddress(rs.getString("Address"));
            location.setLatitude(rs.getBigDecimal("Latitude"));
            location.setLongitude(rs.getBigDecimal("Longitude"));

            return location;
        }
    }

    /*
 .----------------.  .----------------.  .----------------.  .----------------.  .-----------------. .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .-----------------.
| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
| |     ____     | || |  _______     | || |    ______    | || |      __      | || | ____  _____  | || |     _____    | || |   ________   | || |      __      | || |  _________   | || |     _____    | || |     ____     | || | ____  _____  | |
| |   .'    `.   | || | |_   __ \    | || |  .' ___  |   | || |     /  \     | || ||_   \|_   _| | || |    |_   _|   | || |  |  __   _|  | || |     /  \     | || | |  _   _  |  | || |    |_   _|   | || |   .'    `.   | || ||_   \|_   _| | |
| |  /  .--.  \  | || |   | |__) |   | || | / .'   \_|   | || |    / /\ \    | || |  |   \ | |   | || |      | |     | || |  |_/  / /    | || |    / /\ \    | || | |_/ | | \_|  | || |      | |     | || |  /  .--.  \  | || |  |   \ | |   | |
| |  | |    | |  | || |   |  __ /    | || | | |    ____  | || |   / ____ \   | || |  | |\ \| |   | || |      | |     | || |     .'.' _   | || |   / ____ \   | || |     | |      | || |      | |     | || |  | |    | |  | || |  | |\ \| |   | |
| |  \  `--'  /  | || |  _| |  \ \_  | || | \ `.___]  _| | || | _/ /    \ \_ | || | _| |_\   |_  | || |     _| |_    | || |   _/ /__/ |  | || | _/ /    \ \_ | || |    _| |_     | || |     _| |_    | || |  \  `--'  /  | || | _| |_\   |_  | |
| |   `.____.'   | || | |____| |___| | || |  `._____.'   | || ||____|  |____|| || ||_____|\____| | || |    |_____|   | || |  |________|  | || ||____|  |____|| || |   |_____|    | || |    |_____|   | || |   `.____.'   | || ||_____|\____| | |
| |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' 
     */
    @Override
    @Transactional
    public Organization addOrganization(Organization organization) {
        final String INSERT_ORG_INTO_ORGANIZATIONS = "INSERT INTO organizations (LocId, OrgName, Description, Contact) VALUES (?, ?, ?, ?)";
        heySql.update(INSERT_ORG_INTO_ORGANIZATIONS, organization.getOrgLoc().getId(), organization.getName(), organization.getDescription(), organization.getContact());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        organization.setId(newId);
        return organization;
    }

    @Override
    public Organization getAnOrganization(int id) {
        final String GET_A_SINGLE_ORG = "SELECT * FROM organizations WHERE id = ?";
        Organization organization = heySql.queryForObject(GET_A_SINGLE_ORG, new OrgMapper(), id);
        List<Hero> HeroesForOrg = this.getAllHeroesForOrg(organization.getId());
        organization.setHeroesInOrg(HeroesForOrg);
        // location should already be inside of
        return organization;
    }

    @Override
    public List<Organization> getAllOrganizations() {
        final String GET_ALL_ORGS = "SELECT * FROM organizations";
        List<Organization> orgs = heySql.query(GET_ALL_ORGS, new OrgMapper());
        for (Organization org : orgs) {
            List<Hero> heroesForOrgs = this.getAllHeroesForOrg(org.getId());
            org.setHeroesInOrg(heroesForOrgs);
        }
        // location should already be inside of
        return orgs;
    }

    @Override
    public void editOrganization(Organization organization) {
        final String SQL_UPDATE_ORG = "UPDATE organizations "
                + "SET OrgName = ?, Description = ?, Contact = ? "
                + "WHERE Id = ?";
        
        heySql.update(SQL_UPDATE_ORG, 
                organization.getName(), organization.getDescription(), organization.getContact(),
                organization.getId());
        editLocation(organization.getOrgLoc()); // may cause issues?!?!?!
        
        this.removeAllHeroesFromOrg(organization.getId());
        this.addHeroesToOrg(organization.getHeroesInOrg(), organization.getId());
        // remove heroes then add heroes
        }

    @Override
    @Transactional
    public void removeOrganization(int id) {
        // remove association with heroes
        // remove location if no sightings have same location
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class OrgMapper implements RowMapper<Organization> {

        @Override
        public Organization mapRow(ResultSet rs, int index) throws SQLException {
            Organization organization = new Organization();
            organization.setId(rs.getInt("Id"));
            int locId = rs.getInt("LocId");
            organization.setOrgLoc(getALocation(locId));
            organization.setName(rs.getString("OrgName"));
            organization.setDescription(rs.getString("Description"));
            organization.setContact(rs.getString("Contact"));

            return organization;
        }
    }

    /*
 .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .-----------------. .----------------. 
| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
| |    _______   | || |     _____    | || |    ______    | || |  ____  ____  | || |  _________   | || |     _____    | || | ____  _____  | || |    ______    | |
| |   /  ___  |  | || |    |_   _|   | || |  .' ___  |   | || | |_   ||   _| | || | |  _   _  |  | || |    |_   _|   | || ||_   \|_   _| | || |  .' ___  |   | |
| |  |  (__ \_|  | || |      | |     | || | / .'   \_|   | || |   | |__| |   | || | |_/ | | \_|  | || |      | |     | || |  |   \ | |   | || | / .'   \_|   | |
| |   '.___`-.   | || |      | |     | || | | |    ____  | || |   |  __  |   | || |     | |      | || |      | |     | || |  | |\ \| |   | || | | |    ____  | |
| |  |`\____) |  | || |     _| |_    | || | \ `.___]  _| | || |  _| |  | |_  | || |    _| |_     | || |     _| |_    | || | _| |_\   |_  | || | \ `.___]  _| | |
| |  |_______.'  | || |    |_____|   | || |  `._____.'   | || | |____||____| | || |   |_____|    | || |    |_____|   | || ||_____|\____| | || |  `._____.'   | |
| |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' 
     */
    @Override
    @Transactional
    public Sighting addSighting(Sighting sighting) {
        final String INSERT_SIGHTING_INTO_SIGHTINGS = "INSERT INTO sightings (LocId, DateTime) VALUES (?, ?)";
        heySql.update(INSERT_SIGHTING_INTO_SIGHTINGS, sighting.getSightLocation().getId(), sighting.getDateTime());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        sighting.setId(newId);
        return sighting;
    }

    @Override
    public Sighting getASighting(int id) {
        final String GET_A_SINGLE_SIGHTING = "SELECT * FROM sightings WHERE id = ?";
        Sighting aSighting = heySql.queryForObject(GET_A_SINGLE_SIGHTING, new SightingMapper(), id);
        List<Hero> heroesAtSighting = this.getAllHeroesForSighting(aSighting.getId());
        aSighting.setHeroesAtSighting(heroesAtSighting);
        // location should already be inside of
        return aSighting;

    }

    @Override
    public List<Sighting> getAllSightings() {
        final String GET_ALL_SIGHTINGS = "SELECT * FROM sightings";
        List<Sighting> sightings = heySql.query(GET_ALL_SIGHTINGS, new SightingMapper());
        for (Sighting aSighting : sightings) {
            List<Hero> heroesAtSighting = this.getAllHeroesForSighting(aSighting.getId());
            aSighting.setHeroesAtSighting(heroesAtSighting);
        }
        // location should already be inside of
        return sightings;
    }

    @Override
    public void editSighting(Sighting sighting) {
        // remove all heroes
        // add all heroes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void removeSighting(int id) {
        this.removeAllHeroesFromSightings(id);
        final String SQL_DELETE_SIGHTING = "DELETE FROM sightings WHERE Id = ?";
        // remove location if no other sightings or orgLoc?
        heySql.update(SQL_DELETE_SIGHTING, id);
    }

    public class SightingMapper implements RowMapper<Sighting> {

        @Override
        public Sighting mapRow(ResultSet rs, int index) throws SQLException {
            Sighting sighting = new Sighting();
            sighting.setId(rs.getInt("Id"));

            Timestamp sightStamp = rs.getTimestamp("DateTime");
            sightStamp.setNanos(0);
            sighting.setDateTime(sightStamp.toLocalDateTime());

            int locId = rs.getInt("LocId");
            sighting.setSightLocation(getALocation(locId));

            return sighting;
        }
    }

    /*
 .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------. 
| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
| |    _______   | || | _____  _____ | || |   ______     | || |  _________   | || |  _______     | || |   ______     | || |     ____     | || | _____  _____ | || |  _________   | || |  _______     | |
| |   /  ___  |  | || ||_   _||_   _|| || |  |_   __ \   | || | |_   ___  |  | || | |_   __ \    | || |  |_   __ \   | || |   .'    `.   | || ||_   _||_   _|| || | |_   ___  |  | || | |_   __ \    | |
| |  |  (__ \_|  | || |  | |    | |  | || |    | |__) |  | || |   | |_  \_|  | || |   | |__) |   | || |    | |__) |  | || |  /  .--.  \  | || |  | | /\ | |  | || |   | |_  \_|  | || |   | |__) |   | |
| |   '.___`-.   | || |  | '    ' |  | || |    |  ___/   | || |   |  _|  _   | || |   |  __ /    | || |    |  ___/   | || |  | |    | |  | || |  | |/  \| |  | || |   |  _|  _   | || |   |  __ /    | |
| |  |`\____) |  | || |   \ `--' /   | || |   _| |_      | || |  _| |___/ |  | || |  _| |  \ \_  | || |   _| |_      | || |  \  `--'  /  | || |  |   /\   |  | || |  _| |___/ |  | || |  _| |  \ \_  | |
| |  |_______.'  | || |    `.__.'    | || |  |_____|     | || | |_________|  | || | |____| |___| | || |  |_____|     | || |   `.____.'   | || |  |__/  \__|  | || | |_________|  | || | |____| |___| | |
| |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' 
     */
    @Override
    @Transactional
    public SuperPower addSuperpower(SuperPower superpower) {
        final String INSERT_POWER_INTO_POWERS = "INSERT INTO superpowers (PowerName) VALUES (?)";
        heySql.update(INSERT_POWER_INTO_POWERS, superpower.getName());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        superpower.setId(newId);
        return superpower;
    }

    @Override
    public SuperPower getASuperpower(int id) {
        final String GET_A_SINGLE_POWER = "SELECT * FROM SuperPowers WHERE id = ?";
        SuperPower aSuperPower = heySql.queryForObject(GET_A_SINGLE_POWER, new PowerMapper(), id);
        return aSuperPower;
    }

    @Override
    public List<SuperPower> getAllSuperpowers() {
        final String GET_ALL_POWERS = "SELECT * FROM SuperPowers";
        List<SuperPower> powers = heySql.query(GET_ALL_POWERS, new PowerMapper());
        return powers;
    }

    @Override
    public void editSuperpower(SuperPower superpower) {
        final String UPDATE_A_POWER = "UPDATE superpowers "
                + "SET PowerName = ? "
                + "WHERE Id = ?";

        heySql.update(UPDATE_A_POWER, superpower.getName(), superpower.getId());
    }

    @Override
    @Transactional
    public void removeSuperpower(int id) {
        this.removeAllHeroesFromPower(id);
        final String DELETE_POWER = "DELETE FROM superpowers WHERE Id = ?";
        heySql.update(DELETE_POWER, id);
    }

    public class PowerMapper implements RowMapper<SuperPower> {

        @Override
        public SuperPower mapRow(ResultSet rs, int index) throws SQLException {
            SuperPower power = new SuperPower();
            power.setId(rs.getInt("Id"));
            power.setName(rs.getString("PowerName"));
            return power;
        }
    }

    /*
 .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------. 
| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
| |  ____  ____  | || |  _________   | || |   _____      | || |   ______     | || |  _________   | || |  _______     | || |    _______   | |
| | |_   ||   _| | || | |_   ___  |  | || |  |_   _|     | || |  |_   __ \   | || | |_   ___  |  | || | |_   __ \    | || |   /  ___  |  | |
| |   | |__| |   | || |   | |_  \_|  | || |    | |       | || |    | |__) |  | || |   | |_  \_|  | || |   | |__) |   | || |  |  (__ \_|  | |
| |   |  __  |   | || |   |  _|  _   | || |    | |   _   | || |    |  ___/   | || |   |  _|  _   | || |   |  __ /    | || |   '.___`-.   | |
| |  _| |  | |_  | || |  _| |___/ |  | || |   _| |__/ |  | || |   _| |_      | || |  _| |___/ |  | || |  _| |  \ \_  | || |  |`\____) |  | |
| | |____||____| | || | |_________|  | || |  |________|  | || |  |_____|     | || | |_________|  | || | |____| |___| | || |  |_______.'  | |
| |              | || |              | || |              | || |              | || |              | || |              | || |              | |
| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
 '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' 
     */
    /////////////////////////// HERO HELPERS ///////////////////////////
    public List<SuperPower> getAllPowersForHero(int heroId) {
        String SQL_POWERS_FOR_HERO = "SELECT * FROM SuperPowers "
                + "JOIN heroesandsuperpowers ON heroesandsuperpowers.SuperPowerId = superpowers.Id "
                + "WHERE heroesandsuperpowers.HeroId = ?";
        return heySql.query(SQL_POWERS_FOR_HERO, new PowerMapper(), heroId);
    }

    public void removeAllPowersFromHero(int heroId) {
        String SQL_REMOVE_POWERS_FROM_HERO = "DELETE FROM heroesandsuperpowers WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_POWERS_FROM_HERO, heroId);
    }

    public void addPowerToHero(int heroId, int superPowerId) {
        String SQL_HERO_POWER = "INSERT INTO heroesandsuperpowers (HeroId, SuperPowerId) VALUES (?, ?)";
        heySql.update(SQL_HERO_POWER, heroId, superPowerId);
    }

    public void addPowersToHero(List<SuperPower> heroPowers, int heroId) {
        for (SuperPower aPower : heroPowers) {
            this.addPowerToHero(heroId, aPower.getId());
        }
    }

    public void removeAllOrgsFromHero(int heroId) {
        String SQL_REMOVE_ORGS_FROM_HERO = "DELETE FROM heroesandorganizations WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_ORGS_FROM_HERO, heroId);
    }

    public void removeAllSightingsFromHero(int heroId) {
        String SQL_REMOVE_SIGHTINGS_FROM_HERO = "DELETE FROM heroesandsightings WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_SIGHTINGS_FROM_HERO, heroId);
    }

    public void removeSightingsWithNoHeroes() {
        List<Sighting> allSightings = getAllSightings();
        for (Sighting aSighting : allSightings) {
            if (aSighting.getHeroesAtSighting().isEmpty()) {
                this.removeSighting(aSighting.getId());
            }
        }
    }

    /////////////////////////// SIGHTINGS HELPERS ///////////////////////////
    public void removeAllHeroesFromSightings(int sightingId) {
        String REMOVE_SIGHTINGS_FROM_HEROES = "DELETE FROM heroesandsightings WHERE SightingId = ?";
        heySql.update(REMOVE_SIGHTINGS_FROM_HEROES, sightingId);
    }

    public List<Hero> getAllHeroesForSighting(int sightId) {
        String SQL_HEROES_FOR_SIGHTING = "SELECT * FROM heroes "
                + "JOIN heroesandsightings ON heroesandsightings.HeroId = heroes.Id "
                + "WHERE heroesandsightings.SightingId = ?";
        return heySql.query(SQL_HEROES_FOR_SIGHTING, new HeroMapper(), sightId);
    }

    /////////////////////////// POWER HELPERS ///////////////////////////
    public void removeAllHeroesFromPower(int powerId) {
        String REMOVE_HEROES_FROM_POWER = "DELETE FROM heroesandsuperpowers WHERE SuperPowerId = ?";
        heySql.update(REMOVE_HEROES_FROM_POWER, powerId);
    }

    /////////////////////////// ORG HELPERS ///////////////////////////
    public List<Hero> getAllHeroesForOrg(int orgId) {
        String SQL_HEROES_FOR_ORG = "SELECT * FROM heroes "
                + "JOIN heroesandorganizations ON heroesandorganizations.HeroId = heroes.Id "
                + "WHERE heroesandorganizations.OrgId = ?";
        // getAllPowersForHero();
        return heySql.query(SQL_HEROES_FOR_ORG, new HeroMapper(), orgId);
    }
    
    public void removeAllHeroesFromOrg(int orgId) {
        String REMOVE_HEROES_FROM_ORG = "DELETE FROM heroesandorganizations WHERE OrgId = ?";
        heySql.update(REMOVE_HEROES_FROM_ORG, orgId);
    }
    
    public void addHeroToOrg(int heroId, int orgId) {
        String SQL_ORG_HERO = "INSERT INTO heroesandorganizations (HeroId, OrgId) VALUES (?, ?)";
        heySql.update(SQL_ORG_HERO, heroId, orgId);
    }

    public void addHeroesToOrg(List<Hero> heroesInOrg, int orgId) {
        for (Hero aHero : heroesInOrg) {
            this.addHeroToOrg(aHero.getId(), orgId);
        }
    }
}
