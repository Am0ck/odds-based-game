package com.rivertech.game.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("players")
data class Player(
    @Id
    val id: UUID? = null,

    val username: String,  //  Uniqueness handled via DB constraint
    val name: String,
    val surname: String,
    var walletBalance: Double = 1000.0  // Initial balance for the player
)
//{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private val id = 0
//
//    override fun toString(): String {
//        return "Player(id=$id, firstName='$firstName', lastName='$lastName', username='$username', balance=$balance)"
//    }
//
//}