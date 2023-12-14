package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    private AccountDao accountDao;
    private UserDao userDao;
    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }
    /**
     * return a list of transfers of the account
     */
    @RequestMapping(path = "/transfers/{userId}", method = RequestMethod.GET)
    public List<Transfer> listOfTransfersByUser(@PathVariable int userId) {
        try {
            List<Transfer> transfers = transferDao.getTransfersByUserId(userId);
            if (transfers == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transfer not found.");
            } else {
                return transfers;
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.");
        }
    }
    /**
     * return a list of pending transfers of the account
     */
    @RequestMapping(path = "/transfers/pending/{userId}", method = RequestMethod.GET)
    public List<Transfer> listOfPendingTransfers(@PathVariable int userId) {
        try {
            List<Transfer> transfers = transferDao.getPendingTransferById(userId);
            if (transfers == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transfer not found.");
            } else {
                return transfers;
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.");
        }
    }

    /**
     * Update the balance of sender and receiver
     */
    @RequestMapping(path = "/transfers/send", method = RequestMethod.POST)
    public ResponseEntity<String> sendMoney(@RequestBody TransferDto transferDto) {
        try {
            transferDao.sendMoney(transferDto.getUserIdTo(), transferDto.getUserIdFrom(), transferDto.getAmount());
            return new ResponseEntity<>("Money sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send money: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a request money method POST
     */
    @RequestMapping(path = "/transfers/request", method = RequestMethod.POST)
    public ResponseEntity<String> requestMoney(@RequestBody TransferDto transferDto) {
        try {
            transferDao.requestMoney(transferDto.getUserIdTo(), transferDto.getUserIdFrom(), transferDto.getAmount());
            return new ResponseEntity<>("Money Requested successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to Request money: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updating pending requests PUT
     * Update balance and update transfer table (pending -> approved)
     */
}
