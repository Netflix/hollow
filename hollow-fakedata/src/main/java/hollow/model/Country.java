package hollow.model;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class Country {
    @HollowInline
    String id;

    public Country(String id) {
        this.id = id;
    }
}
