package com.vision.project.services.base;

import com.vision.project.models.EmailToken;
import com.vision.project.models.UserModel;
import javax.mail.MessagingException;

public interface EmailTokenService {
    void createVerificationToken(UserModel user, String token);

    EmailToken getToken(String token);

    void delete(EmailToken token);

    void sendVerificationEmail(UserModel user) throws MessagingException;
}
