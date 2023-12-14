package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    public String API_BASE_URL = "http://localhost:8080/transfers";

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
     * List all past transfer
     */
    public Transfer[] listOfTransfersByUser(int userId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> responseEntity =
                    restTemplate.exchange(API_BASE_URL + "/" + userId,
                            HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    /**
     * List pending transfers
     */
    public Transfer[] listPendingTransfer(int userId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> responseEntity =
                    restTemplate.exchange(API_BASE_URL + "/pending/" + userId,
                            HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    /**
     * Process a transfer with TransferDto
     */
    public boolean processTransfer(TransferDto transferDto) {
        boolean success = false;
        try {
            restTemplate.exchange(API_BASE_URL + "/" + transferDto.getTransferType(), HttpMethod.POST, makeTransferDtoEntity(transferDto), Void.class);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }



    /**
     * Creates a new HttpEntity with the `Authorization: Bearer:` header and a Transfer request body
     */
    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }
    /**
     * Creates a new HttpEntity with the `Authorization: Bearer:` header and a TransferDto request body
     */
    private HttpEntity<TransferDto> makeTransferDtoEntity(TransferDto transferDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferDto, headers);
    }

    /**
     * Returns an HttpEntity with the `Authorization: Bearer:` header
     */
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
