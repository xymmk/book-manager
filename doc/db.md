# DBの構造


## authors
|項目|型|説明|制約|
|--|--|--|--|
|author_id|SERIAL PRIMARY KEY|著者ID|PK|
|author_name|VARCHAR(500) NOT NULL|著者名|必須|
|birth_date|DATE|生年月日|必須|


## books

|項目|型|説明|制約|
|--|--|--|--|
|book_id|SERIAL PRIMARY KEY|書籍ID|PK|
|price|DECIMAL|価格|必須|
|title|VARCHAR(500)|書籍タイトル|必須|
|publication_status|VARCHAR(100)|出版状況|必須|


## author_book

|項目|型|説明|制約|
|--|--|--|--|
|book_id|INT|書籍ID|books_fk(booksのbook_idの外部キー)|
|author_id|INT|著者ID|authors_fk(authorsのauthor_idの外部キー)|