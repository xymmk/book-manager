package com.quo.book.manager.service.book

import com.quo.book.manager.record.AuthorBooks
import com.quo.book.manager.repository.AuthorBooksRepository
import org.springframework.stereotype.Service

/**
 * 著者と書籍の関連情報登録サービス
 */
@Service
class RegisterAuthorBooksService(val authorBooksRepository: AuthorBooksRepository) {
    /**
     * 著者と書籍の関連情報を登録する
     * @param authorBooks 著者と書籍の関連情報
     */
    fun registerAuthorBooks(authorBooks: AuthorBooks) {
        return authorBooksRepository.register(authorBooks)
    }

}
