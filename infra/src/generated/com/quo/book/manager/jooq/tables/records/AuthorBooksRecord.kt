/*
 * This file is generated by jOOQ.
 */
package com.quo.book.manager.jooq.tables.records


import com.quo.book.manager.jooq.tables.AuthorBooks
import com.quo.book.manager.jooq.tables.interfaces.IAuthorBooks

import org.jooq.Record2
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class AuthorBooksRecord() : UpdatableRecordImpl<AuthorBooksRecord>(AuthorBooks.AUTHOR_BOOKS), IAuthorBooks {

    open override var bookId: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open override var authorId: Int?
        set(value): Unit = set(1, value)
        get(): Int? = get(1) as Int?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record2<Int?, Int?> = super.key() as Record2<Int?, Int?>

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    override fun from(from: IAuthorBooks) {
        this.bookId = from.bookId
        this.authorId = from.authorId
        resetChangedOnNotNull()
    }

    override fun <E : IAuthorBooks> into(into: E): E {
        into.from(this)
        return into
    }

    /**
     * Create a detached, initialised AuthorBooksRecord
     */
    constructor(bookId: Int? = null, authorId: Int? = null): this() {
        this.bookId = bookId
        this.authorId = authorId
        resetChangedOnNotNull()
    }
}
