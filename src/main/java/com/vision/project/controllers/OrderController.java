package com.vision.project.controllers;

import com.vision.project.models.*;
import com.vision.project.services.base.OrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/auth/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/findAll")
    public List<Order> findAllNotes(){
        return orderService.findAll();
    }

    @GetMapping(value = "/findById/{id}")
    public Order order(@PathVariable(name = "id") int id) throws IOException {
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
    DeferredResult<List<Order>> getUpdates(@RequestParam(name = "dateTime") LocalDateTime dateTime){
        DeferredResult<List<Order>> deferredResult = new DeferredResult<>(100000L,"Time out.");
        UserRequest userRequest = new UserRequest(deferredResult, dateTime);
        orderService.findMoreRecent(userRequest);
        return deferredResult;
    }
}
