package com.rivertech.game.repository

import com.rivertech.game.dto.LeaderboardDTO
import com.rivertech.game.entity.Player
import org.springframework.data.r2dbc.repository.Query
//import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

//interface PlayerRepository : JpaRepository<Player?, UUID?>
interface PlayerRepository : R2dbcRepository<Player, UUID> {
    @Query("""
        SELECT p.id, p.username, p.name, p.surname, SUM(wt.amount) AS total_winnings
        FROM players p
        JOIN wallet_transactions wt ON p.id = wt.player_id
        WHERE wt.transaction_type = 'CREDIT'
        GROUP BY p.id, p.username, p.name, p.surname
        ORDER BY total_winnings DESC
        LIMIT :limit
    """)
    fun findTopPlayersByWinnings(limit: Int): Flux<LeaderboardDTO>

    fun findByUsername(username: String): Mono<Player>
}