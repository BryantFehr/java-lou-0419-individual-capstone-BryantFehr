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

    private static final class HeroMapper implements RowMapper<Hero> {

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Hero getALocation(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Location> getAllLocations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void editLocation(Location location) {
        // remove association with sightings/orgs then put back?
        // HOPEFULLY NOT!
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void removeLocation(int id) {
        // remove sighting because sighting is useless without a location
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        // add in list of heroes as well?
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Organization getAnOrganization(int id) {
        // and get a list of heroes in org
        // get a location?
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Organization> getAllOrganizations() {
        // and get a list of heroes in all orgs
        // get locations?
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void editOrganization(Organization organization) {
        // remove heroes then add heroes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void removeOrganization(int id) {
        // remove association with heroes
        // remove location if no sightingsn have same location
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        // list of heroes

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Sighting getASighting(int id) {
        // has list of heroes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Sighting> getAllSightings() {
        // has list of heroes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    private static final class PowerMapper implements RowMapper<SuperPower> {

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
    private List<SuperPower> getAllPowersForHero(int heroId) {
        String SQL_POWERS_FOR_HERO = "SELECT * FROM SuperPowers "
                + "JOIN heroesandsuperpowers ON heroesandsuperpowers.SuperPowerId = superpowers.Id "
                + "WHERE heroesandsuperpowers.HeroId = ?";
        return heySql.query(SQL_POWERS_FOR_HERO, new PowerMapper(), heroId);
    }

    private void removeAllPowersFromHero(int heroId) {
        String SQL_REMOVE_POWERS_FROM_HERO = "DELETE FROM heroesandsuperpowers WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_POWERS_FROM_HERO, heroId);
    }

    private void addPowerToHero(int superPowerId, int heroId) {
        String SQL_HERO_POWER = "INSERT INTO `heroesandsuperpowers` (`HeroId`, `SuperPowerId`) VALUES (?, ?)";
        heySql.update(SQL_HERO_POWER, superPowerId, heroId);
    }

    private void addPowersToHero(List<SuperPower> heroPowers, int heroId) {
        for (SuperPower aPower : heroPowers) {
            this.addPowerToHero(aPower.getId(), heroId);
        }
    }

    private void removeAllOrgsFromHero(int heroId) {
        String SQL_REMOVE_ORGS_FROM_HERO = "DELETE FROM heroesandorganizations WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_ORGS_FROM_HERO, heroId);
    }

    private void removeAllSightingsFromHero(int heroId) {
        String SQL_REMOVE_SIGHTINGS_FROM_HERO = "DELETE FROM heroesandsightings WHERE HeroId = ?";
        heySql.update(SQL_REMOVE_SIGHTINGS_FROM_HERO, heroId);
    }

    private void removeSightingsWithNoHeroes() {
        List<Sighting> allSightings = getAllSightings();
        for (Sighting aSighting : allSightings) {
            if (aSighting.getHeroesAtSighting().isEmpty()) {
                this.removeSighting(aSighting.getId());
            }
        }
    }

    /////////////////////////// SIGHTINGS HELPERS ///////////////////////////
    private void removeAllHeroesFromSightings(int sightingId) {
        String REMOVE_SIGHTINGS_FROM_HEROES = "DELETE FROM heroesandsightings WHERE SightingId = ?";
        heySql.update(REMOVE_SIGHTINGS_FROM_HEROES, sightingId);
    }

    /////////////////////////// POWER HELPERS ///////////////////////////
    private void removeAllHeroesFromPower(int powerId) {
        String REMOVE_HEROES_FROM_POWER = "DELETE FROM heroesandsuperpowers WHERE SuperPowerId = ?";
        heySql.update(REMOVE_HEROES_FROM_POWER, powerId);
    }
}
