package com.vision.project.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.UserRequestDto;
import com.vision.project.repositories.base.MessageRepository;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.repositories.base.RestaurantRepository;
import com.vision.project.services.base.LongPollingService;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LongPollingServiceImpl implements LongPollingService {
    private Cache<Integer, UserRequest> userRequests = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();
    Cache<Integer, Cache<UserRequest, UserRequest>> restaurants = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();

    private final OrderRepository orderRepository;
    private final MessageRepository messageRepository;
    private final RestaurantRepository restaurantRepository;

    public LongPollingServiceImpl(OrderRepository orderRepository, MessageRepository messagesRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.messageRepository = messagesRepository;
        this.restaurantRepository = restaurantRepository;
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

            waitingResult.setResult(new UserRequestDto(currentRequest));
            waitingResult = null;
        }
        currentRequest.setRequest(waitingResult);
    }

    private void setMoreRecentData(UserRequest newRequest) {
        DeferredResult<UserRequestDto> waitingResult = newRequest.getRequest();
        int userId = newRequest.getUserId();
        LocalDateTime lastCheck = newRequest.getLastCheck();

        Restaurant restaurant = restaurantRepository.getOne(newRequest.getRestaurantId());
        List<Order> newOrders = orderRepository.findMoreRecent(lastCheck, restaurant);
        List<Message> newMessages = messageRepository.findMostRecentMessages(userId, lastCheck.toLocalDate(), lastCheck.toLocalTime());
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

        Cache<UserRequest, UserRequest> userRequests = restaurants.getIfPresent(request.getRestaurantId());
        if(userRequests == null){
            userRequests = CacheBuilder.newBuilder()
                    .expireAfterWrite(15, TimeUnit.MINUTES).build();
            restaurants.put(request.getRestaurantId(), userRequests);
        }
        userRequests.put(request, request);
    }

    public void checkRestaurants(Object obj, int restaurantId){
        Cache<UserRequest, UserRequest> userRequests = restaurants.getIfPresent(restaurantId);

        if(userRequests != null){
            for (UserRequest userRequest : userRequests.asMap().keySet()) {
                try {
                    userRequest.getLock().lock();
                    if(obj.getClass() == Order.class){
                        userRequest.getOrders().add((Order)obj);
                    }else {
                        userRequest.getDishes().add((Dish) obj);
                    }
                }finally {
                    userRequest.getLock().unlock();
                }
            }
        }
    }
}
