package com.carizo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carizo.model.Order;
import com.carizo.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> { 
	
	List<Order> findByUser(User user);
	
	  @Query("SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.product p WHERE p.user.id = :adminId")
	    List<Order> findOrdersByAdminProducts(@Param("adminId") Long adminId);

	
}