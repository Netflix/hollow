package com.netflix.hollow.core.write.objectmapper.flatrecords.dto;


import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

import java.util.List;
import java.util.Map;
import java.util.Set;

@HollowPrimaryKey(fields = {"id"})
public class Movie {
  public int id; // primitive
  public String name; // primitive
  public Long duration; // primitive
  public double price; // primitive
  public float averageRating; // primitive
  public byte bytes; // nonsense primitive
  public Boolean isReleased; // boxed primitive
  public Double popularity;

  @HollowTypeName(name = "MovieTitle")
  public String title; // HollowTypeName

  public int releaseYear;

  @HollowTypeName(name = "MovieGenre")
  public String primaryGenre;

  public MaturityRating maturityRating; // nested object

  public Set<Country> countries; // set

  @HollowHashKey(fields = {"value"})
  public Map<Tag, TagValue> tags; // map

  @HollowHashKey(fields = {"id"})
  public Set<CastMember> cast;

  public Map<Set<Country>, Set<CastMember>> mapOfCountryToCastMembers; // complex structure

  public List<Award> awardsReceived; // list
}
