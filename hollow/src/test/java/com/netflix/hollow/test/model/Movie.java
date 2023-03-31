package com.netflix.hollow.test.model;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields = {"id"})
public class Movie {
    long id;
    String title;
    int year;

    public Movie(long id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }
}
