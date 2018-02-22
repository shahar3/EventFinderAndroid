package com.example.shaha.eventfinderandroid;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by shaha on 22/02/2018.
 */

public class MessageEntity extends TableServiceEntity {
    private String message;

    private String user;

    public MessageEntity() {
        //the default constructor
    }

    public MessageEntity(int eventId, String eventName) {
        this.partitionKey = String.valueOf(eventId);
        this.rowKey = eventName + "_" + timeStamp;
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
