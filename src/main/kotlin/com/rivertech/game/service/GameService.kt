package com.rivertech.game.service

import org.springframework.stereotype.Service
import java.lang.Math.*
import kotlin.random.Random

@Service
class GameService(private val random: Random = Random.Default) {

    // Handles the logic of placing a bet and determining the result
    fun processBet(betNumber: Int, betAmount: Double): BetResult {
        val randomNumber = random.nextInt(1, 11)  // Random number between 1 and 10
        println("RNG: $randomNumber")
        var result = "LOSE"
        var winnings = 0.0

        // Apply game rules based on the player's bet number
        when {
            betNumber == randomNumber -> {
                result = "WIN"
                winnings = betAmount * 10  // 10x winnings for an exact match
            }
            kotlin.math.abs(betNumber - randomNumber) == 1 -> {
                result = "CLOSE WIN"
                winnings = betAmount * 5  // 5x winnings for being 1 number off
            }
            kotlin.math.abs(betNumber - randomNumber) == 2 -> {
                result = "PARTIAL WIN"
                winnings = betAmount * 0.5  // 0.5x winnings for being 2 numbers off
            }
        }

        return BetResult(randomNumber, result, winnings)
    }
}
data class BetResult(
    val randomNumber: Int,
    val result: String,
    val winnings: Double
)
