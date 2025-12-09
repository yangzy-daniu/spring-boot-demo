package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusOrderByRankPositionAsc(String status);

    List<Product> findTop5ByStatusOrderByMonthlySalesDesc(String status);

    @Query("SELECT p FROM Product p WHERE p.status = 'active' ORDER BY p.monthlySales DESC")
    List<Product> findPopularProducts();

    @Query("SELECT p.category as category, SUM(p.monthlySales) as sales FROM Product p WHERE p.status = 'active' GROUP BY p.category ORDER BY sales DESC")
    List<Object[]> findCategorySales();

    @Query("SELECT p.category as category, SUM(p.monthlySales * p.price) as revenue FROM Product p WHERE p.status = 'active' GROUP BY p.category ORDER BY revenue DESC")
    List<Object[]> findCategoryRevenue();
}