package com.cass.pinkyBar.repository;

import com.cass.pinkyBar.entity.Order;
import com.cass.pinkyBar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Trouver toutes les commandes d’un utilisateur donné
    List<Order> findByUser(User user);

    // Trouver commandes par status, utile pour barmaker (ex: status != terminé)
    List<Order> findByStatus(String status);
}
