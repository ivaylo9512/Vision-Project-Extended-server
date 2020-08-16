package com.vision.project.controllers;

import com.vision.project.models.*;
import com.vision.project.models.DTOs.DishDto;
import com.vision.project.models.DTOs.OrderDto;
import com.vision.project.security.Jwt;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/order/auth")
public class OrderController {
    private final OrderService orderService;
    private final LongPollingService longPollingService;
    private SimpMessagingTemplate messagingTemplate;

    public OrderController(OrderService orderService, LongPollingService longPollingService, SimpMessagingTemplate messagingTemplate) {
        this.orderService = orderService;
        this.longPollingService = longPollingService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping(value = "/findAll")
    public List<OrderDto> findAllOrders(){
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

        return new OrderDto(longPollingService.addOrder(order, restaurantId, userId));
    }

    @MessageMapping("/createOrder")
    @Transactional
    public void createOrder(Principal principal, Order order, SimpMessageHeaderAccessor headers) throws  Exception {
        UserDetails loggedUser;
        try{
            String auth = headers.getNativeHeader("Authorization").get(0);
            String token = auth.substring(6);
            loggedUser = Jwt.validate(token);
        }catch (Exception e){
            throw new BadCredentialsException("Jwt token is missing or is incorrect.");
        }

        int restaurantId = loggedUser.getRestaurantId();
        int userId = loggedUser.getId();

        OrderDto orderDto = new OrderDto(longPollingService.addOrder(order, restaurantId, userId));
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/createOrder", orderDto);
    }

    @PatchMapping(value = "/update/{orderId}/{dishId}")
    public DishDto update(@PathVariable(name = "orderId") int orderId,
                          @PathVariable(name = "dishId")  int dishId){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        return new DishDto(longPollingService.addDish(orderId, dishId, loggedUser.getId(), loggedUser.getRestaurantId()));
    }
}
