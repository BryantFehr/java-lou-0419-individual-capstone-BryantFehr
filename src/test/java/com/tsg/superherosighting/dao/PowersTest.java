/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import com.tsg.superherosighting.dto.Hero;
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
public class PowersTest extends TestDBSetUpMethods {

    public PowersTest() {
    }

    @Before
    public void setUp() {
        emptyDB();
        populateDB();
    }

    @Test
    public void testMethodAddPower() {
        SuperPower testPower = new SuperPower(4, "testPower");

        testDao.addSuperpower(testPower);
        List<SuperPower> allPowers = testDao.getAllSuperpowers();

        Assert.assertNotNull("List of powers should never be null", allPowers);
        Assert.assertTrue("List of powers should include testPower", allPowers.contains(testPower));
        Assert.assertEquals("Should have 4 power in list", 4, allPowers.size());

        SuperPower shouldBeTestPower = allPowers.get(3);
        Assert.assertEquals("Should have 4 as Id", testPower.getId(), shouldBeTestPower.getId());
        Assert.assertEquals("Should have testPower as name", testPower.getName(), shouldBeTestPower.getName());
    }

    @Test
    public void testMethodGetAPower() {
        SuperPower testPower = new SuperPower(4, "testPower");
        testDao.addSuperpower(testPower);

        SuperPower retrievedPower = testDao.getASuperpower(testPower.getId());
        Assert.assertEquals("Gotten power should be testPower", testPower, retrievedPower);
    }

    @Test
    public void testMethodGetAllPowers() {
        List<SuperPower> allPowers = testDao.getAllSuperpowers();
        Assert.assertNotNull("List of powers should not be null", allPowers);
        Assert.assertEquals("Should have 3 powers in list", 3, allPowers.size());
    }

    @Test
    public void testMethodEditPower() {
        SuperPower powerToEdit = testDao.getASuperpower(1);
        Assert.assertEquals("powerToEdit should be Rich", "Rich", powerToEdit.getName());

        powerToEdit.setName("testPower");
        testDao.editSuperpower(powerToEdit);
        Assert.assertEquals("powerToEdit should now be testPower", "testPower", powerToEdit.getName());

    }

    @Test
    public void testMethodRemovePower() {
        SuperPower testPower = new SuperPower(4, "testPower");
        testDao.addSuperpower(testPower);
        List<SuperPower> allPowers = testDao.getAllSuperpowers();

        Hero testHero = testDao.getAHero(1);
        List<SuperPower> heroPowers = testHero.getHeroPowers();
        Assert.assertEquals("testHero should have 2 powers", 2, heroPowers.size());

        Assert.assertEquals("Should have 4 powers in list", 4, allPowers.size());
        Assert.assertTrue("List of powers should include testPower", allPowers.contains(testPower));

        testDao.removeSuperpower(testPower.getId());
        allPowers = testDao.getAllSuperpowers();

        Assert.assertFalse("List of powers should NOT include testPower", allPowers.contains(testPower));
        Assert.assertTrue("List of powers should still include rich", allPowers.contains(testDao.getASuperpower(1)));
        Assert.assertTrue("List of powers should still include genius", allPowers.contains(testDao.getASuperpower(2)));
        Assert.assertTrue("List of powers should still include crazy", allPowers.contains(testDao.getASuperpower(3)));

        testDao.removeSuperpower(1);
        testDao.removeSuperpower(2);
        testDao.removeSuperpower(3);
        allPowers = testDao.getAllSuperpowers();

        Assert.assertFalse("List of powers should still NOT include testPower", allPowers.contains(testPower));
        Assert.assertTrue("List of powers should be empty", allPowers.isEmpty());

        testHero = testDao.getAHero(1);
        heroPowers = testHero.getHeroPowers();
        Assert.assertTrue("Hero list of powers should be empty", heroPowers.isEmpty());
    }
}
