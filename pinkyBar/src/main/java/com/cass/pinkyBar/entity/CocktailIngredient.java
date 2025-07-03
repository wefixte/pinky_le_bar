package com.cass.pinkyBar.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CocktailIngredientId.class)
public class CocktailIngredient {

    @Id
    @ManyToOne
    @JoinColumn(name = "cocktail_id")
    @MapsId("cocktail")
    @JsonIgnore
    private Cocktail cocktail;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    @MapsId("ingredient")
    private Ingredient ingredient;

    private Double quantity;
}
