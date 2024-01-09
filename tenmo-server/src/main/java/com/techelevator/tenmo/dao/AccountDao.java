package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    Account getAccountById(int id);

    BigDecimal getBalance(int id);

    void updateAccountBalance(int accountId, BigDecimal amount);

    int getAccountIdByUsername(String username);
}
