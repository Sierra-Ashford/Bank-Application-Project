package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountById(int id) {
        String sql = "SELECT account_id, balance FROM account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            return mapRowToAccount(results);
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal getBalance(int id) {
        String sql = "SELECT balance FROM account WHERE account_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            return result.getBigDecimal("balance");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }

    @Override
    public void updateAccountBalance(int accountId, BigDecimal amount) {
        Account account = getAccountById(accountId);
        account.setBalance(account.getBalance().add(amount));

        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql, account.getBalance(), account.getAccountId());
    }

    @Override
    public int getAccountIdByUsername(String username){
        String sql = "SELECT account_id FROM account a " +
                "JOIN tenmo_user tu ON tu.user_id = a.user_id " +
                "WHERE tu.username = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if (result.next()){
            return result.getInt("account_id");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
