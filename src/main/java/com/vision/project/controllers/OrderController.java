package com.vision.project.controllers;

import com.vision.project.models.Order;
import com.vision.project.services.base.OrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/api/order")
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

    @PostMapping(value = "/save")
    public Order order(@RequestBody Order order){
        return orderService.save(new Order());
    }

    @GetMapping("/getUpdates")
    DeferredResult<Order> getUpdates(){
        DeferredResult<Order> deferredResult = new DeferredResult<>(100000L,"Time out.");
        CompletableFuture.runAsync(()->{
            try {
                orderService.getRequests().add(new UserRequest(deferredResult, new Date()));
            }catch (Exception ex){
            }
        });
        return deferredResult;
    }
}
