package com.netflix.vms.transformer.hollowoutput;


public class MoviePersonCharacter implements Cloneable {
    public long movieId = java.lang.Long.MIN_VALUE;
    public long personId = java.lang.Long.MIN_VALUE;
    public long characterId = java.lang.Long.MIN_VALUE;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (characterId ^ (characterId >>> 32));
        result = prime * result + (int) (movieId ^ (movieId >>> 32));
        result = prime * result + (int) (personId ^ (personId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MoviePersonCharacter other = (MoviePersonCharacter) obj;
        if (characterId != other.characterId)
            return false;
        if (movieId != other.movieId)
            return false;
        if (personId != other.personId)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "MoviePersonCharacter [movieId=" + movieId + ", personId="
                + personId + ", characterId=" + characterId + "]";
    }

    public MoviePersonCharacter clone() {
        try {
            MoviePersonCharacter clone = (MoviePersonCharacter)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}