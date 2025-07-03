package com.cass.pinkyBar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDto {
    private Long id;
    private String cocktailName;
    private String size;    // ex: "S", "M", "L"
    private int quantity;
    private String step;
}
