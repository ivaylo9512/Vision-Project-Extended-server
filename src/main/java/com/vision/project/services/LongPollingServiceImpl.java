package com.vision.project.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.UserRequestDto;
import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.LongPollingService;
import com.vision.project.services.base.OrderService;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LongPollingServiceImpl implements LongPollingService {
    private Cache<Integer, UserRequest> userRequests = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();
    Map<Integer, Cache<UserRequest, UserRequest>> restaurants = new HashMap<>();

    private final OrderService orderService;
    private final ChatService chatService;

    public LongPollingServiceImpl(OrderService orderService, ChatService chatService ) {
        this.orderService = orderService;
        this.chatService = chatService;
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
            newRequest.setOrders(newOrders.stream().collect(Collectors.toMap(Order::getId, order -> order)));

            waitingResult.setResult(new UserRequestDto(newRequest));
            waitingResult = null;
        }
        newRequest.setRequest(waitingResult);
    }

    public void addRequest(UserRequest request){
        int userId = request.getUserId();
        userRequests.put(userId, request);

        Cache<UserRequest, UserRequest> userRequests = restaurants.get(request.getRestaurantId());
        if(userRequests == null){
            userRequests = CacheBuilder.newBuilder()
                    .expireAfterWrite(15, TimeUnit.MINUTES).build();
            restaurants.put(request.getRestaurantId(), userRequests);
        }
        userRequests.put(request, request);
    }

    public void checkRestaurants(Object obj, int restaurantId){
        Cache<UserRequest, UserRequest> userRequests = restaurants.get(restaurantId);

        if(userRequests != null){
            for (UserRequest userRequest : userRequests.asMap().keySet()) {
                try {
                    userRequest.getLock().lock();
                    if(userRequest.getRequest() != null && !userRequest.getRequest().isSetOrExpired()) {
                        if (obj.getClass() == Order.class) {
                            Order order = (Order) obj;
                            userRequest.getOrders().put(order.getId(), order);
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
        UserRequest userRequest = userRequests.getIfPresent(message.getReceiverId());
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
