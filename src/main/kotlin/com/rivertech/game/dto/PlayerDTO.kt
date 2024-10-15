package com.rivertech.game.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class PlayerDTO(
    val id: UUID?,
    val username: String,
    val name: String,
    val surname: String,
    val walletBalance: Double?
)
