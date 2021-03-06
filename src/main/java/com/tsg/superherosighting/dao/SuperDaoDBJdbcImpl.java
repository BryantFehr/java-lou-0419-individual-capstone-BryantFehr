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
import java.util.stream.Collectors;
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
        final String INSERT_HERO_INTO_HEROES = "INSERT INTO Heroes (HeroName, IsHero, Description) VALUES (?,?,?)";
        heySql.update(INSERT_HERO_INTO_HEROES, hero.getName(), hero.getIsHero(), hero.getDescription());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        hero.setId(newId);
        return hero;
    }

    @Override
    public Hero getAHero(int id) {
        final String GET_A_SINGLE_HERO = "SELECT * FROM Heroes WHERE id = ?";
        Hero aHero = heySql.queryForObject(GET_A_SINGLE_HERO, new HeroMapper(), id);
        List<SuperPower> powersForHero = this.getAllPowersForHero(aHero.getId());
        aHero.setHeroPowers(powersForHero);
        return aHero;
    }

    @Override
    public List<Hero> getAllHeroes() {
        final String GET_ALL_HEROES = "SELECT * FROM Heroes";
        List<Hero> heroes = heySql.query(GET_ALL_HEROES, new HeroMapper());
        for (Hero aHero : heroes) {
            List<SuperPower> powersForHero = this.getAllPowersForHero(aHero.getId());
            aHero.setHeroPowers(powersForHero);
        }

        return heroes;
    }

    @Override
    public void editHero(Hero hero) {
        final String SQL_UPDATE_A_HERO = "UPDATE Heroes "
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
        final String SQL_DELETE_HERO = "DELETE FROM Heroes WHERE id = ?";
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
        final String INSERT_LOCATION_INTO_LOCATIONS = "INSERT INTO Locations (LocName, Description, Address, Latitude, Longitude) VALUES (?, ?, ?, ?, ?)";
        heySql.update(INSERT_LOCATION_INTO_LOCATIONS, location.getName(), location.getDescription(), location.getAddress(), location.getLatitude(), location.getLongitude());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        location.setId(newId);
        return location;
    }

    @Override
    public Location getALocation(int id) {
        final String GET_A_SINGLE_LOCATION = "SELECT * FROM Locations WHERE id = ?";
        Location aLoc = heySql.queryForObject(GET_A_SINGLE_LOCATION, new LocMapper(), id);
        return aLoc;
    }

    @Override
    public List<Location> getAllLocations() {
        final String GET_ALL_LOCATIONS = "SELECT * FROM Locations";
        List<Location> locations = heySql.query(GET_ALL_LOCATIONS, new LocMapper());
        return locations;
    }

    @Override
    public void editLocation(Location location) {
        String EDIT_A_LOC = "UPDATE Locations "
                + "SET LocName = ?, Description = ?, Address = ?, Latitude = ?, Longitude = ? "
                + "WHERE Id = ?";
        heySql.update(EDIT_A_LOC, location.getName(), location.getDescription(), location.getAddress(),
                location.getLatitude(), location.getLongitude(),
                location.getId());
    }

    @Override
    @Transactional
    public void removeLocation(int id) {
        // remove sighting because sighting is useless without a location
        // update orgs to NA location
        this.updateOrgLocationToNA(id);
        List<Sighting> sightsToRemove = this.getAllSightings()
                .stream()
                .filter(aSighting -> aSighting.getSightLocation().getId() == id)
                .collect(Collectors.toList());
        this.removeSightingsFromBridge(sightsToRemove);
        this.deleteSightings(sightsToRemove);

        String DELETE_LOC = "DELETE FROM Locations WHERE Id = ?";
        heySql.update(DELETE_LOC, id);

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
        final String INSERT_ORG_INTO_ORGANIZATIONS = "INSERT INTO Organizations (LocId, OrgName, Description, Contact) VALUES (?, ?, ?, ?)";
        heySql.update(INSERT_ORG_INTO_ORGANIZATIONS, organization.getOrgLoc().getId(), organization.getName(), organization.getDescription(), organization.getContact());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        organization.setId(newId);
        return organization;
    }

    @Override
    public Organization getAnOrganization(int id) {
        final String GET_A_SINGLE_ORG = "SELECT * FROM Organizations WHERE id = ?";
        Organization organization = heySql.queryForObject(GET_A_SINGLE_ORG, new OrgMapper(), id);
        List<Hero> HeroesForOrg = this.getAllHeroesForOrg(organization.getId());
        organization.setHeroesInOrg(HeroesForOrg);
        // location should already be inside of
        return organization;
    }

    @Override
    public List<Organization> getAllOrganizations() {
        final String GET_ALL_ORGS = "SELECT * FROM Organizations";
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
        final String SQL_UPDATE_ORG = "UPDATE Organizations "
                + "SET LocId = ?, OrgName = ?, Description = ?, Contact = ? "
                + "WHERE Id = ?";

        heySql.update(SQL_UPDATE_ORG,
                organization.getOrgLoc().getId(), organization.getName(), organization.getDescription(), organization.getContact(),
                organization.getId());

        this.removeAllHeroesFromOrg(organization.getId());
        this.addHeroesToOrg(organization.getHeroesInOrg(), organization.getId());
        // remove Heroes then add heroes
    }

    @Override
    @Transactional
    public void removeOrganization(int id) {
        // remove association with heroes
        this.removeAllHeroesFromOrg(id);
        final String SQL_DELETE_ORG = "DELETE FROM Organizations WHERE id = ?";
        heySql.update(SQL_DELETE_ORG, id);
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
        final String INSERT_SIGHTING_INTO_SIGHTINGS = "INSERT INTO Sightings (LocId, DateTime) VALUES (?, ?)";
        heySql.update(INSERT_SIGHTING_INTO_SIGHTINGS, sighting.getSightLocation().getId(), sighting.getDateTime().withNano(0));
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        sighting.setId(newId);
        return sighting;
    }

    @Override
    public Sighting getASighting(int id) {
        final String GET_A_SINGLE_SIGHTING = "SELECT * FROM Sightings WHERE id = ?";
        Sighting aSighting = heySql.queryForObject(GET_A_SINGLE_SIGHTING, new SightingMapper(), id);
        List<Hero> heroesAtSighting = this.getAllHeroesForSighting(aSighting.getId());
        aSighting.setHeroesAtSighting(heroesAtSighting);
        // location should already be inside of
        return aSighting;

    }

    @Override
    public List<Sighting> getAllSightings() {
        final String GET_ALL_SIGHTINGS = "SELECT * FROM Sightings";
        List<Sighting> sightings = heySql.query(GET_ALL_SIGHTINGS, new SightingMapper());
        for (Sighting aSighting : sightings) {
            List<Hero> heroesAtSighting = this.getAllHeroesForSighting(aSighting.getId());
            aSighting.setHeroesAtSighting(heroesAtSighting);
        }
        // location should already be inside of
        return sightings;
    }

    @Override
    public List<Sighting> get10RecentSightings() {
        final String GET_10_RECENT_SIGHTINGS = "SELECT * FROM Sightings ORDER BY DateTime DESC LIMIT 10";
        List<Sighting> recentSightings = heySql.query(GET_10_RECENT_SIGHTINGS, new SightingMapper());
        for (Sighting aSighting : recentSightings) {
            List<Hero> heroesAtSighting = this.getAllHeroesForSighting(aSighting.getId());
            aSighting.setHeroesAtSighting(heroesAtSighting);
        }
        // location should already be inside of
        return recentSightings;
    }

    @Override
    public void editSighting(Sighting sighting) {
        final String SQL_UPDATE_SIGHTING = "UPDATE Sightings "
                + "SET LocId = ?, DateTime = ? "
                + "WHERE Id = ?";

        heySql.update(SQL_UPDATE_SIGHTING,
                sighting.getSightLocation().getId(), sighting.getDateTime(), sighting.getId());

        this.removeAllHeroesFromSightings(sighting.getId());
        this.addHeroesToSighting(sighting.getHeroesAtSighting(), sighting.getId());
    }

    @Override
    @Transactional
    public void removeSighting(int id) {
        this.removeAllHeroesFromSightings(id);
        final String SQL_DELETE_SIGHTING = "DELETE FROM Sightings WHERE Id = ?";
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
        final String INSERT_POWER_INTO_POWERS = "INSERT INTO Superpowers (PowerName) VALUES (?)";
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
        final String UPDATE_A_POWER = "UPDATE Superpowers "
                + "SET PowerName = ? "
                + "WHERE Id = ?";

        heySql.update(UPDATE_A_POWER, superpower.getName(), superpower.getId());
    }

    @Override
    @Transactional
    public void removeSuperpower(int id) {
        this.removeAllHeroesFromPower(id);
        final String DELETE_POWER = "DELETE FROM Superpowers WHERE Id = ?";
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
                + "JOIN HeroesAndSuperPowers ON HeroesAndSuperPowers.SuperPowerId = SuperPowers.Id "
                + "WHERE HeroesAndSuperPowers.HeroId = ?";
        return heySql.query(SQL_POWERS_FOR_HERO, new PowerMapper(), heroId);
    }

    public void removeAllPowersFromHero(int heroId) {
        String SQL_REMOVE_POWERS_FROM_HERO = "DELETE FROM HeroesAndSuperPowers WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_POWERS_FROM_HERO, heroId);
    }

    public void addPowerToHero(int heroId, int superPowerId) {
        String SQL_HERO_POWER = "INSERT INTO HeroesAndSuperPowers (HeroId, SuperPowerId) VALUES (?, ?)";
        heySql.update(SQL_HERO_POWER, heroId, superPowerId);
    }

    public void addPowersToHero(List<SuperPower> heroPowers, int heroId) {
        for (SuperPower aPower : heroPowers) {
            this.addPowerToHero(heroId, aPower.getId());
        }
    }

    public void removeAllOrgsFromHero(int heroId) {
        String SQL_REMOVE_ORGS_FROM_HERO = "DELETE FROM HeroesAndOrganizations WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_ORGS_FROM_HERO, heroId);
    }

    public void removeAllSightingsFromHero(int heroId) {
        String SQL_REMOVE_SIGHTINGS_FROM_HERO = "DELETE FROM HeroesAndSightings WHERE HeroId = ?";
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

    /////////////////////////// LOCATION HELPERS ///////////////////////////
    private void updateOrgLocationToNA(int locId) {
        String SQL_UPDATE_ORG_BY_LOCID = "UPDATE Organizations SET LocId = '-1' WHERE LocId = ?";
        heySql.update(SQL_UPDATE_ORG_BY_LOCID, locId);
    }

    private void deleteSightings(List<Sighting> sightsToRemove) {
        String DELETE_SIGHTING_WITH_LOC = "DELETE FROM Sightings WHERE Id = ?";
        for (Sighting aSighting : sightsToRemove) {
            heySql.update(DELETE_SIGHTING_WITH_LOC, aSighting.getId());
        }
    }

    /////////////////////////// ORG HELPERS ///////////////////////////
    public List<Hero> getAllHeroesForOrg(int orgId) {
        String SQL_HEROES_FOR_ORG = "SELECT * FROM Heroes "
                + "JOIN HeroesAndOrganizations ON HeroesAndOrganizations.HeroId = Heroes.Id "
                + "WHERE HeroesAndOrganizations.OrgId = ?";
        List<Hero> orgHeroes = heySql.query(SQL_HEROES_FOR_ORG, new HeroMapper(), orgId);

        for (Hero aHero : orgHeroes) {
            List<SuperPower> heroesPowers = this.getAllPowersForHero(aHero.getId());
            aHero.setHeroPowers(heroesPowers);
        }
        return orgHeroes;
    }

    public void removeAllHeroesFromOrg(int orgId) {
        String REMOVE_HEROES_FROM_ORG = "DELETE FROM HeroesAndOrganizations WHERE OrgId = ?";
        heySql.update(REMOVE_HEROES_FROM_ORG, orgId);
    }

    public void addHeroToOrg(int heroId, int orgId) {
        String SQL_ORG_HERO = "INSERT INTO HeroesAndOrganizations (HeroId, OrgId) VALUES (?, ?)";
        heySql.update(SQL_ORG_HERO, heroId, orgId);
    }

    public void addHeroesToOrg(List<Hero> heroesInOrg, int orgId) {
        for (Hero aHero : heroesInOrg) {
            this.addHeroToOrg(aHero.getId(), orgId);
        }
    }

    /////////////////////////// SIGHTINGS HELPERS ///////////////////////////
    public void removeAllHeroesFromSightings(int sightingId) {
        String REMOVE_SIGHTINGS_FROM_HEROES = "DELETE FROM HeroesAndSightings WHERE SightingId = ?";
        heySql.update(REMOVE_SIGHTINGS_FROM_HEROES, sightingId);
    }

    public List<Hero> getAllHeroesForSighting(int sightId) {
        String SQL_HEROES_FOR_SIGHTING = "SELECT * FROM Heroes "
                + "JOIN HeroesAndSightings ON HeroesAndSightings.HeroId = Heroes.Id "
                + "WHERE HeroesAndSightings.SightingId = ?";
        List<Hero> sightHeroes = heySql.query(SQL_HEROES_FOR_SIGHTING, new HeroMapper(), sightId);
        for (Hero aHero : sightHeroes) {
            List<SuperPower> heroesPowers = this.getAllPowersForHero(aHero.getId());
            aHero.setHeroPowers(heroesPowers);
        }

        return sightHeroes;
    }

    public void addHeroToSighting(int heroId, int sightingId) {
        String SQL_SIGHTING_HERO = "INSERT INTO HeroesAndSightings (HeroId, SightingId) VALUES (?, ?)";
        heySql.update(SQL_SIGHTING_HERO, heroId, sightingId);
    }

    public void addHeroesToSighting(List<Hero> heroesInSighting, int sightingId) {
        for (Hero aHero : heroesInSighting) {
            this.addHeroToSighting(aHero.getId(), sightingId);
        }
    }

    /////////////////////////// POWER HELPERS ///////////////////////////
    public void removeAllHeroesFromPower(int powerId) {
        String REMOVE_HEROES_FROM_POWER = "DELETE FROM HeroesAndSuperPowers WHERE SuperPowerId = ?";
        heySql.update(REMOVE_HEROES_FROM_POWER, powerId);
    }

    private void removeSightingsFromBridge(List<Sighting> sightsToRemove) {
        String DELETE_SIGHTING_FROM_BRIDGE = "DELETE FROM HeroesAndSightings WHERE SightingId = ?";
        for (Sighting aSighting : sightsToRemove) {
            heySql.update(DELETE_SIGHTING_FROM_BRIDGE, aSighting.getId());
        }
    }

}
