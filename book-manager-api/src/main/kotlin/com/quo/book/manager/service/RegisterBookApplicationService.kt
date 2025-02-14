package com.quo.book.manager.service

import com.quo.book.manager.dto.ResponseStatus
import com.quo.book.manager.dto.book.RegisterBookRequest
import com.quo.book.manager.dto.book.RegisterBookResponse
import com.quo.book.manager.record.Author
import com.quo.book.manager.record.AuthorBooks
import com.quo.book.manager.record.Book
import com.quo.book.manager.service.author.FindAuthorService
import com.quo.book.manager.service.book.RegisterAuthorBooksService
import com.quo.book.manager.service.book.RegisterBookService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍登録アプリケーションサービス
 */
@Service
class RegisterBookApplicationService(
    val registerBookService: RegisterBookService,
    val authorService: FindAuthorService,
    val authorBooksService: RegisterAuthorBooksService
) {

    private val _failedMessage = "登録失敗"
    private val _successMessage = "登録成功"

    /**
     * 著者の情報を取得する
     * @param authors 著者IDリスト
     * @return List<Author?> 著者情報リスト
     */
    private fun getAuthors(authors: List<String>): List<Author?> {
        val foundAuthors = authors.map { authorId ->
            authorService.findAuthorBy(authorId)
        }
        return foundAuthors
    }

    /**
     * 著者と書籍の関連情報を登録する
     * @param book 書籍情報
     * @param authors 著者情報リスト
     */
    private fun registerAuthorBooks(book: Book, authors: List<Author?>) {
        // 著者と書籍の関連情報を作成する
        val authorBooks = authors.mapNotNull { author ->
            author?.let {
                AuthorBooks(
                    authorId = it.authorId!!,
                    authorName = it.authorName,
                    birthDate = it.birthDate,
                    books = listOf(book)
                )
            }
        }
        authorBooks.forEach {
            authorBooksService.registerAuthorBooks(it)
        }
    }

    @Transactional
    fun registerBook(registerBookRequest: RegisterBookRequest): RegisterBookResponse {
        val title = registerBookRequest.title
        val price = registerBookRequest.price
        val authors = registerBookRequest.authors
        val publicationStatus = registerBookRequest.publicationStatus

        // 著者のIDによって、DBのデータ照合し、著者情報を取得
        val foundAuthors = getAuthors(authors)

        // 著者リストが全て存在する場合
        if (!foundAuthors.any { it == null }) {
            // 新しい書籍を作成
            val newBook = Book(
                title = title,
                price = price,
                publicationStatus = publicationStatus
            )
            // 書籍を登録し、登録の書籍の情報を取得
            val book = registerBookService.registerBook(newBook)

            // 書籍が存在する場合
            if (book != null) {
                // 著者と書籍の関連情報を登録
                registerAuthorBooks(book, foundAuthors)

                // 書籍登録成功
                return RegisterBookResponse(
                    ResponseStatus.OK,
                    String.format("%s 書籍番号:%s", _successMessage, book.bookId!!)
                )
            }
            // 書籍登録失敗
            return RegisterBookResponse(ResponseStatus.FAILED, _failedMessage)
        }
        return RegisterBookResponse(ResponseStatus.FAILED, String.format("%s 著者情報が存在しません", _failedMessage))
    }
}
