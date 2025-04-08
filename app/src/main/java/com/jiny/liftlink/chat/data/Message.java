package com.jiny.liftlink.chat.data;

import com.google.firebase.Timestamp;

public class Message {
    private String message;
    private String sender;
    private Timestamp timestamp;

    // 기본 생성자 (Firestore에서 데이터를 가져올 때 필요)
    public Message() {
    }

    public Message(String message, String sender, Timestamp timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
