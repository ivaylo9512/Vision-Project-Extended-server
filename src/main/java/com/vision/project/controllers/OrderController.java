package com.vision.project.controllers;

import com.vision.project.models.*;
import com.vision.project.models.DTOs.DishDto;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.models.specs.OrderCreateSpec;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import com.vision.project.services.base.RestaurantService;
import com.vision.project.services.base.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/order/auth")
public class OrderController {
    private final OrderService orderService;
    private final LongPollingService longPollingService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public OrderController(OrderService orderService, LongPollingService longPollingService, UserService userService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.longPollingService = longPollingService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/findNotReady/{page}/{pageSize}/{restaurantId}")
    public Map<Long, OrderDto> findNotReady(@PathVariable("page") int page,
                                      @PathVariable("pageSize") int pageSize){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return orderService.findNotReady(restaurantService.getById(loggedUser.getRestaurantId()), page, pageSize).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> new OrderDto(o.getValue()),
                        (existing, replacement) -> existing, LinkedHashMap::new));
    }

    @GetMapping(value = "/findById/{id}")
    public OrderDto findById(@PathVariable(name = "id") long id) throws IOException {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new OrderDto(orderService.findById(id, restaurantService.getById(loggedUser.getId())));
    }

    @PostMapping(value = "/create")
    public OrderDto create(@RequestBody OrderCreateSpec orderSpec) throws ExpiredJwtException{
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        long restaurantId = loggedUser.getRestaurantId();
        long userId = loggedUser.getId();

        Order order = new Order(orderSpec, restaurantService.getById(restaurantId), userService.getById(userId));
        Order savedOrder = orderService.create(order);
        longPollingService.checkOrders(savedOrder, restaurantId, userId);

        return new OrderDto(savedOrder);
    }

    @PatchMapping(value = "/update/{orderId}/{dishId}")
    public DishDto update(@PathVariable(name = "orderId") long orderId,
                          @PathVariable(name = "dishId") long dishId){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        long restaurantId = loggedUser.getRestaurantId();
        long userId = loggedUser.getId();

        Dish dish = orderService.update(orderId, dishId, restaurantService.getById(restaurantId), userService.getById(userId));
        longPollingService.checkDishes(dish, restaurantId, userId);

        return new DishDto(dish);
    }
}
