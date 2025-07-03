package com.cass.pinkyBar.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.cass.pinkyBar.entity.CocktailIngredient;
import com.cass.pinkyBar.entity.CocktailIngredientId;

import java.util.List;

public interface CocktailIngredientRepository extends JpaRepository<CocktailIngredient, CocktailIngredientId> {
    List<CocktailIngredient> findByCocktailId(Long cocktailId);
}