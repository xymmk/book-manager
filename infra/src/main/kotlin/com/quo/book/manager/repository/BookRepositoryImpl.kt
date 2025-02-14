package com.quo.book.manager.repository

import com.quo.book.manager.jooq.tables.references.BOOKS
import com.quo.book.manager.record.Book
import com.quo.book.manager.record.PublicationStatus
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class BookRepositoryImpl(private val dsl: DSLContext) : BookRepository {
    override fun register(book: Book): Book?{
        // DBに登録する際に、PublicationStatusの値をvalueとして保存する
        val publicationStatus = when (book.publicationStatus.value) {
            PublicationStatus.PUBLISHED.value -> PublicationStatus.PUBLISHED.value
            PublicationStatus.UNPUBLISHED.value -> PublicationStatus.UNPUBLISHED.value
            else -> throw IllegalArgumentException("publicationStatusの値は無効です")
        }

        // DBに登録する際に、BigDecimal型に変換して保存する
        val record = dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, book.title)
            .set(BOOKS.PUBLICATION_STATUS, publicationStatus)
            .set(BOOKS.PRICE, BigDecimal(book.price))
            .returning(BOOKS.BOOK_ID, BOOKS.PRICE, BOOKS.TITLE, BOOKS.PUBLICATION_STATUS)
            .fetchOne()

        // recordがnullでない場合、Bookオブジェクトを生成して返す
        return record?.let {
            Book(
                bookId = it.getValue(BOOKS.BOOK_ID)!!.toString(),
                price = it.getValue(BOOKS.PRICE)!!.toDouble(),
                title = it.getValue(BOOKS.TITLE)!!.toString(),
                publicationStatus = when (it.getValue(BOOKS.PUBLICATION_STATUS)) {
                    PublicationStatus.PUBLISHED.value -> PublicationStatus.PUBLISHED
                    PublicationStatus.UNPUBLISHED.value -> PublicationStatus.UNPUBLISHED
                    else -> throw IllegalArgumentException("publicationStatusの値は無効です")
                }
            )
        }
    }

    override fun updateBook(oldBook: Book, newBook: Book) {
        TODO("Not yet implemented")
    }
}
