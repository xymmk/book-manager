package com.quo.book.manager.repository

import com.quo.book.manager.record.AuthorBooks

/**
 * 著者と書籍の関連情報リポジトリ
 */
interface AuthorBooksRepository {
    /**
     * 著者と書籍の関連情報を登録する
     * @param authorBooks 著者と書籍の関連情報
     */
    fun register(authorBooks: AuthorBooks)
}
