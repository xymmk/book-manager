package com.quo.book.manager.controller.author

import com.quo.book.manager.model.PublicationStatus
import com.quo.book.manager.model.book.Book
import com.quo.book.manager.service.author.AuthorService
import com.quo.book.manager.service.book.BookService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureMockMvc
internal class AuthorManageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var authorService: AuthorService

    @Autowired
    private lateinit var bookService: BookService

    @DisplayName("著者登録エンドポイントのテスト - 著者名が空の場合のテスト")
    @Order(1)
    @Test
    fun registerAuthorWithEmptyNameTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 著者名が500文字以上の場合のテスト")
    @Order(2)
    @Test
    fun registerAuthorWithLongNameTest() {
        val longName = "a".repeat(501)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "$longName",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 生年月日が空の場合のテスト")
    @Order(3)
    @Test
    fun registerAuthorWithNullBirthDateTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 生年月日が不正な形式の場合のテスト")
    @Order(4)
    @Test
    fun registerAuthorWithInvalidBirthDateFormatTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "01-01-2021",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 生年月日が現在時刻の場合のテスト")
    @Order(5)
    @Test
    fun registerAuthorWithNowBirthDateFormatTest() {
        val birthDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "$birthDate",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"

        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日は現在の日付より過去と設定してください"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 書籍IDが無効な場合")
    @Order(6)
    @Test
    fun registerAuthorWithInvalidBookIdsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "2021-01-01",
                        "books": ["100"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない書籍があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト")
    @Order(7)
    @Test
    fun registerAuthorTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者番号:"), "著者登録成功メッセージが含まれていること")

        // 登録された著者IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredAuthorId = message.split("著者番号:")[1].trim()
        Assertions.assertTrue(
            authorService.findAuthorBy(registeredAuthorId)!!.authorName == "test author",
            "著者名が登録されていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 書籍ID付き登録")
    @Order(8)
    @Test
    fun registerAuthorWithBookTest() {
        // 書籍登録
        val book1 = Book(
            price = 1000.1323,
            title = "test book1",
            publicationStatus = PublicationStatus.UNPUBLISHED
        )
        // 書籍登録
        val book2 = Book(
            price = 1000.1323,
            title = "test book2",
            publicationStatus = PublicationStatus.UNPUBLISHED
        )
        bookService.registerBook(book1, listOf("1"))
        bookService.registerBook(book2, listOf("1"))

        // 書籍1と書籍2に紐づく著者を登録
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author2",
                        "birth_date": "2021-01-01",
                        "books": ["1", "2"]
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者番号:"), "著者登録成功メッセージが含まれていること")

        // 登録された著者IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredAuthorId = message.split("著者番号:")[1].trim()

        // 著者名が登録されているか確認
        Assertions.assertTrue(
            authorService.findAuthorBy(registeredAuthorId)!!.authorName == "test author2",
            "著者名が登録されていること"
        )
        val books = bookService.getBooksInfoByAuthorId(registeredAuthorId)

        // 著者に紐づく書籍情報が登録されているか確認
        Assertions.assertTrue(
            books.all { it.getAuthors().contains("1") && it.getAuthors().contains("2") },
            "著者に紐づく書籍情報が登録されていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 著者が存在しない場合")
    @Order(9)
    @Test
    fun updateAuthorWithNonExistentAuthorTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1000/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "new author",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者が存在しません"), "エラーメッセージが含まれていること")
    }


    @DisplayName("著者更新エンドポイントのテスト - 著者名が空の場合")
    @Order(10)
    @Test
    fun updateAuthorWithEmptyNameTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 著者名が500文字以上の場合")
    @Order(11)
    @Test
    fun updateAuthorWithLongNameTest() {
        val longName = "a".repeat(501)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "$longName",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 生年月日が空の場合")
    @Order(12)
    @Test
    fun updateAuthorWithNullBirthDateTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 生年月日が不正な形式の場合")
    @Order(13)
    @Test
    fun updateAuthorWithInvalidBirthDateFormatTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "01-01-2021",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 生年月日が現在時刻の場合のテスト")
    @Order(14)
    @Test
    fun updateAuthorWithNowBirthDateFormatTest() {
        val birthDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "$birthDate",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日は現在の日付より過去と設定してください"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 書籍IDが無効な場合")
    @Order(15)
    @Test
    fun updateAuthorWithInvalidBookIdsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "2021-01-01",
                        "books": ["100"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない書籍があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト")
    @Order(16)
    @Test
    fun updateAuthorTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "updated author",
                        "birth_date": "2021-01-01",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること");
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること");
        val author = authorService.findAuthorBy("1")
        Assertions.assertTrue(author!!.authorName == "updated author", "著者名が更新されていること")
        Assertions.assertTrue(author!!.birthDate == LocalDate.parse("2021-01-01"), "生年月日が更新されていること")
    }

    @DisplayName("著者登録エンドポイントのテスト - 無効なJSON形式の場合")
    @Order(17)
    @Test
    fun registerAuthorWithInvalidJsonTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リクエストの形式が不正です"),
            "JSON解析エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 必須フィールドが欠落している場合")
    @Order(18)
    @Test
    fun registerAuthorWithMissingFieldsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "birth_date": "2021-01-01",
                    "books": []
                }
                """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リクエストの形式が不正です"),
            "必須フィールド欠落エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 著者名の境界値テスト")
    @Order(19)
    @Test
    fun registerAuthorWithBoundaryNameTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "a",
                    "birth_date": "2021-01-01",
                    "books": []
                }
                """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者番号:"), "著者登録成功メッセージが含まれていること")
    }

    @DisplayName("著者登録エンドポイントのテスト - 無効な書籍IDが含まれている場合")
    @Order(20)
    @Test
    fun registerAuthorWithInvalidBookIdTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "test author",
                    "birth_date": "2021-01-01",
                    "books": ["invalid_id"]
                }
                """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
    }
}
