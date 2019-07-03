/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.Location;
import com.tsg.superherosighting.dto.Organization;
import java.math.BigDecimal;
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
public class OrgsTest extends TestDBSetUpMethods {

    public OrgsTest() {
    }

    @Before
    public void setUp() {
        emptyDB();
        populateDB();
    }

    @Test
    public void testMethodAddAndGetOrgWithNoHeroes() {
        Organization testOrg = new Organization();
        testOrg.setName("testOrg");
        testOrg.setDescription("testy");
        testOrg.setContact("8675309");
        
        Location testLoc = new Location();
        testLoc.setName("testLoc");
        testLoc.setDescription("testy");
        testLoc.setAddress("Nowhere lane");
        
        BigDecimal latLong = new BigDecimal("10.00000001");
        testLoc.setLatitude(latLong);
        testLoc.setLongitude(latLong);
        testDao.addLocation(testLoc);
        
        testOrg.setOrgLoc(testLoc);
        testDao.addOrganization(testOrg);
        List<Organization> allOrgs = testDao.getAllOrganizations();
        
        
        
        Assert.assertNotNull("List of orgs should never be null", allOrgs);
        Assert.assertTrue("List of orgs should include testOrg", allOrgs.contains(testOrg));
        Assert.assertEquals("Should have 3 orgs in list", 3, allOrgs.size());
        
        Organization shouldBeTestOrg = testDao.getAnOrganization(testOrg.getId());
        Assert.assertEquals("Should have same Id as testOrg", testOrg.getId(), shouldBeTestOrg.getId());
        Assert.assertEquals("Should have same name as testOrg", testOrg.getName(), shouldBeTestOrg.getName());
        Assert.assertEquals("Should have same desc as testOrg", testOrg.getDescription(), shouldBeTestOrg.getDescription());
        Assert.assertEquals("Should have same contact as testOrg", testOrg.getContact(), shouldBeTestOrg.getContact());
        Assert.assertEquals("Should have same loc as testOrg", testOrg.getOrgLoc(), shouldBeTestOrg.getOrgLoc());
        Assert.assertTrue("List of heroes should be empty", shouldBeTestOrg.getHeroesInOrg().isEmpty());
        
    }
    
    @Test
    public void testMethodAddAndGetOrgWithOneHero() {
        Organization testOrg = new Organization();
        testOrg.setName("testOrg");
        testOrg.setDescription("testy");
        testOrg.setContact("8675309");
        
        Location testLoc = new Location();
        testLoc.setName("testLoc");
        testLoc.setDescription("testy");
        testLoc.setAddress("Nowhere lane");
        
        BigDecimal latLong = new BigDecimal("10.00000001");
        testLoc.setLatitude(latLong);
        testLoc.setLongitude(latLong);
        testDao.addLocation(testLoc);
        
        testOrg.setOrgLoc(testLoc);
        
        testDao.addOrganization(testOrg);
        
        Hero aHero = testDao.getAHero(1);
        testOrg.getHeroesInOrg().add(aHero);
        
        testDao.addHeroToOrg(aHero.getId(), testOrg.getId());
        
        List<Hero> testOrgHeroes = testDao.getAllHeroesForOrg(testOrg.getId());
        Assert.assertEquals("List of heroes should have 1 hero", 1, testOrgHeroes.size());
        Assert.assertTrue("List of heroes should containt aHero", testOrgHeroes.contains(aHero));
        
        List<Organization> allOrgs = testDao.getAllOrganizations();
        
        Assert.assertNotNull("List of orgs should never be null", allOrgs);
        Assert.assertTrue("List of orgs should include testOrg", allOrgs.contains(testOrg));
        Assert.assertEquals("Should have 3 orgs in list", 3, allOrgs.size());
        
        Organization shouldBeTestOrg = testDao.getAnOrganization(testOrg.getId());
        Assert.assertEquals("Should have same Id as testOrg", testOrg.getId(), shouldBeTestOrg.getId());
        Assert.assertEquals("Should have same name as testOrg", testOrg.getName(), shouldBeTestOrg.getName());
        Assert.assertEquals("Should have same desc as testOrg", testOrg.getDescription(), shouldBeTestOrg.getDescription());
        Assert.assertEquals("Should have same contact as testOrg", testOrg.getContact(), shouldBeTestOrg.getContact());
        Assert.assertEquals("Should have same loc as testOrg", testOrg.getOrgLoc(), shouldBeTestOrg.getOrgLoc());
        
        
        
    }
    
}


//////////////////// LOC
//    private int id;
//    private String name;
//    private String description;
//    private String contact;
//    private List<Hero> heroesInOrg = new ArrayList();
//    private Location orgLoc;