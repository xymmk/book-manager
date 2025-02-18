FROM amazoncorretto:21 AS builder
ENV TZ="Asia/Tokyo"

USER root
RUN mkdir -p /usr/local/app/
COPY . /usr/local/app/
WORKDIR /usr/local/app/
RUN chmod +x /usr/local/app/gradlew
RUN /usr/local/app/gradlew :book-manager-api:build


FROM amazoncorretto:21 AS java21
RUN yum update -y
COPY --from=builder /usr/local/app/book-manager-api/build/libs/book-manager-api /app/
ENTRYPOINT ["/app/book-manager-api"]
