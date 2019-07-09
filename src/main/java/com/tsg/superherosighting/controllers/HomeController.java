/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.controllers;

import com.tsg.superherosighting.dao.SuperDaoDBJdbcImpl;
import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.Location;
import com.tsg.superherosighting.dto.Sighting;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Bryant
 */
@Controller
public class HomeController {

    @Autowired
    SuperDaoDBJdbcImpl superDao;

    @GetMapping("home")
    public String displayRecentSightings(Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();
        List<Sighting> sightings = superDao.get10RecentSightings();
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("sightings", sightings);
        // list of 10 recent sightings
        return "home";
    }
}
