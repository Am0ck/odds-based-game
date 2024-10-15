//package com.rivertech.game.service
//
//import com.rivertech.game.entity.Player
//import com.rivertech.game.entity.TransactionType
//import com.rivertech.game.entity.WalletTransaction
//import com.rivertech.game.exception.InsufficientBalanceException
//import com.rivertech.game.repository.PlayerRepository
//import com.rivertech.game.repository.WalletTransactionRepository
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import java.util.*
//
//class WalletServiceTest {
//
//    private lateinit var walletService: WalletService
//    private lateinit var playerRepository: PlayerRepository
//    private lateinit var walletTransactionRepository: WalletTransactionRepository
//
//    @BeforeEach
//    fun setUp() {
//        playerRepository = mockk()
//        walletTransactionRepository = mockk()
//        walletService = WalletService(walletTransactionRepository, playerRepository)
//    }
//
//    @Test
//    fun `createTransaction should save a transaction`() {
//        val transaction = WalletTransaction(UUID.randomUUID(), UUID.randomUUID(), TransactionType.CREDIT, 100.0)
//
//        every { walletTransactionRepository.save(transaction) } returns Mono.just(transaction)
//
//        val result = walletService.createTransaction(transaction)
//
//        StepVerifier.create(result)
//            .expectNext(transaction)
//            .verifyComplete()
//
//        verify(exactly = 1) { walletTransactionRepository.save(transaction) }
//    }
//
//    @Test
//    fun `getTransactionsByPlayer should return transactions for a given player`() {
//        val playerId = UUID.randomUUID()
//        val transactions = listOf(
//            WalletTransaction(UUID.randomUUID(), playerId, TransactionType.DEBIT, 50.0),
//            WalletTransaction(UUID.randomUUID(), playerId, TransactionType.CREDIT, 100.0)
//        )
//
//        every { walletTransactionRepository.findByPlayerId(playerId) } returns Flux.fromIterable(transactions)
//
//        val result = walletService.getTransactionsByPlayer(playerId)
//
//        StepVerifier.create(result)
//            .expectNext(transactions[0], transactions[1])
//            .verifyComplete()
//
//        verify(exactly = 1) { walletTransactionRepository.findByPlayerId(playerId) }
//    }
//
//    @Test
//    fun `deductFromWallet should deduct amount if sufficient balance`() {
//        val playerId = UUID.randomUUID()
//        val player = Player(playerId, "testuser", "Test", "User", 100.0)
//        val updatedPlayer = player.copy(walletBalance = 50.0)
//
//        every { playerRepository.findById(playerId) } returns Mono.just(player)
//        every { playerRepository.save(updatedPlayer) } returns Mono.just(updatedPlayer)
//
//        // Use match to focus on relevant fields
//        every {
//            walletTransactionRepository.save(match {
//                it.playerId == playerId && it.transactionType == TransactionType.DEBIT && it.amount == 50.0
//            })
//        } returns Mono.just(WalletTransaction(UUID.randomUUID(), playerId, TransactionType.DEBIT, 50.0))
//
//        val result = walletService.deductFromWallet(playerId, 50.0)
//
//        StepVerifier.create(result)
//            .verifyComplete()
//
//        verify(exactly = 1) { playerRepository.findById(playerId) }
//        verify(exactly = 1) { playerRepository.save(updatedPlayer) }
//        verify(exactly = 1) {
//            walletTransactionRepository.save(match {
//                it.playerId == playerId && it.transactionType == TransactionType.DEBIT && it.amount == 50.0
//            })
//        }
//    }
//
//
//    @Test
//    fun `deductFromWallet should throw InsufficientBalanceException if balance is too low`() {
//        val playerId = UUID.randomUUID()
//        val player = Player(playerId, "testuser", "Test", "User", 30.0)  // Insufficient balance
//
//        every { playerRepository.findById(playerId) } returns Mono.just(player)
//
//        val result = walletService.deductFromWallet(playerId, 50.0)
//
//        StepVerifier.create(result)
//            .expectErrorMatches { it is InsufficientBalanceException && it.message == "Insufficient balance for player $playerId" }
//            .verify()
//
//        verify(exactly = 1) { playerRepository.findById(playerId) }
//        verify(exactly = 0) { playerRepository.save(any()) }
//        verify(exactly = 0) { walletTransactionRepository.save(any()) }
//    }
//
//    @Test
//    fun `creditToWallet should credit the correct amount to the player`() {
//        val playerId = UUID.randomUUID()
//        val player = Player(playerId, "testuser", "Test", "User", 100.0)
//        val updatedPlayer = player.copy(walletBalance = 150.0)
//
//        every { playerRepository.findById(playerId) } returns Mono.just(player)
//        every { playerRepository.save(updatedPlayer) } returns Mono.just(updatedPlayer)
//
//        every {
//            walletTransactionRepository.save(match {
//                it.playerId == playerId && it.transactionType == TransactionType.CREDIT && it.amount == 50.0
//            })
//        } returns Mono.just(WalletTransaction(UUID.randomUUID(), playerId, TransactionType.CREDIT, 50.0))
//
//        val result = walletService.creditToWallet(playerId, 50.0)
//
//        StepVerifier.create(result)
//            .verifyComplete()
//
//        verify(exactly = 1) { playerRepository.findById(playerId) }
//        verify(exactly = 1) { playerRepository.save(updatedPlayer) }
//        verify(exactly = 1) {
//            walletTransactionRepository.save(match {
//                it.playerId == playerId && it.transactionType == TransactionType.CREDIT && it.amount == 50.0
//            })
//        }
//    }
//
//}
