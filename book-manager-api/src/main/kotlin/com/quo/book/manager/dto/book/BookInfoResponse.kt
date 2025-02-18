package com.quo.book.manager.dto.book

import com.quo.book.manager.dto.ResponseStatus
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.ErrorResponse

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
data class BookInfoResponse(
    val result: ResponseStatus,
    val data: List<BookResponseData>
)
