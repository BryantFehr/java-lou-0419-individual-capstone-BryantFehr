/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.Location;
import com.tsg.superherosighting.dto.Sighting;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class SightingsTest extends TestDBSetUpMethods {

    public SightingsTest() {
    }

    @Before
    public void setUp() {
        emptyDB();
        populateDB();
    }

    @Test
    public void testMethodAddAndGetSighting() {
        Sighting testSight = new Sighting();
        testSight.setDateTime(LocalDateTime.now().withNano(0));
        
        Location testLoc = new Location();
        testLoc.setName("testLoc");
        testLoc.setDescription("testy");
        testLoc.setAddress("Nowhere lane");
        
        BigDecimal latLong = new BigDecimal("10.00000001");
        testLoc.setLatitude(latLong);
        testLoc.setLongitude(latLong);
        testDao.addLocation(testLoc);
        
        testSight.setSightLocation(testLoc);
        
        testDao.addSighting(testSight);
        
        Hero aHero = testDao.getAHero(1);
        testSight.getHeroesAtSighting().add(aHero);
        
        testDao.addHeroToSighting(aHero.getId(), testSight.getId());
        
        // test hero put in correctly
        List<Hero> testSightHeroes = testDao.getAllHeroesForSighting(testSight.getId());
        Assert.assertEquals("List of heroes should have 1 hero", 1, testSightHeroes.size());
        Assert.assertTrue("List of heroes should contain aHero", testSightHeroes.contains(aHero));
        
        // test list of sightings works correctly
        List<Sighting> allSights = testDao.getAllSightings();
        
        Assert.assertNotNull("List of shights should never be null", allSights);
        Assert.assertTrue("List of sights should include testOrg", allSights.contains(testSight));
        Assert.assertEquals("should have 3 sights in list", 3, allSights.size());
        
        Sighting shouldBeTestSight = testDao.getASighting(testSight.getId());
        Assert.assertEquals("Should have same Id as testSight", shouldBeTestSight.getId(), testSight.getId());
        Assert.assertEquals("Should have same DateTime as testSight", shouldBeTestSight.getDateTime(), testSight.getDateTime());
        Assert.assertEquals("Should have same Location as testSight", shouldBeTestSight.getSightLocation(), testSight.getSightLocation());
        Assert.assertEquals("Should have same Heroes as testSight", shouldBeTestSight.getHeroesAtSighting(), testSight.getHeroesAtSighting());
    }
    
    @Test
    public void testMethodGetAllSightings() {
        List<Sighting> allSights = testDao.getAllSightings();
        Assert.assertNotNull("List of sights should not be null", allSights);
        Assert.assertEquals("Should have 2 sights in list", 2, allSights.size());
    }
    
    @Test
    public void TestMethodEditSighting() {
        Sighting sightToEdit = testDao.getASighting(2);
        
        sightToEdit.setSightLocation(testDao.getALocation(1));
        sightToEdit.setDateTime(LocalDateTime.now().withNano(0));
        
        List<Hero> allHeroes = testDao.getAllHeroes();
        sightToEdit.setHeroesAtSighting(allHeroes);
        testDao.editSighting(sightToEdit);
        
        Sighting shouldBeEdittedSighting = testDao.getASighting(sightToEdit.getId());
        
        Assert.assertEquals("shouldBeEdittedSighting should now have first location (batcave)", sightToEdit.getSightLocation(), shouldBeEdittedSighting.getSightLocation());
        Assert.assertEquals("shouldBeEdittedSighting should now have current time", sightToEdit.getDateTime(), shouldBeEdittedSighting.getDateTime());
        Assert.assertEquals("shouldBeEdittedSighting should now have all heroes", sightToEdit.getHeroesAtSighting(), shouldBeEdittedSighting.getHeroesAtSighting());
        Assert.assertEquals("shouldBeEdittedSighting should be sightToEdit", sightToEdit, shouldBeEdittedSighting);
    }
    
    @Test
    public void testMethodRemoveSighting () {
        Sighting testSight = new Sighting();
        testSight.setDateTime(LocalDateTime.now().withNano(0));
        
        Location testLoc = new Location();
        testLoc.setName("testLoc");
        testLoc.setDescription("testy");
        testLoc.setAddress("Nowhere lane");
        
        BigDecimal latLong = new BigDecimal("10.00000001");
        testLoc.setLatitude(latLong);
        testLoc.setLongitude(latLong);
        testDao.addLocation(testLoc);
        
        testSight.setSightLocation(testLoc);
        
        testDao.addSighting(testSight);
        
        Hero aHero = testDao.getAHero(1);
        testSight.getHeroesAtSighting().add(aHero);
        
        testDao.addHeroToSighting(aHero.getId(), testSight.getId());
        
        List<Sighting> allSights = testDao.getAllSightings();
        Assert.assertEquals("Should have 3 sightings in list", 3, allSights.size());
        Assert.assertTrue("List of sightings should include testSight", allSights.contains(testSight));
        
        testDao.removeSighting(testSight.getId());
        allSights = testDao.getAllSightings();
        
        // test sight was removed correctly
        Assert.assertFalse("List of sights should NOT include testSight", allSights.contains(testSight));
        Assert.assertTrue("List of sights should still include the first sighting", allSights.contains(testDao.getASighting(1)));
        Assert.assertTrue("List of sights should still include the second sighting", allSights.contains(testDao.getASighting(2)));
        
        // remove all existing sights
        testDao.removeSighting(1);
        testDao.removeSighting(2);
        allSights = testDao.getAllSightings();
        
        // test that no more sights exist
        Assert.assertFalse("List of sights should still NOT include testSight", allSights.contains(testSight));
        Assert.assertTrue("List of sights should not be empty", allSights.isEmpty());
    }
    
}
