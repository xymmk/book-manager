package com.quo.book.manager.dto.book

import com.fasterxml.jackson.annotation.JsonProperty
import com.quo.book.manager.dto.author.AuthorInfoResponse

data class BookResponseData(
    @JsonProperty("book_id")
    val bookId: String,
    val price: String,
    val title: String,
    @JsonProperty("publication_status")
    val publicationStatus: String,
    @JsonProperty("association_authors")
    val authors: List<AuthorInfoResponse>
)
