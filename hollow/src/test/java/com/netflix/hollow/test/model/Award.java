package com.netflix.hollow.test.model;

import java.util.Set;

public class Award {
    long id;
    Movie winner;
    Set<Movie> nominees;

    public Award(long id, Movie winner, Set<Movie> nominees) {
        this.id = id;
        this.winner = winner;
        this.nominees = nominees;
    }
}