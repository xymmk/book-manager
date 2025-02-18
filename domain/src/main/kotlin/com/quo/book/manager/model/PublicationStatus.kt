package com.quo.book.manager.model

enum class PublicationStatus(val value: String, val description: String) {
    PUBLISHED("PUBLISHED", "出版済み"),
    UNPUBLISHED("UNPUBLISHED", "未出版");
}
