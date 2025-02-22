package com.quo.book.manager.service.author

import com.quo.book.manager.model.author.Author
import com.quo.book.manager.repository.AuthorRepository
import com.quo.book.manager.service.book.BookValidationService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import java.util.*

/**
 * 著者コマンドサービス
 */
@Service
class AuthorCommandService(@Lazy val bookValidationService: BookValidationService,
                           val authorRepository: AuthorRepository) {

    /**
     * 著者を登録する
     * @param author 著者情報
     * @param books 書籍IDリスト
     * @return Author? 著者情報
     */
    fun registerAuthor(author: Author, books: List<String>): Author {
        // 書籍IDリストを設定していない場合、著者の情報のみ登録
        if (CollectionUtils.isEmpty(books)) {
            val registeredAuthor = authorRepository.register(author)
            require(registeredAuthor != null) { "著者の登録に失敗しました" }
            return registeredAuthor
        }

        bookValidationService.checkAllBooksExists(books)

        // 著者に書籍を追加
        author.addBooks(books)

        // 著者を登録
        val registeredAuthor = authorRepository.register(author)
        require(registeredAuthor != null) { "著者の登録に失敗しました" }
        return registeredAuthor
    }

    /**
     * 著者を更新する
     * @param oldAuthorId 更新前の著者ID
     * @param newAuthor 更新後の著者
     * @param books 書籍IDリスト
     */
    fun updateAuthor(oldAuthorId: String, newAuthor: Author, books: List<String>) {
        val updatedAuthor = newAuthor.copy(authorId = oldAuthorId)

        // 書籍IDリストは空と設定されている場合、書籍情報も空と設定し、著者の書籍情報を削除
        if (CollectionUtils.isEmpty(books)) {
            updatedAuthor.updateBook(Collections.emptyList())
            // 著者を更新し、処理を終了
            authorRepository.updateAuthor(updatedAuthor)
            return
        }

        bookValidationService.checkAllBooksExists(books)

        updatedAuthor.updateBook(books)

        // 著者を更新
        authorRepository.updateAuthor(updatedAuthor)
    }
}
