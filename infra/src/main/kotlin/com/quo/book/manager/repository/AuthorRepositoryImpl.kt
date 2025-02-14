package com.quo.book.manager.repository

import com.quo.book.manager.jooq.tables.references.AUTHORS
import com.quo.book.manager.record.Author
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepositoryImpl(val dsl: DSLContext) : AuthorRepository {
    override fun findAuthorBy(authorId: String): Author? {
        return dsl.selectFrom(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(authorId.toInt()))
            .fetchOneInto(Author::class.java)
    }

    override fun register(author: Author): Author? {
        val record = dsl.insertInto(AUTHORS)
            .set(AUTHORS.AUTHOR_NAME, author.authorName)
            .set(AUTHORS.BIRTH_DATE, author.birthDate)
            .returning(AUTHORS.AUTHOR_ID, AUTHORS.AUTHOR_NAME, AUTHORS.BIRTH_DATE)
            .fetchOne()
        return record?.let {
            Author(
                authorId = it.getValue(AUTHORS.AUTHOR_ID)!!.toString(),
                authorName = it.getValue(AUTHORS.AUTHOR_NAME)!!.toString(),
                birthDate = it.getValue(AUTHORS.BIRTH_DATE)!!
            )
        }
    }
}
