package com.quo.book.manager.service.author

import com.quo.book.manager.dto.BookManagerApiResponse
import com.quo.book.manager.dto.ResponseStatus
import com.quo.book.manager.dto.author.AuthorControllerRequest
import com.quo.book.manager.model.author.Author
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Service
class AuthorApplicationService(
    val authorCommandService: AuthorCommandService,
    val authorValidationService: AuthorValidationService
) {
    private val _logger = KotlinLogging.logger {}

    /**
     * 生年月日の文字列をLocalDateに変換する
     * @param birthDate 生年月日の文字列
     * @return LocalDate 生年月日
     */
    private fun convertBirthDate(birthDate: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(birthDate, formatter)
    }

    /**
     * 著者登録
     * @param authorControllerRequest 著者登録情報
     * @return BookManagerApiResponse 登録結果
     * @throws Exception 登録失敗時
     */
    @Transactional
    fun registerAuthor(authorControllerRequest: AuthorControllerRequest): BookManagerApiResponse {
        try{
            // 生年月日をLocalDateに変換
            val birthDate = convertBirthDate(authorControllerRequest.birthDate)

            // 著者情報を作成
            val author = Author(
                authorName = authorControllerRequest.authorName,
                birthDate = birthDate
            )
            // 著者登録し、登録済の著者情報を取得
            val registered = authorCommandService.registerAuthor(author, authorControllerRequest.books)

            return BookManagerApiResponse(
                ResponseStatus.OK, String.format("%s 著者番号:%s", REGISTER_SUCCESS_MESSAGE, registered.authorId!!)
            )

        }catch (e: Exception){
            _logger.error(e) { "著者登録は失敗でした。エラー: $e" }
            return BookManagerApiResponse(
                ResponseStatus.FAILED, String.format(
                    "%s %s",
                    REGISTER_FAILED_MESSAGE, e.message
                )
            )
        }

    }

    /**
     * 著者更新
     * @param authorId 著者ID
     * @param authorControllerRequest 著者更新情報
     * @return BookManagerApiResponse 更新結果
     * @throws Exception 更新失敗時
     */
    @Transactional
    fun updateAuthor(authorId: String, authorControllerRequest: AuthorControllerRequest): BookManagerApiResponse {
        try {
            // 著者が存在しない場合、エラーメッセージを返却
            if (authorValidationService.checkAuthorExists(authorId).not()) {
                return BookManagerApiResponse(
                    ResponseStatus.NOT_FOUND, "著者が見つかりませんでした"
                )
            }

            // 著者に紐づく書籍情報を取得し、著者との関係を削除される場合、全ての著者が1人以上設定されているか確認する
            authorValidationService.checkBookRelationExists(authorId, authorControllerRequest.books)

            // 生年月日をLocalDateに変換
            val birthDate = convertBirthDate(authorControllerRequest.birthDate)

            // 更新対象を作成
            val newAuthor = Author(
                authorId = authorId,
                authorName = authorControllerRequest.authorName,
                birthDate = birthDate
            )
            // 著者更新
            authorCommandService.updateAuthor(authorId, newAuthor, authorControllerRequest.books)

            // 更新成功メッセージを返却
            return BookManagerApiResponse(
                ResponseStatus.OK,
                UPDATE_SUCCESS_MESSAGE
            )

        }catch (e: Exception){
            _logger.error(e) { "著者更新は失敗でした。エラー: $e" }
            return BookManagerApiResponse(
                ResponseStatus.FAILED, String.format(
                    "%s %s",
                    UPDATE_FAILED_MESSAGE, e.message
                )
            )
        }
    }
    companion object {
        private const val REGISTER_FAILED_MESSAGE = "登録失敗"
        private const val REGISTER_SUCCESS_MESSAGE = "登録成功"
        private const val UPDATE_FAILED_MESSAGE = "更新失敗"
        private const val UPDATE_SUCCESS_MESSAGE = "更新成功"
    }
}
