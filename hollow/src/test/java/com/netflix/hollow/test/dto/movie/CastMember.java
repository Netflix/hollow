package com.netflix.hollow.test.dto.movie;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class CastMember {
  public int id;
  @HollowInline String name;
  public CastRole role;

  public CastMember(int id, String name, CastRole role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }
}
