package com.quo.book.manager.dto

import org.springframework.http.ResponseEntity

/**
 * レスポンスステータス
 */
enum class ResponseStatus(
    val builder: ResponseEntity.BodyBuilder
) {
    OK(ResponseEntity.ok()),
    FAILED(ResponseEntity.status(500)),
    NOT_FOUND(ResponseEntity.status(404))
}

