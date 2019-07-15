/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dao;

import com.tsg.superherosighting.dto.Location;
import com.tsg.superherosighting.dto.Organization;
import com.tsg.superherosighting.dto.Sighting;
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
public class LocationsTest extends TestDBSetUpMethods {

    public LocationsTest() {
    }

    @Before
    public void setUp() {
        emptyDB();
        populateDB();
    }

    @Test
    public void testMethodAddAndGetLocation() {
        Location testLoc = new Location();
        testLoc.setName("testLoc");
        testLoc.setDescription("testy");
        testLoc.setAddress("Nowhere lane");

        BigDecimal latLong = new BigDecimal("10.123456");
        testLoc.setLatitude(latLong);
        testLoc.setLongitude(latLong);
        testDao.addLocation(testLoc);

        List<Location> allLocs = testDao.getAllLocations();

        Assert.assertNotNull("List of locs should never be null", allLocs);
        Assert.assertTrue("List of locs should inclue testLoc", allLocs.contains(testLoc));

        Location shouldBeTestLoc = testDao.getALocation(testLoc.getId());
        Assert.assertEquals("testLoc should be shouldBeTestLoc", testLoc, shouldBeTestLoc);
    }

    @Test
    public void testMethodGetAllLocations() {
        List<Location> allLocs = testDao.getAllLocations();
        Assert.assertNotNull("List of locations should not be null", allLocs);
        Assert.assertEquals("List of locations should have 5 locations", 5, allLocs.size());

    }

    @Test
    public void testMethodEditLocation() {
        Location locToEdit = testDao.getALocation(4);

        locToEdit.setName("test");
        locToEdit.setDescription("testy");
        locToEdit.setAddress("Test Lane");

        BigDecimal latLong = new BigDecimal("99.123456");
        locToEdit.setLatitude(latLong);
        locToEdit.setLongitude(latLong);
        testDao.editLocation(locToEdit);

        Location shouldBeTestLoc = testDao.getALocation(locToEdit.getId());

        Assert.assertEquals("shouldBeTestLoc should now be test as name", locToEdit.getName(), shouldBeTestLoc.getName());
        Assert.assertEquals("shouldBeTestLoc should now have testy descrption", locToEdit.getDescription(), shouldBeTestLoc.getDescription());
        Assert.assertEquals("shouldBeTestLoc should now be Test Lane address", locToEdit.getAddress(), shouldBeTestLoc.getAddress());
        Assert.assertEquals("shouldBeTestLoc should now have 99.00000009 lat", locToEdit.getLatitude(), shouldBeTestLoc.getLatitude());
        Assert.assertEquals("shouldBeTestLoc should now have 99.00000009 long", locToEdit.getLongitude(), shouldBeTestLoc.getLongitude());
        Assert.assertEquals("shouldBeTestLoc should equal locToEdit", locToEdit, shouldBeTestLoc);
    }

    @Test
    public void testMethodRemoveLocation() {
        Location locToDelete = testDao.getALocation(2);
        Sighting sightToDelete = testDao.getASighting(2);
        testDao.removeLocation(locToDelete.getId());

        // organizations location should be NA
        Organization shouldBeNALoc = testDao.getAnOrganization(2);
        Assert.assertEquals("Org LocId should be -1 (NA)", -1, shouldBeNALoc.getOrgLoc().getId());

        // sighting with location to delete should be deleted
        List<Sighting> allSights = testDao.getAllSightings();
        Assert.assertFalse("List of sightings should not contain sightToDelete", allSights.contains(sightToDelete));

        // lcoation should be deleted
        List<Location> allLocs = testDao.getAllLocations();
        Assert.assertFalse("List of locations should not contain locToDelete", allLocs.contains(locToDelete));

    }

}
