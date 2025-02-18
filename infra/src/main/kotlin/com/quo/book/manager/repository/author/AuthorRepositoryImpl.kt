package com.quo.book.manager.repository.author

import com.quo.book.manager.jooq.tables.AuthorBook
import com.quo.book.manager.jooq.tables.references.AUTHORS
import com.quo.book.manager.jooq.tables.references.AUTHOR_BOOK
import com.quo.book.manager.model.author.Author
import com.quo.book.manager.repository.AuthorRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepositoryImpl(val dsl: DSLContext) : AuthorRepository {
    private val _logger = KotlinLogging.logger {}


    /**
     * 書籍と著者関連情報を更新する
     * @param authorId 著者情報
     * @param bookIdList 書籍情報
     */
    private fun updateAssociation(authorId: Int, bookIdList: List<String>) {
        // 既存の関連情報を削除
        dsl.deleteFrom(AuthorBook.AUTHOR_BOOK)
            .where(AuthorBook.AUTHOR_BOOK.AUTHOR_ID.eq(authorId))
            .execute()
        _logger.info { "著者ID: $authorId に紐づく書籍情報を削除しました" }

        // 新しい著者情報を登録
        insertAssociation(authorId, bookIdList)
    }

    /**
     * 書籍と著者関連情報を登録する
     * @param authorId 著者情報
     * @param bookIdList 書籍情報
     */
    private fun insertAssociation(authorId: Int, bookIdList: List<String>) = bookIdList.forEach {
        dsl.insertInto(AuthorBook.AUTHOR_BOOK)
            .set(AuthorBook.AUTHOR_BOOK.AUTHOR_ID, authorId)
            .set(AuthorBook.AUTHOR_BOOK.BOOK_ID, it.toInt())
            .execute()
        _logger.info { "著者ID: $authorId に書籍ID: $it を紐づけました" }
    }

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

    override fun findAuthorBy(authorId: String): Author? {
        val authorRecord = dsl.selectFrom(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(authorId.toInt()))
            .fetchOne()
        val bookIdList = findBooksByAuthorId(authorId)
        return authorRecord?.let {
            val author = Author(
                authorId = it.getValue(AUTHORS.AUTHOR_ID)!!.toString(),
                authorName = it.getValue(AUTHORS.AUTHOR_NAME)!!.toString(),
                birthDate = it.getValue(AUTHORS.BIRTH_DATE)!!
            )
            // 著者に紐づく書籍情報を追加
            author.addBooks(bookIdList)
            return author
        }
    }

    override fun register(author: Author): Author? {
        val record = dsl.insertInto(AUTHORS)
            .set(AUTHORS.AUTHOR_NAME, author.authorName)
            .set(AUTHORS.BIRTH_DATE, author.birthDate)
            .returning(AUTHORS.AUTHOR_ID, AUTHORS.AUTHOR_NAME, AUTHORS.BIRTH_DATE)
            .fetchOne()
        return record?.let {
            // 著者と書籍の関連情報を登録
            val authorId = it.getValue(AUTHORS.AUTHOR_ID)!!
            _logger.info { "著者ID: $authorId の情報を登録しました" }
            insertAssociation(authorId, author.getBooks())
            Author(
                authorId = authorId.toString(),
                authorName = it.getValue(AUTHORS.AUTHOR_NAME)!!.toString(),
                birthDate = it.getValue(AUTHORS.BIRTH_DATE)!!
            )
        }
    }

    override fun updateAuthor(author: Author) {
        dsl.update(AUTHORS)
            .set(AUTHORS.AUTHOR_NAME, author.authorName)
            .set(AUTHORS.BIRTH_DATE, author.birthDate)
            .where(AUTHORS.AUTHOR_ID.eq(author.authorId!!.toInt()))
            .execute()
        updateAssociation(author.authorId!!.toInt(), author.getBooks())
        _logger.info { "著者ID: ${author.authorId} の情報を更新しました" }
    }
}
