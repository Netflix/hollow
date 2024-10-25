package com.netflix.hollow.test.dto.movie;


import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

import java.util.List;
import java.util.Map;
import java.util.Set;

@HollowPrimaryKey(fields = {"id"})
public class Movie {
  public int id;

  @HollowTypeName(name = "MovieTitle")
  public String title;
  public int releaseYear;
  @HollowTypeName(name = "MovieGenre")
  public String primaryGenre;
  public MaturityRating maturityRating;

  public Set<Country> countries;
  @HollowHashKey(fields = {"value"})
  public Map<Tag, TagValue> tags;

  @HollowHashKey(fields = {"id"})
  public Set<CastMember> cast;

  public List<Award> awardsReceived;
}
