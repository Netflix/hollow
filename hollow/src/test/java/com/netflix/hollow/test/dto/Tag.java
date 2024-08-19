package com.netflix.hollow.test.dto;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class Tag {
  @HollowInline String value;

  public Tag(String value) {
    this.value = value;
  }
}
