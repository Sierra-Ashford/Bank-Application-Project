package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransactionDto;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<TransactionDto> findAllTransfers(int id);

    List<TransactionDto> getPendingTransfers(String transferStatus, int id);

    TransactionDto getTransferById(int id);

    void createTransfer(Transfer transfer);

    void updateTransfer(int id, String transferStatus);

    // completeTransfer(Account accountFrom, Account accountTo, BigDecimal amount)

    // updateTransfer(int transferId, String transferStatus)

    // getPendingTransfers(int accountId)
}
