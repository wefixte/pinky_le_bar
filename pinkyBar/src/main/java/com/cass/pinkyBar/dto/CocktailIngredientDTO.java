package com.cass.pinkyBar.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocktailIngredientDTO {
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
}