package com.rivertech.game.service

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class GameServiceTest {

    private lateinit var gameService: GameService
    private val random: Random = mockk()  // Mock the Random object

    @BeforeEach
    fun setUp() {
        gameService = GameService(random)  // Inject mocked Random object into GameService
    }

    @Test
    fun `when player guesses exact number, prize is 10 times the bet amount`() {
        every { random.nextInt(1, 11) } returns 5

        val result = gameService.processBet(5, 100.0)
        assertEquals("WIN", result.result)
        assertEquals(1000.0, result.winnings)
    }

    @Test
    fun `when player guesses one number off, prize is 5 times the bet amount`() {
        every { random.nextInt(1, 11) } returns 5

        val result = gameService.processBet(4, 100.0)
        assertEquals("CLOSE WIN", result.result)
        assertEquals(500.0, result.winnings)
    }
    @Test
    fun `when player guesses two numbers off, half bet amount is regained`() {
        every { random.nextInt(1, 11) } returns 5

        val result = gameService.processBet(3, 100.0)
        assertEquals("PARTIAL WIN", result.result)
        assertEquals(50.0, result.winnings)
    }
    @Test
    fun `when player guesses three ore more numbers off, bet is lost`() {
        every { random.nextInt(1, 11) } returns 5

        val result = gameService.processBet(8, 100.0)
        assertEquals("LOSE", result.result)
        assertEquals(0.0, result.winnings)
    }
    @Test
    fun `when player bet lowest`() {
        every { random.nextInt(1, 11) } returns 5

        val result = gameService.processBet(1, 100.0)
        assertEquals("LOSE", result.result)
        assertEquals(0.0, result.winnings)
    }
}

//    @Test
//    fun `should win 10x bet amount when bet matches random number`() {
//        // Simulating an exact match
//        val result = gameService.processBet(5, 100.0)
//        Assertions.assertEquals(5, result.randomNumber)  // Ensure the random number matches the bet
//        Assertions.assertEquals("WIN", result.result)
//        Assertions.assertEquals(1000.0, result.winnings)  // 10x the bet amount
//    }
//
//    @Test
//    fun `should win 5x bet amount when bet is one number off`() {
//        // Simulating a win for one number off (bet on 4, random number is 5)
//        val result = gameService.processBet(4, 100.0)
//        assertNotEquals(4, result.randomNumber)  // Random number is different
//        Assertions.assertEquals("CLOSE WIN", result.result)
//        Assertions.assertEquals(500.0, result.winnings)  // 5x the bet amount
//    }
//
//    @Test
//    fun `should win half the bet amount when bet is two numbers off`() {
//        // Simulating a win for two numbers off (bet on 3, random number is 5)
//        val result = gameService.processBet(3, 100.0)
//        assertNotEquals(3, result.randomNumber)
//        Assertions.assertEquals("PARTIAL WIN", result.result)
//        Assertions.assertEquals(50.0, result.winnings)  // Half the bet amount
//    }
//
//    @Test
//    fun `should lose bet when bet is three or more numbers off`() {
//        // Simulating a loss (bet on 1, random number is far from 1)
//        val result = gameService.processBet(1, 100.0)
//        assertNotEquals(1, result.randomNumber)
//        Assertions.assertEquals("LOSE", result.result)
//        Assertions.assertEquals(0.0, result.winnings)  // No winnings
//    }
