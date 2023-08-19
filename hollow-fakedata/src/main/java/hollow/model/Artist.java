package hollow.model;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields = "name")
public class Artist {
    String name;

    String city;

    public Artist(String name, String city) {
        this.name = name;
        this.city = city;
    }
}
