package com.quo.book.manager.service.book

import com.quo.book.manager.model.book.Book
import com.quo.book.manager.repository.BookRepository
import org.springframework.stereotype.Service

/**
 * 書籍クエリサービス
 */
@Service
class BookQueryService(val bookRepository: BookRepository) {
    /**
     * 書籍を取得する
     * @param bookId 書籍ID
     * @return Book? 書籍情報
     */
    fun findBookBy(bookId: String): Book? {
        return bookRepository.findBookBy(bookId)
    }



    /**
     * 著者IDによって、書籍を取得する
     * @param authorId 著者ID
     * @return List<Book> 書籍情報リスト
     */
    fun getBooksInfoByAuthorId(authorId: String): List<Book> {
        return bookRepository.findBookByAuthorId(authorId)
    }
}
