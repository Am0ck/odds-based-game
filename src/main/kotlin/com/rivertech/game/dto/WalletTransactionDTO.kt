package com.rivertech.game.dto

import com.rivertech.game.entity.TransactionType
import java.time.LocalDateTime
import java.util.UUID

data class WalletTransactionDTO(
    val id: UUID? = null,
    val playerId: UUID,    // Reference to the player
    val transactionType: TransactionType,  // "DEBIT" or "CREDIT"
    val amount: Double,
    val timestamp: LocalDateTime = LocalDateTime.now()  // Automatically set to the current time when creating
)