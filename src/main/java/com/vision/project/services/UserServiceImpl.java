package com.vision.project.services;

import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.RegisterSpec;
import com.vision.project.models.UserDetails;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.base.UserService;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService,UserDetailsService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserModel> findAll() { ;
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    @Override
    public UserModel create(UserModel user) {
        UserModel existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser != null) {
            throw new UsernameExistsException("Username is already taken.");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(4)));
        return userRepository.save(user);
    }

    @Override
    public UserModel save(UserModel userModel){
        return userRepository.save(userModel);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel userModel = userRepository.findByUsername(username);
        if(userModel == null){
            throw new BadCredentialsException("Bad credentials");
        }

        Restaurant restaurant = userModel.getRestaurant();
        Hibernate.initialize(restaurant.getMenu());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userModel.getRole()));

        return new UserDetails(userModel,authorities);
    }

    @Override
    public UserModel changeUserInfo(int loggedUser, RegisterSpec registerSpec){
        UserModel user = userRepository.findById(loggedUser)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        user.setFirstName(registerSpec.getFirstName());
        user.setLastName(registerSpec.getLastName());
        user.setAge(registerSpec.getAge());
        user.setCountry(registerSpec.getCountry());

        return userRepository.save(user);
    }
}
