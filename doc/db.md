# DBの構造

```mermaid
erDiagram
    authors {
        int author_id PK
        varchar author_name
        date birth_date
    }
    books {
        int book_id PK
        decimal price
        varchar title
        varchar publication_status
    }
    author_book {
        int book_id FK
        int author_id FK
        PRIMARY KEY (book_id, author_id)
    }
    authors ||--o{ author_book : "writes"
    books ||--o{ author_book : "written by"