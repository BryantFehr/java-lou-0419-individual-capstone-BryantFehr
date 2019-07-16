/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
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
public class Organization {

    private int id;

    @NotBlank(message = "Name must not be empty.")
    @Size(max = 100, message = "Max characters for name is 100.")
    private String name;

    @Size(max = 250, message = "Max characters for description is 250.")
    private String description;

    @NotBlank(message = "Contact info cannot be empty. Type NA if no contact info is available.")
    @Size(max = 50, message = "Max characters for contact is 50.")
    private String contact;

    private List<Hero> heroesInOrg = new ArrayList();

    @NotNull(message = "Location cannot be null. Choose NA if no location info is available.")
    private Location orgLoc;

}
