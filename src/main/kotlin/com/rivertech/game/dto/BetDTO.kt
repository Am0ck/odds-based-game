package com.rivertech.game.dto

import java.time.LocalDateTime
import java.util.UUID

data class BetDTO(
    val id: UUID? = null,
    val playerId: UUID,
    val betAmount: Double,
    val betNumber: Int,
    val randomNumber: Int?,
    val result: String?,
    val winnings: Double? = 0.0,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
