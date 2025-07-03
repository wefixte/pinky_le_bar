package com.cass.pinkyBar.service;

import com.cass.pinkyBar.dto.*;
import com.cass.pinkyBar.entity.*;
import com.cass.pinkyBar.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CocktailServiceImpl implements CocktailService {

    private final CocktailRepository cocktailRepository;
    private final CocktailIngredientRepository cocktailIngredientRepository;
    private final CocktailSizeRepository cocktailSizeRepository;
    private final IngredientRepository ingredientRepository;
    private final EntityManager entityManager;

    // --- Mapper Entities -> DTO ---

    private CocktailDTO toDto(Cocktail cocktail) {
        if (cocktail == null) return null;

        return CocktailDTO.builder()
                .id(cocktail.getId())
                .name(cocktail.getName())
                .description(cocktail.getDescription())
                .imageUrl(cocktail.getImageUrl())
                .categoryId(cocktail.getCategory() != null ? cocktail.getCategory().getId() : null)
                .categoryName(cocktail.getCategory() != null ? cocktail.getCategory().getName() : null)
                .ingredients(cocktail.getIngredients() == null ? null : cocktail.getIngredients().stream()
                        .map(ci -> new CocktailIngredientDTO(
                                ci.getIngredient().getId(),
                                ci.getIngredient().getName(),
                                ci.getQuantity()
                        ))
                        .collect(Collectors.toList()))
                .sizes(cocktail.getSizes() == null ? null : cocktail.getSizes().stream()
                        .map(size -> new CocktailSizeDTO(
                                size.getId(),
                                size.getSize().name(),
                                size.getPrice()
                        ))
                        .collect(Collectors.toList()))
                .build();
    }

    // --- Mapper DTO -> Entity (partiel, pour création/update) ---

    private void updateEntityFromDto(Cocktail cocktail, CocktailDTO dto) {
        cocktail.setName(dto.getName());
        cocktail.setDescription(dto.getDescription());
        cocktail.setImageUrl(dto.getImageUrl());
        // Category must be set separately (in methods)
    }

    // --- CRUD Cocktail ---

    @Override
    public CocktailDTO createCocktail(CocktailDTO cocktailDTO, Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        if (category == null) {
            throw new ResourceAccessException("Category with id " + categoryId + " not found");
        }
        Cocktail cocktail = new Cocktail();
        updateEntityFromDto(cocktail, cocktailDTO);
        cocktail.setCategory(category);
        Cocktail saved = cocktailRepository.save(cocktail);
        return toDto(saved);
    }

    @Override
    public List<CocktailDTO> getAllCocktails() {
        return cocktailRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CocktailDTO> getCocktailsByCategory(Long categoryId) {
        return cocktailRepository.findByCategoryId(categoryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CocktailDTO getCocktailById(Long id) {
        Cocktail cocktail = cocktailRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Cocktail with id " + id + " not found"));
        return toDto(cocktail);
    }

    @Override
    public CocktailDTO updateCocktail(Long id, CocktailDTO cocktailDTO, Long categoryId) {
        Cocktail cocktail = cocktailRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Cocktail with id " + id + " not found"));

        updateEntityFromDto(cocktail, cocktailDTO);

        Category category = entityManager.find(Category.class, categoryId);
        if (category == null) {
            throw new ResourceAccessException("Category with id " + categoryId + " not found");
        }
        cocktail.setCategory(category);

        Cocktail updated = cocktailRepository.save(cocktail);
        return toDto(updated);
    }

    @Override
    public void deleteCocktail(Long id) {
        Cocktail cocktail = cocktailRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Cocktail with id " + id + " not found"));
        cocktailRepository.delete(cocktail);
    }

    // --- Gestion ingrédients ---

    @Override
    public CocktailDTO addIngredient(Long cocktailId, Long ingredientId, String quantity) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new ResourceAccessException("Cocktail with id " + cocktailId + " not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceAccessException("Ingredient with id " + ingredientId + " not found"));

        CocktailIngredientId ciId = new CocktailIngredientId(cocktailId, ingredientId);

        if (cocktailIngredientRepository.existsById(ciId)) {
            throw new IllegalStateException("Ingredient already exists in cocktail");
        }

        CocktailIngredient ci = CocktailIngredient.builder()
                .cocktail(cocktail)
                .ingredient(ingredient)
                .quantity(Double.parseDouble(quantity.replaceAll("[^0-9.]", "")))
                .build();

        cocktail.getIngredients().add(ci);
        cocktailIngredientRepository.save(ci);

        return toDto(cocktailRepository.save(cocktail));
    }

    @Override
    public CocktailDTO updateIngredient(Long cocktailId, Long ingredientId, String quantity) {
        CocktailIngredientId ciId = new CocktailIngredientId(cocktailId, ingredientId);
        CocktailIngredient ci = cocktailIngredientRepository.findById(ciId)
                .orElseThrow(() -> new ResourceAccessException("CocktailIngredient not found"));

        ci.setQuantity(Double.parseDouble(quantity.replaceAll("[^0-9.]", "")));
        cocktailIngredientRepository.save(ci);

        return getCocktailById(cocktailId);
    }

    @Override
    public CocktailDTO removeIngredient(Long cocktailId, Long ingredientId) {
        CocktailIngredientId ciId = new CocktailIngredientId(cocktailId, ingredientId);
        CocktailIngredient ci = cocktailIngredientRepository.findById(ciId)
                .orElseThrow(() -> new ResourceAccessException("CocktailIngredient not found"));

        cocktailIngredientRepository.delete(ci);

        return getCocktailById(cocktailId);
    }

    // --- Gestion tailles ---

    @Override
    public CocktailDTO addSize(Long cocktailId, CocktailSize.Size size, Double price) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new ResourceAccessException("Cocktail with id " + cocktailId + " not found"));

        CocktailSize cocktailSize = CocktailSize.builder()
                .cocktail(cocktail)
                .size(size)
                .price(price)
                .build();

        cocktail.getSizes().add(cocktailSize);
        cocktailSizeRepository.save(cocktailSize);

        return toDto(cocktailRepository.save(cocktail));
    }

    @Override
    public CocktailDTO updateSize(Long sizeId, CocktailSize.Size size, Double price) {
        CocktailSize cocktailSize = cocktailSizeRepository.findById(sizeId)
                .orElseThrow(() -> new ResourceAccessException("CocktailSize not found"));

        cocktailSize.setSize(size);
        cocktailSize.setPrice(price);
        cocktailSizeRepository.save(cocktailSize);

        return getCocktailById(cocktailSize.getCocktail().getId());
    }

    @Override
    public CocktailDTO removeSize(Long sizeId) {
        CocktailSize cocktailSize = cocktailSizeRepository.findById(sizeId)
                .orElseThrow(() -> new ResourceAccessException("CocktailSize not found"));

        Long cocktailId = cocktailSize.getCocktail().getId();
        cocktailSizeRepository.delete(cocktailSize);

        return getCocktailById(cocktailId);
    }
}
