package com.cass.pinkyBar.service;

import com.cass.pinkyBar.dto.CocktailDTO;
import com.cass.pinkyBar.entity.CocktailSize;

import java.util.List;

public interface CocktailService {
    CocktailDTO createCocktail(CocktailDTO cocktailDTO, Long categoryId);

    List<CocktailDTO> getAllCocktails();

    List<CocktailDTO> getCocktailsByCategory(Long categoryId);

    CocktailDTO getCocktailById(Long id);

    CocktailDTO updateCocktail(Long id, CocktailDTO cocktailDTO, Long categoryId);

    void deleteCocktail(Long id);

    // Gestion ingrédients
    CocktailDTO addIngredient(Long cocktailId, Long ingredientId, String quantity);
    CocktailDTO updateIngredient(Long cocktailId, Long ingredientId, String quantity);
    CocktailDTO removeIngredient(Long cocktailId, Long ingredientId);

    // Gestion tailles
    CocktailDTO addSize(Long cocktailId, CocktailSize.Size size, Double price);
    CocktailDTO updateSize(Long sizeId, CocktailSize.Size size, Double price);
    CocktailDTO removeSize(Long sizeId);
}
