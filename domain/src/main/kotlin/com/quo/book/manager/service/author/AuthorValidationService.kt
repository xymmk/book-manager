package com.quo.book.manager.service.author

import com.quo.book.manager.repository.AuthorRepository
import org.springframework.stereotype.Service

/**
 * 著者検証サービス
 */
@Service
class AuthorValidationService(val authorRepository: AuthorRepository) {

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


}
