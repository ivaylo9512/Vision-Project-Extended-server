package com.vision.project.services.base;

import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;

public interface RestaurantService {
    Restaurant findById(long id, UserModel loggedUser);

    Restaurant findByToken(String token);

    Restaurant create(Restaurant restaurant);

    void delete(long id, UserModel loggedUser);

    Restaurant getById(long restaurantId);
}
