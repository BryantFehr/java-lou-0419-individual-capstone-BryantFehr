/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.controllers;

import com.tsg.superherosighting.dao.SuperDaoDBJdbcImpl;
import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.Location;
import com.tsg.superherosighting.dto.Organization;
import com.tsg.superherosighting.dto.Sighting;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Bryant
 */
@Controller
public class SightingController {
    
    @Autowired
    SuperDaoDBJdbcImpl superDao;
    
    @GetMapping("sightings")
    public String displaySightings(Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();
        List<Sighting> sightings = superDao.getAllSightings();
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("sightings", sightings);
        
        return "sightings";
    }
    
    @PostMapping("addSighting")
    public String addSightings(Sighting sighting, HttpServletRequest request) {
        String[] heroIds = request.getParameterValues("heroId");
        
        List<Hero> heroes = new ArrayList<>();
        for (String heroId : heroIds) {
            heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
        }
        String dateTime = request.getParameter("sighttime");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        sighting.setDateTime(localDateTime);
        
        String locId = request.getParameter("locationId");
                sighting.setSightLocation(superDao.getALocation(Integer.parseInt(locId)));
                superDao.addSighting(sighting);
                superDao.addHeroesToSighting(heroes, sighting.getId());
        
        return "redirect:/sightings";
    }
    
    @GetMapping("editSighting")
    public String editSighting(HttpServletRequest request, Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();

        int id = Integer.parseInt(request.getParameter("id"));
        Sighting sighting = superDao.getASighting(id);
        
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("sighting", sighting);
        
        return "editSighting";
    }

    @PostMapping("editSighting")
    public String performEditSighting(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Sighting sighting = superDao.getASighting(id);
        sighting.setHeroesAtSighting(new ArrayList<>());
        String[] heroIds = request.getParameterValues("heroId");
        
        List<Hero> heroes = new ArrayList<>();
        for (String heroId : heroIds) {
            heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
        }
        String dateTime = request.getParameter("sighttime");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        sighting.setDateTime(localDateTime);
        
        String locId = request.getParameter("locationId");
        sighting.setSightLocation(superDao.getALocation(Integer.parseInt(locId)));
        
        superDao.editSighting(sighting);
        superDao.addHeroesToSighting(heroes, sighting.getId());

        return "redirect:/sightings";
    }
}
