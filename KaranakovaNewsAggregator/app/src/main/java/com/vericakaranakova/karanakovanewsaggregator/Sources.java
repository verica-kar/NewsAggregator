package com.vericakaranakova.karanakovanewsaggregator;

import androidx.annotation.NonNull;

import java.io.Serializable;

import javax.xml.transform.Source;

public class Sources implements Comparable<Sources>, Serializable {

    public String id;
    public String name;
    public String category;

    public Sources(String i, String n, String c) {
        id = i;
        name = n;
        category = c;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }

    @Override
    public int compareTo(Sources o) {
        return name.compareTo(o.name);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
