package com.vision.project.controllers;

import com.vision.project.models.*;
import com.vision.project.models.DTOs.DishDto;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/order/auth")
public class OrderController {
    private final OrderService orderService;
    private final LongPollingService longPollingService;

    public OrderController(OrderService orderService, LongPollingService longPollingService) {
        this.orderService = orderService;
        this.longPollingService = longPollingService;
    }

    @GetMapping("/findNotReady/{page}/{pageSize}/{restaurantId}")
    public Map<Integer, OrderDto> findNotReady(@PathVariable("page") int page,
                                      @PathVariable("pageSize") int pageSize,
                                      @PathVariable("restaurantId") int restaurantId){
        return orderService.findNotReady(restaurantId, page, pageSize).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, o -> new OrderDto((Order) o),
                        (existing, replacement) -> existing, LinkedHashMap::new));
    }

    @GetMapping(value = "/findById/{id}")
    public OrderDto findById(@PathVariable(name = "id") int id) throws IOException {
        return new OrderDto(orderService.findById(id));
    }

    @PostMapping(value = "/create")
    public OrderDto create(@RequestBody Order order) throws ExpiredJwtException{
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        int restaurantId = loggedUser.getRestaurantId();
        int userId = loggedUser.getId();

        return new OrderDto(longPollingService.addOrder(order, restaurantId, userId));
    }

    @PatchMapping(value = "/update/{orderId}/{dishId}")
    public DishDto update(@PathVariable(name = "orderId") int orderId,
                          @PathVariable(name = "dishId")  int dishId){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        return new DishDto(longPollingService.addDish(orderId, dishId, loggedUser.getId(), loggedUser.getRestaurantId()));
    }
}
