/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.controllers;

import com.tsg.superherosighting.dao.SuperDaoDBJdbcImpl;
import com.tsg.superherosighting.dto.Hero;
import com.tsg.superherosighting.dto.SuperPower;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
public class HeroController {

    @Autowired
    SuperDaoDBJdbcImpl superDao;

    @GetMapping("heroes")
    public String displayHeroes(Model model) {
        List<Hero> heroes = superDao.getAllHeroes();
        List<SuperPower> powers = superDao.getAllSuperpowers();
        model.addAttribute("heroes", heroes);
        model.addAttribute("powers", powers);
        return "heroes";
    }

    @PostMapping("addHero")
    public String addHero(Hero hero, HttpServletRequest request) {
        String name = request.getParameter("name");
        String[] powerIds = request.getParameterValues("powerId");

        List<SuperPower> powers = new ArrayList<>();
        for (String powerId : powerIds) {
            powers.add(superDao.getASuperpower(Integer.parseInt(powerId)));
        }

        String description = request.getParameter("description");
        String isHeroAsString = request.getParameter("ishero");
        Boolean isHero = Boolean.parseBoolean(isHeroAsString);

        hero.setName(name);
        hero.setDescription(description);
        hero.setIsHero(isHero);
        superDao.addHero(hero);
        superDao.addPowersToHero(powers, hero.getId());

        return "redirect:heroes";
    }

    @GetMapping("editHero")
    public String editHero(HttpServletRequest request, Model model) {
        List<SuperPower> powers = superDao.getAllSuperpowers();
        int id = Integer.parseInt(request.getParameter("id"));
        Hero hero = superDao.getAHero(id);

        model.addAttribute("hero", hero);
        model.addAttribute("powers", powers);

        return "editHero";
    }

    @PostMapping("editHero")
    public String performEditHero(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Hero hero = superDao.getAHero(id);
        hero.setHeroPowers(new ArrayList<>());
        String[] powerIds = request.getParameterValues("powerId");

        List<SuperPower> powers = new ArrayList<>();
        for (String powerId : powerIds) {
            powers.add(superDao.getASuperpower(Integer.parseInt(powerId)));
        }
        
        String isHeroAsString = request.getParameter("ishero");
        Boolean isHero = Boolean.parseBoolean(isHeroAsString);
        
        hero.setName(request.getParameter("name"));
        hero.setDescription(request.getParameter("description"));
        hero.setIsHero(isHero);
        superDao.editHero(hero);
        superDao.addPowersToHero(powers, hero.getId());
        
        return "redirect:heroes";
    }
    
    @GetMapping("deleteHero/{id}")
    public String deleteHero(@PathVariable Integer id) {
        superDao.removeHero(id);
        return "redirect:/heroes";
    }

}
