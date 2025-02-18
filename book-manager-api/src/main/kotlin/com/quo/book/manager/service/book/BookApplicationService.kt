package com.quo.book.manager.service.book

import com.quo.book.manager.dto.BookManagerApiResponse
import com.quo.book.manager.dto.ResponseStatus
import com.quo.book.manager.dto.author.AuthorInfoResponse
import com.quo.book.manager.dto.book.BookControllerRequest
import com.quo.book.manager.dto.book.BookInfoResponse
import com.quo.book.manager.dto.book.BookResponseData
import com.quo.book.manager.model.book.Book
import com.quo.book.manager.service.author.AuthorService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * 書籍登録アプリケーションサービス
 */
@Service
class BookApplicationService(val bookService: BookService, val authorService: AuthorService) {
    private val _logger = KotlinLogging.logger {}

    private fun getAuthorInfoBy(authorId: String): AuthorInfoResponse {
        val author = authorService.findAuthorBy(authorId)
        return AuthorInfoResponse(
            id = author!!.authorId!!,
            name = author.authorName,
            birth = author.birthDate.toString()
        )
    }

    private fun getAllAuthorsInfo(book: Book): List<AuthorInfoResponse> {
        return book.getAuthors().map {
            getAuthorInfoBy(it)
        }
    }

    /**
     * 書籍登録
     * @param bookControllerRequest 書籍登録情報
     * @return BookManagerApiResponse 登録結果
     * @throws Exception 登録失敗時
     */
    @Transactional
    fun registerBook(bookControllerRequest: BookControllerRequest): BookManagerApiResponse {
        try {
            val book = Book(
                title = bookControllerRequest.title,
                price = bookControllerRequest.price,
                publicationStatus = bookControllerRequest.publicationStatus
            )

            // 書籍登録し、登録済の書籍情報を取得
            val registeredBook = bookService.registerBook(book, bookControllerRequest.authors)

            return BookManagerApiResponse(
                ResponseStatus.OK,
                String.format("%s 書籍番号:%s", REGISTER_SUCCESS_MESSAGE, registeredBook.bookId!!)
            )
        } catch (e: Exception) {
            _logger.error(e) { "書籍登録は失敗でした。エラー: $e" }
            return BookManagerApiResponse(
                ResponseStatus.FAILED, String.format(
                    "%s %s",
                    REGISTER_FAILED_MESSAGE, e.message
                )
            )
        }
    }

    /**
     * 書籍更新
     * @param oldBookId 更新対象書籍ID
     * @param request 書籍更新情報
     * @return BookManagerApiResponse 更新結果
     * @throws Exception 更新失敗時
     */
    @Transactional
    fun updateBook(oldBookId: String, request: BookControllerRequest): BookManagerApiResponse {
        try{
            // 更新対象を作成
            val newBook = Book(
                title = request.title,
                price = request.price,
                publicationStatus = request.publicationStatus
            )
            // 書籍更新
            bookService.updateBook(oldBookId, newBook, request.authors)

            // 更新成功メッセージを返却
            return BookManagerApiResponse(
                ResponseStatus.OK,
                UPDATE_SUCCESS_MESSAGE
            )
        }catch (e: Exception){
            _logger.error(e) { "書籍更新は失敗でした。エラー: $e" }
            return BookManagerApiResponse(
                ResponseStatus.FAILED, String.format(
                    "%s %s",
                    UPDATE_FAILED_MESSAGE, e.message
                )
            )
        }
    }

    fun getBooksInfoByAuthorId(authorId: String): BookInfoResponse {
        try{
            val books = bookService.getBooksInfoByAuthorId(authorId)
            val bookResponseList = books.map {
                val authorInfoResponseList = getAllAuthorsInfo(it)
                BookResponseData(
                    bookId = it.bookId!!,
                    price = it.price.toString(),
                    title = it.title,
                    publicationStatus = it.publicationStatus.description,
                    authors = authorInfoResponseList
                )
            }
            return BookInfoResponse(ResponseStatus.OK, bookResponseList)
        }catch (e: Exception){
            _logger.error(e) { "著者ID: $authorId に対する書籍情報取得は失敗でした。エラー: $e" }
            return BookInfoResponse(
                ResponseStatus.FAILED, Collections.emptyList()
            )
        }
    }

    companion object {
        private const val REGISTER_FAILED_MESSAGE = "登録失敗"
        private const val REGISTER_SUCCESS_MESSAGE = "登録成功"
        private const val UPDATE_FAILED_MESSAGE = "更新失敗"
        private const val UPDATE_SUCCESS_MESSAGE = "更新成功"
    }
}
