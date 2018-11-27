package com.example.user.drawinggame.database_classes;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    public Message(int messageID, int senderID, String senderName, int receiverID, String receiverName, String msgContent, String msgTime, int msgStatus, int msgType) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.senderName = senderName;
        this.receiverID = receiverID;
        this.receiverName = receiverName;
        this.msgContent = msgContent;
        this.msgTime = msgTime;
        this.msgStatus = msgStatus;
        this.msgType = msgType;
    }

    @PrimaryKey
    @ColumnInfo(name = "msg_id")
    private int messageID;

    @ColumnInfo(name = "sender_id")
    private int senderID;

    @ColumnInfo(name = "sender_name")
    private String senderName;

    @ColumnInfo(name = "receiver_id")
    private int receiverID;

    @ColumnInfo(name = "receiver_name")
    private String receiverName;

    @ColumnInfo(name = "content")
    private String msgContent;

    @ColumnInfo(name = "time")
    private String msgTime;

    @ColumnInfo(name = "status")
    private int msgStatus;

    @ColumnInfo(name = "type")
    private int msgType;

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public int getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
