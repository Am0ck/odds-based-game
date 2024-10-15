package com.rivertech.game.entity

import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.relational.core.mapping.Column

@Table("bets")
data class Bet(
    @Id
    val id: UUID? = null,

    @Column("player_id")
    val playerId: UUID,

    val betAmount: Double,
    val betNumber: Int,
    val randomNumber: Int,
    val result: String,
    val winnings: Double = 0.0,

    val timestamp: LocalDateTime = LocalDateTime.now()
)
