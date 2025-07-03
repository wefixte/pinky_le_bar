package com.cass.pinkyBar.controller;

import com.cass.pinkyBar.dto.CocktailDTO;
import com.cass.pinkyBar.entity.Category;
import com.cass.pinkyBar.entity.CocktailSize;
import com.cass.pinkyBar.entity.Ingredient;
import com.cass.pinkyBar.repository.CategoryRepository;
import com.cass.pinkyBar.repository.IngredientRepository;
import com.cass.pinkyBar.service.CocktailService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cocktails")
@RequiredArgsConstructor
public class CocktailController {

    private final CocktailService cocktailService;
    private final CategoryRepository categoryRepository;       // Tu peux les garder si besoin front
    private final IngredientRepository ingredientRepository;

    // -- COCKTAIL CRUD --

    @PostMapping
    public ResponseEntity<CocktailDTO> createCocktail(@RequestBody CocktailDTO cocktailDTO,
                                                      @RequestParam Long categoryId) {
        try {
            CocktailDTO created = cocktailService.createCocktail(cocktailDTO, categoryId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<CocktailDTO>> getAllCocktails() {
        return ResponseEntity.ok(cocktailService.getAllCocktails());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CocktailDTO>> getCocktailsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(cocktailService.getCocktailsByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CocktailDTO> getCocktailById(@PathVariable Long id) {
        try {
            CocktailDTO cocktail = cocktailService.getCocktailById(id);
            return ResponseEntity.ok(cocktail);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CocktailDTO> updateCocktail(@PathVariable Long id,
                                                      @RequestBody CocktailDTO cocktailDTO,
                                                      @RequestParam Long categoryId) {
        try {
            CocktailDTO updated = cocktailService.updateCocktail(id, cocktailDTO, categoryId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCocktail(@PathVariable Long id) {
        cocktailService.deleteCocktail(id);
        return ResponseEntity.noContent().build();
    }

    // -- INGREDIENTS pour un cocktail --

    @PostMapping("/{cocktailId}/ingredients")
    public ResponseEntity<CocktailDTO> addIngredient(@PathVariable Long cocktailId,
                                                     @RequestParam Long ingredientId,
                                                     @RequestParam String quantity) {
        try {
            CocktailDTO updated = cocktailService.addIngredient(cocktailId, ingredientId, quantity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{cocktailId}/ingredients/{ingredientId}")
    public ResponseEntity<CocktailDTO> updateIngredient(@PathVariable Long cocktailId,
                                                        @PathVariable Long ingredientId,
                                                        @RequestParam String quantity) {
        try {
            CocktailDTO updated = cocktailService.updateIngredient(cocktailId, ingredientId, quantity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{cocktailId}/ingredients/{ingredientId}")
    public ResponseEntity<CocktailDTO> removeIngredient(@PathVariable Long cocktailId,
                                                        @PathVariable Long ingredientId) {
        try {
            CocktailDTO updated = cocktailService.removeIngredient(cocktailId, ingredientId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // -- SIZES --

    @PostMapping("/{cocktailId}/sizes")
    public ResponseEntity<CocktailDTO> addSize(@PathVariable Long cocktailId,
                                               @RequestParam CocktailSize.Size size,
                                               @RequestParam Double price) {
        try {
            CocktailDTO updated = cocktailService.addSize(cocktailId, size, price);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/sizes/{sizeId}")
    public ResponseEntity<CocktailDTO> updateSize(@PathVariable Long sizeId,
                                                  @RequestParam CocktailSize.Size size,
                                                  @RequestParam Double price) {
        try {
            CocktailDTO updated = cocktailService.updateSize(sizeId, size, price);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/sizes/{sizeId}")
    public ResponseEntity<CocktailDTO> removeSize(@PathVariable Long sizeId) {
        try {
            CocktailDTO updated = cocktailService.removeSize(sizeId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // -- CATEGORIES & INGREDIENTS pour affichage front (optionnel) --

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/ingredients")
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }
}
