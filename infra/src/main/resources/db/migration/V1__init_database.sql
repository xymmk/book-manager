CREATE TABLE IF NOT EXISTS authors (
    author_id SERIAL PRIMARY KEY,
    author_name VARCHAR(500) NOT NULL,
    birth_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    book_id SERIAL PRIMARY KEY,
    price DECIMAL NOT NULL,
    title VARCHAR(500) NOT NULL,
    publication_status VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS author_book (
    book_id INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);
