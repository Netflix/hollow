package com.netflix.hollow.core.write.objectmapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class TypeWithSpecialTypes {
    Date date;
    Instant instant;
    LocalDate localDate;

    public TypeWithSpecialTypes(
          Date date,
          Instant instant,
          LocalDate localDate
    ) {
        this.date = date;
        this.instant = instant;
        this.localDate = localDate;
    }
}
