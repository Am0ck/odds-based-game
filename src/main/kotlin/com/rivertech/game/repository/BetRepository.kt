package com.rivertech.game.repository

import com.rivertech.game.entity.Bet
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import java.util.*

interface BetRepository : R2dbcRepository<Bet, UUID> {
    fun findByPlayerId(playerId: UUID): Flux<Bet>
}