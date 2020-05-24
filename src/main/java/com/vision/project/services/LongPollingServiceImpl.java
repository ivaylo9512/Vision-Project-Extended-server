package com.vision.project.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.*;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LongPollingServiceImpl implements LongPollingService {
    private Cache<Integer, UserRequest> userRequests = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();
    Map<Integer, Cache<Integer, UserRequest>> restaurants = new HashMap<>();

    private final OrderService orderService;
    private final ChatService chatService;
    private final MessageService messageService;

    public LongPollingServiceImpl(OrderService orderService, ChatService chatService, MessageService messageService) {
        this.orderService = orderService;
        this.chatService = chatService;
        this.messageService = messageService;
    }

    public void setAndAddRequest(UserRequest newRequest){
        int userId = newRequest.getUserId();

        UserRequest currentRequest = userRequests.getIfPresent(userId);
        if(currentRequest == null){
            setMoreRecentData(newRequest);
            addRequest(newRequest);
        }else {
            try {
                currentRequest.getLock().lock();
                currentRequest.setRequest(newRequest.getRequest());

                setDataFromRequest(currentRequest);
                addRequest(currentRequest);
            }finally {
                currentRequest.getLock().unlock();
            }
        }
    }

    private void setDataFromRequest(UserRequest currentRequest) {
        DeferredResult<UserRequestDto> waitingResult = currentRequest.getRequest();

        if (currentRequest.getDishes().size() > 0 || currentRequest.getMessages().size() > 0 || currentRequest.getOrders().size() > 0) {

            currentRequest.setLastCheck(LocalDateTime.now());
            waitingResult.setResult(new UserRequestDto(currentRequest));
            waitingResult = null;

            clearData(currentRequest);
        }
        currentRequest.setRequest(waitingResult);
    }

    private void setMoreRecentData(UserRequest newRequest) {
        DeferredResult<UserRequestDto> waitingResult = newRequest.getRequest();
        int userId = newRequest.getUserId();
        LocalDateTime lastCheck = newRequest.getLastCheck();

        List<Order> newOrders = orderService.findMoreRecent(lastCheck, newRequest.getRestaurantId());
        List<Message> newMessages = chatService.findMoreRecentMessages(userId, lastCheck);
        newRequest.setLastCheck(LocalDateTime.now());

        if(newMessages.size() > 0 || newOrders.size() > 0) {
            newRequest.setMessages(newMessages);
            newRequest.setOrders(newOrders);

            waitingResult.setResult(new UserRequestDto(newRequest));
            waitingResult = null;
        }
        newRequest.setRequest(waitingResult);
    }

    public void addRequest(UserRequest request){
        int userId = request.getUserId();
        userRequests.put(userId, request);

        Cache<Integer, UserRequest> restaurantRequests = restaurants.get(request.getRestaurantId());
        if(restaurantRequests == null){
            restaurantRequests = CacheBuilder.newBuilder()
                    .expireAfterWrite(15, TimeUnit.MINUTES).build();
            restaurants.put(request.getRestaurantId(), restaurantRequests);
        }
        restaurantRequests.put(userId, request);
    }

    public Dish addDish(int orderId, int dishId, int userId, int restaurantId){
        Dish dish = orderService.update(orderId, dishId, userId);

        new Thread(() -> checkRestaurants(dish, restaurantId, userId)).start();

        return dish;
    }

    public Order addOrder(Order order, int restaurantId, int userId){
        Order updatedOrder = orderService.create(order, restaurantId, userId);

        new Thread(() -> checkRestaurants(updatedOrder, restaurantId, userId)).start();

        return updatedOrder;
    }

    public MessageDto addMessage(MessageDto messageDto){
        MessageDto message = chatService.addNewMessage(messageDto);

        new Thread(() -> checkUsers(message, message.getReceiverId())).start();

        return message;
    }

    public void checkUsers(MessageDto message, int userId){
        UserRequest userRequest = userRequests.getIfPresent(userId);
        if(userRequest != null) {
            try {
                userRequest.getLock().lock();
                if(userRequest.getRequest() != null && !userRequest.getRequest().isSetOrExpired()) {
                    userRequest.getMessages().add(message);
                    userRequest.setLastCheck(LocalDateTime.now());
                    userRequest.getRequest().setResult(new UserRequestDto(userRequest));
                    userRequest.setRequest(null);

                    clearData(userRequest);
                }
            }finally {
                userRequest.getLock().unlock();
            }
        }
    }

    public void checkRestaurants(Object obj, int restaurantId, int addedBy){
        Cache<Integer, UserRequest> userRequests = restaurants.get(restaurantId);

        if(userRequests != null){
            for (UserRequest userRequest : userRequests.asMap().values()) {
                try {
                    userRequest.getLock().lock();
                    if(userRequest.getRequest() != null && !userRequest.getRequest().isSetOrExpired() && userRequest.getUserId() != addedBy) {
                        if (obj.getClass() == Order.class) {
                            Order order = (Order) obj;
                            userRequest.getOrders().add(order);
                        } else {
                            userRequest.getDishes().add((Dish) obj);
                        }
                        userRequest.setLastCheck(LocalDateTime.now());
                        userRequest.getRequest().setResult(new UserRequestDto(userRequest));
                        userRequest.setRequest(null);

                        clearData(userRequest);
                    }
                }finally {
                    userRequest.getLock().unlock();
                }
            }
        }
    }

    public void checkMessages(Message message){
        UserRequest userRequest = userRequests.getIfPresent(message.getReceiver().getId());
        if(userRequest != null && userRequest.getRequest() != null && !userRequest.getRequest().isSetOrExpired()){
            userRequest.getMessages().add(message);
            userRequest.setLastCheck(LocalDateTime.now());
            userRequest.getRequest().setResult(new UserRequestDto(userRequest));
            userRequest.setRequest(null);

            clearData(userRequest);
        }
    }

    public void clearData(UserRequest userRequest){
        userRequest.getOrders().clear();
        userRequest.getDishes().clear();
        userRequest.getMessages().clear();
    }
}
