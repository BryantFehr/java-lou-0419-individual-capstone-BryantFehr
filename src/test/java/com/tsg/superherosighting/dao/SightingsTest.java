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
    public void testMethodAddSighting() {
        Sighting testSight = new Sighting();
        testSight.setDateTime(LocalDateTime.now());
        
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
        
        
        List<Hero> testSightHeroes = testDao.getAllHeroesForSighting(testSight.getId());
        Assert.assertEquals("List of heroes should have 1 hero", 1, testSightHeroes.size());
        Assert.assertTrue("List of heroes should contain aHero", testSightHeroes.contains(aHero));
        
        
        
        
        
        
        
        
        
    }
    
}
