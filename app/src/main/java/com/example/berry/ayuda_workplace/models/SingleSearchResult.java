package com.example.berry.ayuda_workplace.models;

public class SingleSearchResult {
    String title, callNumber, author;
    Boolean isAvailable, isRovi;

    public SingleSearchResult(String title, String callNumber, Boolean isAvailable, String author, Boolean isRovi) {
        this.title = title;
        this.callNumber = callNumber;
        this.isAvailable = isAvailable;
        this.author = author;
        this.isRovi = isRovi;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public Boolean getAvailability() {
        return isAvailable;
    }

    public void setAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getRovi() {
        return isRovi;
    }

    public void setRovi(Boolean rovi) {
        isRovi = rovi;
    }
}
