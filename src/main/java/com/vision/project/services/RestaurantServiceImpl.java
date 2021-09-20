package com.vision.project.services;

import com.vision.project.exceptions.InvalidRestaurantTokenException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.RestaurantSpec;
import com.vision.project.repositories.base.RestaurantRepository;
import com.vision.project.services.base.RestaurantService;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Restaurant findById(int id, UserModel loggedUser){
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Restaurant not found."));
    }

    @Override
    public Restaurant findByToken(String token){
        return restaurantRepository.findByToken(token).orElseThrow(() ->
                new InvalidRestaurantTokenException("Restaurant token is invalid."));
    }

    @Override
    public Restaurant create(Restaurant restaurant){
        restaurant.getMenu().forEach(menu -> menu.setRestaurant(restaurant));

        restaurantRepository.save(restaurant);
        restaurant.setToken(UUID.randomUUID().toString() + restaurant.getId());
        restaurantRepository.save(restaurant);

        System.out.println(restaurant.getToken());

        return restaurant;
    }

    @Override
    public void delete(int id, UserModel loggedUser){
        if(id != loggedUser.getRestaurant().getId() &&
                !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("You are not authorized to delete this restaurant.");
        }

        restaurantRepository.deleteById(id);
    }
}
