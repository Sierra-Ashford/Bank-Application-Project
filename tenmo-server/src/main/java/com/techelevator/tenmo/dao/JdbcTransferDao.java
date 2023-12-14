package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }

    /**
     * Need to get the transfers by user_id
     * @param userId use the userId to link to Account id where account_from = account_id
     * @return
     */
    @Override
    public List<Transfer> getTransfersByUserId(int userId) {
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE account_from = (SELECT account_id FROM account WHERE user_id = ?);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        List<Transfer> transfers = new ArrayList<>();

        while(results.next()) {
            transfers.add(mapResultToTransfer(results));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransferById(int userId) {
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE account_from = (SELECT account_id FROM account WHERE user_id = ?) OR " +
                "account_to = (SELECT account_id FROM account WHERE user_id = ?)" +
                "AND transfer_status_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId, 1);
        List<Transfer> transfers = new ArrayList<>();
        while(results.next()) {
            transfers.add(mapResultToTransfer(results));
        }
        return transfers;
    }
    @Override
    public void sendMoney(int accountTo, int accountFrom, BigDecimal amount) {
        Account user = accountDao.getAccountByUserId(accountFrom);
        if (accountFrom != accountTo && user.getBalance().compareTo(amount) != -1 && amount.compareTo(new BigDecimal(0)) == 1) {
            String sql = "BEGIN TRANSACTION; " +
                    "UPDATE account SET balance = balance - ? WHERE user_id = ?; " +
                    "UPDATE account SET balance = balance + ? WHERE user_id = ?; " +
                    "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                    "VALUES (2, 2, (SELECT account_id FROM account WHERE user_id = ?), " +
                    "(SELECT account_id FROM account WHERE user_id = ?), ?); " +
                    "COMMIT;";
            jdbcTemplate.update(sql, amount, accountFrom, amount, accountTo, accountFrom, accountTo, amount);
        }
    }

    @Override
    public void requestMoney(int accountTo, int accountFrom, BigDecimal amount) {
        Account user = accountDao.getAccountByUserId(accountFrom);
        if (accountFrom != accountTo && amount.compareTo(new BigDecimal(0)) == 1) {
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                    "VALUES (1, 1, (SELECT account_id FROM account WHERE user_id = ?), " +
                    "(SELECT account_id FROM account WHERE user_id = ?), ?); ";
            jdbcTemplate.update(sql, accountFrom, accountTo, amount);
        }
    }

    private Transfer mapResultToTransfer(SqlRowSet result) {
        int transferId = result.getInt("transfer_id");
        int transferTypeId = result.getInt("transfer_type_id");
        int transferStatusId = result.getInt("transfer_status_id");
        int accountFrom = result.getInt("account_from");
        int accountTo = result.getInt("account_to");
        String amountDouble = result.getString("amount");

        Transfer transfer = new Transfer(transferId, transferTypeId, transferStatusId, accountFrom, accountTo, new BigDecimal(amountDouble));
        return transfer;
    }

}
