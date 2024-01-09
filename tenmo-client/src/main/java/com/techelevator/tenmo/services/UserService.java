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

    public static final String API_BASE_URL = "http://localhost:8080/users";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;
    private User currentUser = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Integer getAccountId(int userId) {
        Integer accountId = null;
        try{
            ResponseEntity<Integer> response = restTemplate.exchange(API_BASE_URL + "/" + userId + "/account", HttpMethod.GET, makeAuthEntity(), Integer.class);
            accountId = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accountId;
    }

    public User[] getUsers() {
        User[] users = null;
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(), User[].class);
            users = response.getBody();
        }  catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
