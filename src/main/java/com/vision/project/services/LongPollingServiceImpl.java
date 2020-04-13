package com.vision.project.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vision.project.models.Message;
import com.vision.project.models.Order;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserRequest;
import com.vision.project.repositories.base.MessageRepository;
import com.vision.project.repositories.base.OrderRepository;
import com.vision.project.services.base.LongPollingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LongPollingServiceImpl implements LongPollingService {
    private Cache<Integer, UserRequest> userRequests = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();
    private Cache<Integer, UserRequest> restaurants = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();

    private final OrderRepository orderRepository;
    private final MessageRepository messageRepository;

    public LongPollingServiceImpl(OrderRepository orderRepository, MessageRepository messagesRepository) {
        this.orderRepository = orderRepository;
        this.messageRepository = messagesRepository;
    }

    public void setOrAddRequest(UserRequest newRequest){
        int userId = newRequest.getUserId();
        int restaurantId = newRequest.getRestaurantId();
        LocalDateTime lastCheck = newRequest.getLastCheck();

        UserRequest currentRequest = userRequests.getIfPresent(userId);
        DeferredResult<UserRequestDto> waitingResult = newRequest.getRequest();
        if(currentRequest == null){
            Restaurant restaurant = restaurantRepository.getOne(restaurantId);
            
            List<Message> newMessages = messageRepository.findMostRecentMessages(userId, lastCheck.toLocalDate(), lastCheck.toLocalTime());
            List<Order> newOrders = orderRepository.findMoreRecent(lastCheck, restaurant);

            if(newMessages.size() > 0 || newOrders.size() > 0) {
                newRequest.setMessages(newMessages);
                newRequest.setOrders(newOrders);

                waitingResult.setResult(new UserRequestDto(newRequest));
                waitingResult = null;
            }
            newRequest.setRequest(waitingResult);
            userRequests.put(userId, newRequest);
            userRequests.put(userId, newRequest);

        }else {
            try {
                currentRequest.getLock().lock();
                if (currentRequest.getDishes().size() > 0 || currentRequest.getMessages().size() > 0 || currentRequest.getOrders().size() > 0) {

                    waitingResult.setResult(new UserRequestDto(currentRequest));
                    waitingResult = null;
                }
                currentRequest.setRequest(waitingResult);
                userRequests.put(userId, currentRequest);
                restaurants.put(userId, currentRequest);
            }finally {
                currentRequest.getLock().unlock();
            }
        }
    }
}
