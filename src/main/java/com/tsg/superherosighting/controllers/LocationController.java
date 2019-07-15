/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.controllers;

import com.tsg.superherosighting.dao.SuperDaoDBJdbcImpl;
import com.tsg.superherosighting.dto.Location;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

/**
 *
 * @author Bryant
 */
@Controller
public class LocationController {

    @Autowired
    SuperDaoDBJdbcImpl superDao;

    Set<ConstraintViolation<Location>> violations = new HashSet<>();

    @GetMapping("locations")
    public String displayLocations(Model model) {
        List<Location> locations = superDao.getAllLocations();
        Location naLocation = superDao.getALocation(-1);
        locations.remove(naLocation);
        
        model.addAttribute("locations", locations);
        model.addAttribute("errors", violations);
        return "locations";
    }

    @PostMapping("addLocation")
    public String addLocation(HttpServletRequest request) {
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String description = request.getParameter("description");
        String latAsString = request.getParameter("latitude");
        String longAsString = request.getParameter("longitude");

        Location location = new Location();
        location.setName(name);
        location.setAddress(address);
        location.setDescription(description);
        location.setLatitude(new BigDecimal(latAsString).setScale(6, RoundingMode.HALF_EVEN));
        location.setLongitude(new BigDecimal(longAsString).setScale(6, RoundingMode.HALF_EVEN));

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(location);

        if (violations.isEmpty()) {
            superDao.addLocation(location);
        }

        return "redirect:locations";
    }

    @GetMapping("editLocation")
    public String editLocation(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Location location = superDao.getALocation(id);

        model.addAttribute("location", location);
        model.addAttribute("errors", violations);

        return "editLocation";
    }

    @PostMapping("editLocation")
    public String performEditLocation(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Location location = superDao.getALocation(id);

        location.setName(request.getParameter("name"));
        location.setAddress(request.getParameter("address"));
        location.setDescription(request.getParameter("description"));
        location.setLatitude(new BigDecimal(request.getParameter("latitude")).setScale(6, RoundingMode.HALF_EVEN));
        location.setLongitude(new BigDecimal(request.getParameter("longitude")).setScale(6, RoundingMode.HALF_EVEN));

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(location);

        if (violations.isEmpty()) {
            superDao.editLocation(location);
            return "redirect:locations";
        } else {
            model.addAttribute("location", location);
            model.addAttribute("errors", violations);
            return "editLocation";
        }

    }

    @GetMapping("deleteLocation/{id}")
    public String deleteLocation(@PathVariable Integer id) {
        superDao.removeLocation(id);
        return "redirect:/locations";
    }
}
