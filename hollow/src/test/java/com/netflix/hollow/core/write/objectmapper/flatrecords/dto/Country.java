package com.netflix.hollow.core.write.objectmapper.flatrecords.dto;


import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class Country {
  @HollowInline public String value;

  public Country(String value) {
    this.value = value;
  }
}
