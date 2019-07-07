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
public class OrganizationController {
    
    @Autowired
    SuperDaoDBJdbcImpl superDao;

    @GetMapping("organizations")
    public String displayOrgs(Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();
        List<Organization> organizations = superDao.getAllOrganizations();
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("organizations", organizations);
        return "organizations";
    }
    
    @PostMapping("addOrganization")
    public String addOrganization(Organization org, HttpServletRequest request) {
        String name = request.getParameter("name");
        String[] heroIds = request.getParameterValues("heroId");
        
        List<Hero> heroes = new ArrayList<>();
        for (String heroId : heroIds) {
            heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
        }
        
        String description = request.getParameter("description");
        String contact = request.getParameter("contact");
        String locId = request.getParameter("locationId");
        
        org.setName(name);
        org.setDescription(description);
        org.setContact(contact);
        org.setOrgLoc(superDao.getALocation(Integer.parseInt(locId)));
        superDao.addOrganization(org);
        superDao.addHeroesToOrg(heroes, org.getId());
        
        return "redirect:/organizations";
    }
    
}
