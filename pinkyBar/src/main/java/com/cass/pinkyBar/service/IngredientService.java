package com.cass.pinkyBar.service;

import com.cass.pinkyBar.entity.Ingredient;

import java.util.List;

public interface IngredientService {
    Ingredient createIngredient(Ingredient ingredient);

    List<Ingredient> getAllIngredients();

    Ingredient getIngredientById(Long id);

    Ingredient updateIngredient(Long id, Ingredient ingredientDetails);

    void deleteIngredient(Long id);
}
