package com.rivertech.game.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("wallet_transactions")
data class WalletTransaction(
    @Id
    val id: UUID? = null,

    val playerId: UUID,  // Store playerId directly to represent the relationship to Player

    val transactionType: TransactionType,  // "DEBIT" or "CREDIT"
    val amount: Double,
    val timestamp: LocalDateTime = LocalDateTime.now()  // Automatically set the timestamp
)
