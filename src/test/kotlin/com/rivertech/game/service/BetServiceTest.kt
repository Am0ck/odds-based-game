package com.rivertech.game.service

import com.rivertech.game.repository.BetRepository
import com.rivertech.game.entity.Bet
import com.rivertech.game.entity.Player
import com.rivertech.game.exception.PlayerNotFoundException
import com.rivertech.game.repository.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.UUID

class BetServiceTest {

    private lateinit var betService: BetService
    private val betRepository = mockk<BetRepository>()
    private val playerRepository = mockk<PlayerRepository>()
    private val gameService = mockk<GameService>()
    private val walletService = mockk<WalletService>()

    private val playerId = UUID.randomUUID()
    private val betAmount = 100.0
    private val betNumber = 5
    private val betResult = BetResult(randomNumber = 9, result = "LOSE", winnings = 0.0)
    private val bet = Bet(null, playerId, betAmount, betNumber, betResult.randomNumber, betResult.result, betResult.winnings)

    @BeforeEach
    fun setUp() {
        betService = BetService(betRepository, playerRepository, gameService, walletService)
    }

    @Test
    fun `test place bet with valid player and sufficient balance`() {
        val player = Player(playerId, "testuser", "Test", "User", 1000.0)
        every { playerRepository.findById(playerId) } returns Mono.just(player)

        every { walletService.deductFromWallet(playerId, betAmount) } returns Mono.empty()

        every { gameService.processBet(betNumber, betAmount) } returns betResult

        every { betRepository.save(any()) } returns Mono.just(bet)

        val result = betService.placeBet(playerId, betAmount, betNumber)

        StepVerifier.create(result)
            .expectNextMatches { it.result == "LOSE" && it.winnings == 0.0 }
            .verifyComplete()

        // Verify that the wallet deduction, game service, and bet saving were called
        verify(exactly = 1) { walletService.deductFromWallet(playerId, betAmount) }
        verify(exactly = 1) { gameService.processBet(betNumber, betAmount) }
        verify(exactly = 1) { betRepository.save(any()) }
    }

    @Test
    fun `placeBet should throw PlayerNotFoundException for non-existent playerId`() {
        // Given a non-existent playerId
        val nonExistentPlayerId = UUID.randomUUID()
        val betAmount = 100.0
        val betNumber = 5

        every { playerRepository.findById(nonExistentPlayerId) } returns Mono.empty()

        // Call the service method
        val result = betService.placeBet(nonExistentPlayerId, betAmount, betNumber)

        // Assert that PlayerNotFoundException is thrown with the correct message
        StepVerifier.create(result)
            .expectErrorMatches { error ->
                error is PlayerNotFoundException && error.message == "Player with id $nonExistentPlayerId not found"
            }
            .verify()

        // Verify interaction with the player repository
        verify(exactly = 1) { playerRepository.findById(nonExistentPlayerId) }
    }

}
