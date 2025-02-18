package com.quo.book.manager.repository

import com.quo.book.manager.model.author.Author

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

    /**
     * 著者を登録する
     * @param author 著者情報
     * @return author 著者
     */
    fun register(author: Author): Author?

    /**
     * 著者を更新する
     * @param author 著者情報
     * @return author 著者
     */
    fun updateAuthor(author: Author)
}
