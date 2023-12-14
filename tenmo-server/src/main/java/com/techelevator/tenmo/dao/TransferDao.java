package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> getTransfersByUserId(int userId);

    void sendMoney(int accountTo, int accountFrom, BigDecimal amount);
    void requestMoney(int accountTo, int accountFrom, BigDecimal amount);
    List<Transfer> getPendingTransferById(int userId);
}

