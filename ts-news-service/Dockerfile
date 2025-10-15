FROM golang:1.21-alpine


RUN mkdir -p /app
WORKDIR /app

COPY ./src/main/main.go ./src/main/lib.go ./
RUN go mod init news-service && go mod tidy
RUN go build -o app .

CMD [ "./app" ]

EXPOSE 12862
