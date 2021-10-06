package com.vision.project.controllers;

import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserDetails;
import com.vision.project.models.specs.RestaurantSpec;
import com.vision.project.services.base.RestaurantService;
import com.vision.project.services.base.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController()
@RequestMapping(value = "/api/restaurant/auth")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final UserService userService;

    public RestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @GetMapping(value = "/findById/{id}")
    public RestaurantDto findById(@PathVariable("id") int id) {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new RestaurantDto(restaurantService.findById(id, userService.findById(loggedUser.getId())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/create")
    public RestaurantDto create(@Valid @RequestBody RestaurantSpec restaurant){
        return new RestaurantDto(restaurantService.create(new Restaurant(restaurant)));
    }

    @PostMapping(value = "/delete/{id}")
    public void delete(@PathVariable("id") int id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        restaurantService.delete(id, userService.findById(loggedUser.getId()));
    }
}
