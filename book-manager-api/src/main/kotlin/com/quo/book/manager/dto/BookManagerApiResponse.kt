package com.quo.book.manager.dto

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.ErrorResponse

/**
 * 書籍管理APIレスポンス
 */
@ApiResponses(
    value = [
        ApiResponse(responseCode = "200", description = "成功"),
        ApiResponse(
            responseCode = "500",
            description = "サーバーエラー",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ]
)
data class BookManagerApiResponse(val result: ResponseStatus, val message: String)
