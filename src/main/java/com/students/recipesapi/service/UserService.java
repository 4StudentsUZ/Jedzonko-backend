package com.students.recipesapi.service;

import com.students.recipesapi.entity.RecoveryToken;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.*;
import com.students.recipesapi.model.RecoveryModel;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.model.UserUpdateModel;
import com.students.recipesapi.repository.RecoveryTokenRepository;
import com.students.recipesapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RecoveryTokenRepository recoveryTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${SUPPORT_EMAIL:DefaultEmail}")
    private String supportEmail;

    @Value("${SUPPORT_EMAIL_PASSWORD:DefaultPassword}")
    private String supportEmailPassword;

    @Value("${ACCOUNTS_REQUIRE_ACTIVATION:false}")
    private boolean accountsRequireActivation;

    public UserService(UserRepository userRepository, RecoveryTokenRepository recoveryTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recoveryTokenRepository = recoveryTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found.", userId)));
    }

    public UserEntity findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("User with username \"%s\" not found.", username)));
    }

    public UserEntity register(RegisterModel registerModel) {
        validateUsername(registerModel.getUsername());
        validatePassword(registerModel.getPassword());

        registerModel.setUsername(registerModel.getUsername().trim());

        if (userRepository.existsByUsername(registerModel.getUsername())) {
            throw new AlreadyExistsException(String.format("User with e-mail \"%s\" already exists.", registerModel.getUsername()));
        }
        if (registerModel.getPassword().length() < 8) {
            throw new InvalidInputException("The password provided is too short (less than 8 characters).");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerModel.getUsername());
        userEntity.setPassword(passwordEncoder.encode(registerModel.getPassword()));
        userEntity.setFirstName("");
        userEntity.setLastName("");
        userEntity.setEnabled(!isAccountActivationRequired());
        userRepository.save(userEntity);

        if (isAccountActivationRequired()) {
            RecoveryToken token = generateRegistrationToken(userEntity);
            userEntity.setActivationToken(token.getToken());
            userRepository.save(userEntity);

            String subject = "Activate your Jedzonko.pl account";
            String body = "Open this link to activate your jedzonko.pl account: ";
            body += "https://uz-recipes-rest.herokuapp.com/users/activate?token=" + token.getToken();
            sendEmail(userEntity.getUsername(), subject, body);
        }

        return userEntity;
    }

    public void activate(String token) {
        RecoveryToken recoveryToken = recoveryTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new ExpiredTokenException("This activation link has expired or never existed, please create a new account."));

        recoveryToken.getUserEntity().setEnabled(true);
        userRepository.save(recoveryToken.getUserEntity());
    }

    public UserEntity update(String username, UserUpdateModel userUpdateModel) {
        if (username == null || username.isEmpty()) {
            throw new InvalidInputException("Tried to update user without a username.");
        }

        if (userUpdateModel == null) {
            throw new InvalidInputException("An update model has to be provided.");
        }

        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (!userEntityOptional.isPresent()) {
            throw new NotFoundException(String.format("User with username \"%s\" not found.", username));
        }

        UserEntity userEntity = userEntityOptional.get();
        if (userUpdateModel.getFirstName() != null) userEntity.setFirstName(userUpdateModel.getFirstName());
        if (userUpdateModel.getLastName() != null) userEntity.setLastName(userUpdateModel.getLastName());
        if (userUpdateModel.getPassword() != null) {
            validatePassword(userUpdateModel.getPassword());
            userEntity.setPassword(passwordEncoder.encode(userUpdateModel.getPassword()));
        }
        userRepository.save(userEntity);

        return userEntity;
    }

    public void sendRecoveryToken(String username) {
        validateUsername(username);

        UserEntity userEntity = findByUsername(username);
        RecoveryToken token = generateRecoveryToken(userEntity);

        String subject = "Your recovery token for Jedzonko.pl";
        String body = String.format("Your recovery token is: %s", token.getToken());

        sendEmail(userEntity.getUsername(), subject, body);
    }

    public void resetPassword(RecoveryModel recoveryModel) {
        validateUsername(recoveryModel.getUsername());
        validatePassword(recoveryModel.getPassword());

        RecoveryToken recoveryToken = recoveryTokenRepository.findByToken(recoveryModel.getToken())
                .orElseThrow(() -> new ExpiredTokenException("Recovery token has expired or never existed."));

        if (recoveryToken.getExpirationDate().isBefore(LocalDateTime.now(ZoneId.of("Europe/Warsaw")))) {
            throw new ExpiredTokenException("Recovery token has expired or never existed.");
        }

        recoveryToken.getUserEntity().setPassword(passwordEncoder.encode(recoveryModel.getPassword()));
        userRepository.save(recoveryToken.getUserEntity());
        recoveryTokenRepository.delete(recoveryToken);
    }

    public void delete(String username) {
        validateUsername(username);

        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("User with username \"%s\" not found.", username)));

        userEntity.setUsername("");
        userEntity.setFirstName("Account");
        userEntity.setLastName("Removed");
        userEntity.setPassword("");
        userEntity.setEnabled(false);

        userRepository.save(userEntity);

        String subject = "Your Jedzonko.pl account has been deleted";
        String body = "Your Jedzonko.pl account has been deleted through the application.";
        sendEmail(username, subject, body);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        final String fromEmail = supportEmail;
        final String password = supportEmailPassword;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        try {
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("no_reply@example.com", "NoReply"));
            msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));
            msg.setSentDate(new Date());
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);
        } catch (Exception e) {
            throw new SendingEmailException("A problem occurred during sending email.\n" + e.getMessage());
        }
    }

    public void validatePassword(String password) {
        if (password == null) {
            throw new InvalidInputException("Password was not provided.");
        }
        if (password.length() < 8) {
            throw new InvalidInputException("Provided password is too short.");
        }
    }

    public void validateUsername(String username) {
        if (username == null) {
            throw new InvalidInputException("Username was not provided.");
        }
        if (username.length() < 3) {
            throw new InvalidInputException("Provided username is too short.");
        }
    }

    public RecoveryToken generateRecoveryToken(UserEntity userEntity) {
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserEntity(userEntity);
        recoveryToken.setToken(UUID.randomUUID().toString());
        recoveryToken.setExpirationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).plusMinutes(5));
        recoveryTokenRepository.save(recoveryToken);
        return recoveryToken;
    }

    public RecoveryToken generateRegistrationToken(UserEntity userEntity) {
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserEntity(userEntity);
        recoveryToken.setToken(UUID.randomUUID().toString());
        recoveryToken.setExpirationDate(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).plusDays(1));
        recoveryTokenRepository.save(recoveryToken);
        return recoveryToken;
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void removeExpiredRecoveryTokens() {
        List<RecoveryToken> list = recoveryTokenRepository.findAll();
        list = list.stream().filter(t -> t.getExpirationDate().isBefore(LocalDateTime.now(ZoneId.of("Europe/Warsaw")))).collect(Collectors.toList());
        for (RecoveryToken recoveryToken : list) {
            if (!recoveryToken.getUserEntity().isEnabled()) {
                userRepository.delete(recoveryToken.getUserEntity());
            }
            recoveryTokenRepository.delete(recoveryToken);
        }
    }

    public boolean isAccountActivationRequired() {
        return accountsRequireActivation;
    }
}
