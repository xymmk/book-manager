package com.quo.book.manager.controller.book

import com.quo.book.manager.dto.BookManagerApiResponse
import com.quo.book.manager.dto.ResponseStatus
import com.quo.book.manager.dto.book.BookControllerRequest
import com.quo.book.manager.dto.book.BookInfoResponse
import com.quo.book.manager.service.book.BookApplicationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 書籍管理コントローラー
 */
@RestController
@RequestMapping("/book")
class BookManageController(val bookApplicationService: BookApplicationService) {

    @PostMapping("/register")
    @Operation(description = "書籍登録", summary = "書籍", tags = ["book"], operationId = "registerBook")
    fun registerBook(@RequestBody @Validated @Parameter(description = "書籍登録情報") bookControllerRequest: BookControllerRequest): ResponseEntity<BookManagerApiResponse> {
        // 書籍を登録し、結果を返却
        val registeredResponse = bookApplicationService.registerBook(bookControllerRequest)

        // 登録結果がOKの場合は200を返却、それ以外は500を返却
        return if (registeredResponse.result == ResponseStatus.OK) {
            ResponseEntity.ok(registeredResponse)
        } else {
            ResponseEntity.status(500).body(registeredResponse)
        }
    }

    @PutMapping("/{book_id}/update")
    @Operation(description = "書籍更新", summary = "書籍", tags = ["book"], operationId = "updateBook")
    fun updateBook(
        @PathVariable(name = "book_id") bookId: String,
        @RequestBody @Validated @Parameter(description = "書籍更新情報") request: BookControllerRequest
    ): ResponseEntity<BookManagerApiResponse> {
        val updatedResponse = bookApplicationService.updateBook(bookId, request)
        return if (updatedResponse.result == ResponseStatus.OK) {
            ResponseEntity.ok(updatedResponse)
        } else {
            ResponseEntity.status(500).body(updatedResponse)
        }
    }

    @GetMapping("/{author_id}/list")
    @Operation(description = "書籍取得", summary = "書籍", tags = ["book"], operationId = "getBook")
    fun getBook(@PathVariable(name = "author_id") authorId: String): ResponseEntity<BookInfoResponse> {
        val bookResponse = bookApplicationService.getBooksInfoByAuthorId(authorId)
        if (bookResponse.result != ResponseStatus.OK) {
            return ResponseEntity.status(500).body(bookResponse)
        }
        return ResponseEntity.ok(bookResponse)
    }

}
