package com.supportportal.service;

import com.supportportal.domain.Source;
import com.supportportal.domain.User;
import com.supportportal.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email, String language) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String language, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage, List<Long> sourceIds) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email, String language) throws MessagingException, EmailNotFoundException;

    void subscriptionRequest(String email, String plan, String language) throws MessagingException, EmailNotFoundException;

    void sendMessage(String email, String message) throws MessagingException, EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;
}
