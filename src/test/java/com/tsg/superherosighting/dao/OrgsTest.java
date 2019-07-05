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
        Assert.assertTrue("List of heroes should contain aHero", testOrgHeroes.contains(aHero));
        
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
    
    @Test
    public void testMethodGetAllOrgs() {
        List<Organization> allOrgs = testDao.getAllOrganizations();
        Assert.assertNotNull("List of orgs should not be null", allOrgs);
        Assert.assertEquals("Should have 2 orgs in list", 2, allOrgs.size());
    }
    
    @Test
    public void testMethodEditOrg() {
        Organization orgToEdit = testDao.getAnOrganization(1);
        
        orgToEdit.setName("Test Org");
        orgToEdit.setDescription("Testy");
        orgToEdit.setContact("0000000");
        orgToEdit.setOrgLoc(testDao.getALocation(2));
        
        List <Hero> allHeroes = testDao.getAllHeroes();
        orgToEdit.setHeroesInOrg(allHeroes);
        testDao.editOrganization(orgToEdit);
        
        Organization shouldBeTestOrg = testDao.getAnOrganization(orgToEdit.getId());
        
        Assert.assertEquals("shouldBeTestOrg should now be Test Org as name", orgToEdit.getName(), shouldBeTestOrg.getName());
        Assert.assertEquals("shouldBeTestOrg should now have desc of Testy", orgToEdit.getDescription(), shouldBeTestOrg.getDescription());
        Assert.assertEquals("shouldBeTestOrg should now have contact of 0000000", orgToEdit.getContact(), shouldBeTestOrg.getContact());
        Assert.assertEquals("shouldBeTestOrg should have all heroes", orgToEdit.getHeroesInOrg(), shouldBeTestOrg.getHeroesInOrg());
        Assert.assertEquals("shouldBeTestOrg should now be at Asylum", orgToEdit.getOrgLoc(), shouldBeTestOrg.getOrgLoc());
        Assert.assertEquals("shouldBeTestOrg should equal orgToEdit", orgToEdit, shouldBeTestOrg);
    }
    
    @Test
    public void testMethodRemoveHero() {
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
        Assert.assertEquals("Should have 3 orgs in list", 3, allOrgs.size());
        Assert.assertTrue("List of orgs should include testOrg", allOrgs.contains(testOrg));
        
        
        testDao.removeOrganization(testOrg.getId());
        allOrgs = testDao.getAllOrganizations();
        
        // test that org was removed correclty
        Assert.assertFalse("List of orgs should NOT include testOrg", allOrgs.contains(testOrg));
        Assert.assertTrue("List of orgs should still include Justice League", allOrgs.contains(testDao.getAnOrganization(1)));
        Assert.assertTrue("List of orgs should still include  Baddy Inc", allOrgs.contains(testDao.getAnOrganization(2)));
        
        // remove all existing orgs
        testDao.removeOrganization(1);
        testDao.removeOrganization(2);
        allOrgs = testDao.getAllOrganizations();
        
        // test that no more orgs exist
        Assert.assertFalse("List of orgs should still NOT include testOrg", allOrgs.contains(testOrg));
        Assert.assertTrue("List of orgs should now be empty", allOrgs.isEmpty());
    }
}