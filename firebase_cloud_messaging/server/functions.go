package server

import (
	"context"
	"encoding/json"
	"firebase.google.com/go/v4/messaging"
	"fmt"
	"log"
	"net/http"
	"regexp"
	"strings"

	"firebase.google.com/go/v4"
)

type Request struct {
	UserId string `json:"userId"`
}

type User struct {
	Id      string   `json:"id"`
	Tokens  []string `json:"tokens"`
	Message string   `json:"message"`
}

func HelloWorld(w http.ResponseWriter, r *http.Request) {
	// firestoreを初期化
	app, err := firebase.NewApp(context.Background(), nil)
	if err != nil {
		log.Println(err)
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte("cannot initialize firebase app"))
		return
	}

	// 呼び出し元を検証
	auth, err := app.Auth(context.Background())
	authHeader := r.Header.Get("Authentication")
	if authHeader == "" {
		w.WriteHeader(http.StatusForbidden)
		log.Println("empty auth header")
		return
	}
	if !strings.Contains(strings.ToLower(authHeader), "bearer") {
		w.WriteHeader(http.StatusForbidden)
		log.Println("auth header not contain bearer")
		return
	}
	re := regexp.MustCompile("(?i)bearer\\s+")
	idToken := re.ReplaceAllString(authHeader, "")

	_, err = auth.VerifyIDToken(context.Background(), idToken)
	if err != nil {
		w.WriteHeader(http.StatusForbidden)
		log.Println("error while verifying id token", err)
		w.Write([]byte(fmt.Sprintf("error while verifying id token: %v", err)))
		return
	}

	firestore, err := app.Firestore(context.Background())
	if err != nil {
		log.Println(err)
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte("cannot initialize firestore"))
		return
	}

	// リクエストをデコード
	var request Request
	if err = json.NewDecoder(r.Body).Decode(&request); err != nil {
		log.Println(fmt.Errorf("error while deconding request: %v", err))
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	// ユーザーデータを参照
	doc := firestore.Collection("users").Doc(request.UserId)
	snapshot, err := doc.Get(context.Background())
	if err != nil {
		log.Println(fmt.Errorf("error while quering user document: %v", err))
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(fmt.Sprintf("error while quering user document: %v", err)))
		return
	}

	var user User
	if err = snapshot.DataTo(&user); err != nil {
		log.Println(fmt.Errorf("error while deconding user data: %v", err))
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(fmt.Sprintf("error while deconding user data: %v", err)))
		return
	}

	// 登録されたトークンにPush通知を作成
	fcm, err := app.Messaging(context.Background())
	if err != nil {
		log.Println(fmt.Errorf("error getting Messaging client: %v", err))
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(fmt.Sprintf("error getting Messaging client: %v", err)))
		return
	}

	message := &messaging.MulticastMessage{
		Data: map[string]string{
			"message": user.Message,
		},
		Tokens: user.Tokens,
	}
	br, err := fcm.SendMulticast(context.Background(), message)
	if err != nil {
		log.Println(fmt.Errorf("error while sending multicast message: %v", err))
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(fmt.Sprintf("error while sending multicast message: %v", err)))
		return
	}

	w.Write([]byte(fmt.Sprintf("Successfully sent to %d devices: %v\n", br.SuccessCount, user.Tokens)))
	log.Printf("Successfully sent to %d devices\n", br.SuccessCount)
}
