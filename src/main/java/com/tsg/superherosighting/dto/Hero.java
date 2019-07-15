/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Bryant
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hero {

    private int Id;

    @Size(max = 100, message = "Max characters for name is 100.")
    @NotBlank(message = "Name must not be empty.")
    private String name;

    @NotNull(message = "Must be a hero or villain.")
    private Boolean isHero;

    @Size(max = 250, message = "Max characters for description is 250.")
    private String Description;

    @NotEmpty(message = "Hero should have at least 1 power.")
    private List<SuperPower> heroPowers = new ArrayList();

}
