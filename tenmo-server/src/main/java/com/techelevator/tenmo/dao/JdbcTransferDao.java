package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransactionDto;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

    @Override
    public List<TransactionDto> findAllTransfers(int id) {
        List<TransactionDto> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer AS t " +
                "JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE ts.transfer_status_desc != 'Rejected' AND (t.account_from = ? OR t.account_to = ?) " +
                "ORDER BY t.transfer_id";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);

            transfers.add(createTransactionDtoFromTransfer(transfer));
        }
        return transfers;
    }

    @Override
    public List<TransactionDto> getPendingTransfers(String transferStatus, int id) {
        List<TransactionDto> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer AS t " +
                "JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE ts.transfer_status_desc = ? AND t.account_from = ? " +
                "ORDER BY t.transfer_id";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferStatus, id);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);

            transfers.add(createTransactionDtoFromTransfer(transfer));
        }
        return transfers;
    }

    @Override
    public TransactionDto getTransferById(int id) {
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer AS t " +
                "JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE t.transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            return createTransactionDtoFromTransfer(mapRowToTransfer(results));
        } else {
            return null;
        }
    }

    @Override
    public void createTransfer(Transfer transfer) {

        String sql = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES ((SELECT transfer_type_id FROM transfer_type WHERE transfer_type_desc = ?), " +
                "(SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = ?), ?, ?, ?)";

        jdbcTemplate.update(sql, transfer.getTransferType(), transfer.getTransferStatus(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

        // completeTransfer
        // subtract amount from accountFrom.Balance
        // add amount to accountTo.Balance
    }

    @Override
    public void updateTransfer(int id, String transferStatus) {

        String sql = "UPDATE transfer " +
                "SET transfer_status_id = (SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = ?) " +
                "WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferStatus, id);
    }

    private TransactionDto createTransactionDtoFromTransfer(Transfer transfer) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransferId(transfer.getTransferId());
        transactionDto.setTransferType(transfer.getTransferType());
        transactionDto.setTransferStatus(transfer.getTransferStatus());
        transactionDto.setUserFrom(userDao.findUsernameByAccountId(transfer.getAccountFrom()));
        transactionDto.setUserTo(userDao.findUsernameByAccountId(transfer.getAccountTo()));
        transactionDto.setAmount(transfer.getAmount());
        return transactionDto;
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferType(rs.getString("transfer_type_desc"));
        transfer.setTransferStatus(rs.getString("transfer_status_desc"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
