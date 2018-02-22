package com.example.shaha.eventfinderandroid;

import com.microsoft.azure.storage.table.TableServiceEntity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by shaha on 22/02/2018.
 */

public class MessageEntity extends TableServiceEntity {
    private String message;

    private String user;

    public MessageEntity() {
        //the default constructor
    }

    public MessageEntity(int eventId, String eventType) {
        this.partitionKey =eventType +"_"+ String.valueOf(eventId);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Long time = new Timestamp(Long.MAX_VALUE).getTime() - now.getTime();
        this.rowKey = String.valueOf(time);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
