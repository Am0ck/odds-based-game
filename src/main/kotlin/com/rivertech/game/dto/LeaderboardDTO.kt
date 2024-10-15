package com.rivertech.game.dto

import java.util.*

data class LeaderboardDTO(
    val playerId: UUID? = null,
    val username: String,
    val name: String,
    val surname: String,
    val totalWinnings: Double
)
