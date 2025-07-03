package com.cass.pinkyBar.controller;

import com.cass.pinkyBar.dto.CreateOrderRequest;
import com.cass.pinkyBar.dto.OrderDto;
import com.cass.pinkyBar.dto.OrderItemDto;
import com.cass.pinkyBar.repository.UserRepository;
import com.cass.pinkyBar.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    // --- CREATE ORDER (via email) ---
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // généralement le 'sub' du JWT

        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        OrderDto created = orderService.createOrder(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    // --- GET ORDERS BY USER ---
    @GetMapping("/user")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@RequestParam String email) {
        return ResponseEntity.ok(orderService.getOrdersByUser(email));
    }

    // --- GET ORDER DETAIL ---
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetail(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrderDetail(orderId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    // --- GET ALL ORDERS (barmaker) ---
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // --- GET ORDER ITEMS ---
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    // --- UPDATE ORDER ITEM STEP ---
    @PutMapping("/items/{itemId}/step")
    public ResponseEntity<OrderItemDto> updateOrderItemStep(@PathVariable Long itemId,
                                                            @RequestParam String newStep) {
        try {
            return ResponseEntity.ok(orderService.updateOrderItemStep(itemId, newStep));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // --- UPDATE ORDER STATUS IF FINISHED ---
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatusIfFinished(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.updateOrderStatusIfFinished(orderId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
