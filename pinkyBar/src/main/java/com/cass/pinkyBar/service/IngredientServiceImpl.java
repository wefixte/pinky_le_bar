package com.cass.pinkyBar.service;

import com.cass.pinkyBar.entity.Ingredient;
import com.cass.pinkyBar.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Override
    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Override
    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Ingredient with id " + id + " not found"));
    }

    @Override
    public Ingredient updateIngredient(Long id, Ingredient ingredientDetails) {
        Ingredient ingredient = getIngredientById(id);
        ingredient.setName(ingredientDetails.getName());
        return ingredientRepository.save(ingredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        Ingredient ingredient = getIngredientById(id);
        ingredientRepository.delete(ingredient);
    }
}
