package com.vision.project.controllers;

import com.vision.project.models.*;
import com.vision.project.services.base.OrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/api/auth/order")
public class OrderController {
    private OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/findAll")
    public List<Order> findAllNotes(){
        return orderService.findAll();
    }

    @GetMapping(value = "/findById/{id}")
    public Order order(@PathVariable(name = "id") int id){
        return orderService.findById(id);
    }

    @PostMapping(value = "/create")
    public Order create(@RequestBody Order order){
        return orderService.create(order);
    }

    @PostMapping(value = "/update")
    public Order update(@RequestBody Order order){
        return orderService.update(order);
    }

    @GetMapping("/getUpdates")
    DeferredResult<List<Order>> getUpdates(){
        DeferredResult<List<Order>> deferredResult = new DeferredResult<>(100000L,"Time out.");
        UserRequest userRequest = new UserRequest(deferredResult, new Date());
        orderService.findMoreRecent(userRequest);
        return deferredResult;
    }
}
