package com.rivertech.game.repository

import com.rivertech.game.entity.WalletTransaction
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import java.util.*

interface WalletTransactionRepository : R2dbcRepository<WalletTransaction, UUID> {
    fun findByPlayerId(playerId: UUID): Flux<WalletTransaction>
}