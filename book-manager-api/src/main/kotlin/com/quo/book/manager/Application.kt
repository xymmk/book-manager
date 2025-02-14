package com.quo.book.manager

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
	info = Info(
		title = "書籍管理API",
		version = "1.0",
		description = "書籍情報・著者情報を管理するAPIです。",
	)
)
@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
