package com.quo.book.manager.dto.book

import com.fasterxml.jackson.annotation.JsonProperty
import com.quo.book.manager.model.PublicationStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

/**
 * 書籍コントローラリクエストクラス
 */
data class BookControllerRequest(
    @Schema(description = "タイトル", example = "書籍タイトル", required = true)
    @field:NotBlank(message = "タイトルは必須です")
    @field:Size(min = 1, max = 500, message = "タイトルは1文字以上500文字以下です")
    @JsonProperty("title")
    val title: String,

    @Schema(description = "価格", example = "0", required = true)
    @field:Min(value = 0, message = "価格は0以上です")
    @JsonProperty("price")
    val price: Double,

    @Schema(description = "著者IDリスト", example = "[\"1\", \"2\", \"3\"]", required = true)
    @field: NotEmpty(message = "著者IDリストは必須です")
    @JsonProperty("authors")
    val authors: List<@Min(1) String>,

    @Schema(description = "出版状況", example = "PUBLISHED", required = true)
    @JsonProperty("publication_status")
    val publicationStatus: PublicationStatus
)
