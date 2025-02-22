package com.quo.book.manager.controller.book

import com.quo.book.manager.model.PublicationStatus
import com.quo.book.manager.service.book.BookQueryService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureMockMvc
internal class BookManageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookQueryService: BookQueryService

    // DB初期化の時登録済の著者ID
    private val registeredAuthorId1: String = "9000"
    private val registeredAuthorId2: String = "9001"
    private val registeredAuthorId3: String = "9002"
    private val registeredAuthorId4: String = "9003"

    // DBに存在しない著者ID
    private val authorIdNotExists = "9999"

    // 書籍登録時に登録される書籍ID
    private val registeredBookId = "8000"

    @DisplayName("書籍登録エンドポイントのテスト - 書籍タイトルが空の場合のテスト")
    @Order(101)
    @Test
    fun registerBookWithEmptyTitleTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["$registeredAuthorId1", "$registeredAuthorId2"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍タイトルは必須です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 書籍タイトルが500文字以上の場合のテスト")
    @Order(102)
    @Test
    fun registerBookWithTooLongTitleTest() {
        val longName = "a".repeat(501)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$longName",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["$registeredAuthorId1", "$registeredAuthorId2"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍タイトルは1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 書籍タイトルの境界値テスト")
    @Order(103)
    @Test
    fun registerAuthorWithBoundaryNameTest() {
        val longName = "a".repeat(500)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$longName",
                        "price": 1000.0,
                        "publication_status": "PUBLISHED",
                        "authors": ["$registeredAuthorId1", "$registeredAuthorId2"]
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("書籍番号:"), "書籍登録成功メッセージが含まれていること")
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredBookId = message.split("書籍番号:")[1].trim()
        val book = bookQueryService.findBookBy(registeredBookId)
        Assertions.assertEquals(longName, book!!.title, "書籍タイトルが登録されていること")
        Assertions.assertEquals(1000.0, book.price, "書籍価格が登録されていること")
        Assertions.assertEquals(PublicationStatus.PUBLISHED, book.publicationStatus, "書籍の状態が登録されていること")
        Assertions.assertTrue(
            book.getAuthors().contains(registeredAuthorId1) && book.getAuthors().contains(registeredAuthorId2),
            "著者が登録されていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 書籍価格が負の場合のテスト")
    @Order(104)
    @Test
    fun registerBookWithNegativePriceTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": -1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["$registeredAuthorId1", $registeredAuthorId2]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍価格は0以上である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 著者IDが登録していない場合")
    @Order(105)
    @Test
    fun registerBookWithNotExistsAuthorIdTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": [$authorIdNotExists, $registeredAuthorId2]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない著者があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 著者IDが無効な場合")
    @Order(106)
    @Test
    fun registerBookWithInvalidAuthorIdTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["invalid_id"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
    }

    @DisplayName("書籍登録エンドポイントのテスト - 著者ID設定していない場合")
    @Order(107)
    @Test
    fun registerBookWithoutAuthorIdsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者IDリストは必須です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト")
    @Order(108)
    @Test
    fun registerBookTest() {
        val title = "test book"
        val price = 1000.0
        val publicationStatus = "PUBLISHED"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$title",
                        "price": $price,
                        "publication_status": "$publicationStatus",
                        "authors": ["$registeredAuthorId1", "$registeredAuthorId2"]
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("書籍番号:"), "書籍登録成功メッセージが含まれていること")

        // 登録された書籍IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredBookId = message.split("書籍番号:")[1].trim()
        val registeredBook = bookQueryService.findBookBy(registeredBookId)
        Assertions.assertEquals(
            "test book",
            registeredBook!!.title,
            "書籍タイトルが登録されていること"
        )
        Assertions.assertEquals(
            1000.0,
            registeredBook.price,
            "書籍価格が登録されていること"
        )
        Assertions.assertEquals(
            PublicationStatus.PUBLISHED,
            registeredBook.publicationStatus,
            "書籍の状態が登録されていること"
        )
        Assertions.assertTrue(
            registeredBook.getAuthors().contains(registeredAuthorId1) && registeredBook.getAuthors()
                .contains(registeredAuthorId2), "著者が登録されていること"
        )

    }

    @DisplayName("書籍登録エンドポイントのテスト - 無効な書籍IDが含まれている場合")
    @Order(109)
    @Test
    fun registerBookWithInvalidBookIdTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["invalid_id"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
    }

    @DisplayName("書籍登録エンドポイントのテスト - 無効なJSON形式の場合")
    @Order(110)
    @Test
    fun registerBookWithInvalidJsonTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リクエストの形式が不正です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 必須フィールドが欠落している場合")
    @Order(111)
    @Test
    fun registerBookWithMissingRequiredFieldsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["$registeredAuthorId1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リクエストの形式が不正です"),
            "エラーメッセージが含まれていること"
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // 書籍更新エンドポイントのテスト
    ////////////////////////////////////////////////////////////////////////////////////

    @DisplayName("書籍更新エンドポイントのテスト - 書籍が存在しない場合")
    @Order(112)
    @Test
    fun updateBookWithNonExistentBookTest() {
        val bookNotExists = "10000"
        val title = "updateBookWithNonExistentBookTest"
        val price = 1000.0
        val publicationStatus = "PUBLISHED"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$bookNotExists/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$title",
                        "price": $price,
                        "publication_status": "$publicationStatus",
                        "authors": ["$registeredAuthorId3", "$registeredAuthorId4"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(404, result.response.status, "HTTPステータスコードが404であること")
        Assertions.assertTrue(result.response.contentAsString.contains("書籍が見つかりませんでした"), "エラーメッセージが含まれていること")
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍タイトルが空の場合")
    @Order(113)
    @Test
    fun updateBookWithEmptyTitleTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$registeredBookId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "",
                        "price": 1000.0,
                        "publication_status": "PUBLISHED",
                        "authors": ["$registeredAuthorId3"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍タイトルは必須です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍タイトルが500文字以上の場合")
    @Order(114)
    @Test
    fun updateBookWithLongTitleTest() {
        val longName = "a".repeat(501)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$registeredBookId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$longName",
                        "price": 1000.0,
                        "publication_status": "PUBLISHED",
                        "authors": ["$registeredAuthorId3", "$registeredAuthorId4"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍タイトルは1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )

    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍タイトルの境界値テスト")
    @Order(114)
    @Test
    fun updateBookWithBoundaryTitleTest() {
        val longTitle = "a".repeat(500)
        val price = 9000.0
        val publishedStatus = "PUBLISHED"

        // 書籍を更新
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/${registeredBookId}/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$longTitle",
                        "price": $price,
                        "publication_status": "$publishedStatus",
                        "authors": ["$registeredAuthorId3", "$registeredAuthorId4"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること")
        val updatedBook = bookQueryService.findBookBy(registeredBookId)
        Assertions.assertEquals(longTitle, updatedBook!!.title, "書籍タイトルが更新されていること")
        Assertions.assertEquals(price, updatedBook.price, "書籍価格が更新されていること")
        Assertions.assertEquals(publishedStatus, updatedBook.publicationStatus.value, "書籍の状態が更新されていること")
        Assertions.assertTrue(
            updatedBook.getAuthors().contains(registeredAuthorId3) && updatedBook.getAuthors()
                .contains(registeredAuthorId4), "著者が更新されていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍価格が負の場合")
    @Order(115)
    @Test
    fun updateBookWithNegativePriceTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$registeredBookId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": -1000.0,
                        "publication_status": "PUBLISHED",
                        "authors": ["$registeredAuthorId3"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍価格は0以上である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 著者ID設定していない場合")
    @Order(116)
    @Test
    fun updateBookWithoutAuthorIdsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$registeredBookId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": -1000.0,
                        "publication_status": "PUBLISHED",
                        "authors": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍価格は0以上である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 著者IDが登録していない場合")
    @Order(117)
    @Test
    fun updateBookWithInvalidAuthorIdsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$registeredBookId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "PUBLISHED",
                        "authors": ["$authorIdNotExists"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない著者があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍の状態が公開状態から未公開状態に変更する場合")
    @Order(118)
    @Test
    fun updateBookWithInvalidPublicationStatusChangeTest() {
        val publishedBookId = "8003"

        // 書籍の状態を未公開状態に変更
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/$publishedBookId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["$registeredAuthorId3"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("公開済の書籍は非公開に変更できません"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト")
    @Order(119)
    @Test
    fun updateBookTest() {
        val title = "updateBookTest"
        val price = 2138.17
        val publicationStatus = "PUBLISHED"

        // 書籍を更新
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/${registeredBookId}/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "$title",
                        "price": $price,
                        "publication_status": "$publicationStatus",
                        "authors": ["$registeredAuthorId3", "$registeredAuthorId4"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること")
        val updatedBook = bookQueryService.findBookBy(registeredBookId)
        Assertions.assertEquals(title, updatedBook!!.title, "書籍タイトルが更新されていること")
        Assertions.assertEquals(price, updatedBook.price, "書籍価格が更新されていること")
        Assertions.assertEquals(publicationStatus, updatedBook.publicationStatus.value, "書籍の状態が更新されていること")
        Assertions.assertTrue(
            updatedBook.getAuthors().contains(registeredAuthorId3) && updatedBook.getAuthors()
                .contains(registeredAuthorId4), "著者が更新されていること"
        )
    }

    @DisplayName("書籍取得エンドポイントのテスト - 著者IDが無効な場合")
    @Order(120)
    @Test
    fun getBooksByInvalidAuthorIdTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/book/invalid_id/list")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
    }

    @DisplayName("書籍取得エンドポイントのテスト")
    @Order(121)
    @Test
    fun getBooksByAuthorIdTest() {
        val expectedJson = """
            {"result":"OK","data":[{"book_id":"5000","publication_status":"出版済み","association_authors":[{"id":"6000","name":"Author 6000","birth":"2000-01-01"}],"price":"123.99","title":"title 5000-6000"},{"book_id":"5001","publication_status":"出版済み","association_authors":[{"id":"6000","name":"Author 6000","birth":"2000-01-01"},{"id":"6001","name":"Author 6001","birth":"2000-02-01"},{"id":"6002","name":"Author 6002","birth":"2000-03-01"}],"price":"222.99","title":"title 5001-6000"},{"book_id":"5002","publication_status":"未出版","association_authors":[{"id":"6000","name":"Author 6000","birth":"2000-01-01"},{"id":"6002","name":"Author 6002","birth":"2000-03-01"}],"price":"456.99","title":"title 5002-6000"},{"book_id":"5003","publication_status":"未出版","association_authors":[{"id":"6000","name":"Author 6000","birth":"2000-01-01"},{"id":"6002","name":"Author 6002","birth":"2000-03-01"}],"price":"129.99","title":"title 5003-6000"},{"book_id":"5004","publication_status":"出版済み","association_authors":[{"id":"6000","name":"Author 6000","birth":"2000-01-01"},{"id":"6002","name":"Author 6002","birth":"2000-03-01"}],"price":"99.99","title":"title 5004-6000"},{"book_id":"5005","publication_status":"出版済み","association_authors":[{"id":"6000","name":"Author 6000","birth":"2000-01-01"},{"id":"6001","name":"Author 6001","birth":"2000-02-01"}],"price":"299.99","title":"title 5005-6000"}]}
        """.trimIndent()
        val mapper = ObjectMapper()
        val expected = mapper.readTree(expectedJson)
        val checkAuthorId = "6000"
        // 著者IDで書籍を取得
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/book/${checkAuthorId}/list")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        val actual = mapper.readTree(result.response.contentAsString)
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertEquals(expected, actual, "書籍情報が取得されていること")
    }

}
