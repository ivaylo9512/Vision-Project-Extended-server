package com.vision.project.controllers;

import com.vision.project.exceptions.NonExistingOrder;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.DishDto;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.services.base.OrderService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/order/auth")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/findAll")
    public List<OrderDto> findAllNotes(){
        return orderService.findAll().stream().map(OrderDto::new).collect(Collectors.toList());
    }

    @GetMapping(value = "/findAllNotReady")
    public List<OrderDto> findNotReady(Restaurant restaurant){
        return orderService.findAllNotReady(restaurant).stream().map(OrderDto::new).collect(Collectors.toList());
    }

    @GetMapping(value = "/findById/{id}")
    public OrderDto order(@PathVariable(name = "id") int id) throws IOException {
        return new OrderDto(orderService.findById(id));
    }

    @PostMapping(value = "/create")
    public OrderDto create(@RequestBody Order order) throws ExpiredJwtException{
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        int restaurantId = loggedUser.getRestaurantId();
        int userId = loggedUser.getId();

        return new OrderDto(orderService.create(order, restaurantId, userId));
    }

    @PatchMapping(value = "/update/{orderId}/{dishId}")
    public DishDto update(@PathVariable(name = "orderId") int orderId,
                          @PathVariable(name = "dishId")  int dishId){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        return new DishDto(orderService.update(orderId, dishId, loggedUser.getId()));
    }


    @ExceptionHandler
    ResponseEntity handleNonExistingOrder(NonExistingOrder e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
