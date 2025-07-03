package com.cass.pinkyBar.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    CLIENT(0, "Client"),
    BARMAKER(1, "Barmaker");

    private final int id;
    private final String libelle;
}
