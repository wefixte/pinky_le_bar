package com.cass.pinkyBar.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CocktailIngredientId implements Serializable {

    private Long cocktail;
    private Long ingredient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CocktailIngredientId that)) return false;
        return Objects.equals(cocktail, that.cocktail) &&
               Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cocktail, ingredient);
    }
}
