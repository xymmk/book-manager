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

フレームワーク: Spring Boot(3.3.8)、jOOQ(3.19.11)

Java: 21

MacOS: Apple M2(Sonoma 14.6.1)

# 機能一覧

- [x] 書籍情報を登録する
- [x] 書籍情報を更新する
- [x] 著者情報を登録する
- [x] 著者情報を更新する
- [x] 著者に紐づく本を取得する

# API 起動

jOOQのコードは生成済で、APIを起動するだけなら、JOOQのコード生成は不要です。

JOOQのコードを生成したい場合、JOOQのコード生成を参考します。

起動できたら、下記のサイトにアクセスできます。

> swagger: http://localhost:9099/book-manager-api/swagger-ui/index.html#/

## ローカル起動

ローカルから起動する場合、java21環境が必要です。

```
# 下記のコマンドで、postgresqlのdockerコンテナを起動します
docker compose up postgres -d

# アプリケーションを起動します。
./gradlew :book-manager-api:bootRun
```

## Docker環境起動

Docker環境で起動する場合、コンテナ内部でjarを作成しています。

従って、java環境は不要です。

> 参考: https://github.com/xymmk/book-manager/blob/163b6e942a85937a6ab3c72c447f59b2352c725a/Dockerfile#L10

下記のコマンドで起動します。

```
docker compose up
```

# 単体テスト

jOOQのコードは生成済で、APIを起動するだけなら、JOOQのコード生成は不要です。

JOOQのコードを生成したい場合、[JOOQのコード](#jooqのコード生成)生成を参考します。

テストを実行するために、[testcontainers](https://testcontainers.com/)を使っていますので、Docker環境が必要です。

## テストコマンド

- 全部実行

```
./gradlew :book-manager-api:test
```

- 個別のメソッド実行

```
./gradlew :book-manager-api:test "{テストしたいクラス名}.{テストしたいメソッド}"
```


# jooqのコード生成

jooqのコード生成はローカルから実行します。

postgresqlを起動しておく必要があります。

jooqのコードを生成してから、アプリケーションを起動したい場合、下記のコマンドを実行します。

```
docker compose up postgres -d

./gradlew :book-manager-api:bootRun -PenableJooqCodegen=true
```

※ enableJooqCodegenの設定は下記を参考

> 参考: https://github.com/xymmk/book-manager/blob/163b6e942a85937a6ab3c72c447f59b2352c725a/infra/build.gradle#L62

# DB設計

> 参考: https://github.com/xymmk/book-manager/blob/main/doc/db.md
# IF設計

> 参考: https://github.com/xymmk/book-manager/blob/main/doc/api_if.md