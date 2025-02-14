package com.quo.book.manager.record

import java.time.LocalDate

data class AuthorBooks(
    val authorId: String,
    val authorName: String,
    val birthDate: LocalDate,
    val books: List<Book>
)
