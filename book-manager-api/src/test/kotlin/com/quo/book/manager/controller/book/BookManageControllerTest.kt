package com.quo.book.manager.controller.book

import com.quo.book.manager.model.PublicationStatus
import com.quo.book.manager.model.book.Book
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
internal class BookManageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookService: BookService

    @DisplayName("書籍登録エンドポイントのテスト - 書籍タイトルが空の場合のテスト")
    @Order(1)
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
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍タイトルは必須です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 書籍価格が負の場合のテスト")
    @Order(2)
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
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍価格は0以上である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト - 著者IDが無効な場合")
    @Order(3)
    @Test
    fun registerBookWithInvalidAuthorIdsTest() {
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
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない著者があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍登録エンドポイントのテスト")
    @Order(4)
    @Test
    fun registerBookTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/book/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()

        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("書籍番号:"), "書籍登録成功メッセージが含まれていること")

        // 登録された書籍IDを取得
        val objectMapper = ObjectMapper()
        val jsonResponse: JsonNode = objectMapper.readTree(result.response.contentAsString)
        val message = jsonResponse.get("message").asText()
        val registeredBookId = message.split("書籍番号:")[1].trim()
        Assertions.assertTrue(
            bookService.findBookBy(registeredBookId)!!.title == "test book",
            "書籍タイトルが登録されていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍が存在しない場合")
    @Order(5)
    @Test
    fun updateBookWithNonExistentBookTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/1000/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "new book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(result.response.contentAsString.contains("書籍が存在しません"), "エラーメッセージが含まれていること")
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍タイトルが空の場合")
    @Order(6)
    @Test
    fun updateBookWithEmptyTitleTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍タイトルは必須です"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍価格が負の場合")
    @Order(7)
    @Test
    fun updateBookWithNegativePriceTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": -1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 400, "HTTPステータスコードが400であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("書籍価格は0以上である必要があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト - 書籍の状態が公開状態から未公開状態に変更する場合")
    @Order(8)
    @Test
    fun updateBookWithInvalidPublicationStatusChangeTest() {
        // まず書籍を公開状態で登録
        val book = Book(
            price = 1000.0,
            title = "test book",
            publicationStatus = PublicationStatus.PUBLISHED
        )
        bookService.registerBook(book, listOf("1"))

        // 書籍の状態を未公開状態に変更
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/${book.bookId}/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "test book",
                        "price": 1000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("公開済の書籍は非公開に変更できません"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍更新エンドポイントのテスト")
    @Order(9)
    @Test
    fun updateBookTest() {
        // まず書籍を登録
        val book = Book(
            price = 1000.0,
            title = "test book",
            publicationStatus = PublicationStatus.UNPUBLISHED
        )
        bookService.registerBook(book, listOf("1"))

        // 書籍を更新
        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/book/${book.bookId}/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "title": "updated book",
                        "price": 2000.0,
                        "publication_status": "UNPUBLISHED",
                        "authors": ["1"]
                    }
                    """.trimIndent()
                )
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("更新成功"), "更新成功メッセージが含まれていること")
        val updatedBook = bookService.findBookBy(book.bookId)
        Assertions.assertTrue(updatedBook!!.title == "updated book", "書籍タイトルが更新されていること")
        Assertions.assertTrue(updatedBook.price == 2000.0, "書籍価格が更新されていること")
    }

    @DisplayName("書籍取得エンドポイントのテスト - 著者IDが無効な場合")
    @Order(10)
    @Test
    fun getBooksByInvalidAuthorIdTest() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/book/invalid_id/list")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 500, "HTTPステータスコードが500であること")
        Assertions.assertTrue(
            result.response.contentAsString.contains("リストの中に登録していない著者があります"),
            "エラーメッセージが含まれていること"
        )
    }

    @DisplayName("書籍取得エンドポイントのテスト")
    @Order(11)
    @Test
    fun getBooksByAuthorIdTest() {
        // まず書籍を登録
        val book = Book(
            price = 1000.0,
            title = "test book",
            publicationStatus = PublicationStatus.UNPUBLISHED
        )
        bookService.registerBook(book, listOf("1"))

        // 著者IDで書籍を取得
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/book/1/list")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        result.response.characterEncoding = "UTF-8"
        Assertions.assertTrue(result.response.status == 200, "HTTPステータスコードが200であること")
        Assertions.assertTrue(result.response.contentAsString.contains("書籍情報"), "書籍情報が含まれていること")
    }
}
