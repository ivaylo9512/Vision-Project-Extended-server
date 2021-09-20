package com.vision.project.services;

import com.vision.project.exceptions.DisabledUserException;
import com.vision.project.exceptions.EmailExistsException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.exceptions.UsernameExistsException;
import com.vision.project.models.Restaurant;
import com.vision.project.models.UserModel;
import com.vision.project.models.specs.NewPasswordSpec;
import com.vision.project.models.UserDetails;
import com.vision.project.models.specs.UserSpec;
import com.vision.project.repositories.base.UserRepository;
import com.vision.project.services.base.UserService;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
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
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if(!user.isEnabled()){
            throw new DisabledUserException("You must complete the registration. Check your email.");
        }

        return user;
    }

    @Override
    public UserModel getById(int id){
        return userRepository.getById(id);
    }

    @Override
    public UserModel create(UserModel user) {
        UserModel existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (existingUser != null) {
            if(existingUser.getUsername().equals(user.getUsername())){
                throw new UsernameExistsException("Username is already taken.");
            }
            throw new EmailExistsException("Email is already taken.");
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

        return new UserDetails(userModel, authorities);
    }

    @Override
    public UserModel changePassword(NewPasswordSpec passwordSpec, UserDetails loggedUser){
        UserModel user = this.findById(loggedUser.getId());

        if (!BCrypt.checkpw(passwordSpec.getCurrentPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid current password.");
        }

        user.setPassword(BCrypt.hashpw(passwordSpec.getNewPassword(),BCrypt.gensalt(4)));
        return userRepository.save(user);
    }

    @Override
    public UserModel changeUserInfo(UserSpec userSpec, UserModel loggedUser){
        if(userSpec.getId() != loggedUser.getId() &&
                !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized");
        }

        UserModel user = userRepository.findById(userSpec.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if(!user.getUsername().equals(userSpec.getUsername())){
            UserModel existingUser = userRepository.findByUsername(userSpec.getUsername());

            if(existingUser != null){
                throw new UsernameExistsException("Username is already taken.");
            }
        }

        user.setUsername(userSpec.getUsername());
        user.setEmail(userSpec.getEmail());
        user.setFirstName(userSpec.getFirstName());
        user.setLastName(userSpec.getLastName());
        user.setAge(userSpec.getAge());
        user.setCountry(userSpec.getCountry());

        return userRepository.save(user);
    }

    @Override
    public void setEnabled(boolean state, int id){
        UserModel user = userRepository.getById(id);
        user.setEnabled(true);

        userRepository.save(user);
    }
}
