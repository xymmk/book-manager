package com.quo.book.manager.repository

import com.quo.book.manager.record.Author

/**
 * 著者リポジトリ
 */
interface AuthorRepository {
    /**
     * 著者を著者IDで検索する
     * @param authorId 著者ID
     * @return author 著者
     */
    fun findAuthorBy(authorId: String): Author?
    fun register(author: Author): Author?
}
