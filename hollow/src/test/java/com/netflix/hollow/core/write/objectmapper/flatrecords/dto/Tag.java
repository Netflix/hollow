package com.netflix.hollow.core.write.objectmapper.flatrecords.dto;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class Tag {
  @HollowInline public final String value;

  public Tag(String value) {
    this.value = value;
  }
}
