package com.quo.book.manager.record

data class Book(
    val bookId: String? = "",
    val price: Double,
    val title: String,
    val publicationStatus: PublicationStatus
)
