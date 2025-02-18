package com.quo.book.manager.service.book

import com.quo.book.manager.model.PublicationStatus
import com.quo.book.manager.model.book.Book
import com.quo.book.manager.repository.AuthorRepository
import com.quo.book.manager.repository.BookRepository
import org.springframework.stereotype.Service

/**
 * 書籍サービス
 */
@Service
class BookService(val bookRepository: BookRepository,
                  val authorRepository: AuthorRepository
) {

    /**
     * リストの著者IDは登録済となっているかどうか確認する
     * @param authors 著者IDリスト
     * @return Boolean 全ての著者が登録済の場合はtrue
     */
    private fun checkALlAuthorsExists(authors: List<String>): Boolean {
        val foundAuthors = authors.map { authorId ->
            require(authorId.isNotEmpty()) { "著者IDが不正です" }
            authorRepository.findAuthorBy(authorId)
        }
        return foundAuthors.all { it != null }
    }



    /**
     * 書籍を取得する
     * @param bookId 書籍ID
     * @return Book? 書籍情報
     */
    fun findBookBy(bookId: String): Book? {
        return bookRepository.findBookBy(bookId)
    }

    /**
     * 書籍を登録する
     * @param book 書籍情報
     * @return Book? 登録した書籍情報
     */
    fun registerBook(book: Book, authors: List<String>): Book {
        // 著者リストの著者IDが全て登録済かチェックする
        require(checkALlAuthorsExists(authors)) { "リストの中に登録していない著者があります" }

        // 書籍に著者を追加
        book.addAuthor(authors)

        // 書籍を登録
        val registeredBook = bookRepository.register(book)
        require(registeredBook != null) { "書籍の登録に失敗しました" }

        return registeredBook
    }

    /**
     * 書籍を更新する
     * @param oldBookId 更新前の書籍ID
     * @param newBook 更新後の書籍
     */
    fun updateBook(oldBookId: String, newBook: Book, authors: List<String>) {
        val oldBook = findBookBy(oldBookId)
        requireNotNull(oldBook) { "書籍が存在しません" }

        // 書籍の状態が公開状態から未公開状態に変更する場合、エラーを返却
        require(!(oldBook.publicationStatus == PublicationStatus.PUBLISHED &&
                newBook.publicationStatus == PublicationStatus.UNPUBLISHED)) { "公開済の書籍は非公開に変更できません" }

        // 著者リストの著者IDが全て登録済かチェックする
        require(checkALlAuthorsExists(authors)) { "リストの中に登録していない著者があります" }

        val updatedBook = Book(
            bookId = oldBook.bookId,
            title = newBook.title,
            price = newBook.price,
            publicationStatus = newBook.publicationStatus
        )

        // 著者を更新
        updatedBook.updateAuthor(authors)

        // 書籍を更新
        bookRepository.updateBook(updatedBook)
    }

    /**
     * 著者IDによって、書籍を取得する
     * @param authorId 著者ID
     * @return List<Book> 書籍情報リスト
     */
    fun getBooksInfoByAuthorId(authorId: String): List<Book> {
        return bookRepository.findBookByAuthorId(authorId)
    }

}
