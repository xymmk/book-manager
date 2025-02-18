package com.quo.book.manager.dto.author

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.util.*

/**
 * 著者コントローラリクエストクラス
 */
data class AuthorControllerRequest(
    @Schema(description = "著者名", example = "著者名", required = true)
    @field:NotBlank(message = "著者名は必須です")
    @field:Size(min = 1, max = 500, message = "著者名は1文字以上500文字以下です")
    @JsonProperty("name")
    val authorName: String,

    @Schema(description = "生年月日", example = "2000-12-10", required = true)
    @field:NotNull(message = "生年月日は必須です")
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "生年月日はYYYY-MM-DD形式である必要があります")
    @JsonProperty("birth_date")
    val birthDate: String,

    @Schema(description = "書籍IDリスト", example = "[\"1\", \"2\", \"3\"]", required = true)
    val books: List<String> = listOf()
)
