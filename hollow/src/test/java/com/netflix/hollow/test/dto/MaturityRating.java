package com.netflix.hollow.test.dto;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

public class MaturityRating {
  @HollowTypeName(name = "TestMaturityRatingName")
  public String rating;
  @HollowTypeName(name = "TestMaturityAdvisoryName")
  public String advisory;

  public MaturityRating(String rating, String advisory) {
    this.rating = rating;
    this.advisory = advisory;
  }
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MaturityRating)) {
      return false;
    }
    return ((MaturityRating)obj).rating.equals(rating) && ((MaturityRating)obj).advisory.equals(advisory);
  }

}
