package com.quo.book.manager.controller

import com.quo.book.manager.repository.BookRepository
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Healthy (private val bookRepository: BookRepository) {

    @GetMapping("/health")
    @Operation(description = "ヘルスチェック", summary = "ヘルスチェック", tags = ["healthy"], operationId = "health")
    fun health(): String { return "ok"
    }
}
