package com.rivertech.game.controller

import com.rivertech.game.dto.BetDTO
import com.rivertech.game.entity.Bet
import com.rivertech.game.exception.InvalidBetException
import com.rivertech.game.rest.BetController
import com.rivertech.game.service.BetService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
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

@WebFluxTest(BetController::class)
class BetControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var betService: BetService

    private lateinit var betDTO: BetDTO
    private lateinit var bet: Bet

    @BeforeEach
    fun setUp() {
        // Initialize the test data
        val playerId = UUID.randomUUID()
        betDTO = BetDTO(
            id = UUID.randomUUID(),
            playerId = playerId,
            betAmount = 100.0,
            betNumber = 5,
            randomNumber = 4,
            result = "LOSE",
            winnings = 0.0
//            timestamp = System.currentTimeMillis()
        )

        bet = Bet(
            id = UUID.randomUUID(),
            playerId = playerId,
            betAmount = 100.0,
            betNumber = 5,
            randomNumber = 4,
            result = "LOSE",
            winnings = 0.0
//            timestamp = System.currentTimeMillis()
        )
    }

    @Test
    fun `test place bet with valid bet number`() {
        // Provide actual UUID, double, and integer values instead of matchers
        Mockito.`when`(betService.placeBet(betDTO.playerId, 100.0, 5))
            .thenReturn(Mono.just(bet))

        // Define the JSON payload
        val jsonPayload = """
        {
            "playerId": "${betDTO.playerId}",
            "betAmount": 100.0,
            "betNumber": 5
        }
    """.trimIndent()

        // Perform the POST request and verify the response
        webTestClient.post()
            .uri("/bets/place")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonPayload)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.playerId").isEqualTo(betDTO.playerId.toString())
            .jsonPath("$.betAmount").isEqualTo(100.0)
            .jsonPath("$.betNumber").isEqualTo(5)
            .jsonPath("$.randomNumber").isEqualTo(4)
            .jsonPath("$.result").isEqualTo("LOSE")
            .jsonPath("$.winnings").isEqualTo(0.0)
    }

    @Test
    fun `test place bet with invalid bet number`() {
        // Define the JSON payload with an invalid bet number
        val jsonPayload = """
            {
                "playerId": "${betDTO.playerId}",
                "betAmount": 100.0,
                "betNumber": 11
            }
        """.trimIndent()

        // Perform the POST request and verify the response
        webTestClient.post()
            .uri("/bets/place")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonPayload)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.message").isEqualTo("Bet number must be between 1 and 10.")
    }

    @Test
    fun `test get bets by player`() {
        // Mock the service method
        Mockito.`when`(betService.getBetsByPlayer(any(UUID::class.java) ?: UUID.randomUUID()))
            .thenReturn(Flux.just(bet))

        // Perform the GET request and verify the response
        webTestClient.get()
            .uri("/bets/{playerId}", betDTO.playerId)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].playerId").isEqualTo(betDTO.playerId.toString())
            .jsonPath("$[0].betAmount").isEqualTo(100.0)
            .jsonPath("$[0].betNumber").isEqualTo(5)
            .jsonPath("$[0].randomNumber").isEqualTo(4)
            .jsonPath("$[0].result").isEqualTo("LOSE")
            .jsonPath("$[0].winnings").isEqualTo(0.0)
    }
}
