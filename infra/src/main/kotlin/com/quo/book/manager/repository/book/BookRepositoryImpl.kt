package com.quo.book.manager.repository.book

import com.quo.book.manager.jooq.tables.AuthorBook
import com.quo.book.manager.jooq.tables.references.AUTHOR_BOOK
import com.quo.book.manager.jooq.tables.references.BOOKS
import com.quo.book.manager.model.PublicationStatus
import com.quo.book.manager.model.book.Book
import com.quo.book.manager.repository.BookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class BookRepositoryImpl(val dsl: DSLContext) : BookRepository {
    private val _logger = KotlinLogging.logger {}

    /**
     * 著者に紐づく書籍IDを取得する
     * @param authorID 著者ID
     * @return 紐づく書籍のIDリスト
     */
    private fun findBooksByAuthorId(authorID: String): List<String> {
        val bookIdRecords = dsl.select(AUTHOR_BOOK.BOOK_ID)
            .from(AuthorBook.AUTHOR_BOOK)
            .where(AuthorBook.AUTHOR_BOOK.AUTHOR_ID.eq(authorID.toInt())).fetch()
        return bookIdRecords.map { it.getValue(AUTHOR_BOOK.BOOK_ID).toString() }
    }

    /**
     * 書籍と著者関連情報を登録する
     * @param bookId 書籍情報
     * @param authorIdList 著者情報
     */
    private fun insertAssociation(bookId: Int, authorIdList: List<String>) {
        authorIdList.forEach() {
            dsl.insertInto(AuthorBook.AUTHOR_BOOK)
                .set(AuthorBook.AUTHOR_BOOK.BOOK_ID, bookId)
                .set(AuthorBook.AUTHOR_BOOK.AUTHOR_ID, it.toInt())
                .execute()
            _logger.info { "書籍ID: $bookId に著者ID: $it を紐づけました" }
        }
    }

    /**
     * 書籍と著者関連情報を更新する
     * @param bookId 書籍情報
     * @param authorIdList 著者情報
     */
    private fun updateAssociation(bookId: Int, authorIdList: List<String>) {
        // 既存の関連情報を削除
        dsl.deleteFrom(AuthorBook.AUTHOR_BOOK)
            .where(AuthorBook.AUTHOR_BOOK.BOOK_ID.eq(bookId))
            .execute()
        _logger.info { "書籍ID: $bookId に紐づく著者情報を削除しました" }

        // 新しい著者情報を登録
        insertAssociation(bookId, authorIdList)
    }

    /**
     * 書籍に紐づく著者情報を取得する
     * @param bookId 書籍ID
     * @return 紐づく著者のIDリスト
     */
    private fun findAuthorsByBookId(bookId: String): List<String> {
        val authorIdRecords = dsl.select(AUTHOR_BOOK.AUTHOR_ID)
            .from(AUTHOR_BOOK)
            .where(AUTHOR_BOOK.BOOK_ID.eq(bookId.toInt())).fetch()
        return authorIdRecords.map { it.getValue(AUTHOR_BOOK.AUTHOR_ID).toString() }
    }


    override fun register(book: Book): Book? {
        // DBに登録する際に、PublicationStatusの値をvalueとして保存する
        val publicationStatus = when (book.publicationStatus.value) {
            PublicationStatus.PUBLISHED.value -> PublicationStatus.PUBLISHED.value
            PublicationStatus.UNPUBLISHED.value -> PublicationStatus.UNPUBLISHED.value
            else -> throw IllegalArgumentException()
        }

        // DBに登録する際に、BigDecimal型に変換して保存する
        val record = dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, book.title)
            .set(BOOKS.PUBLICATION_STATUS, publicationStatus)
            .set(BOOKS.PRICE, BigDecimal(book.price))
            .returning(BOOKS.BOOK_ID, BOOKS.PRICE, BOOKS.TITLE, BOOKS.PUBLICATION_STATUS)
            .fetchOne()

        return record?.let {
            // 書籍と著者関連情報を登録
            val bookId = it.getValue(BOOKS.BOOK_ID)!!
            _logger.info { "書籍ID: $bookId を登録しました" }
            insertAssociation(bookId, book.getAuthors())

            // Bookオブジェクトを生成して返す
            Book(
                bookId = bookId.toString(),
                price = it.getValue(BOOKS.PRICE)!!.toDouble(),
                title = it.getValue(BOOKS.TITLE)!!.toString(),
                publicationStatus = when (it.getValue(BOOKS.PUBLICATION_STATUS)) {
                    PublicationStatus.PUBLISHED.value -> PublicationStatus.PUBLISHED
                    PublicationStatus.UNPUBLISHED.value -> PublicationStatus.UNPUBLISHED
                    else -> throw IllegalArgumentException(INVALID_PUBLICATION)
                }
            )
        }
    }

    override fun updateBook(newBook: Book) {
        // DBに更新する際に、PublicationStatusの値をvalueとして保存する
        val publicationStatus = when (newBook.publicationStatus.value) {
            PublicationStatus.PUBLISHED.value -> PublicationStatus.PUBLISHED.value
            PublicationStatus.UNPUBLISHED.value -> PublicationStatus.UNPUBLISHED.value
            else -> throw IllegalArgumentException("publicationStatusの値は無効です")
        }

        // DBに更新する際に、BigDecimal型に変換して保存する
        dsl.update(BOOKS)
            .set(BOOKS.TITLE, newBook.title)
            .set(BOOKS.PUBLICATION_STATUS, publicationStatus)
            .set(BOOKS.PRICE, BigDecimal(newBook.price))
            .where(BOOKS.BOOK_ID.eq(newBook.bookId!!.toInt()))
            .execute()

        updateAssociation(newBook.bookId!!.toInt(), newBook.getAuthors())
        _logger.info { "書籍ID: ${newBook.bookId} を更新しました" }
    }

    override fun findBookBy(bookId: String): Book? {
        // 書籍情報を取得
        val bookRecord = dsl.selectFrom(BOOKS).where(BOOKS.BOOK_ID.eq(bookId.toInt())).fetchOne()

        val authors = findAuthorsByBookId(bookId)

        // bookRecordがnullでない場合、Bookオブジェクトを生成して返す
        return bookRecord?.let {
            val foundBook = Book(
                bookId = it.getValue(BOOKS.BOOK_ID)!!.toString(),
                price = it.getValue(BOOKS.PRICE)!!.toDouble(),
                title = it.getValue(BOOKS.TITLE)!!.toString(),
                publicationStatus = when (it.getValue(BOOKS.PUBLICATION_STATUS)) {
                    PublicationStatus.PUBLISHED.value -> PublicationStatus.PUBLISHED
                    PublicationStatus.UNPUBLISHED.value -> PublicationStatus.UNPUBLISHED
                    else -> throw IllegalArgumentException(INVALID_PUBLICATION)
                }
            )
            // 著者情報を追加
            foundBook.addAuthor(authors)
            return foundBook
        }
    }

    override fun findBookByAuthorId(authorId: String): List<Book> {
        val bookIdList = findBooksByAuthorId(authorId)
        return bookIdList.map { findBookBy(it)!! }
    }


    companion object {
        private const val INVALID_PUBLICATION = "publicationStatusの値は無効です"

    }
}
