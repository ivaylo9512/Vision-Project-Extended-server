package com.vision.project.controllers;

import com.vision.project.exceptions.NonExistingOrder;
import com.vision.project.models.*;
import com.vision.project.services.base.OrderService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/order/auth")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/findAll")
    public List<Order> findAllNotes(){
        return orderService.findAll();
    }

    @GetMapping(value = "/findAllNotReady")
    public List<Order> findNotReady(Restaurant restaurant){
        return orderService.findAllNotReady(restaurant);
    }

    @GetMapping(value = "/findById/{id}")
    public Order order(@PathVariable(name = "id") int id) throws IOException {
        return orderService.findById(id);
    }

    @PostMapping(value = "/create")
    public Order create(@RequestBody Order order) throws ExpiredJwtException{
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        int restaurantId = loggedUser.getRestaurantId();
        int userId = loggedUser.getId();

        return orderService.create(order, restaurantId, userId);
    }

    @PatchMapping(value = "/update/{orderId}/{dishId}")
    public Dish update(@PathVariable(name = "orderId") int orderId,
                          @PathVariable(name = "dishId")  int dishId){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        return orderService.update(orderId, dishId, loggedUser.getId());
    }


    @ExceptionHandler
    ResponseEntity handleNonExistingOrder(NonExistingOrder e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
