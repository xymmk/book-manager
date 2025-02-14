package com.quo.book.manager.repository

import com.quo.book.manager.record.Book

/**
 *  書籍リポジトリ
 */
interface BookRepository {
    /**
     *  書籍を登録する
     *  @param book 登録する書籍
     */
    fun register(book: Book): Book?

    /**
     *  書籍を更新する
     *  @param oldBook 更新前の書籍
     *  @param newBook 更新後の書籍
     */
    fun updateBook(oldBook: Book, newBook: Book)
}
