package com.cass.pinkyBar.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItemCreateDto> items;

    @Data
    public static class OrderItemCreateDto {
        private Long cocktailSizeId;
        private int quantity;
    }
}
