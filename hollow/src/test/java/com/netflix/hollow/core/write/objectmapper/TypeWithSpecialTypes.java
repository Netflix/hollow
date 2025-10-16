package com.netflix.hollow.core.write.objectmapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class TypeWithSpecialTypes {
    Date date;
    Instant instant;
    UUID uuid;
    LocalDate localDate;

    public TypeWithSpecialTypes(
          Date date,
          Instant instant,
          UUID uuid,
          LocalDate localDate
    ) {
        this.date = date;
        this.instant = instant;
        this.uuid = uuid;
        this.localDate = localDate;
    }
}
