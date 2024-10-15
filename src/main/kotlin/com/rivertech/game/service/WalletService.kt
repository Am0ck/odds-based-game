package com.rivertech.game.service

import com.rivertech.game.repository.PlayerRepository
import com.rivertech.game.repository.WalletTransactionRepository
import com.rivertech.game.entity.TransactionType
import com.rivertech.game.entity.WalletTransaction
import com.rivertech.game.exception.InsufficientBalanceException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class WalletService(private val walletTransactionRepository: WalletTransactionRepository, private val playerRepository: PlayerRepository) {
    fun createTransaction(transaction: WalletTransaction): Mono<WalletTransaction> {
        return walletTransactionRepository.save(transaction)
    }

    fun getTransactionsByPlayer(playerId: UUID): Flux<WalletTransaction> {
        return walletTransactionRepository.findByPlayerId(playerId)
    }
    fun deductFromWallet(playerId: UUID, amount: Double): Mono<Void> {
        return playerRepository.findById(playerId)
            .flatMap { player ->
                if (player.walletBalance >= amount) {
                    val newBalance = player.walletBalance - amount
                    val updatedPlayer = player.copy(walletBalance = newBalance)
                    val transaction = WalletTransaction(
                        playerId = playerId,
                        transactionType = TransactionType.DEBIT,
                        amount = amount
                    )

                    println("Deducting $amount, new balance: $newBalance")

                    playerRepository.save(updatedPlayer).then(walletTransactionRepository.save(transaction)).then()
                } else {
                    Mono.error(InsufficientBalanceException("Insufficient balance for player $playerId"))
                }
            }
    }

    // Credit an amount to the player's wallet balance
    fun creditToWallet(playerId: UUID, amount: Double): Mono<Void> {
        return playerRepository.findById(playerId)
            .flatMap { player ->
                // Update the player's balance by adding the winnings
                val newBalance = player.walletBalance + amount
                val updatedPlayer = player.copy(walletBalance = newBalance)
                val transaction = WalletTransaction(
                    playerId = playerId,
                    transactionType = TransactionType.CREDIT,
                    amount = amount
                )
                // Save the updated player back to the repository
                playerRepository.save(updatedPlayer)
                    .then(walletTransactionRepository.save(transaction))
                    .then(Mono.empty())
            }
    }

}

