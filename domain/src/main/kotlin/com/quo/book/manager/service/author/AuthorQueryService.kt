package com.quo.book.manager.service.author

import com.quo.book.manager.model.author.Author
import com.quo.book.manager.repository.AuthorRepository
import org.springframework.stereotype.Service

/**
 * 著者検索サービス
 */
@Service
class AuthorQueryService(val authorRepository: AuthorRepository) {


    /**
     * 著者を著者IDで検索する
     * @param authorId 著者ID
     * @return Author? 著者情報
     */
    fun findAuthorBy(authorId: String): Author? {
        return authorRepository.findAuthorBy(authorId)
    }
}
