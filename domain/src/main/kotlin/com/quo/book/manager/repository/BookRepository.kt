package com.quo.book.manager.repository

import com.quo.book.manager.model.book.Book

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
     *  @param newBook 更新後の書籍
     */
    fun updateBook(newBook: Book)

    /**
     *  bookIdによって、書籍を取得する
     *  @param bookId 書籍ID
     *  @return Book? 書籍情報
     */
    fun findBookBy(bookId: String): Book?

    /**
     *  著者IDによって、書籍を取得する
     *  @param authorId 著者ID
     *  @return List<Book> 書籍情報リスト
     */
    fun findBookByAuthorId(authorId: String): List<Book>
}
