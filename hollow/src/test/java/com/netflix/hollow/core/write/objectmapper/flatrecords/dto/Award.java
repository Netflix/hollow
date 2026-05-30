package com.netflix.hollow.core.write.objectmapper.flatrecords.dto;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

public class Award {
  @HollowTypeName(name = "TestAwardName")
  public String name;
  public int year;

  public Award(String name, int year) {
    this.name = name;
    this.year = year;
  }
}
