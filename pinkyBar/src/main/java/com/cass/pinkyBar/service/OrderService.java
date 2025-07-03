package com.cass.pinkyBar.service;

import com.cass.pinkyBar.dto.CreateOrderRequest;
import com.cass.pinkyBar.dto.OrderDto;
import com.cass.pinkyBar.dto.OrderItemDto;

import java.util.List;

public interface OrderService {

    // Création d’une commande : on passe l'email maintenant
    OrderDto createOrder(String userEmail, CreateOrderRequest request);

    // Récupérer les commandes d’un utilisateur (email)
    List<OrderDto> getOrdersByUser(String userEmail);

    // Détail d’une commande
    OrderDto getOrderDetail(Long orderId);

    // Toutes commandes (barmaker)
    List<OrderDto> getAllOrders();

    // Items d’une commande
    List<OrderItemDto> getOrderItems(Long orderId);

    // Mise à jour de l’étape d’un item
    OrderItemDto updateOrderItemStep(Long itemId, String newStep);

    // Mise à jour du statut global si tous les items sont terminés
    OrderDto updateOrderStatusIfFinished(Long orderId);

}
