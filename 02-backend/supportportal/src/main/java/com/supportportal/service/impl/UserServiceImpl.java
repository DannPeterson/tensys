package com.supportportal.service.impl;

import com.supportportal.domain.Source;
import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.enumeration.Role;
import com.supportportal.exception.domain.*;
import com.supportportal.repository.UserRepository;
import com.supportportal.service.EmailService;
import com.supportportal.service.LoginAttemptService;
import com.supportportal.service.SourceService;
import com.supportportal.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.supportportal.constant.FileConstant.DEFAULT_USER_IMAGE_PATH;
import static com.supportportal.constant.FileConstant.USER_FOLDER;
import static com.supportportal.constant.UserImplConstant.*;
import static com.supportportal.enumeration.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private SourceService sourceService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService,
                           SourceService sourceService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.sourceService = sourceService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email, String language) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, username, email, language);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setLanguage(language);
        user.setJoinDate(new Date());
        user.setPaidUntil(java.sql.Date.valueOf(LocalDate.now().plusDays(14)));
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setSources(sourceService.getAll());
        userRepository.save(user);
        emailService.sendRegistrationEmail(firstName, username, password, email);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String language, String role, boolean isNonLocked, boolean isActive, List<Long> sourceIds) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User currentUser = userRepository.findUserByUsername(currentUsername);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setLanguage(language);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        List<Source> sources = new ArrayList<>();
        for(Long id : sourceIds) {
            sources.add(sourceService.findSourceById(id));
        }
        currentUser.setSources(sources);
        userRepository.save(currentUser);
        return currentUser;
    }

    @Override
    public void resetPassword(String email, String language) throws MessagingException, EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(noUserFoundByEmailConst(language) + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), user.getUsername(), password, user.getEmail());
    }

    @Override
    public void subscriptionRequest(String email, String plan, String language) throws MessagingException, EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            throw new EmailNotFoundException(noUserFoundByEmailConst(language) + email);
        }
        emailService.sendSubscriptionRequest(email, plan);
    }

    @Override
    public void sendMessage(String email, String message) throws MessagingException, EmailNotFoundException {
        emailService.sendMessage(email, message);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void deleteUser(String username) throws IOException {
        User user = userRepository.findUserByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail, String language) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(noUserFoundByUsernameConst(language) + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(usernameAlreadyExistsConst(language));
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(emailAlreadyExistsConst(language));
            }
            return currentUser;
        } else {
            if(userByNewUsername != null) {
                throw new UsernameExistException(usernameAlreadyExistsConst(language));
            }
            if(userByNewEmail != null) {
                throw new EmailExistException(emailAlreadyExistsConst(language));
            }
            return null;
        }
    }

    private String noUserFoundByEmailConst(String language){
        switch (language) {
            case "et": return NO_USER_FOUND_BY_EMAIL_ET;
            case "lt": return NO_USER_FOUND_BY_EMAIL_LT;
            case "lv": return NO_USER_FOUND_BY_EMAIL_LV;
            case "ru": return NO_USER_FOUND_BY_EMAIL_RU;
            default: return NO_USER_FOUND_BY_EMAIL_EN;
        }
    }

    private String noUserFoundByUsernameConst(String language){
        switch (language) {
            case "et": return NO_USER_FOUND_BY_USERNAME_ET;
            case "lt": return NO_USER_FOUND_BY_USERNAME_LT;
            case "lv": return NO_USER_FOUND_BY_USERNAME_LV;
            case "ru": return NO_USER_FOUND_BY_USERNAME_RU;
            default: return NO_USER_FOUND_BY_USERNAME_EN;
        }
    }

    private String usernameAlreadyExistsConst(String language){
        switch (language) {
            case "et": return USERNAME_ALREADY_EXISTS_ET;
            case "lt": return USERNAME_ALREADY_EXISTS_LT;
            case "lv": return USERNAME_ALREADY_EXISTS_LV;
            case "ru": return USERNAME_ALREADY_EXISTS_RU;
            default: return USERNAME_ALREADY_EXISTS_EN;
        }
    }

    private String emailAlreadyExistsConst(String language){
        switch (language) {
            case "et": return EMAIL_ALREADY_EXISTS_ET;
            case "lt": return EMAIL_ALREADY_EXISTS_LT;
            case "lv": return EMAIL_ALREADY_EXISTS_LV;
            case "ru": return EMAIL_ALREADY_EXISTS_RU;
            default: return EMAIL_ALREADY_EXISTS_EN;
        }
    }
}