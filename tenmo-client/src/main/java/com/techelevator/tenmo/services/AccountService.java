package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/accounts";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;
    private User currentUser = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Account getCurrentBalance(int accountId){
        Account account = null;
        try{
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "/" + accountId, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();

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
