package com.cass.pinkyBar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocktailSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Size size;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    public enum Size {
        S, M, L
    }
}
