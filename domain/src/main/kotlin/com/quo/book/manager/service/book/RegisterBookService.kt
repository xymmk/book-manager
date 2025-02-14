package com.quo.book.manager.service.book

import com.quo.book.manager.record.Book
import com.quo.book.manager.repository.BookRepository
import org.springframework.stereotype.Service

/**
 * 書籍登録サービス
 */
@Service
class RegisterBookService(val bookRepository: BookRepository) {
    /**
     * 書籍を登録する
     * @param book 書籍情報
     * @return Book? 登録した書籍情報
     */
    fun registerBook(book: Book): Book? {
        return bookRepository.register(book)
    }
}
