package com.rivertech.game.rest

import com.rivertech.game.dto.BetDTO
import com.rivertech.game.entity.Bet
import com.rivertech.game.exception.InvalidBetException
import com.rivertech.game.service.BetService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/bets")
class BetController(val betService: BetService) {

    // API endpoint to create a new bet
    @PostMapping("/place")
    fun placeBet(@RequestBody betDTO: BetDTO): Mono<BetDTO> {
        if (betDTO.betNumber !in 1..10) {
            throw InvalidBetException("Bet number must be between 1 and 10.")
        }
        return betService.placeBet(betDTO.playerId, betDTO.betAmount, betDTO.betNumber)
            .map { savedBet ->
                BetDTO(
                    id = savedBet.id,
                    playerId = savedBet.playerId,
                    betAmount = savedBet.betAmount,
                    betNumber = savedBet.betNumber,
                    randomNumber = savedBet.randomNumber,
                    result = savedBet.result,
                    winnings = savedBet.winnings,
                    timestamp = savedBet.timestamp
                )
            }
    }

    // API endpoint to get all bets for a player by player ID
    @GetMapping("/{playerId}")
    fun getBetsByPlayer(@PathVariable playerId: UUID): Flux<BetDTO> {
        return betService.getBetsByPlayer(playerId)
            .map { bet ->
                BetDTO(
                    id = bet.id,
                    playerId = bet.playerId,
                    betAmount = bet.betAmount,
                    betNumber = bet.betNumber,
                    randomNumber = bet.randomNumber,
                    result = bet.result,
                    winnings = bet.winnings,
                    timestamp = bet.timestamp
                )
            }
    }
}
