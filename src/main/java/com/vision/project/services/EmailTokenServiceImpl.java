package com.vision.project.services;

import com.vision.project.models.EmailToken;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.EmailTokenRepository;
import com.vision.project.services.base.EmailTokenService;

public class EmailTokenServiceImpl implements EmailTokenService {
    private final EmailTokenRepository tokenRepository;

    public EmailTokenServiceImpl(EmailTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void createVerificationToken(UserModel user, String token) {
        EmailToken myToken = new EmailToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public EmailToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public UserModel getUser(String verificationToken) {
        return tokenRepository.findByToken(verificationToken).getUser();
    }
}
