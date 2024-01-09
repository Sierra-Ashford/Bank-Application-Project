package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
//@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


//    public AccountController() {
//         accountDao= new JdbcAccountDao(new JdbcTemplate() );
//
//    }

    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.GET)
    public Account getUserAccount(@PathVariable int id) {
        Account accountRetrieved = accountDao.getAccountById(id);
        if (accountRetrieved == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        } else {
            return accountRetrieved;
        }
    }

    @RequestMapping(path = "/accounts/{id}/balance", method = RequestMethod.GET)
    public BigDecimal getAccountBalance(@PathVariable int id) {
        return accountDao.getBalance(id);
    }

}
