package com.vision.project.services.base;

import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;

public interface RestaurantService {
    Restaurant findById(int id, UserModel loggedUser);

    Restaurant findByToken(String token);

    Restaurant create(Restaurant restaurant);

    void delete(int id, UserModel loggedUser);

    Restaurant getById(int restaurantId);
}
