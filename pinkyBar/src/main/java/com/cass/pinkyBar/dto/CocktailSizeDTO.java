package com.cass.pinkyBar.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocktailSizeDTO {
    private Long id;  
    private String size;
    private Double price;
}
