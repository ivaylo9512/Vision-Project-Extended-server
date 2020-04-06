package com.vision.project.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.exceptions.NonExistingOrder;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.services.base.OrderService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.time.LocalDateTime;
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
    public List<Order> findNotReady(){
        return orderService.findAllNotReady();
    }

    @GetMapping(value = "/findById/{id}")
    public Order order(@PathVariable(name = "id") int id) throws IOException {
        return orderService.findById(id);
    }

    @GetMapping(value = "/getMostRecentDate/{restaurantId}")
    public LocalDateTime getMostRecentDate(@PathVariable(name = "restaurantId") int id){
        return orderService.getMostRecentDate(id);
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

        return orderService.update(orderId, dishId);
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @PatchMapping("/getUpdates")
    DeferredResult<List<OrderDto>> getUpdates(
            @RequestBody LocalDateTime lastUpdate,
            @RequestParam(name = "restaurantId") int id){

        DeferredResult<List<OrderDto>> deferredResult = new DeferredResult<>(100000L,"Time out.");
        OrderRequest orderRequest = new OrderRequest(deferredResult, lastUpdate, id);

        deferredResult.onTimeout(() -> orderService.removeUserRequest(orderRequest));
        orderService.findMoreRecent(orderRequest);

        return deferredResult;
    }

    @ExceptionHandler
    ResponseEntity handleNonExistingOrder(NonExistingOrder e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
