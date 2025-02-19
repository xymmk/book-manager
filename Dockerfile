FROM amazoncorretto:21 AS builder
ENV TZ="Asia/Tokyo"

USER root
RUN mkdir -p /usr/local/app/
COPY . /usr/local/app/
WORKDIR /usr/local/app/
RUN chmod +x /usr/local/app/gradlew
ENV SPRING_PROFILES_ACTIVE=dev
RUN /usr/local/app/gradlew :book-manager-api:build -x test


FROM amazoncorretto:21 AS java21
RUN yum update -y
ENV SPRING_PROFILES_ACTIVE=dev
RUN echo ${SPRING_PROFILES_ACTIVE}
COPY --from=builder /usr/local/app/book-manager-api/build/libs/book-manager-api /app/
ENTRYPOINT ["/app/book-manager-api"]
