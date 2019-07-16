/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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
public class Sighting {

    private int id;

    @PastOrPresent(message = "You are not a time traveler. Sightings cannot be in the future.")
    private LocalDateTime dateTime;

    @NotEmpty(message = "Sighting should have at least 1 hero.")
    private List<Hero> heroesAtSighting = new ArrayList();

    @NotNull(message = "Location cannot be null.")
    private Location sightLocation;

}
