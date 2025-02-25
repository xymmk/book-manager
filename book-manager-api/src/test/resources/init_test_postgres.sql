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

-- テスト用の著者を登録
INSERT INTO authors (author_id, author_name, birth_date) VALUES (9000 ,'Author 1', '1990-01-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (9001 ,'Author 2', '1999-02-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (9002 ,'Author 3', '1999-03-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (9003 ,'Author 4', '1999-04-01');

INSERT INTO authors (author_id, author_name, birth_date) VALUES (5999 ,'Author 5999', '2003-01-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (6000 ,'Author 6000', '2000-01-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (6001 ,'Author 6001', '2000-02-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (6002 ,'Author 6002', '2000-03-01');
INSERT INTO authors (author_id, author_name, birth_date) VALUES (6003 ,'Author 6003', '1999-03-01');

-- テスト用の本を登録
INSERT INTO books (book_id, price, title, publication_status) VALUES (8000 , 123.111, 'title 8000', 'UNPUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (8001 , 123.112, 'title 8001', 'UNPUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (8002 , 123.999, 'title 8002', 'UNPUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (8003 , 123.99, 'title PUBLISHED', 'PUBLISHED');

INSERT INTO books (book_id, price, title, publication_status) VALUES (5000 , 123.99, 'title 5000-6000', 'PUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5001 , 222.99, 'title 5001-6000', 'PUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5002 , 456.99, 'title 5002-6000', 'UNPUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5003 , 129.99, 'title 5003-6000', 'UNPUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5004 , 99.99, 'title 5004-6000', 'PUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5005 , 299.99, 'title 5005-6000', 'PUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5006 , 799.99, 'title 5006-5999', 'PUBLISHED');
INSERT INTO books (book_id, price, title, publication_status) VALUES (5007 , 999.99, 'title 5007-6003', 'PUBLISHED');


-- テスト用の著者と本の関連を登録
INSERT INTO author_book (book_id, author_id) VALUES (8000, 9000);
INSERT INTO author_book (book_id, author_id) VALUES (8001, 9000);
INSERT INTO author_book (book_id, author_id) VALUES (8002, 9000);
INSERT INTO author_book (book_id, author_id) VALUES (8003, 9000);
INSERT INTO author_book (book_id, author_id) VALUES (5000, 6000);
INSERT INTO author_book (book_id, author_id) VALUES (5001, 6000);
INSERT INTO author_book (book_id, author_id) VALUES (5002, 6000);
INSERT INTO author_book (book_id, author_id) VALUES (5003, 6000);
INSERT INTO author_book (book_id, author_id) VALUES (5004, 6000);
INSERT INTO author_book (book_id, author_id) VALUES (5005, 6000);
INSERT INTO author_book (book_id, author_id) VALUES (5001, 6001);
INSERT INTO author_book (book_id, author_id) VALUES (5001, 6002);
INSERT INTO author_book (book_id, author_id) VALUES (5002, 6002);
INSERT INTO author_book (book_id, author_id) VALUES (5003, 6002);
INSERT INTO author_book (book_id, author_id) VALUES (5004, 6002);
INSERT INTO author_book (book_id, author_id) VALUES (5005, 6001);
INSERT INTO author_book (book_id, author_id) VALUES (5006, 6003);
INSERT INTO author_book (book_id, author_id) VALUES (5007, 6003);
INSERT INTO author_book (book_id, author_id) VALUES (5006, 5999);
