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
import org.springframework.security.core.authority.AuthorityUtils;
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
    public UserModel findById(int id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserModel not found."));

        if(!user.isEnabled()){
            throw new UnauthorizedException("User is unavailable.");
        }

        return user;
    }

    @Override
    public UserModel getById(int id){
        return userRepository.getById(id);
    }

    @Override
    public UserModel create(UserModel user) {
        userRepository.findFirstByUsernameOrEmail(user.getUsername(), user.getEmail()).ifPresent(existingUser -> {
            if(existingUser.getUsername().equals(user.getUsername())){
                throw new UsernameExistsException("{ \"username\": \"Username is already taken.\" }");
            }
            throw new EmailExistsException("{ \"email\": \"Email is already taken.\" }");
        });

        user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(4)));
        return userRepository.save(user);
    }

    @Override
    public UserModel save(UserModel userModel){
        return userRepository.save(userModel);
    }

    @Override
    public void delete(int id, UserDetails loggedUser) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserModel not found."));

        if(id != loggedUser.getId() &&
                !AuthorityUtils.authorityListToSet(loggedUser.getAuthorities()).contains("ROLE_ADMIN")){
            throw new UnauthorizedException("You are not allowed to modify the user.");
        }

        userRepository.delete(user);
    }

    @Override
    public void delete(UserModel user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadCredentialsException("Bad credentials."));

        if(!user.isEnabled()){
            throw new DisabledUserException("You must complete the registration. Check your email.");
        }

        Restaurant restaurant = user.getRestaurant();
        Hibernate.initialize(restaurant.getMenu());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new UserDetails(user, authorities);
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
    public UserModel changeUserInfo(UserSpec userSpec, UserModel user){
        String newUsername = user.getUsername().equals(userSpec.getUsername()) ? null : userSpec.getUsername();
        String newEmail = user.getEmail().equals(userSpec.getEmail()) ? null : userSpec.getEmail();

        if(newUsername != null || newEmail != null){
            userRepository.findFirstByUsernameOrEmail(newUsername, newEmail).ifPresent(existingUser -> {
                if(existingUser.getUsername().equals(userSpec.getUsername())){
                    throw new UsernameExistsException("{ \"username\": \"Username is already taken.\" }");
                }
                throw new EmailExistsException("{ \"email\": \"Email is already taken.\" }");
            });
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
        UserModel user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("UserModel not found."));
        user.setEnabled(state);

        userRepository.save(user);
    }
}
