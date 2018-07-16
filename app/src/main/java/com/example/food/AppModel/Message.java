package com.example.food.AppModel;

public class Message {
    private String message;
    private int memberId;
    private String nickname;

    public Message(String message, int memberId, String nickname) {
        this.message = message;
        this.memberId = memberId;
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
