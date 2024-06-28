package com.supportportal.resource;

import com.supportportal.domain.*;
import com.supportportal.exception.ExceptionHandling;
import com.supportportal.exception.domain.*;
import com.supportportal.service.UserService;
import com.supportportal.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.supportportal.constant.FileConstant.*;
import static com.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.supportportal.constant.UserImplConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = { "/", "/user"})
public class UserResource extends ExceptionHandling {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserResource(AuthenticationManager authenticationManager,
                        UserService userService,
                        JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getLanguage());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("language") String language,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isNonLocked") String isNonLocked,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                       @RequestParam("sourceIds") String sourceIdsAsString) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        String idString = sourceIdsAsString.replaceAll("\\[", "").replaceAll("]", "");
        List<String> idsAsString = Arrays.asList(idString.split(",").clone());
        List<Long> sourceIds = new ArrayList<>();
        for(String id : idsAsString) {
            sourceIds.add(Long.valueOf(id));
        }
        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, language, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), sourceIds);
        return new ResponseEntity<>(updatedUser, OK);
    }
    
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/subscription_until/{username}")
    public ResponseEntity<Date> getUserSubscriptionDate(@PathVariable("username") String username) {
        return new ResponseEntity<>(userService.findUserByUsername(username).getPaidUntil(), OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetpassword/{email}/{language}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email,
                                                      @PathVariable("language") String language) throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email, language);
        return response(OK, emailSentConst(language) + email);
    }

    @GetMapping("/subscription/{email}/{plan}/{language}")
    public ResponseEntity<HttpResponse> subscription(@PathVariable("email") String email,
                                                     @PathVariable("plan") String plan,
                                                     @PathVariable("language") String language) throws MessagingException, EmailNotFoundException {
        userService.subscriptionRequest(email, plan, language);
        return response(OK, subscriptionRequestSuccessConst(language));
    }

    @GetMapping("/message/{email}/{language}/{message}")
    public ResponseEntity<HttpResponse> sendMessage(@PathVariable("email") String email,
                                                    @PathVariable("language") String language,
                                                    @PathVariable("message") String message) throws MessagingException, EmailNotFoundException {
        userService.sendMessage(email, message);
        return response(OK, messageSentSuccessConst(language));
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, userDeletedSuccessConst("en"));
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private String emailSentConst(String language) {
        switch (language) {
            case "et": return EMAIL_SENT_ET;
            case "lt": return EMAIL_SENT_LT;
            case "lv": return EMAIL_SENT_LV;
            case "ru": return EMAIL_SENT_RU;
            default: return EMAIL_SENT_EN;
        }
    }

    private String subscriptionRequestSuccessConst(String language) {
        switch (language) {
            case "et": return SUBSCRIPTION_REQUEST_SUCCESSFULLY_ET;
            case "lt": return SUBSCRIPTION_REQUEST_SUCCESSFULLY_LT;
            case "lv": return SUBSCRIPTION_REQUEST_SUCCESSFULLY_LV;
            case "ru": return SUBSCRIPTION_REQUEST_SUCCESSFULLY_RU;
            default: return SUBSCRIPTION_REQUEST_SUCCESSFULLY_EN;
        }
    }

    private String userDeletedSuccessConst(String language) {
        switch (language) {
            case "et": return USER_DELETED_SUCCESSFULLY_ET;
            case "lt": return USER_DELETED_SUCCESSFULLY_LT;
            case "lv": return USER_DELETED_SUCCESSFULLY_LV;
            case "ru": return USER_DELETED_SUCCESSFULLY_RU;
            default: return USER_DELETED_SUCCESSFULLY_EN;
        }
    }

    private String messageSentSuccessConst(String language) {
        switch (language) {
            case "et": return MESSAGE_SENT_ET;
            case "lt": return MESSAGE_SENT_LT;
            case "lv": return MESSAGE_SENT_LV;
            case "ru": return MESSAGE_SENT_RU;
            default: return MESSAGE_SENT_EN;
        }
    }
}