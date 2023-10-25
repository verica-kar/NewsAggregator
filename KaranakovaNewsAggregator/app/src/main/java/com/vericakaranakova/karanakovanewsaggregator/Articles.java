package com.vericakaranakova.karanakovanewsaggregator;

import androidx.annotation.NonNull;

public class Articles {

    public String author;
    public String title;
    public String description;
    public String url;
    public String imageUrl;
    public String publishedAt;

    public Articles(String a, String t, String d, String u, String iu, String p) {
        author = a;
        title = t;
        description = d;
        url = u;
        imageUrl = iu;
        publishedAt = p;
    }

    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getPublishedAt() {
        return publishedAt;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
