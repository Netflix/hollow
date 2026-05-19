package com.netflix.hollow.core.write.objectmapper.flatrecords.dto;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class TagValue {
  @HollowInline public String value;

  public TagValue(String value) {
    this.value = value;
  }
}


