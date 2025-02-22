package com.quo.book.manager.service.book

import com.quo.book.manager.model.book.Book
import com.quo.book.manager.repository.BookRepository
import com.quo.book.manager.service.author.AuthorValidationService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

/**
 * 書籍コマンドサービス
 */
@Service
class BookCommandService(val bookRepository: BookRepository,
                         val bookValidationService: BookValidationService,
                         @Lazy val authorValidationService: AuthorValidationService
) {
    /**
     * 書籍を登録する
     * @param book 書籍情報
     * @return Book? 登録した書籍情報
     */
    fun registerBook(book: Book, authors: List<String>): Book {
        // 著者リストの著者IDが全て登録済かチェックする
        authorValidationService.checkALlAuthorsExists(authors)

        // 書籍に著者を追加
        book.addAuthor(authors)

        // 書籍を登録
        return bookRepository.register(book) ?: throw IllegalArgumentException("書籍の登録に失敗しました")
    }

    /**
     * 書籍を更新する
     * @param oldBookId 更新前の書籍ID
     * @param newBook 更新後の書籍
     */
    fun updateBook(oldBookId: String, newBook: Book, authors: List<String>) {
        // 更新前の書籍情報を取得
        val oldBook = bookRepository.findBookBy(oldBookId) ?: throw IllegalArgumentException("書籍が見つかりません")

        // 書籍の状態が公開状態から未公開状態に変更する場合、エラーを返却
        bookValidationService.validatePublicationStatusChange(oldBook, newBook.publicationStatus)

        // 著者リストの著者IDが全て登録済かチェックする
        authorValidationService.checkALlAuthorsExists(authors)

        val updatedBook = newBook.copy(bookId = oldBookId)

        // 著者を更新
        updatedBook.updateAuthor(authors)

        // 書籍を更新
        bookRepository.updateBook(updatedBook)
    }
}
