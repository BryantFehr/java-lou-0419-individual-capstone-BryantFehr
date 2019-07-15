/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.controllers;

import com.tsg.superherosighting.dao.SuperDaoDBJdbcImpl;
import com.tsg.superherosighting.dto.SuperPower;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Bryant
 */
@Controller
public class PowerController {

    @Autowired
    SuperDaoDBJdbcImpl superDao;

    Set<ConstraintViolation<SuperPower>> violations = new HashSet<>();

    @GetMapping("powers")
    public String displayPowers(Model model) {
        List<SuperPower> powers = superDao.getAllSuperpowers();
        model.addAttribute("powers", powers);
        model.addAttribute("errors", violations);
        return "powers";
    }

    @PostMapping("addPower")
    public String addPower(HttpServletRequest request) {
        String name = request.getParameter("name");

        SuperPower power = new SuperPower();
        power.setName(name);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(power);

        if (violations.isEmpty()) {
            superDao.addSuperpower(power);
        }

        return "redirect:powers";
    }

    @GetMapping("editPower")
    public String editPower(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        SuperPower power = superDao.getASuperpower(id);

        model.addAttribute("power", power);
        model.addAttribute("errors", violations);
        return "editPower";
    }

    @PostMapping("editPower")
    public String performEditPower(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        SuperPower power = superDao.getASuperpower(id);
        power.setName(request.getParameter("name"));

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(power);

        if (violations.isEmpty()) {
            superDao.editSuperpower(power);
            return "redirect:powers";
        } else {
            power = superDao.getASuperpower(id);
            model.addAttribute("power", power);
            model.addAttribute("errors", violations);
            return "editPower";
        }
    }

    @GetMapping("deletePower/{id}")
    public String deletePower(@PathVariable Integer id) {
        superDao.removeSuperpower(id);
        return "redirect:/powers";
    }

}
