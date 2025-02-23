package com.quo.book.manager.model.author

import org.springframework.util.CollectionUtils
import java.time.LocalDate

/**
 * 著者情報
 */
data class Author(
    val authorId: String? = "",
    val authorName: String,
    val birthDate: LocalDate
) {
    private val books: MutableList<String> = mutableListOf()

    init {
        require(birthDate.isBefore(LocalDate.now())) { "生年月日は現在の日付より過去と設定してください" }
    }

    /**
     * 書籍IDを追加する
     * @param bookIdList 書籍IDリスト
     */
    fun addBooks(bookIdList: List<String>) {
        books.addAll(bookIdList)
    }

    /**
     * 書籍IDを更新する
     * @param bookIdList 書籍IDリスト
     */
    fun updateBook(bookIdList: List<String>) {
        books.clear()
        // 書籍IDリストが空の場合は処理を終了
        if (CollectionUtils.isEmpty(bookIdList)) {
            return
        }
        books.addAll(bookIdList)
    }

    /**
     * 書籍IDリストを取得する
     * @return List<String> 書籍IDリスト
     */
    fun getBooks(): List<String> {
        return books
    }
}
