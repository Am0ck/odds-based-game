package com.rivertech.game.service

import com.rivertech.game.dto.LeaderboardDTO
import com.rivertech.game.entity.Player
import com.rivertech.game.exception.UsernameExistsException
import com.rivertech.game.repository.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import reactor.core.publisher.Flux


class PlayerServiceTest {

    private val playerRepository: PlayerRepository = mockk()
    private val playerService = PlayerService(playerRepository)

    private val existingPlayer = Player(
        id = UUID.randomUUID(),
        username = "TestUsername",
        name = "TestName",
        surname = "TestSurname",
        walletBalance = 1000.0
    )

    @Test
    fun `register player should throw UsernameExistsException if username is already taken`() {
        // Mocking findByUsername to return a player, indicating that the username exists
        every { playerRepository.findByUsername(existingPlayer.username) } returns Mono.just(existingPlayer)

        // Mocking save method, though it won't be reached in this test
        every { playerRepository.save(any()) } returns Mono.just(existingPlayer)

        // Assert that UsernameExistsException is thrown when trying to register with an existing username
        assertThrows(UsernameExistsException::class.java) {
            playerService.registerPlayer(existingPlayer).block()
        }
    }

    @Test
    fun `register new player`() {
        val newPlayer = Player(
            id = UUID.randomUUID(),
            username = "NewUsername",
            name = "NewName",
            surname = "NewSurname",
            walletBalance = 1000.0
        )

        // Mocking findByUsername to return Mono.empty(), indicating that the username is not taken
        every { playerRepository.findByUsername(newPlayer.username) } returns Mono.empty()

        // Mocking save method to simulate saving the new player
        every { playerRepository.save(newPlayer) } returns Mono.just(newPlayer)

        // Call the service and verify it completes successfully
        val savedPlayer = playerService.registerPlayer(newPlayer).block()

        // Assertions to check that the player was saved correctly
        assert(savedPlayer != null)
        assert(savedPlayer!!.username == "NewUsername")
        assert(savedPlayer.name == "NewName")
    }
    @Test
    fun `getAllPlayers should return all players`() {
        val players = listOf(
            Player(id = UUID.randomUUID(), username = "player1", name = "Player", surname = "One", walletBalance = 1000.0),
            Player(id = UUID.randomUUID(), username = "player2", name = "Player", surname = "Two", walletBalance = 800.0)
        )

        every { playerRepository.findAll() } returns Flux.fromIterable(players)

        val result = playerService.getAllPlayers()

        StepVerifier.create(result)
            .expectNext(players[0], players[1])
            .verifyComplete()

        verify(exactly = 1) { playerRepository.findAll() }
    }
    @Test
    fun `getPlayerById should return player for valid id`() {
        val playerId = UUID.randomUUID()
        val player = Player(id = playerId, username = "testuser", name = "Test", surname = "User", walletBalance = 1000.0)

        every { playerRepository.findById(playerId) } returns Mono.just(player)

        val result = playerService.getPlayerById(playerId)

        StepVerifier.create(result)
            .assertNext { assertEquals(player, it) }
            .verifyComplete()

        verify(exactly = 1) { playerRepository.findById(playerId) }
    }
    @Test
    fun `deletePlayer should delete player by id`() {
        val playerId = UUID.randomUUID()

        every { playerRepository.deleteById(playerId) } returns Mono.empty()

        val result = playerService.deletePlayer(playerId)

        StepVerifier.create(result)
            .verifyComplete()

        verify(exactly = 1) { playerRepository.deleteById(playerId) }
    }

    @Test
    fun `getLeaderboard should return leaderboard of top players`() {
        // Mock data
        val limit = 5
        val leaderboard = listOf(
            LeaderboardDTO(
                playerId = UUID.randomUUID(),
                username = "player1",
                name = "Player",
                surname = "One",
                totalWinnings = 1000.0
            ),
            LeaderboardDTO(
                playerId = UUID.randomUUID(),
                username = "player2",
                name = "Player",
                surname = "Two",
                totalWinnings = 900.0
            )
        )

        every { playerRepository.findTopPlayersByWinnings(limit) } returns Flux.fromIterable(leaderboard)

        // Call the service
        val result = playerService.getLeaderboard(limit)

        // Assert the result
        StepVerifier.create(result)
            .expectNext(leaderboard[0], leaderboard[1])
            .verifyComplete()

        // Verify interaction with the repository
        verify(exactly = 1) { playerRepository.findTopPlayersByWinnings(limit) }
    }
}
