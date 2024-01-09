package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.TransactionDto;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.TransferStatusDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {

    private final TransferDao transferDao;
    private final AccountDao accountDao;
    private final UserDao userDao;

    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<TransactionDto> getTransfers(@RequestParam(required = false) String transferStatus, Principal principal ) {
        int accountId = accountDao.getAccountIdByUsername(principal.getName());
        if (transferStatus == null) {
            return transferDao.findAllTransfers(accountId);
        } else {
            return transferDao.getPendingTransfers(transferStatus, accountId);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void addTransfer(@RequestBody TransferDto transferDto) {

        if (transferDto.getTransferType().equals("Send")){

            boolean isValidTransaction = validateTransfer(transferDto.getUserIdFrom(), transferDto.getUserIdTo(), transferDto.getAmount());
            if (isValidTransaction) {

                int accountIdFrom = userDao.findAccountIdByUserId(transferDto.getUserIdFrom());
                accountDao.updateAccountBalance(accountIdFrom, transferDto.getAmount().multiply(new BigDecimal("-1")));

                int accountIdTo = userDao.findAccountIdByUserId(transferDto.getUserIdTo());
                accountDao.updateAccountBalance(accountIdTo, transferDto.getAmount());

                transferDao.createTransfer(createTransferFromDto(transferDto));

            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds.");
            }

        } else if (transferDto.getTransferType().equals("Request")) {

            transferDao.createTransfer(createTransferFromDto(transferDto));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Transfer Type.");
        }


    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public TransactionDto getTransfer(@PathVariable int id) {
        return transferDao.getTransferById(id);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void updateTransferStatus(@RequestBody TransferStatusDto transferStatusDto, @PathVariable int id) {

        TransactionDto dto = transferDao.getTransferById(id);

        int userIdFrom = userDao.findIdByUsername(dto.getUserFrom());
        int userIdTo = userDao.findIdByUsername(dto.getUserTo());
        BigDecimal transferAmount = dto.getAmount();

        if (transferStatusDto.getTransferStatus().equals("Approved")) {

            boolean isValidTransaction = validateTransfer(userIdFrom, userIdTo, transferAmount);
            if (isValidTransaction) {

                int accountIdFrom = userDao.findAccountIdByUserId(userIdFrom);
                accountDao.updateAccountBalance(accountIdFrom, transferAmount.multiply(new BigDecimal("-1")));
                int accountIdTo = userDao.findAccountIdByUserId(userIdTo);
                accountDao.updateAccountBalance(accountIdTo, transferAmount);

                transferDao.updateTransfer(id, transferStatusDto.getTransferStatus());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds.");
            }

        } else if (transferStatusDto.getTransferStatus().equals("Rejected")) {
            transferDao.updateTransfer(id, transferStatusDto.getTransferStatus());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Transfer Status.");
        }
    }

    private boolean validateTransfer(int userIdFrom, int userIdTo, BigDecimal amountToTransfer) {
        boolean isValidTransaction = false;
        int accountIdFrom = userDao.findAccountIdByUserId(userIdFrom);
        int accountIdTo = userDao.findAccountIdByUserId(userIdTo);
        if (amountToTransfer.compareTo(accountDao.getBalance(accountIdFrom)) == 0 || amountToTransfer.compareTo(accountDao.getBalance(accountIdFrom)) == -1 ) {
            isValidTransaction = true;
        }
        return isValidTransaction;
    }

    private Transfer createTransferFromDto(TransferDto transferDto) {
        Transfer transfer = new Transfer();
        transfer.setTransferType(transferDto.getTransferType());
        if (transferDto.getTransferType().equals("Send")) {
            transfer.setTransferStatus("Approved");
        }
        if (transferDto.getTransferType().equals("Request")) {
            transfer.setTransferStatus("Pending");
        }
        transfer.setAccountFrom(userDao.findAccountIdByUserId(transferDto.getUserIdFrom()));
        transfer.setAccountTo(userDao.findAccountIdByUserId(transferDto.getUserIdTo()));
        transfer.setAmount(transferDto.getAmount());
        return transfer;
    }
}
