/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.superherosighting.dto;

import java.math.BigDecimal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
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
public class Location {

    private int id;
    
    @Size(max = 100, message = "Max characters for name is 100.")
    @NotBlank(message = "Name must not be empty.")
    private String name;
    
    @Size(max = 250, message = "Max characters for description is 250.")
    private String description;
    
    @Size(max = 150, message = "Max characters for address is 150.")
    @NotBlank(message = "Address must not be empty.")
    private String address;
    
    @Digits(integer=2, fraction=6, message = "Latitude must have 6 decimal places.")
    private BigDecimal latitude;
    
    @Digits(integer=3, fraction=6, message = "Longitude must have 6 decimal places.")
    private BigDecimal longitude;

}
