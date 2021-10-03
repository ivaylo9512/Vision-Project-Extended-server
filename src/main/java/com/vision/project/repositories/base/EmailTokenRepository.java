package com.vision.project.repositories.base;

import com.vision.project.models.EmailToken;
import com.vision.project.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    EmailToken findByToken(String token);

    EmailToken findByUser(UserModel user);
}