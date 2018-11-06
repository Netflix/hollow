/*
 *
 *  Copyright 2018 Netflix, Inc.
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

package com.netflix.hollow.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang.StringEscapeUtils;

public class EscapingTool {

    public String html(Object string) {
        return string == null ? null : StringEscapeUtils.escapeHtml(String.valueOf(string));
    }

    public String url(Object string) {
        if (string == null) {
            return null;
        } else {
            try {
                return URLEncoder.encode(String.valueOf(string), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        }
    }
}
