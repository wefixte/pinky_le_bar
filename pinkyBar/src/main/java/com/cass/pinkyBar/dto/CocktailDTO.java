package com.cass.pinkyBar.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocktailDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private List<CocktailIngredientDTO> ingredients;
    private List<CocktailSizeDTO> sizes;
}
