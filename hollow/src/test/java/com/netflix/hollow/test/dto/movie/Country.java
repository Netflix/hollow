package com.netflix.hollow.test.dto.movie;


import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class Country {
  @HollowInline String value;

  public Country(String value) {
    this.value = value;
  }
}
