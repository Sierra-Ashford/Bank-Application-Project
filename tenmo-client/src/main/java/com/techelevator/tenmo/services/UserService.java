package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class UserService {
    public String API_BASE_URL = "http://localhost:8080/users";

    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;
    private User currentUser = null;
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * List all the users
     */
    public User[] listUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity entity = new HttpEntity(headers);
        User[] users = null;
        try {
            users = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, entity, User[].class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }
    /**
     * Get User by id
     */
    public User getUserById(int id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity entity = new HttpEntity(headers);
        User user = null;
        try {
            ResponseEntity<User> responseEntity
                    = restTemplate.exchange(API_BASE_URL + "/" + id, HttpMethod.GET, entity, User.class);
            user = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;

    }
}
