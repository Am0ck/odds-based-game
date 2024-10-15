package com.rivertech.game.exception

data class CustomErrorResponse(
    var status: Int = 0,
    var message: String? = null,
    var timeStamp: Long = 0L
)

//class CustomErrorResponse {
//    private var status: Int = 0
//    private var message: String? = null
//    private var timeStamp: Long = 0
//
//    constructor()
//
//    constructor(status: Int, message: String?, timeStamp: Long) {
//        this.status = status
//        this.message = message
//        this.timeStamp = timeStamp
//    }
//}