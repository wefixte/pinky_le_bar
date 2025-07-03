package com.cass.pinkyBar.service;

import com.cass.pinkyBar.dto.CreateOrderRequest;
import com.cass.pinkyBar.dto.OrderDto;
import com.cass.pinkyBar.dto.OrderItemDto;
import com.cass.pinkyBar.entity.Order;
import com.cass.pinkyBar.entity.OrderItem;
import com.cass.pinkyBar.entity.User;
import com.cass.pinkyBar.entity.CocktailSize;
import com.cass.pinkyBar.repository.OrderItemRepository;
import com.cass.pinkyBar.repository.OrderRepository;
import com.cass.pinkyBar.repository.UserRepository;
import com.cass.pinkyBar.repository.CocktailSizeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CocktailSizeRepository cocktailSizeRepository;

    private OrderItemDto toDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .cocktailName(item.getSize().getCocktail().getName())
                .size(item.getSize().getSize().name())
                .quantity(item.getQuantity())
                .step(item.getStep())
                .build();
    }

    private OrderDto toDto(Order order) {
        List<OrderItemDto> items = orderItemRepository.findByOrder(order).stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    @Override
    public OrderDto createOrder(String userEmail, CreateOrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceAccessException("User not found for email: " + userEmail));

        Order order = Order.builder()
                .user(user)
                .status("EN_COURS")
                .build();
        order = orderRepository.save(order);

        for (CreateOrderRequest.OrderItemCreateDto dto : request.getItems()) {
            CocktailSize size = cocktailSizeRepository.findById(dto.getCocktailSizeId())
                    .orElseThrow(() -> new ResourceAccessException("Cocktail size not found"));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .size(size)
                    .quantity(dto.getQuantity())
                    .step("EN_ATTENTE")
                    .build();
            orderItemRepository.save(item);
        }

        order = orderRepository.findById(order.getId())
                .orElseThrow(() -> new ResourceAccessException("Order not found"));

        return toDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceAccessException("User not found for email: " + userEmail));
        return orderRepository.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceAccessException("Order not found"));

        return toDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceAccessException("Order not found"));

        return orderItemRepository.findByOrder(order).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemDto updateOrderItemStep(Long itemId, String newStep) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceAccessException("Order item not found"));

        item.setStep(newStep);
        orderItemRepository.save(item);

        return toDto(item);
    }

    @Override
    public OrderDto updateOrderStatusIfFinished(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceAccessException("Order not found"));

        boolean allFinished = orderItemRepository.findByOrder(order).stream()
                .allMatch(item -> "TERMINE".equalsIgnoreCase(item.getStep()));

        if (allFinished) {
            order.setStatus("TERMINE");
            orderRepository.save(order);
        }

        return toDto(order);
    }
}
