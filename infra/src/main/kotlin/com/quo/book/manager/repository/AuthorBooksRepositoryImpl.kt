package com.quo.book.manager.repository

import com.quo.book.manager.jooq.tables.references.AUTHOR_BOOKS
import com.quo.book.manager.record.AuthorBooks
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorBooksRepositoryImpl(val dsl: DSLContext) : AuthorBooksRepository {
    override fun register(authorBooks: AuthorBooks) {
        authorBooks.books.forEach {
            dsl.insertInto(AUTHOR_BOOKS)
                .set(AUTHOR_BOOKS.BOOK_ID, it.bookId!!.toInt())
                .set(AUTHOR_BOOKS.AUTHOR_ID, authorBooks.authorId!!.toInt())
                .execute()
        }
    }
}
