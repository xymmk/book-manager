package com.quo.book.manager.model.book

import com.quo.book.manager.model.PublicationStatus

/**
 * 書籍情報
 */
data class Book(
    val bookId: String? = "",
    val price: Double,
    val title: String,
    val publicationStatus: PublicationStatus
) {
    private val authors: MutableList<String> = mutableListOf()

    /**
     * 著者IDを追加する
     * @param authorIdList 著者IDリスト
     */
    fun addAuthor(authorIdList: List<String>) {
        authors.addAll(authorIdList)
    }

    /**
     * 著者情報を更新する
     * @param authorIdList 著者IDリスト
     */
    fun updateAuthor(authorIdList: List<String>) {
        authors.clear()
        authors.addAll(authorIdList)
    }

    /**
     *  著者IDリストを取得する
     * @return List<String> 著者IDリスト
     */
    fun getAuthors(): List<String> {
        return authors
    }
}
