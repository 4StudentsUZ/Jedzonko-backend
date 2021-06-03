package com.students.recipesapi;

import com.students.recipesapi.entity.RecoveryToken;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.AlreadyExistsException;
import com.students.recipesapi.exception.ExpiredTokenException;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.model.UserUpdateModel;
import com.students.recipesapi.repository.RecoveryTokenRepository;
import com.students.recipesapi.repository.UserRepository;
import com.students.recipesapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "application-test")
public class UserTests {
    UserService userService;
    UserRepository userRepository;
    RecoveryTokenRepository tokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final String testEmail = "email@test.com";
    private final String testPassword = "12345678";
    private final String testUUIDToken = "1792830-345hdkfg-345ghh9g7";
    private UserEntity testUserEntity;
    private UserUpdateModel testUserUpdateModel;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(RecoveryTokenRepository.class);
        userService = spy(new UserService(userRepository, tokenRepository, passwordEncoder));
        doNothing().when(userService).sendEmail(anyString(), anyString(), anyString());
        when(userService.isAccountActivationRequired()).thenReturn(true);

        testUserEntity = new UserEntity(testEmail, "Jan", "Kowalski", testPassword);
        testUserUpdateModel = new UserUpdateModel("Piotr", "Nowak", "87654321");
    }

    @Test
    void register_InputIsCorrect_CreateNewUserEntity() {
        //given
        RegisterModel registerModel = new RegisterModel(testEmail, testPassword);
        when(userRepository.existsByUsername(registerModel.getUsername())).thenReturn(false);

        //when
        UserEntity result = userService.register(registerModel);

        //then
        assertThat(result.getUsername()).isEqualTo(registerModel.getUsername());
        assertThat(passwordEncoder.matches(registerModel.getPassword(), result.getPassword())).isTrue();
    }

    @Test
    void register_EmailIsNull_ThrowInvalidInputException() {
        //given
        RegisterModel registerModel = new RegisterModel(null, testPassword);
        when(userRepository.existsByUsername(registerModel.getUsername())).thenReturn(false);

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.register(registerModel)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Username was not provided.");
    }

    @Test
    void register_PasswordIsNull_ThrowInvalidInputException() {
        //given
        RegisterModel registerModel = new RegisterModel(testEmail, null);
        when(userRepository.existsByUsername(registerModel.getUsername())).thenReturn(false);

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.register(registerModel)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Password was not provided.");
    }

    @Test
    void register_PasswordIsTooShort_ThrowInvalidInputException() {
        //given
        RegisterModel registerModel = new RegisterModel(testEmail, "1234567");
        when(userRepository.existsByUsername(registerModel.getUsername())).thenReturn(false);

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.register(registerModel)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Provided password is too short.");
    }

    @Test
    void register_UserAlreadyExists_ThrowAlreadyExistsException() {
        //given
        RegisterModel registerModel = new RegisterModel(testEmail, testPassword);
        when(userRepository.existsByUsername(registerModel.getUsername())).thenReturn(true);

        //when
        AlreadyExistsException result = assertThrows(
                AlreadyExistsException.class,
                () -> userService.register(registerModel)
        );

        //then
        assertThat(result.getMessage()).isEqualTo(String.format("User with e-mail \"%s\" already exists.", registerModel.getUsername()));
    }

    @Test
    void activate_UserJustRegistered_UserEntityIsDisabled() {
        //given
        RegisterModel registerModel = new RegisterModel(testEmail, testPassword);

        //when
        UserEntity userEntity = userService.register(registerModel);

        //then
        assertThat(userEntity.isEnabled()).isEqualTo(false);
    }

    @Test
    void activate_TokenIsCorrect_ActivateTheAccount() {
        //given
        UserEntity userEntity = new UserEntity(testEmail, "Jan", "Kowalski", testPassword);
        RecoveryToken registrationToken = new RecoveryToken(0L, testUUIDToken, userEntity, LocalDateTime.now(ZoneId.of("Europe/Warsaw")).plusDays(1));
        userEntity.setActivationToken(registrationToken.getToken());
        when(tokenRepository.findRecoveryTokenByToken(testUUIDToken)).thenReturn(Optional.of(registrationToken));

        //when
        userService.activate(userEntity.getActivationToken());

        //then
        assertThat(userEntity.isEnabled()).isEqualTo(true);
    }

    @Test
    void activate_TokenHasExpired_ThrowExpiredTokenException() {
        //given
        testUserEntity.setActivationToken(testUUIDToken);
        when(tokenRepository.findRecoveryTokenByToken(testUUIDToken)).thenReturn(Optional.empty());

        //when
        ExpiredTokenException result = assertThrows(
                ExpiredTokenException.class,
                () -> userService.activate(testUserEntity.getActivationToken())
        );

        //then
        assertThat(result.getMessage()).isEqualTo("This activation link has expired or never existed, please create a new account.");
    }

    @Test
    void activate_TokenIsNull_ThrowExpiredTokenException() {
        //given
        testUserEntity.setActivationToken(testUUIDToken);
        when(tokenRepository.findRecoveryTokenByToken(testUUIDToken)).thenReturn(Optional.empty());

        //when
        ExpiredTokenException result = assertThrows(
                ExpiredTokenException.class,
                () -> userService.activate(null)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("This activation link has expired or never existed, please create a new account.");
    }

    @Test
    void findById_IdExists_ReturnUserEntity() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUserEntity));

        //when
        UserEntity result = userService.findById(0L);

        //then
        assertThat(result).isEqualTo(testUserEntity);
    }

    @Test
    void findById_IdDoesntExist_ThrowNotFoundException() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        NotFoundException result = assertThrows(
                NotFoundException.class,
                () -> userService.findById(0L)
        );

        //then
        assertThat(result.getMessage()).isEqualTo(String.format("User with id %d not found.", 0L));
    }

    @Test
    void findByUsername_UsernameExists_ReturnUserEntity() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUserEntity));

        //when
        UserEntity result = userService.findByUsername(testUserEntity.getUsername());

        //then
        assertThat(result).isEqualTo(testUserEntity);
    }

    @Test
    void findByUsername_UsernameDoesntExist_ThrowNotFoundException() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when
        NotFoundException result = assertThrows(
                NotFoundException.class,
                () -> userService.findByUsername(testUserEntity.getUsername())
        );

        //then
        assertThat(result.getMessage()).isEqualTo(String.format("User with username \"%s\" not found.", testUserEntity.getUsername()));
    }

    @Test
    void findByUsername_UsernameIsNull_ThrowNotFoundException() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when
        NotFoundException result = assertThrows(
                NotFoundException.class,
                () -> userService.findByUsername(testEmail)
        );

        //then
        assertThat(result.getMessage()).isEqualTo(String.format("User with username \"%s\" not found.", testUserEntity.getUsername()));
    }

    @Test
    void update_UserDoesntExist_ThrowNotFoundException() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when
        NotFoundException result = assertThrows(
                NotFoundException.class,
                () -> userService.update(testEmail, testUserUpdateModel)
        );

        //then
        assertThat(result.getMessage()).isEqualTo(String.format("User with username \"%s\" not found.", testEmail));
    }

    @Test
    void update_UsernameIsNull_ThrowInvalidInputException() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUserEntity));

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.update(null, testUserUpdateModel)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Tried to update user without a username.");
    }

    @Test
    void update_UserUpdateModelIsNull_ThrowInvalidInputException() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUserEntity));

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.update(testEmail, null)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("An update model has to be provided.");
    }

    @Test
    void update_UserUpdateModelHasNullFields_DoesntThrow() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUserEntity));

        //then
        assertThatCode(() -> userService.update(testEmail, new UserUpdateModel(null, null, null)))
            .doesNotThrowAnyException();
    }

    @Test
    void update_UserUpdateModelHasAllFields_UpdateUserEntity() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUserEntity));

        //when
        userService.update(testEmail, testUserUpdateModel);

        //then
        assertThat(testUserEntity.getFirstName()).isEqualTo(testUserUpdateModel.getFirstName());
        assertThat(testUserEntity.getLastName()).isEqualTo(testUserUpdateModel.getLastName());
        assertThat(passwordEncoder.matches(testUserUpdateModel.getPassword(), testUserEntity.getPassword())).isTrue();
    }

    @Test
    void delete_UsernameIsNull_ThrowNotFoundException() {
        //given

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.delete(null)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Username was not provided.");
    }

    @Test
    void delete_UserWithUsernameDoesntExist_ThrowNotFoundException() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when
        NotFoundException result = assertThrows(
                NotFoundException.class,
                () -> userService.delete(testEmail)
        );

        //then
        assertThat(result.getMessage()).isEqualTo(String.format("User with username \"%s\" not found.", testEmail));
    }

    @Test
    void delete_UserExists_UserEntityShouldBeUpdated() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUserEntity));

        //when
        userService.delete(testEmail);

        //then
        assertThat(testUserEntity.getUsername()).isEqualTo("");
        assertThat(testUserEntity.getFirstName()).isEqualTo("Account");
        assertThat(testUserEntity.getLastName()).isEqualTo("Removed");
        assertThat(testUserEntity.getPassword()).isEqualTo("");
        assertThat(testUserEntity.isEnabled()).isEqualTo(false);
    }

    @Test
    void validatePassword_PasswordIsNull_InvalidInputException() {
        //given

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.validatePassword(null)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Password was not provided.");
    }

    @Test
    void validatePassword_PasswordIsTooShort_InvalidInputException() {
        //given

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.validatePassword("123456")
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Provided password is too short.");
    }

    @Test
    void validatePassword_PasswordIsCorrect_DoesntThrow() {
        assertThatCode(() -> userService.validatePassword(testPassword)).doesNotThrowAnyException();
    }

    @Test
    void validateUsername_UsernameIsNull_InvalidInputException() {
        //given

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.validateUsername(null)
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Username was not provided.");
    }

    @Test
    void validateUsername_UsernameIsTooShort_InvalidInputException() {
        //given

        //when
        InvalidInputException result = assertThrows(
                InvalidInputException.class,
                () -> userService.validateUsername("ab")
        );

        //then
        assertThat(result.getMessage()).isEqualTo("Provided username is too short.");
    }

    @Test
    void validateUsername_UsernameIsCorrect_DoesntThrow() {
        assertThatCode(() -> userService.validateUsername(testEmail)).doesNotThrowAnyException();
    }
}
