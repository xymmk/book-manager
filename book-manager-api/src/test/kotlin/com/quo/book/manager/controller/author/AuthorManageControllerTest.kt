package com.quo.book.manager.controller.author

import com.quo.book.manager.service.author.AuthorQueryService
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureMockMvc
internal class AuthorManageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var authorQueryService: AuthorQueryService

    @Autowired
    private lateinit var bookQueryService: BookQueryService

    private val registeredAuthorId = "9000"

    private val bookIdNotExists = "100"

    @DisplayName("著者登録エンドポイントのテスト - 著者名が500文字以上の場合のテスト")
    @Order(1)
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 著者名が空の場合のテスト")
    @Order(2)
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 著者名の境界値テスト")
    @Order(3)
    @Test
    fun registerAuthorWithBoundaryNameTest() {
        val longName = "a".repeat(500)
        val birthDate = "2021-01-01"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "$longName",
                    "birth_date": "$birthDate",
                    "books": []
                }
                """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者番号:"), "著者登録成功メッセージが含まれていること")
        // 登録された著者IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredAuthorId = message.split("著者番号:")[1].trim()
        val author = authorQueryService.findAuthorBy(registeredAuthorId)
        Assertions.assertEquals(
            longName,
            author!!.authorName,
            "著者名が登録されていること"
        )
        Assertions.assertEquals(
            LocalDate.parse(
                birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ), author.birthDate,
            "生年月日が登録されていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 生年月日が空の場合のテスト")
    @Order(4)
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 生年月日が不正な形式の場合のテスト")
    @Order(5)
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 生年月日が現在時刻の場合のテスト")
    @Order(6)
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

        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日は現在の日付より過去と設定してください"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 書籍IDが登録していない場合")
    @Order(7)
    @Test
    fun registerAuthorWitNotExistsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "2021-01-01",
                        "books": ["$bookIdNotExists"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない書籍があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 無効な書籍IDが含まれている場合")
    @Order(8)
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
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
    }

    @DisplayName("著者登録エンドポイントのテスト - 必須フィールドが欠落している場合")
    @Order(9)
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リクエストの形式が不正です"),
            "必須フィールド欠落エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 無効なJSON形式の場合")
    @Order(10)
    @Test
    fun registerAuthorWithInvalidJsonTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リクエストの形式が不正です"),
            "JSON解析エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト")
    @Order(11)
    @Test
    fun registerAuthorTest() {
        val birthDate = "2021-01-01"
        val name = "registerAuthorTest"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "$name",
                        "birth_date": "$birthDate",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者番号:"), "著者登録成功メッセージが含まれていること")

        // 登録された著者IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredAuthorId = message.split("著者番号:")[1].trim()
        val author = authorQueryService.findAuthorBy(registeredAuthorId)
        Assertions.assertEquals(
            name,
            author!!.authorName,
            "著者名が登録されていること"
        )
        Assertions.assertEquals(
            LocalDate.parse(
                birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ), author.birthDate,
            "生年月日が登録されていること"
        )
    }

    @DisplayName("著者登録エンドポイントのテスト - 書籍ID付き登録")
    @Order(12)
    @Test
    fun registerAuthorWithBookTest() {
        val registeredBookId1 = "8000"
        val registeredBookId2 = "8001"
        val registeredBookId3 = "8002"
        val registeredBookId4 = "8003"
        val birthDate = "2021-01-01"
        val name = "registerAuthorWithBookTest"

        // 書籍1と書籍2に紐づく著者を登録
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/author/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "$name",
                        "birth_date": "$birthDate",
                        "books": ["$registeredBookId1", "$registeredBookId2", "$registeredBookId3", "$registeredBookId4"]
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者番号:"), "著者登録成功メッセージが含まれていること")

        // 登録された著者IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredAuthorId = message.split("著者番号:")[1].trim()
        val author = authorQueryService.findAuthorBy(registeredAuthorId)
        LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // 著者名が登録されているか確認
        Assertions.assertEquals(
            name,
            author!!.authorName,
            "著者名が登録されていること"
        )
        Assertions.assertEquals(
            LocalDate.parse(
                birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ), author.birthDate,
            "生年月日が登録されていること"
        )
        val books = bookQueryService.getBooksInfoByAuthorId(registeredAuthorId)

        // 著者に紐づく書籍情報が登録されているか確認
        Assertions.assertTrue(
            books.all { it.getAuthors().contains(registeredAuthorId) },
            "著者に紐づく書籍情報が登録されていること"
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // 著者更新エンドポイントのテスト
    ////////////////////////////////////////////////////////////////////////////////////

    @DisplayName("著者更新エンドポイントのテスト - 著者が存在しない場合")
    @Order(13)
    @Test
    fun updateAuthorWithNonExistentAuthorTest() {
        val authorIdNotExists = "1000"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$authorIdNotExists/update")
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
        Assertions.assertEquals(404, result.response.status, "HTTPステータスコードが404であること")
        Assertions.assertTrue(result.response.contentAsString.contains("著者が見つかりませんでした"), "エラーメッセージが含まれていること")
    }


    @DisplayName("著者更新エンドポイントのテスト - 著者名が空の場合")
    @Order(14)
    @Test
    fun updateAuthorWithEmptyNameTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 著者名が500文字以上の場合")
    @Order(15)
    @Test
    fun updateAuthorWithLongNameTest() {
        val longName = "a".repeat(501)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者名は1文字以上500文字以下です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 著者名の境界値テスト")
    @Order(16)
    @Test
    fun updateAuthorWithBoundaryNameTest() {
        val longName = "a".repeat(500)
        val birthDate = "2021-01-01"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "$longName",
                    "birth_date": "$birthDate",
                    "books": []
                }
                """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        val author = authorQueryService.findAuthorBy(registeredAuthorId)
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること")
        Assertions.assertEquals(longName, author!!.authorName, "著者名が更新されていること")
        Assertions.assertEquals(LocalDate.parse(birthDate), author.birthDate, "生年月日が更新されていること")
    }

    @DisplayName("著者更新エンドポイントのテスト - 生年月日が空の場合")
    @Order(17)
    @Test
    fun updateAuthorWithNullBirthDateTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 生年月日が不正な形式の場合")
    @Order(18)
    @Test
    fun updateAuthorWithInvalidBirthDateFormatTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
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
        Assertions.assertEquals(400, result.response.status, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日はYYYY-MM-DD形式である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 生年月日が現在時刻の場合のテスト")
    @Order(19)
    @Test
    fun updateAuthorWithNowBirthDateFormatTest() {
        val birthDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
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
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("生年月日は現在の日付より過去と設定してください"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 書籍IDが登録していない場合")
    @Order(20)
    @Test
    fun updateAuthorWithNotExistsTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "test author",
                        "birth_date": "2021-01-01",
                        "books": ["$bookIdNotExists"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない書籍があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 著者との関係を削除される場合、全ての著者が1人以上設定されていない場合")
    @Order(21)
    @Test
    fun updateAuthorDeleteBookRelation() {
        val updateAuthorId = "6003"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$updateAuthorId/update")
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
        Assertions.assertEquals(500, result.response.status, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("著者は1人以上設定する必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト")
    @Order(22)
    @Test
    fun updateAuthorTest() {
        val name = "updateAuthorTest"
        val birthDate = "1800-01-01"
        val registeredBookId1 = "8000"
        val registeredBookId2 = "8002"
        val registeredBookId3 = "8003"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "$name",
                        "birth_date": "$birthDate",
                        "books": []
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること")

        // 更新された著者情報を取得
        val author = authorQueryService.findAuthorBy(registeredAuthorId)
        Assertions.assertEquals(name, author!!.authorName, "著者名が更新されていること")
        Assertions.assertEquals(LocalDate.parse(birthDate), author.birthDate, "生年月日が更新されていること")
        val book1 = bookQueryService.findBookBy(registeredBookId1)
        val book2 = bookQueryService.findBookBy(registeredBookId2)
        val book3 = bookQueryService.findBookBy(registeredBookId3)
        Assertions.assertTrue(
            listOf(book1, book2, book3).all { !(it!!.getAuthors().contains(registeredAuthorId)) },
            "著者に紐づく書籍情報が削除されていること"
        )
    }

    @DisplayName("著者更新エンドポイントのテスト - 書籍ID付き更新")
    @Order(23)
    @Test
    fun updateAuthorWithBookTest() {
        val name = "updateAuthorWithBookTest"
        val birthDate = "1900-01-01"
        val registeredBookId1 = "8000"
        val registeredBookId2 = "8002"
        val registeredBookId3 = "8003"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/author/$registeredAuthorId/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "$name",
                        "birth_date": "$birthDate",
                        "books": ["$registeredBookId1", "$registeredBookId2", "$registeredBookId3"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertEquals(200, result.response.status, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること")

        // 更新された著者情報を取得
        val author = authorQueryService.findAuthorBy(registeredAuthorId)
        Assertions.assertEquals(name, author!!.authorName, "著者名が更新されていること")
        Assertions.assertEquals(LocalDate.parse(birthDate), author.birthDate, "生年月日が更新されていること")
        val book1 = bookQueryService.findBookBy(registeredBookId1)
        val book2 = bookQueryService.findBookBy(registeredBookId2)
        val book3 = bookQueryService.findBookBy(registeredBookId3)
        Assertions.assertTrue(
            listOf(book1, book2, book3).all { it!!.getAuthors().contains(registeredAuthorId) },
            "著者に紐づく書籍情報が登録されていること"
        )
    }

}
