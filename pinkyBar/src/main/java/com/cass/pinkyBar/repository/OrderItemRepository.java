package com.cass.pinkyBar.repository;

import com.cass.pinkyBar.entity.OrderItem;
import com.cass.pinkyBar.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Lister les items d’une commande
    List<OrderItem> findByOrder(Order order);

    // Optionnel : lister les items par étape (ex: ceux en cours)
    List<OrderItem> findByOrderAndStep(Order order, String step);
}
