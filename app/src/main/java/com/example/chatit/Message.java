package com.example.chatit;

public class Message {

    public enum type{TEXT,IMG,AUDIO,VIDEO,DOC}
    public byte[] data;
    public type tp;

    public Message(type tp,byte[] data){
        this.tp = tp;
        this.data = data;
    }
}
