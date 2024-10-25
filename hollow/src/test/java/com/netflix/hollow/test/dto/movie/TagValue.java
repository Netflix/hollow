package com.netflix.hollow.test.dto.movie;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class TagValue {
 @HollowInline public String value;

  public TagValue(String value) {
    this.value = value;
  }
}


