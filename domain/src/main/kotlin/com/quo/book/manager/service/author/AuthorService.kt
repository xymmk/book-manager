package com.quo.book.manager.service.author

import com.quo.book.manager.model.author.Author
import com.quo.book.manager.repository.AuthorRepository
import com.quo.book.manager.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import java.util.*

/**
 * 著者検索サービス
 */
@Service
class AuthorService(
    val bookRepository: BookRepository,
    val authorRepository: AuthorRepository
) {


    /**
     * リストの書籍IDは登録済となっているかどうか確認する
     * @param books 書籍IDリスト
     * @return Boolean 全ての書籍が登録済の場合はtrue
     */
    private fun checkAllBooksExists(books: List<String>): Boolean {
        val foundBooks = books.map { bookId ->
            require(bookId.isNotEmpty()) { "書籍IDが不正です" }
            bookRepository.findBookBy(bookId)
        }
        return foundBooks.all { it != null }
    }


    /**
     * 著者を著者IDで検索する
     * @param authorId 著者ID
     * @return Author? 著者情報
     */
    fun findAuthorBy(authorId: String): Author? {
        return authorRepository.findAuthorBy(authorId)
    }


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

        require(checkAllBooksExists(books)) { "リストの中に登録していない書籍があります" }

        // 著者に書籍を追加
        author.addBooks(books)

        // 著者を登録
        val registeredAuthor = authorRepository.register(author)
        require(registeredAuthor != null) { "著者の登録に失敗しました" }
        return registeredAuthor
    }

    fun updateAuthor(oldAuthorId: String, newAuthor: Author, books: List<String>) {
        val oldAuthor = findAuthorBy(oldAuthorId)
        requireNotNull(oldAuthor) { "著者が存在しません" }

        val updatedAuthor = Author(
            authorId = oldAuthorId,
            authorName = newAuthor.authorName,
            birthDate = newAuthor.birthDate
        )

        // 書籍IDリストは空と設定されている場合、書籍情報も空と設定し、著者の書籍情報を削除
        if (CollectionUtils.isEmpty(books)) {
            updatedAuthor.updateBook(Collections.emptyList())
            // 著者を更新し、処理を終了
            authorRepository.updateAuthor(updatedAuthor)
            return
        }

        require(checkAllBooksExists(books)) { "リストの中に登録していない書籍があります" }

        updatedAuthor.updateBook(books)

        // 著者を更新
        authorRepository.updateAuthor(updatedAuthor)
    }

}
