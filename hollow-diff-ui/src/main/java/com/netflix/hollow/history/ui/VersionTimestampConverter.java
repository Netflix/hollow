/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.history.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class VersionTimestampConverter {

    public static final TimeZone PACIFIC_TIMEZONE = TimeZone.getTimeZone("America/Los_Angeles");

    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

    private static long ADD_MILLIS_TO_TIMESTAMP = 0;

    public static void addMillisToTimestamps(long millis) {
        ADD_MILLIS_TO_TIMESTAMP = millis;
    }

    public static String getTimestamp(long versionLong, TimeZone timeZone) {
        String version = String.valueOf(versionLong);
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        utcFormat.setTimeZone(UTC_TIMEZONE);

        try {
            Date date = utcFormat.parse(version);

            Date adjustedDate = new Date(date.getTime() + ADD_MILLIS_TO_TIMESTAMP);

            SimpleDateFormat sdf = new SimpleDateFormat("[MM/dd HH:mm z] ");
            sdf.setTimeZone(timeZone);

            return sdf.format(adjustedDate);
        } catch (ParseException ignore) {
        }

        return String.valueOf(versionLong);
    }


}
