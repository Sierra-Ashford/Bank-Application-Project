package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    public String API_BASE_URL = "http://localhost:8080/accounts/";
    public String API_BASE_URL2 = "http://localhost:8080/accountByUserId/";
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
     * Get Account by Account_id
     */
    public Account getAccountById(int id) {
        Account account = null;
        try {
            account = restTemplate.getForObject(API_BASE_URL + id, Account.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    /**
     * Get Account by User_id
     */
    public Account getAccountByUserId(int id) {
        Account account = null;
        try {
            account = restTemplate.exchange(API_BASE_URL2 + id, HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
