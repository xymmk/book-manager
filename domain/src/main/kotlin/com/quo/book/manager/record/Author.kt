package com.quo.book.manager.record

import java.time.LocalDate

data class Author(
    val authorId: String? = "",
    val authorName: String,
    val birthDate: LocalDate
)
