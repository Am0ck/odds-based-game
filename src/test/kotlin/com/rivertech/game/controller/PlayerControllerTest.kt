package com.rivertech.game.controller

import com.rivertech.game.dto.LeaderboardDTO
import com.rivertech.game.dto.PlayerDTO
import com.rivertech.game.entity.Player
import com.rivertech.game.exception.UsernameExistsException
import com.rivertech.game.rest.PlayerController
import com.rivertech.game.service.PlayerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@WebFluxTest(PlayerController::class)
class PlayerControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var playerService: PlayerService

    private lateinit var playerDTO: PlayerDTO
    private lateinit var player: Player
    private lateinit var leaderboardDTO: LeaderboardDTO

    @BeforeEach
    fun setUp() {
        val playerId = UUID.randomUUID()
        playerDTO = PlayerDTO(playerId, "testuser", "Test", "User", 1000.0)
        player = Player(id = playerId, username = "testuser", name = "Test", surname = "User", walletBalance = 1000.0)
        leaderboardDTO = LeaderboardDTO(playerId, "testuser", "Test", "User", 5000.0)
    }

    @Test
    fun `test register valid player`() {
        // Define the JSON payload as a string
        val jsonPayload = """
        {
            "username": "TestUsername",
            "name": "TestName",
            "surname": "TestSurname"
        }
    """

        // Create the expected Player object (without id)
        val expectedPlayer = Player(
            username = "TestUsername",
            name = "TestName",
            surname = "TestSurname",
            walletBalance = 1000.0
        )

        // Mock the behavior of the service to return a new player with a generated ID
        Mockito.`when`(playerService.registerPlayer(expectedPlayer))
            .thenReturn(Mono.just(expectedPlayer.copy(id = UUID.randomUUID())))

        // Perform the actual HTTP POST request and verify the response
        webTestClient.post()
            .uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonPayload)  // Send JSON directly
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isEqualTo("TestUsername")
            .jsonPath("$.name").isEqualTo("TestName")
            .jsonPath("$.surname").isEqualTo("TestSurname")
            .jsonPath("$.walletBalance").isEqualTo(1000.0)  // Expect default wallet balance
    }
    @Test
    fun `test register player with invalid payload`() {
        // Define the JSON payload as a string
        val jsonPayload = """
        {
            "name": "TestName",
            "surname": "TestSurname"
        }
    """.trimIndent()

        // Create the expected Player object (without id)
        val expectedPlayer = Player(
            username = "TestUsername",
            name = "TestName",
            surname = "TestSurname",
            walletBalance = 1000.0
        )

        // Mock the behavior of the service to return a new player with a generated ID
        Mockito.`when`(playerService.registerPlayer(expectedPlayer))
            .thenReturn(Mono.just(expectedPlayer.copy(id = UUID.randomUUID())))

        // Perform the actual HTTP POST request and verify the response
        webTestClient.post()
            .uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonPayload)  // Send JSON directly
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
    }

    @Test
    fun `test register player with existing username should throw UsernameExistsException`() {
        // Define the JSON payload as a string
        val jsonPayload = """
        {
            "username": "TestUsername",
            "name": "TestName",
            "surname": "TestSurname"
        }
    """.trimIndent()

        // Create the expected Player object (without id)
        val expectedPlayer = Player(
            username = "TestUsername",
            name = "TestName",
            surname = "TestSurname",
            walletBalance = 1000.0
        )

        // Mock the behavior of the service to throw UsernameExistsException for duplicate username
        Mockito.`when`(playerService.registerPlayer(expectedPlayer))
            .thenReturn(Mono.error(UsernameExistsException("Username '${expectedPlayer.username}' is already taken.")))

        // Perform the actual HTTP POST request and verify the response for exception
        webTestClient.post()
            .uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonPayload)  // Send JSON directly
            .exchange()
            .expectStatus().isEqualTo(409)  // Expect 409 Conflict
            .expectBody()
            .jsonPath("$.message").isEqualTo("Username 'TestUsername' is already taken.")
            .jsonPath("$.status").isEqualTo(409)  // Verify status code in error response

    }


    @Test
    fun `test get all players`() {
        Mockito.`when`(playerService.getAllPlayers()).thenReturn(Flux.just(player))

        webTestClient.get()
            .uri("/api/players")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].username").isEqualTo("testuser")
            .jsonPath("$[0].name").isEqualTo("Test")
            .jsonPath("$[0].surname").isEqualTo("User")
            .jsonPath("$[0].walletBalance").isEqualTo(1000.0)
    }

    @Test
    fun `test get player by id`() {
        Mockito.`when`(playerService.getPlayerById(any(UUID::class.java) ?: UUID.randomUUID()))
            .thenReturn(Mono.just(player))

        webTestClient.get()
            .uri("/api/{playerId}", playerDTO.id)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isEqualTo("testuser")
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.surname").isEqualTo("User")
            .jsonPath("$.walletBalance").isEqualTo(1000.0)
    }



    @Test
    fun `test get leaderboard`() {
        Mockito.`when`(playerService.getLeaderboard(any(Int::class.java)))
            .thenReturn(Flux.just(leaderboardDTO))

        webTestClient.get()
            .uri("/api/leaderboard?limit=10")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].username").isEqualTo("testuser")
            .jsonPath("$[0].totalWinnings").isEqualTo(5000.0)
    }
}
