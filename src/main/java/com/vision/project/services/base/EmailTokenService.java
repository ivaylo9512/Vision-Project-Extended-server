package com.vision.project.services.base;

import com.vision.project.models.EmailToken;
import com.vision.project.models.UserModel;

public interface EmailTokenService {
    void createVerificationToken(UserModel user, String token);

    EmailToken getVerificationToken(String VerificationToken);

    UserModel getUser(String verificationToken);
}
