package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class StockLedgerModels {

    private StockLedgerModels() {
    }

    public record StockLedgerEntry(Integer ledgerId, String transactionId, String transactionType,
                                   String itemName, int quantity, String unit, String debitAccount,
                                   String creditAccount, LocalDate entryDate, String referenceNumber,
                                   BigDecimal totalDebit, BigDecimal totalCredit, String balanceStatus) {
    }
}
