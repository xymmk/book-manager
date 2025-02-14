package com.quo.book.manager.dto.book

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.quo.book.manager.record.PublicationStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

/**
 * 書籍登録するリクエストクラス
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class RegisterBookRequest(
    @Schema(description = "タイトル", example = "書籍タイトル", required = true)
    @field:NotBlank
    @field:Size(min = 1, max = 500)
    val title: String,

    @Schema(description = "価格", example = "書籍価格", required = true)
    @field:Min(0)
    val price: Double,

    @Schema(description = "著者", example = "[1,2,3]", required = true)
    @field: NotEmpty
    val authors: List<@Min(1) String>,

    @Schema(description = "出版状況", example = "published", required = true)
    val publicationStatus: PublicationStatus
)
