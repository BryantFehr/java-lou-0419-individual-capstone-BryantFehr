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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Bryant
 */
@Controller
public class SightingController {

    @Autowired
    SuperDaoDBJdbcImpl superDao;

    Set<ConstraintViolation<Sighting>> violations = new HashSet<>();

    @GetMapping("sightings")
    public String displaySightings(Model model) {
        List<Hero> heroes = superDao.getAllHeroes();

        List<Location> locations = superDao.getAllLocations();
        Location naLocation = superDao.getALocation(-1);
        locations.remove(naLocation);

        List<Sighting> sightings = superDao.getAllSightings();
        model.addAttribute("currentTime", LocalDateTime.now().withNano(0).withSecond(0));
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("sightings", sightings);
        model.addAttribute("errors", violations);

        return "sightings";
    }

    @PostMapping("addSighting")
    public String addSightings(HttpServletRequest request) {
        String[] heroIds = request.getParameterValues("heroId");

        List<Hero> heroes = new ArrayList<>();
        if (heroIds != null) {
            for (String heroId : heroIds) {
                heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
            }
        }

        String dateTime = request.getParameter("sighttime");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);

        Sighting sighting = new Sighting();
        sighting.setDateTime(localDateTime);

        String locId = request.getParameter("locationId");
        sighting.setSightLocation(superDao.getALocation(Integer.parseInt(locId)));

        sighting.setHeroesAtSighting(heroes);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(sighting);

        if (violations.isEmpty()) {
            superDao.addSighting(sighting);
            superDao.addHeroesToSighting(heroes, sighting.getId());
        }
        return "redirect:sightings";
    }

    @GetMapping("editSighting")
    public String editSighting(HttpServletRequest request, Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();
        Location naLocation = superDao.getALocation(-1);
        locations.remove(naLocation);

        int id = Integer.parseInt(request.getParameter("id"));
        Sighting sighting = superDao.getASighting(id);

        model.addAttribute("currentTime", LocalDateTime.now().withNano(0).withSecond(0));
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("sighting", sighting);
        model.addAttribute("errors", violations);

        return "editSighting";
    }

    @PostMapping("editSighting")
    public String performEditSighting(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Sighting sighting = superDao.getASighting(id);
        sighting.setHeroesAtSighting(new ArrayList<>());
        String[] heroIds = request.getParameterValues("heroId");

        List<Hero> heroes = new ArrayList<>();
        if (heroIds != null) {
            for (String heroId : heroIds) {
                heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
            }
        }
        String dateTime = request.getParameter("sighttime");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        sighting.setDateTime(localDateTime);

        String locId = request.getParameter("locationId");
        sighting.setSightLocation(superDao.getALocation(Integer.parseInt(locId)));

        sighting.setHeroesAtSighting(heroes);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(sighting);

        if (violations.isEmpty()) {
            superDao.editSighting(sighting);
            return "redirect:sightings";
        } else {
            sighting = superDao.getASighting(id);
            List<Hero> allHeroes = superDao.getAllHeroes();
            List<Location> allLocs = superDao.getAllLocations();
            Location naLocation = superDao.getALocation(-1);
            allLocs.remove(naLocation);

            model.addAttribute("currentTime", LocalDateTime.now().withNano(0).withSecond(0));
            model.addAttribute("heroes", allHeroes);
            model.addAttribute("locations", allLocs);
            model.addAttribute("sighting", sighting);
            model.addAttribute("errors", violations);
            return "editSighting";
        }

    }

    @GetMapping("deleteSighting/{id}")
    @ResponseBody
    public Sighting deleteSighting(@PathVariable Integer id) {
        Sighting toRemove = superDao.getASighting(id);
        if (toRemove != null) {
            superDao.removeSighting(id);
            return toRemove;
        } else {
            return null;
        }
    }
}
