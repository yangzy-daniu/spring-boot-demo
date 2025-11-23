package com.example.demo.repository;

import com.example.demo.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByParentIdOrderBySortAsc(Long parentId);

    @Query("SELECT m FROM Menu m WHERE m.parentId IS NULL ORDER BY m.sort ASC")
    List<Menu> findRootMenus();

    List<Menu> findByTypeInOrderBySortAsc(List<Integer> types);

    List<Menu> findAllByOrderBySortAsc();
}