package com.quo.book.manager.exception

import com.quo.book.manager.dto.BookManagerApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class RequestException {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<BookManagerApiResponse> {
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            "$fieldName: $errorMessage"
        }.joinToString(", ")

        val response = BookManagerApiResponse(
            result = com.quo.book.manager.dto.ResponseStatus.FAILED,
            message = errors
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}
