package com.vision.project.services;

import com.vision.project.models.EmailToken;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.EmailTokenRepository;
import com.vision.project.services.base.EmailTokenService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class EmailTokenServiceImpl implements EmailTokenService {
    private final EmailTokenRepository tokenRepository;
    private final JavaMailSender javaMailSender;

    public EmailTokenServiceImpl(EmailTokenRepository tokenRepository, JavaMailSender javaMailSender) {
        this.tokenRepository = tokenRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void createVerificationToken(UserModel user, String token) {
        EmailToken myToken = new EmailToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public EmailToken getToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow(() ->
                new EntityNotFoundException("Incorrect token."));
    }

    @Override
    public void delete(EmailToken token){
        tokenRepository.delete(token);
    }

    @Override
    public void sendVerificationEmail(UserModel user) throws MessagingException {
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token);

        String subject = "Activate account.";
        String content = String.format("""
                Click the link to activate your account:\s
                <a href="%s">Activate</a>""", ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/api/users/activate/" + token);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");

        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content, true);

        javaMailSender.send(mimeMessage);
    }
}
