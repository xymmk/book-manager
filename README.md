# 目次

1. [概要](#概要)
2. [アーキテクチャ](#アーキテクチャ)
3. [開発環境](#開発環境)
4. [API 起動](#api-起動)
5. [API 呼び出す例](#api呼び出す例)
6. [単体テスト](#単体テスト)
7. [JOOQ のコード生成](#jooq-のコード生成)
8. [DB 設計](#db設計)
9. [IF 設計](#if設計)

# 概要

書籍・著者管理システム

> 参考: https://quo-digital.hatenablog.com/entry/2024/03/22/143542

# アーキテクチャ

- マルチモジュール

- DDD + クリーンアーキテクチャ

```

├── book-manager-api    (rest api)
├── docker-compose.yml  (docker環境)
├── domain              (ドメイン)
├── infra               (インフラ)
├── config              (DBの設定など)
├── library             (ライブラリ：使っていない)
```

# 開発環境

言語: Kotlin

フレームワーク: Spring Boot(3.3.8)、JOOQ(3.19.11)

Java: 21

MacOS: Apple M2(Sonoma 14.6.1)

# API 起動

JOOQ のコードは生成済です。

API のビルド・起動時に、JOOQ のコード生成は不要です。

> 生成した JOOQ コード: https://github.com/xymmk/book-manager/tree/main/infra/src/generated/com/quo/book/manager/jooq

アプリケーションが起動できたら、下記のサイトにアクセスできます。

> swagger: http://localhost:9099/book-manager-api/swagger-ui/index.html#/

(JOOQ コード生成について、[JOOQ のコード生成](#jooq-のコード生成)を参考してください。)

## ローカル起動

ローカルから起動する場合、ローカルでは Java21 環境が必要です。

```
# 下記のコマンドで、postgresqlのdockerコンテナを起動します。
docker compose up postgres -d

# アプリケーションを起動します。
export SPRING_PROFILES_ACTIVE=local
./gradlew :book-manager-api:bootRun
```

## Docker 環境起動

Docker 環境で起動する場合、コンテナ内部で jar を作成しています。

> 参考: https://github.com/xymmk/book-manager/blob/163b6e942a85937a6ab3c72c447f59b2352c725a/Dockerfile#L10

下記のコマンドを実行し、アプリケーションを起動します。

```
docker compose up
```

# API 呼び出す例

最初は書籍に紐づかない著者を登録しておく必要があります。
(書籍を登録する時、一人以上の著者が必要ですので、著者を先に登録しておかないと、書籍を登録できません。)

- 著者登録

パラメータbooksを設定しなくても登録できます。

登録できたら、著者の番号を返します。

```
# ex:)

curl -X 'POST' \
  'http://localhost:9099/book-manager-api/author/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "著者名",
  "birth_date": "2000-12-10"
}'

# response
{
  "result": "OK",
  "message": "登録成功 著者番号:3"
}
```

- 著者更新

パラメータbooksの中に、何も設定していない場合、著者に紐づく書籍関係を削除し、更新します。

```
# ex:)

curl -X 'PUT' \
  'http://localhost:9099/book-manager-api/author/3/update' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "著者名A",
  "birth_date": "2000-12-10",
  "books": []
}'

# response
{
  "result": "OK",
  "message": "更新成功"
}
```

- 書籍登録

登録済みの著者 ID を authors に設定し、書籍を登録します。

登録できたら、書籍のユニーク番号を返します。

```
# ex:)

curl -X 'POST' \
  'http://localhost:9099/book-manager-api/book/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "書籍タイトル",
  "price": 10.123,
  "authors": [
    "1"
  ],
  "publication_status": "PUBLISHED"
}'

# resposne

{
  "result": "OK",
  "message": "登録成功 書籍番号:1"
}
```

- 書籍更新

書籍番号を指定し、情報を更新します。

```
# ex:)

curl -X 'PUT' \
  'http://localhost:9099/book-manager-api/book/1/update' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "書籍タイトルA",
  "price": 1120,
  "authors": ["1"],
  "publication_status": "PUBLISHED"
}'

# response
{
  "result": "OK",
  "message": "更新成功"
}

```

- 書籍取得

著者の番号を設定し、紐づく書籍の情報を取得します。

```
# ex:)
curl -X 'GET' \
  'http://localhost:9099/book-manager-api/book/1/list' \
  -H 'accept: */*'


# response

{
  "result": "OK",
  "data": [
    {
      "book_id": "1",
      "publication_status": "出版済み",
      "association_authors": [
        {
          "id": "1",
          "name": "著者名",
          "birth": "2000-12-10"
        }
      ],
      "price": "1120.0",
      "title": "書籍タイトルA"
    }
  ]
}
```

# 単体テスト

JOOQ のコードは生成済です。単体テストを実行する場合、JOOQ のコード生成は不要です。

テストを実行するために、[Testcontainers](https://testcontainers.com/)を使っていますので、ローカルではDocker 環境が必要です。

(JOOQ コード生成について、[JOOQ のコード生成](#jooq-のコード生成)を参考してください。)

## テストコマンド

- 全部実行

```
export SPRING_PROFILES_ACTIVE=test
./gradlew :book-manager-api:test
```

- 個別のメソッド実行

```
export SPRING_PROFILES_ACTIVE=test
./gradlew :book-manager-api:test "{テストしたいクラス名}.{テストしたいメソッド}"
```

# JOOQ のコード生成

JOOQ のコード生成はローカルから実行する必要があります。

postgresql を起動しておく必要があります。

JOOQ のコードを生成してから、アプリケーションを起動したい場合、下記のコマンドを実行します。

```
# postgresqlは起動していない場合、エラーとなります。
# 参考: https://github.com/xymmk/book-manager/blob/main/infra/startup_postgresql.sh
docker compose up postgres -d

# enableJooqCodegenのパラメータをtureと設定し、起動します。
export SPRING_PROFILES_ACTIVE=local
./gradlew :book-manager-api:bootRun -PenableJooqCodegen=true
```

※ enableJooqCodegen のパラメータの定義について、下記を参考します。

> 参考: https://github.com/xymmk/book-manager/blob/163b6e942a85937a6ab3c72c447f59b2352c725a/infra/build.gradle#L62

# DB 設計

> 参考: https://github.com/xymmk/book-manager/blob/main/doc/db.md

# IF 設計

> 参考: https://github.com/xymmk/book-manager/blob/main/doc/api_if.md
