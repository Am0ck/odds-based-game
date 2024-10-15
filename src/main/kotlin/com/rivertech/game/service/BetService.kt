package com.rivertech.game.service

import com.rivertech.game.repository.BetRepository
import com.rivertech.game.entity.Bet
import com.rivertech.game.exception.PlayerNotFoundException
import com.rivertech.game.repository.PlayerRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class BetService(val betRepository: BetRepository,
                 val playerRepository: PlayerRepository,
                 val gameService: GameService,
                 val walletService: WalletService) {

    // Method to create a new bet
//    fun createBet(bet: Bet): Mono<Bet> {
//        return betRepository.save(bet)
//    }

    // Method to retrieve all bets by player ID
    fun getBetsByPlayer(playerId: UUID): Flux<Bet> {
        return betRepository.findByPlayerId(playerId)
    }
    fun placeBet(playerId: UUID, betAmount: Double, betNumber: Int): Mono<Bet> {
        println("IN placeBET")

        return playerRepository.findById(playerId)
            .switchIfEmpty(Mono.error(PlayerNotFoundException("Player with id $playerId not found")))
            .flatMap {
                walletService.deductFromWallet(playerId, betAmount)
                    .then(
                        Mono.fromCallable {
                            // Process the bet and get the result (assume gameService is synchronous)
                            val betResult = gameService.processBet(betNumber, betAmount)
                            println("Processed bet: $betResult")

                            // Create the Bet entity
                            Bet(
                                playerId = playerId,
                                betAmount = betAmount,
                                betNumber = betNumber,
                                randomNumber = betResult.randomNumber,
                                result = betResult.result,
                                winnings = betResult.winnings
                            )
                        }
                    )
                    .flatMap { newBet ->
                        // Save the bet in the database
                        betRepository.save(newBet)
                            .doOnNext { savedBet ->
                                println("Saved bet: $savedBet")

                                // If the player won, credit the winnings
                                if (newBet.winnings > 0) {
                                    println("Player won! Crediting ${newBet.winnings}")
                                    walletService.creditToWallet(playerId, newBet.winnings).subscribe()
                                }
                            }
                    }
                    .doOnError { e -> println("Error occurred: ${e.message}") }
            }
    }

//    fun placeBet(playerId: UUID, betAmount: Double, betNumber: Int): Mono<Bet> {
//        println("IN placeBET")
//
//        return walletService.deductFromWallet(playerId, betAmount)
//            .then(
//                Mono.fromCallable {
//                    // Process the bet and get the result (assume gameService is synchronous)
//                    val betResult = gameService.processBet(betNumber, betAmount)
//                    println("Processed bet: $betResult")
//
//                    // Create the Bet entity
//                    Bet(
//                        playerId = playerId,
//                        betAmount = betAmount,
//                        betNumber = betNumber,
//                        randomNumber = betResult.randomNumber,
//                        result = betResult.result,
//                        winnings = betResult.winnings
//                    )
//                }
//            )
//            .flatMap { newBet ->
//                // Save the bet in the database
//                betRepository.save(newBet)
//                    .doOnNext { savedBet ->
//                        println("Saved bet: $savedBet")
//
//                        // If the player won, credit the winnings
//                        if (newBet.winnings > 0) {
//                            println("Player won! Crediting ${newBet.winnings}")
//                            walletService.creditToWallet(playerId, newBet.winnings).subscribe()
//                        }
//                    }
//            }
//            .doOnError { e -> println("Error occurred: ${e.message}") }
//    }

}
