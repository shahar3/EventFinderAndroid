package com.example.shaha.eventfinderandroid;

/**
 * Created by shaha on 22/02/2018.
 */

public class EventMessage {
    private String text;
    private String name;

    public EventMessage(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
