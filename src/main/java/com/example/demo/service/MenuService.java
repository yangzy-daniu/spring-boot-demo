package com.example.demo.service;

import com.example.demo.entity.Menu;
import com.example.demo.repository.MenuRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    @Resource
    private MenuRepository menuRepository;

    public List<Menu> getUserMenus() {
        // 返回树形结构的菜单
        return buildMenuTree(menuRepository.findByTypeInOrderBySortAsc(List.of(0, 1)));
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAllByOrderBySortAsc();
    }

    public List<Menu> getMenuTree() {
        return buildMenuTree(getAllMenus());
    }

    public Menu getMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Menu menu) {
        Menu existingMenu = menuRepository.findById(menu.getId()).orElse(null);
        if (existingMenu != null) {
            existingMenu.setName(menu.getName());
            existingMenu.setPath(menu.getPath());
            existingMenu.setIcon(menu.getIcon());
            existingMenu.setSort(menu.getSort());
            existingMenu.setParentId(menu.getParentId());
            existingMenu.setComponent(menu.getComponent());
            existingMenu.setType(menu.getType());
            return menuRepository.save(existingMenu);
        }
        return null;
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    public void batchDelete(List<Long> ids) {
        menuRepository.deleteAllById(ids);
    }

    private List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == null)
                .collect(Collectors.toList());

        for (Menu rootMenu : rootMenus) {
            buildChildren(rootMenu, menus);
        }

        return rootMenus;
    }

    private void buildChildren(Menu parent, List<Menu> menus) {
        List<Menu> children = menus.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .collect(Collectors.toList());

        parent.setChildren(children);

        for (Menu child : children) {
            buildChildren(child, menus);
        }
    }
}