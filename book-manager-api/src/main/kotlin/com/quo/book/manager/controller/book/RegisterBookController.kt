package com.quo.book.manager.controller.book

import com.quo.book.manager.dto.ResponseStatus
import com.quo.book.manager.dto.book.RegisterBookRequest
import com.quo.book.manager.dto.book.RegisterBookResponse
import com.quo.book.manager.service.RegisterBookApplicationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 書籍登録コントローラー
 */
@RestController
@RequestMapping("/book")
class RegisterBookController(val registerBookApplicationService: RegisterBookApplicationService) {

    @PostMapping("/register")
    @Operation(description = "書籍登録", summary = "書籍", tags = ["book"], operationId = "registerBook")

    fun registerBook(@RequestBody @Validated @Parameter(description = "書籍登録情報") registerBookRequest: RegisterBookRequest): ResponseEntity<RegisterBookResponse> {
        val registerResponse = registerBookApplicationService.registerBook(registerBookRequest)
        return if(registerResponse.result == ResponseStatus.OK){
            ResponseEntity.ok(registerResponse)
        } else {
            ResponseEntity.status(500).body(registerResponse)
        }
    }

}
