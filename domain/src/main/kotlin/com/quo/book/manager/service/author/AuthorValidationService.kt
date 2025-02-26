package com.quo.book.manager.service.author

import com.quo.book.manager.repository.AuthorRepository
import com.quo.book.manager.service.book.BookQueryService
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils

/**
 * 著者検証サービス
 */
@Service
class AuthorValidationService(
    val authorRepository: AuthorRepository,
    val bookQueryService: BookQueryService
) {

    /**
     * リストの著者IDは登録済となっているかどうか確認する
     * @param authors 著者IDリスト
     */
    fun checkALlAuthorsExists(authors: List<String>) {
        val foundAuthors = authors.map { authorId ->
            require(authorId.isNotEmpty()) { "著者IDが不正です" }
            authorRepository.findAuthorBy(authorId)
        }
        require(foundAuthors.all { it != null }) { "リストの中に登録していない著者があります" }
    }

    /**
     * 著者IDが登録済となっているかどうか確認する
     * @param authorId 著者ID
     * @return Boolean 著者が登録済の場合はtrue
     */
    fun checkAuthorExists(authorId: String): Boolean {
        require(authorId.isNotEmpty()) { "著者IDが不正です" }
        return authorRepository.findAuthorBy(authorId) != null
    }

    /**
     * 著者に紐づく書籍情報を取得し、著者との関係を削除される場合、全ての著者が1人以上設定されているか確認する
     * @param oldAuthorId 著者ID
     * @param bookList 書籍IDリスト
     * @throws IllegalArgumentException 著者に紐づく書籍が1人以上設定されていない場合
     */
    fun checkBookRelationExists(oldAuthorId: String, bookList: List<String>) {
        // 著者に紐づく書籍情報を取得
        val books = bookQueryService.getBooksInfoByAuthorId(oldAuthorId)

        // 著者に紐づく書籍を確認し、著者との関係を削除される場合、全ての著者が1人以上設定されているか確認
        books.forEach { book ->
            if (bookList.contains(book.bookId).not()) {
                val deletedOldAuthorList = book.getAuthors().filter { it != oldAuthorId }
                require(
                    CollectionUtils.isEmpty(deletedOldAuthorList).not()
                ) { "書籍番号: ${book.bookId} に紐づく著者は1人以上設定する必要があります。" }

            }
        }
    }


}
