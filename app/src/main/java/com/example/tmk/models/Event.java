package com.example.tmk.models;

public class Event {
    private final String title;
    private final int imageResId;
    private final String eventDate;

    public Event(String title, int imageResId, String eventDate) {
        this.title = title;
        this.imageResId = imageResId;
        this.eventDate = eventDate;
    }

    public Event(String title, int imageResId) {
        this(title, imageResId, "");
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getEventDate() {
        return eventDate;
    }
}