package com.rivertech.game.rest

import com.rivertech.game.dto.LeaderboardDTO
import com.rivertech.game.dto.PlayerDTO
import com.rivertech.game.entity.Player
import com.rivertech.game.exception.PlayerNotFoundException
import com.rivertech.game.service.PlayerService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api")
class PlayerController(private val playerService: PlayerService) {


    @PostMapping("/register")
    fun registerPlayer(@RequestBody playerDTO: PlayerDTO): Mono<PlayerDTO> {
        println("REGISTER!!!")
        val player = Player(username = playerDTO.username, name = playerDTO.name, surname = playerDTO.surname)
        println(player.toString())
        return playerService.registerPlayer(player)
            .map { PlayerDTO(it.id!!, it.username, it.name, it.surname, it.walletBalance) }
    }

    @GetMapping("/players")
    fun getAllPlayers(): Flux<PlayerDTO> {
        return playerService.getAllPlayers()
            .map { PlayerDTO(it.id!!, it.username, it.name, it.surname, it.walletBalance) }
    }
    @GetMapping("/{playerId}")
    fun getPlayerById(@PathVariable playerId: UUID): Mono<PlayerDTO> {
        return playerService.getPlayerById(playerId)
            .switchIfEmpty(Mono.error(PlayerNotFoundException("Player with ID $playerId not found.")))  // Throw exception if player is not found
            .map { PlayerDTO(it.id!!, it.username, it.name, it.surname, it.walletBalance) }
    }

    //    @GetMapping("/{playerId}")
//    fun getPlayerById(@PathVariable playerId: UUID): Mono<PlayerDTO> {
//        return playerService.getPlayerById(playerId)
//            .map { PlayerDTO(it.id!!, it.username, it.name, it.surname, it.walletBalance) }
//    }
    @GetMapping("/leaderboard")
    fun getLeaderboard(@RequestParam(defaultValue = "10") limit: Int): Flux<LeaderboardDTO> {
        return playerService.getLeaderboard(limit)
    }
}
