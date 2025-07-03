package com.cass.pinkyBar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cass.pinkyBar.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}