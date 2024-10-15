package com.rivertech.game

import com.rivertech.game.entity.Player
import com.rivertech.game.service.GameService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GameIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient
    lateinit var gameService: GameService
    private var playerId: UUID? = null

    @BeforeEach
    fun setUp(@Autowired context: ApplicationContext) {
        gameService = GameService()
        playerId = null
    }

    @Test
    fun `should register player, place bet and retrieve wallet balance`() {
        // Step 1: Register a player
        val playerRegistration = mapOf("username" to "testUser", "name" to "John", "surname" to "Doe")

        val createdPlayer = webTestClient.post()
            .uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(playerRegistration)
            .exchange()
            .expectStatus().isOk
            .expectBody<Player>()
            .returnResult().responseBody

        println("CREATED PLAYER: $createdPlayer")
        assert(createdPlayer != null)
        playerId = createdPlayer!!.id  // Capture the player's UUID
        val betRes = gameService.processBet(5,5.0)
        println("Bet result: $betRes")
        // Step 2: Place a bet
        val betRequest = mapOf("playerId" to playerId, "betNumber" to 5, "betAmount" to 100.0, "randomNumber" to betRes.randomNumber, "result" to betRes.result, "winnings" to betRes.winnings)

        webTestClient.post()
            .uri("/bets/place")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(betRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isNotEmpty
            .jsonPath("$.winnings").isNumber

//        webTestClient.get()
//            .uri("/api/{playerId}")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(betRequest)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody()
//            .jsonPath("$.result").isNotEmpty
//            .jsonPath("$.winnings").isNumber

//         Step 3: Retrieve wallet balance
//        val resw = webTestClient.get()
//            .uri("/wallet/{id}", playerId)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody()
//            .jsonPath("$.walletBalance").isNumber
//        println(resw)
    }

//    @Test
//    fun `should retrieve leaderboard`() {
//        // Assuming some players and bets have already been placed
//
//        // Step 1: Retrieve leaderboard
//        webTestClient.get()
//            .uri("/leaderboard")
//            .exchange()
//            .expectStatus().isOk
//            .expectBody()
//            .jsonPath("$.length()").isNumber  // Ensure some players are returned
//            .jsonPath("$[0].username").isNotEmpty  // Ensure at least one player is ranked
//    }
}
