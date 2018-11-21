package com.example.user.drawinggame.database_classes;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey
    @ColumnInfo(name = "msg_id")
    private int messageID;

    @ColumnInfo(name = "sender_id")
    private int senderID;

    @ColumnInfo(name = "receiver_id")
    private int receiverID;

    @ColumnInfo(name = "content")
    private String msgContent;

    @ColumnInfo(name = "status")
    private int msgStatus;

    @ColumnInfo(name = "Type")
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

    public int getReceiverID() {
        return receiverID;
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
