package com.cass.pinkyBar.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.cass.pinkyBar.entity.Cocktail;

import java.util.List;

public interface CocktailRepository extends JpaRepository<Cocktail, Long> {
    List<Cocktail> findByCategoryId(Long categoryId);
}