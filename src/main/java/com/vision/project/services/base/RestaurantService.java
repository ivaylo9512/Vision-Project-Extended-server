package com.vision.project.services.base;

import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.RestaurantSpec;

public interface RestaurantService {
    Restaurant findById(int id, UserModel loggedUser);

    Restaurant findByToken(String token);

    Restaurant create(RestaurantSpec restaurantSpec);

    boolean delete(int id, UserModel loggedUser);
}
