/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private LocalDateTime dateTime;
    private List<Hero> heroesAtSighting = new ArrayList();
    private Location sightLocation;

}
