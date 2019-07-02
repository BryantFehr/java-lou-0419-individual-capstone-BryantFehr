/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.Sighting;
import com.tsg.superherosighting.dto.SuperPower;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Bryant
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HeroesTest extends TestDBSetUpMethods {

    public HeroesTest() {
    }

    @Before
    public void setUp() {
        emptyDB();
        populateDB();
    }

    @Test
    public void testMethodAddAndGetHeroWithNoPowers() {

        Hero testHero = new Hero();
        // testHero.setId(3);
        testHero.setName("Test Man");
        testHero.setIsHero(true);
        testHero.setDescription("He is testy!");

        testDao.addHero(testHero);
        List<Hero> allHeroes = testDao.getAllHeroes();

        Assert.assertNotNull("List of heroes should never be null", allHeroes);
        Assert.assertTrue("List of heroes should include Test Man", allHeroes.contains(testHero));
        Assert.assertEquals("Should have 3 heroes in list", 3, allHeroes.size());

        Hero shouldBeTestHero = testDao.getAHero(testHero.getId());
        Assert.assertEquals("Should have 3 as Id", testHero.getId(), shouldBeTestHero.getId());
        Assert.assertEquals("Should have Test Man as name", testHero.getName(), shouldBeTestHero.getName());
        Assert.assertEquals("Should have true as IsHero", testHero.getIsHero(), shouldBeTestHero.getIsHero());
        Assert.assertEquals("Should have he is testy! as desc", testHero.getDescription(), shouldBeTestHero.getDescription());
        Assert.assertTrue("List of powers should be empty", shouldBeTestHero.getHeroPowers().isEmpty());
    }

    @Test
    public void testMethodAddAndGetHeroWithOnePower() {

        Hero testHero = new Hero();
        // testHero.setId(3);
        testHero.setName("Test Man");
        testHero.setIsHero(true);
        testHero.setDescription("He is testy!");
        testDao.addHero(testHero);
        
        SuperPower superPower = testDao.getASuperpower(1);
        testHero.getHeroPowers().add(superPower);
        
        testDao.addPowerToHero(testHero.getId(), 1);
        
        List <SuperPower> testHeroPowers = testDao.getAllPowersForHero(testHero.getId());
        
        List<Hero> allHeroes = testDao.getAllHeroes();

        Assert.assertNotNull("List of heroes should never be null", allHeroes);
        Assert.assertTrue("List of heroes should include Test Man", allHeroes.contains(testHero));
        Assert.assertEquals("Should have 3 heroes in list", 3, allHeroes.size());

        Hero shouldBeTestHero = testDao.getAHero(testHero.getId());
        Assert.assertEquals("Should have 3 as Id", testHero.getId(), shouldBeTestHero.getId());
        Assert.assertEquals("Should have Test Man as name", testHero.getName(), shouldBeTestHero.getName());
        Assert.assertEquals("Should have true as IsHero", testHero.getIsHero(), shouldBeTestHero.getIsHero());
        Assert.assertEquals("Should have he is testy! as desc", testHero.getDescription(), shouldBeTestHero.getDescription());
        
        Assert.assertEquals("List of powers should have 1 power", 1, testHeroPowers.size());
        
    }

    @Test
    public void testMethodGetAHero() {
        Hero testHero = new Hero();
        // testHero.setId(3);
        testHero.setName("Test Man");
        testHero.setIsHero(true);
        testHero.setDescription("He is testy!");
        testDao.addHero(testHero);
        
        Hero retrievedHero = testDao.getAHero(testHero.getId());
        Assert.assertEquals("Gotten hero should be Test Man", testHero, retrievedHero);
    }

    @Test
    public void testMethodGetAllHeroes() {
        List<Hero> allHeroes = testDao.getAllHeroes();
        Assert.assertNotNull("List of heroes should not be null", allHeroes);
        Assert.assertEquals("Should have 2 heroes in list", 2, allHeroes.size());
    }

    @Test
    public void testMethodEditHero() {
        Hero heroToEdit = testDao.getAHero(1);
        Assert.assertEquals("heroToEdit should be Batman", "Batman", heroToEdit.getName());
        
        heroToEdit.setName("Test Man");
        heroToEdit.setIsHero(false);
        heroToEdit.setDescription("testy");
        List <SuperPower> allPowers = testDao.getAllSuperpowers();
        heroToEdit.setHeroPowers(allPowers);
        testDao.editHero(heroToEdit);
        
        Assert.assertEquals("heroToEdit should now be Test Man", "Test Man", heroToEdit.getName());
        Assert.assertFalse("heroToEdit is now evil", heroToEdit.getIsHero());
        Assert.assertEquals("heroToEdit desc is not testy", "testy", heroToEdit.getDescription());
        Assert.assertEquals("heroToEdit's now has all 3 powers", 3, heroToEdit.getHeroPowers().size());
    }

    @Test
    public void testMethodRemoveHero() {
        Hero testHero = new Hero();
        testHero.setName("Test Man");
        testHero.setIsHero(true);
        testHero.setDescription("He is testy!");
        testDao.addHero(testHero);
        
        List<Hero> allHeroes = testDao.getAllHeroes();
        Assert.assertEquals("Should have 3 heroes in list", 3, allHeroes.size());
        Assert.assertTrue("List of powers should include Test Man", allHeroes.contains(testHero));
        
        testDao.removeHero(testHero.getId());
        allHeroes = testDao.getAllHeroes();
        
        // check hero was removed correctly
        Assert.assertFalse("List of heroes should NOT include Test Man", allHeroes.contains(testHero));
        Assert.assertTrue("List of heroes should still include batman", allHeroes.contains(testDao.getAHero(1)));
        Assert.assertTrue("List of heroes should still include joker", allHeroes.contains(testDao.getAHero(2)));
        
        // test that sightings still exist
        List<Sighting> allSightings = testDao.getAllSightings();
        Assert.assertFalse("List of sightings should NOT be empty yet", allSightings.isEmpty());
        
        // remove all existing heroes
        testDao.removeHero(1);
        testDao.removeHero(2);
        allHeroes = testDao.getAllHeroes();

        // test that no more heroes exist
        Assert.assertFalse("List of heroes should still NOT include Test Man", allHeroes.contains(testHero));
        Assert.assertTrue("List of heroes should be empty", allHeroes.isEmpty());
        
        // test that sightings are now empty because no heroes are associated with them
        allSightings = testDao.getAllSightings();
        Assert.assertTrue("List of sightings should be empty", allSightings.isEmpty());
        
    }
}
