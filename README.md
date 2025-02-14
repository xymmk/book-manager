# 概要

書籍・著者管理システム

# 構成

マルチモジュール

DDD + クリーンアーキテクチャ

> 参考:https://dev.to/borikatsu/kurinakitekutiyato-ddd-woyi-shi-sitapatukezigou-cheng-nituitenosi-an-29bp

```

├── book-manager-api
├── docker-compose.yml
├── domain
├── infra
├── config
├── library
```

# 環境

言語: Kotlin

フレームワーク: Spring Boot、jOOQ

MacOS: Apple M2(Sonoma 14.6.1)

# 実行

##

## API 起動

docker compose up

## 著者登録

## 著者更新

## 書籍登録

## 書籍更新

## 書籍情報取得

# 機能

## 機能一覧

- 書籍情報を登録する api
- 書籍情報を更新する api
- 著者情報を登録する api
- 著者情報を更新する api
- 著者に紐づく本を取得する api

## 各 api の詳細

### 書籍情報を登録する api

#### 要件:

```
書籍の情報を登録する
```

#### 制約:

```
1、タイトルは空文字は禁止、最大の文字制限はデータベースの制限によって、500文字と設定
2、価格0以上設定する必要があり、小数は2桁だけ保存する
3、著者を登録する必要があり、システム上に存在していない著者を指定された場合、登録せず、エラーを表示する
4、出版状況を登録する必要がある
```

#### IF

- path: {context-path}/book/register
- method: post
- param:
  - title:
    - description: 書籍タイトル
    - required: true
    - type: string
    - constraints: 文字数は 1 以上、500 以下
  - price:
    - description: 価格
    - required: true
    - type: double
    - constraints: 0 以上
  - authors:
    - descriptioin: 書籍の著者
    - required: true
    - type: List<String>
    - constraints: 配列の要素は 1 以上と設定する必要がある
  - publication_status:
    - descriptioin: 出版状況
    - required: true
    - type: enum
    - constraints:未出版 or 出版済
- response:

```
{
  "result": "{ok ・ failed}",
  "book_id": "成功登録となる場合、book_idを返す"
}
```

### 書籍情報を更新する api

#### 要件:

```
書籍の情報を更新する
```

#### 制約:

```
1、タイトルは空文字は禁止、最大の文字制限はデータベースの制限によって、500文字と設定
2、価格0以上設定する必要があり、小数は2桁だけ保存する
3、著者を設定する必要があり、システム上に存在していない著者を指定された場合、登録せず、エラーを表示する
4、出版状況を設定する必要があり、出版済みステータスのものを未出版には変更
```

#### IF

- path: {context-path}/book/{book_id}/update
- method: put
- path_param:
  - book_id
    - description: 書籍の ID
    - type: string
- param:
  - title:
    - description: 書籍タイトル
    - required: true
    - type: string
    - constraints: 文字数は 1 以上、500 以下
  - price:
    - description: 価格
    - required: true
    - type: double
    - constraints: 0 以上
  - authors:
    - descriptioin: 書籍の著者
    - required: true
    - type: List<String>
    - constraints: 配列の要素は 1 以上と設定する必要がある
  - publication_status:
    - descriptioin: 出版状況
    - required: true
    - type: enum
    - constraints:未出版 or 出版済
- response:

```
{
  "result": "{ok ・ failed}"
}
```

---

### 著者情報を登録する api

#### 要件

```
著者情報を登録する
```

#### 制約:

```
1、名前は空文字は禁止、最大の文字制限はデータベースの制限によって、500文字と設定
2、生年月日は現在の日付より過去であることと設定する必要がある
3、書籍リストの中に、システム上で存在していない書籍がある場合は登録せず、エラーを表示する
```

#### IF

- path: {context-path}/author/register
- method: post
- param:
  - name:
    - description: 著者の名前
    - required: true
    - type: string
    - constraints: 文字数は 1 以上、500 以下
  - birth_date:
    - description: 生年月日
    - required: true
    - type: Date
    - constraints: 生年月日は現在の日付より過去であることと設定する必要がある
  - books:
    - description: 書籍リスト
    - required: false
    - type: List<String>
    - constraints: システム上で存在していない書籍はリストの中に入っている場合は登録せず、エラーを表示する
- response:

```
{
  "result": "{ok ・ failed}",
  "author_id": "成功登録となる場合、author_idを返す"
}
```

### 著者情報を更新する api

#### 要件

```
著者情報を更新する
```

#### 制約:

```
1、名前は空文字は禁止、最大の文字制限はデータベースの制限によって、500文字と設定
2、生年月日は現在の日付より過去であることと設定する必要がある
3、書籍リストの中に、システム上で存在していない書籍がある場合は更新せず、エラーを表示する
```

#### IF

- path: {context-path}/author/{author_id}/update
- method: put
- path_param:
  - author_id
    - description: 著者の ID
    - type: String
- param:
  - name:
    - description: 著者の名前
    - required: true
    - type: string
    - constraints: 文字数は 1 以上、500 以下
  - birth_date:
    - description: 生年月日
    - required: true
    - type: Date
    - constraints: 生年月日は現在の日付より過去であることと設定する必要がある
  - books:
    - description: 書籍リスト
    - required: false
    - type: List<String>
    - constraints: システム上で存在していない書籍はリストの中に入っている場合は更新せず、エラーを表示する
- response:

```
{
  "result": "{ok ・ failed}"
}
```

---

### 著者に紐づく本を取得する api

#### 要件

```
著者に紐づく本を取得する
```

### 制約

なし

#### IF

- path: {context-path}/book/{author_id}
- method: get
- path_param:
  - author_id
    - description: 著者の ID
    - type: string
- response:

```
{
  "data": [
    {
        "book_id": "書籍のID",
        "price": "書籍の価格",
        "title": "書籍のタイトル",
        "publication_status": "書籍の出版状況"

    }
  ]
}
```
