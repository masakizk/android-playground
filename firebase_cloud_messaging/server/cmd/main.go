package main

import (
	"github.com/GoogleCloudPlatform/functions-framework-go/funcframework"
	"github.com/masakizk/go/fcm/server"
	"log"
)

func main() {
	funcframework.RegisterHTTPFunction("/notify", server.HelloWorld)
	if err := funcframework.Start("8080"); err != nil {
		log.Fatalf("error while stating server: %v", err)
	}
}
