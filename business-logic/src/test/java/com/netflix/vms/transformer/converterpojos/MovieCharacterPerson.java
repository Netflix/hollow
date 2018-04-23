package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="MovieCharacterPerson")
public class MovieCharacterPerson implements Cloneable {

    public long movieId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="CharacterList")
    public List<PersonCharacter> characters = null;

    public MovieCharacterPerson setMovieId(long movieId) {
        this.movieId = movieId;
        return this;
    }
    public MovieCharacterPerson setCharacters(List<PersonCharacter> characters) {
        this.characters = characters;
        return this;
    }
    public MovieCharacterPerson addToCharacters(PersonCharacter personCharacter) {
        if (this.characters == null) {
            this.characters = new ArrayList<PersonCharacter>();
        }
        this.characters.add(personCharacter);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof MovieCharacterPerson))
            return false;

        MovieCharacterPerson o = (MovieCharacterPerson) other;
        if(o.movieId != movieId) return false;
        if(o.characters == null) {
            if(characters != null) return false;
        } else if(!o.characters.equals(characters)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (movieId ^ (movieId >>> 32));
        hashCode = hashCode * 31 + (characters == null ? 1237 : characters.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MovieCharacterPerson{");
        builder.append("movieId=").append(movieId);
        builder.append(",characters=").append(characters);
        builder.append("}");
        return builder.toString();
    }

    public MovieCharacterPerson clone() {
        try {
            MovieCharacterPerson clone = (MovieCharacterPerson)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}