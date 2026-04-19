package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.StockLedgerModels.StockLedgerEntry;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Date;
import java.util.List;

public class StockLedgerSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public StockLedgerSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createLedgerEntry(StockLedgerEntry entry) {
        jdbcOperations.update(
                """
                INSERT INTO stock_ledger_entries
                (transaction_id, transaction_type, item_name, quantity, unit, debit_account, credit_account,
                 entry_date, reference_number, total_debit, total_credit, balance_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, entry.transactionId());
                    statement.setString(2, entry.transactionType());
                    statement.setString(3, entry.itemName());
                    statement.setInt(4, entry.quantity());
                    statement.setString(5, entry.unit());
                    statement.setString(6, entry.debitAccount());
                    statement.setString(7, entry.creditAccount());
                    statement.setDate(8, Date.valueOf(entry.entryDate()));
                    statement.setString(9, entry.referenceNumber());
                    statement.setBigDecimal(10, entry.totalDebit());
                    statement.setBigDecimal(11, entry.totalCredit());
                    statement.setString(12, entry.balanceStatus());
                });
    }

    public List<StockLedgerEntry> listLedgerEntries() {
        return jdbcOperations.query(
                "SELECT * FROM stock_ledger_entries",
                resultSet -> new StockLedgerEntry(
                        resultSet.getInt("ledger_id"),
                        resultSet.getString("transaction_id"),
                        resultSet.getString("transaction_type"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("unit"),
                        resultSet.getString("debit_account"),
                        resultSet.getString("credit_account"),
                        resultSet.getDate("entry_date").toLocalDate(),
                        resultSet.getString("reference_number"),
                        resultSet.getBigDecimal("total_debit"),
                        resultSet.getBigDecimal("total_credit"),
                        resultSet.getString("balance_status")));
    }
}
