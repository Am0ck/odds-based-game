package com.rivertech.game.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler
    fun handleException(e: PlayerNotFoundException): ResponseEntity<CustomErrorResponse> {
        val error = CustomErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = e.message,
            timeStamp = System.currentTimeMillis()
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }
    @ExceptionHandler
    fun handleException(e: UsernameExistsException): ResponseEntity<CustomErrorResponse> {
        val error = CustomErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = e.message,
            timeStamp = System.currentTimeMillis()
        )
        return ResponseEntity(error, HttpStatus.CONFLICT)
    }
    @ExceptionHandler
    fun handleInvalidBetNumberException(e: InvalidBetException): ResponseEntity<CustomErrorResponse> {
        val error = CustomErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = e.message,
            timeStamp = System.currentTimeMillis()
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    fun handleException(e: Exception): ResponseEntity<CustomErrorResponse> {
        val error = CustomErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = e.message,
            timeStamp = System.currentTimeMillis()
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
}