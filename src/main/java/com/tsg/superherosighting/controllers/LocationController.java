/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.controllers;

import com.tsg.superherosighting.dao.SuperDaoDBJdbcImpl;
import com.tsg.superherosighting.dto.Location;
import java.math.BigDecimal;
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
public class LocationController {
    
    @Autowired
    SuperDaoDBJdbcImpl superDao;
    
    @GetMapping("locations")
    public String displayLocations(Model model) {
        List<Location> locations = superDao.getAllLocations();
        model.addAttribute("locations", locations);
        return "locations";
    }
    
    @PostMapping("addLocation")
    public String addLocation(HttpServletRequest request) {
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String description = request.getParameter("description");
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");
        
        Location location = new Location();
        location.setName(name);
        location.setAddress(address);
        location.setDescription(description);
        location.setLatitude(new BigDecimal(latitude));
        location.setLongitude(new BigDecimal(longitude));
        superDao.addLocation(location);
        
        return "redirect:/locations";
    }
    
    @GetMapping("editLocation")
    public String editLocation(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Location location = superDao.getALocation(id);
        
        model.addAttribute("location", location);
        
        return "editLocation";
    }
    
    @PostMapping("editLocation")
    public String performEditLocation(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Location location = superDao.getALocation(id);
        
        location.setName(request.getParameter("name"));
        location.setAddress(request.getParameter("address"));
        location.setDescription(request.getParameter("description"));
        location.setLatitude(new BigDecimal(request.getParameter("latitude")));
        location.setLongitude(new BigDecimal(request.getParameter("longitude")));
        
        superDao.editLocation(location);
        
        
        return "redirect:/locations";
    }

}