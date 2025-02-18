package com.quo.book.manager.controller.author

import com.quo.book.manager.dto.BookManagerApiResponse
import com.quo.book.manager.dto.ResponseStatus
import com.quo.book.manager.dto.author.AuthorControllerRequest
import com.quo.book.manager.service.author.AuthorApplicationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 著者管理コントローラー
 */
@RestController
@RequestMapping("/author")
class AuthorManageController(val authorApplicationService: AuthorApplicationService) {

    @PostMapping("/register")
    @Operation(description = "著者登録", summary = "著者", tags = ["author"], operationId = "registerAuthor")
    fun registerAuthor(@RequestBody @Validated @Parameter(description = "著者登録情報") authorControllerRequest: AuthorControllerRequest): ResponseEntity<BookManagerApiResponse>{
        val registeredResponse = authorApplicationService.registerAuthor(authorControllerRequest)
        return if (registeredResponse.result == ResponseStatus.OK) {
            ResponseEntity.ok(registeredResponse)
        } else {
            ResponseEntity.status(500).body(registeredResponse)
        }
    }

    @PutMapping("/{author_id}/update")
    @Operation(description = "著者更新", summary = "著者", tags = ["author"], operationId = "updateAuthor")
    fun updateAuthor(
        @PathVariable(name = "author_id") authorId: String,
        @RequestBody @Validated @Parameter(description = "著者更新情報") authorControllerRequest: AuthorControllerRequest): ResponseEntity<BookManagerApiResponse>{
        val updatedResponse = authorApplicationService.updateAuthor(authorId, authorControllerRequest)
        return if (updatedResponse.result == ResponseStatus.OK) {
            ResponseEntity.ok(updatedResponse)
        } else {
            ResponseEntity.status(500).body(updatedResponse)
        }
    }
}
