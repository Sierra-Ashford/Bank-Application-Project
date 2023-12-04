package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }
    /**
     * return a list of transfers
     *
     */
    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> listOfTransfers() {
        return transferDao.getAllTransfers();
    }
    /**
     * return a list of pending transfers
     *
     */
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> listOfPendingTransfers(@PathVariable int user_id) {
        return transferDao.getPendingTransfers(user_id);
    }
    /**
     * Create a new transfer
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public void addTransfer(@Valid @RequestBody Transfer newTransfer) {
        try {
            transferDao.createTransfer(newTransfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer failed.");
        }
    }
    /**
     * Update a pending transfer to approved or rejected
     */
}
