package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
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
     *
     */
    @RequestMapping(path = "/transfers/{userId}", method = RequestMethod.GET)
    public List<Transfer> listOfTransfers(@PathVariable int userId) {
        return transferDao.getTransfersByUserId(userId);
    }
    /**
     * return a transfer with transfer Id
     *
     */
    @RequestMapping(path = "/transfers/transferId/{transferId}", method = RequestMethod.GET)
    public Transfer listOfTransfer(@PathVariable int transferId) {
        return transferDao.getTransfersByTransferId(transferId);
    }
    /**
     * Update the balance of sender and receiver
     */
    @RequestMapping(path = "/transfers/send/{accountTo}/{accountFrom}/{amount}", method = RequestMethod.PUT)
    public ResponseEntity<String> sendMoney(@PathVariable int accountTo, @PathVariable int accountFrom, @Valid @PathVariable BigDecimal amount) {
        try {
            transferDao.sendMoney(accountTo, accountFrom, amount);
            return new ResponseEntity<>("Money sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send money: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
