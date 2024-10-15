package com.rivertech.game.service

import com.rivertech.game.repository.PlayerRepository
import com.rivertech.game.dto.LeaderboardDTO
import com.rivertech.game.entity.Player
import com.rivertech.game.exception.UsernameExistsException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import java.util.*

@Service
class PlayerService(private val playerRepository: PlayerRepository) {

    fun registerPlayer(player: Player): Mono<Player> {
        return playerRepository.findByUsername(player.username)
            .flatMap {
                Mono.error<Player>(UsernameExistsException("Username '${player.username}' is already taken."))
            }
            .switchIfEmpty(
                playerRepository.save(player)  // Save if no user found with the same username
            )
    }

    //    fun registerPlayer(player: Player): Mono<Player> {
//        println("ACAS: REG")
//        println(player)
//        return playerRepository.save(player)
//    }
    fun getLeaderboard(limit: Int): Flux<LeaderboardDTO> {
        return playerRepository.findTopPlayersByWinnings(limit)
    }
    fun getPlayerById(id: UUID): Mono<Player> {
        return playerRepository.findById(id)
    }

    fun getAllPlayers(): Flux<Player> {
        return playerRepository.findAll()
    }

    fun deletePlayer(id: UUID): Mono<Void> {
        return playerRepository.deleteById(id)
    }
}
