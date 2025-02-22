package com.quo.book.manager.service.book

import com.quo.book.manager.model.PublicationStatus
import com.quo.book.manager.model.book.Book
import com.quo.book.manager.repository.BookRepository
import org.springframework.stereotype.Service

/**
 * 書籍サービス
 */
@Service
class BookValidationService(val bookRepository: BookRepository) {

    /**
     * リストの書籍IDは登録済となっているかどうか確認する
     * @param books 書籍IDリスト
     * @return Boolean 全ての書籍が登録済の場合はtrue
     */
    fun checkAllBooksExists(books: List<String>) {
        val foundBooks = books.map { bookId ->
            require(bookId.isNotEmpty()) { "書籍IDが不正です" }
            bookRepository.findBookBy(bookId)
        }
        require(foundBooks.all { it != null }) { "リストの中に登録していない書籍があります" }
    }

    /**
     * 書籍IDが登録済となっているかどうか確認する
     * @param bookId 書籍ID
     * @return Boolean 登録済の場合はtrue
     */
    fun checkBookExists(bookId: String): Boolean {
        require(bookId.isNotEmpty()) { "書籍IDが不正です" }
        return bookRepository.findBookBy(bookId) != null
    }

    /**
     * 書籍の公開状態が変更可能かどうか確認する
     * @param oldBook 更新前の書籍情報
     * @param newPublicationStatus 更新後の公開状態
     */
    fun validatePublicationStatusChange(oldBook: Book, newPublicationStatus: PublicationStatus) {
        require(
            (oldBook.publicationStatus == PublicationStatus.PUBLISHED &&
                    newPublicationStatus == PublicationStatus.UNPUBLISHED).not()
        ) { "公開済の書籍は非公開に変更できません" }
    }
}
