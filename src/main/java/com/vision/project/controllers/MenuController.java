package com.vision.project.controllers;

import com.vision.project.models.DTOs.MenuDto;
import com.vision.project.models.Menu;
import com.vision.project.models.UserDetails;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.MenuCreateSpec;
import com.vision.project.models.specs.MenuUpdateSpec;
import com.vision.project.services.base.MenuService;
import com.vision.project.services.base.RestaurantService;
import com.vision.project.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu/auth")
public class MenuController {
    private final MenuService menuService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public MenuController(MenuService menuService, UserService userService, RestaurantService restaurantService) {
        this.menuService = menuService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/create")
    public MenuDto create(@RequestBody() MenuCreateSpec menuCreateSpec){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        UserModel user = userService.findById(loggedUser.getId());

        return new MenuDto(menuService.create(new Menu(menuCreateSpec.getName(), restaurantService.findById(1, user)), user));
    }

    @PatchMapping("/update")
    public MenuDto update(@RequestBody() MenuUpdateSpec menuUpdateSpec){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new MenuDto(menuService.update(menuUpdateSpec, userService.findById(loggedUser.getId())));
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        menuService.delete(id, userService.findById(loggedUser.getId()));
    }
}
