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
public class OrganizationController {

    @Autowired
    SuperDaoDBJdbcImpl superDao;

    Set<ConstraintViolation<Organization>> violations = new HashSet<>();

    @GetMapping("organizations")
    public String displayOrgs(Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();
        List<Organization> organizations = superDao.getAllOrganizations();
        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("organizations", organizations);
        model.addAttribute("errors", violations);
        return "organizations";
    }

    @PostMapping("addOrganization")
    public String addOrganization(HttpServletRequest request) {
        String name = request.getParameter("name");
        String[] heroIds = request.getParameterValues("heroId");

        List<Hero> heroes = new ArrayList<>();
        if (heroIds != null) {
            for (String heroId : heroIds) {
                heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
            }
        }

        String description = request.getParameter("description");
        String contact = request.getParameter("contact");
        String locId = request.getParameter("locationId");

        Organization org = new Organization();
        org.setName(name);
        org.setDescription(description);
        org.setContact(contact);
        org.setOrgLoc(superDao.getALocation(Integer.parseInt(locId)));

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(org);

        if (violations.isEmpty()) {
            superDao.addOrganization(org);
            superDao.addHeroesToOrg(heroes, org.getId());
        }
        return "redirect:organizations";
    }

    @GetMapping("editOrganization")
    public String editOrganization(HttpServletRequest request, Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<Location> locations = superDao.getAllLocations();

        int id = Integer.parseInt(request.getParameter("id"));
        Organization organization = superDao.getAnOrganization(id);

        model.addAttribute("heroes", heroes);
        model.addAttribute("locations", locations);
        model.addAttribute("organization", organization);
        model.addAttribute("errors", violations);

        return "editOrganization";
    }

    @PostMapping("editOrganization")
    public String performEditOrganization(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Organization organization = superDao.getAnOrganization(id);
        organization.setHeroesInOrg(new ArrayList<>());
        String[] heroIds = request.getParameterValues("heroId");

        List<Hero> heroes = new ArrayList<>();
        if (heroIds != null) {
            for (String heroId : heroIds) {
                heroes.add(superDao.getAHero(Integer.parseInt(heroId)));
            }
        }

        String locId = request.getParameter("locationId");

        organization.setName(request.getParameter("name"));
        organization.setDescription(request.getParameter("description"));
        organization.setContact(request.getParameter("contact"));
        organization.setOrgLoc(superDao.getALocation(Integer.parseInt(locId)));

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(organization);
        if (violations.isEmpty()) {
            superDao.editOrganization(organization);
            superDao.addHeroesToOrg(heroes, organization.getId());
            return "redirect:organizations";
        } else {
            organization = superDao.getAnOrganization(id);
            List<Hero> allHeroes = superDao.getAllHeroes();
            List<Location> allLocs = superDao.getAllLocations();
            model.addAttribute("heroes", allHeroes);
            model.addAttribute("locations", allLocs);
            model.addAttribute("organization", organization);
            model.addAttribute("errors", violations);
            return "editOrganization";
        }
    }

    @GetMapping("deleteOrganization/{id}")
    public String deleteOrganization(@PathVariable Integer id) {
        superDao.removeOrganization(id);
        return "redirect:/organizations";
    }
}
