package com.jiny.liftlink.changelog.data.model;// com.jiny.liftlink.changelog.data.model.Changelog.java

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Changelog {
    private String id;
    private String title;
    private String description;
    private String author;
    private String tag;
    private long timestamp;

    public Changelog() {} // Firestore에서 필요

    public Changelog(String title, String description, String author, String tag, long timestamp) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.tag = tag;
        this.timestamp = timestamp;
    }

    // Getter & Setter

}
